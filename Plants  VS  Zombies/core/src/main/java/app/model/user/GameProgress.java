package app.model.user;

import app.enums.SeasonType;
import app.model.Collection;
import game.world.chapter.ChapterId;
import game.world.chapter.ChapterRegistry;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GameProgress {
    private Collection collection;
    private final Map<SeasonType, Integer> seasonProgress;
    private Map<String, StageProgress> stageProgressById;
    private Set<String> seenZombieAliases;
    private int totalGamesPlayed;
    private int totalGamesWon;
    private int totalGamesLost;

    public GameProgress() {
        this.seasonProgress = new HashMap<>();
        this.collection = new Collection();
        this.stageProgressById = new HashMap<>();
        this.seenZombieAliases = new HashSet<>();
        for (SeasonType type : SeasonType.values()) {
            seasonProgress.put(type, 0);
        }
        initializeDefaultStageUnlocks();
    }

    private void initializeDefaultStageUnlocks() {
        for (SeasonType season : SeasonType.values()) {
            if (season == SeasonType.MINIGAME) {
                continue;
            }
            int count = ChapterRegistry.countForSeason(season);
            for (int stage = 1; stage <= count; stage++) {
                String key = new ChapterId(season, stage).toName();
                if (stage == 1) {
                    stageProgressById.put(key, StageProgress.unlockedInitial());
                } else {
                    stageProgressById.putIfAbsent(key, new StageProgress());
                }
            }
        }
    }

    public void ensureStageEntries() {
        if (stageProgressById == null) {
            stageProgressById = new HashMap<>();
        }
        if (seenZombieAliases == null) {
            seenZombieAliases = new HashSet<>();
        }
        for (SeasonType season : SeasonType.values()) {
            if (season == SeasonType.MINIGAME) {
                continue;
            }
            int count = ChapterRegistry.countForSeason(season);
            for (int stage = 1; stage <= count; stage++) {
                String key = new ChapterId(season, stage).toName();
                StageProgress existing = stageProgressById.get(key);
                if (existing == null) {
                    if (stage == 1 || getHighestCompletedLevel(season) + 1 >= stage) {
                        StageProgress unlocked = StageProgress.unlockedInitial();
                        if (getHighestCompletedLevel(season) >= stage) {
                            unlocked.setCompleted(true);
                        }
                        stageProgressById.put(key, unlocked);
                    } else {
                        stageProgressById.put(key, new StageProgress());
                    }
                } else if (!existing.isUnlocked()
                    && (stage == 1 || getHighestCompletedLevel(season) + 1 >= stage)) {
                    existing.unlockNow();
                }
            }
        }
        if (collection == null) {
            collection = new Collection();
        }
        collection.syncFromAliases(seenZombieAliases);
    }

    public void markLevelCompleted(SeasonType season, int level) {
        int current = seasonProgress.getOrDefault(season, 0);
        if (level > current && level >= 1) {
            seasonProgress.put(season, level);
        }
        StageProgress progress = getOrCreateStageProgress(season, level);
        progress.setCompleted(true);
        if (!progress.isUnlocked()) {
            progress.unlockNow();
        }
    }

    public int getHighestCompletedLevel(SeasonType season) {
        return seasonProgress.getOrDefault(season, 0);
    }

    public boolean isLevelCompleted(SeasonType season, int level) {
        return level <= getHighestCompletedLevel(season);
    }

    public boolean isStageUnlocked(SeasonType season, int level) {
        if (season == null || season == SeasonType.MINIGAME) {
            return true;
        }
        ensureStageEntries();
        StageProgress progress = getStageProgress(season, level);
        if (progress != null && progress.isUnlocked()) {
            return true;
        }
        return level <= getHighestCompletedLevel(season) + 1;
    }

    public StageProgress getStageProgress(SeasonType season, int level) {
        if (season == null || season == SeasonType.MINIGAME || level < 1) {
            return null;
        }
        ensureStageEntries();
        return stageProgressById.get(new ChapterId(season, level).toName());
    }

    public StageProgress getOrCreateStageProgress(SeasonType season, int level) {
        ensureStageEntries();
        String key = new ChapterId(season, level).toName();
        return stageProgressById.computeIfAbsent(key, ignored -> new StageProgress());
    }

    public StageProgress getOrCreateStageProgress(ChapterId id) {
        if (id == null || id.seasonType() == SeasonType.MINIGAME) {
            return new StageProgress();
        }
        return getOrCreateStageProgress(id.seasonType(), id.chapterNumber());
    }

    public boolean unlockNextStage(SeasonType season, int completedLevel) {
        if (season == null || season == SeasonType.MINIGAME) {
            return false;
        }
        int next = completedLevel + 1;
        int max = ChapterRegistry.countForSeason(season);
        if (next < 1 || next > max) {
            return false;
        }
        StageProgress progress = getOrCreateStageProgress(season, next);
        boolean wasLocked = !progress.isUnlocked();
        progress.unlockNow();
        return wasLocked;
    }

    public void addSeenZombies(Iterable<String> aliases) {
        ensureStageEntries();
        if (aliases == null) {
            return;
        }
        for (String alias : aliases) {
            if (alias != null && !alias.isBlank()) {
                seenZombieAliases.add(alias);
            }
        }
        collection.syncFromAliases(seenZombieAliases);
    }

    public Set<String> getSeenZombieAliases() {
        ensureStageEntries();
        return Collections.unmodifiableSet(seenZombieAliases);
    }

    public boolean hasSeenZombie(String alias) {
        ensureStageEntries();
        return alias != null && seenZombieAliases.contains(alias);
    }

    public Collection getCollection() {
        ensureStageEntries();
        return collection;
    }

    public void setCollection(Collection collection) {
        this.collection = collection != null ? collection : new Collection();
    }

    public int getNumberOfLevelsCompleted() {
        int completedLevels = 0;
        for (SeasonType type : SeasonType.values()) {
            completedLevels += getHighestCompletedLevel(type);
        }
        return completedLevels;
    }

    public int getTotalGamesPlayed() {
        return totalGamesPlayed;
    }

    public void setTotalGamesPlayed(int totalGamesPlayed) {
        this.totalGamesPlayed = Math.max(0, totalGamesPlayed);
    }

    public int getTotalGamesWon() {
        return totalGamesWon;
    }

    public void setTotalGamesWon(int totalGamesWon) {
        this.totalGamesWon = Math.max(0, totalGamesWon);
    }

    public int getTotalGamesLost() {
        return totalGamesLost;
    }

    public void setTotalGamesLost(int totalGamesLost) {
        this.totalGamesLost = Math.max(0, totalGamesLost);
    }

    public void incrementTotalPlayed() {
        totalGamesPlayed++;
    }

    public void incrementTotalWon() {
        totalGamesWon++;
    }

    public void incrementTotalLost() {
        totalGamesLost++;
    }

    public Map<String, StageProgress> getStageProgressById() {
        ensureStageEntries();
        return stageProgressById;
    }
}

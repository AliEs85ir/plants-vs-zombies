package app.service.progression;

import app.enums.SeasonType;
import app.enums.plant.PlantName;
import app.model.user.GameProgress;
import app.model.user.StageProgress;
import app.model.user.User;
import app.service.AppService;
import app.service.ProgressService;
import game.entity.zombie.data.properties.Property;
import game.world.chapter.Chapter;
import game.world.chapter.ChapterId;
import game.world.chapter.ChapterRegistry;
import game.world.chapter.definition.ChapterUnlockCatalog;
import game.world.chapter.definition.ChapterZombieCatalog;
import game.world.chapter.definition.StartingPlantUnlocks;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class ProgressionService {
    private ProgressionService() {
    }

    public static final class ProgressionDelta {
        private final List<PlantName> newlyUnlockedPlants = new ArrayList<>();
        private final List<String> newlyDiscoveredZombies = new ArrayList<>();
        private boolean unlockedNextStage;
        private Integer nextStageNumber;
        private boolean firstClear;

        public List<PlantName> getNewlyUnlockedPlants() {
            return newlyUnlockedPlants;
        }

        public List<String> getNewlyDiscoveredZombies() {
            return newlyDiscoveredZombies;
        }

        public boolean isUnlockedNextStage() {
            return unlockedNextStage;
        }

        public Integer getNextStageNumber() {
            return nextStageNumber;
        }

        public boolean isFirstClear() {
            return firstClear;
        }
    }

    public static void applyNewUserDefaults(User user) {
        if (user == null) {
            return;
        }
        for (PlantName plant : StartingPlantUnlocks.initialPlants()) {
            user.unlockPlant(plant);
        }
        GameProgress progress = user.getGameProgress();
        progress.ensureStageEntries();
        for (SeasonType season : SeasonType.values()) {
            if (season == SeasonType.MINIGAME) {
                continue;
            }
            grantUnlockRewardsForStage(user, season, 1, null);
        }
        AppService.markGameDataDirty();
    }

    public static void grantUnlockRewardsForStage(User user, SeasonType season, int stageNumber) {
        grantUnlockRewardsForStage(user, season, stageNumber, null);
    }

    public static void grantUnlockRewardsForStage(
        User user,
        SeasonType season,
        int stageNumber,
        ProgressionDelta delta
    ) {
        if (user == null || season == null || season == SeasonType.MINIGAME || stageNumber < 1) {
            return;
        }
        ChapterId id = new ChapterId(season, stageNumber);
        for (PlantName plant : ChapterUnlockCatalog.unlockPlantsFor(id)) {
            if (!user.isUnlocked(plant)) {
                user.unlockPlant(plant);
                if (delta != null) {
                    delta.newlyUnlockedPlants.add(plant);
                }
            }
        }
        List<String> before = new ArrayList<>(user.getGameProgress().getSeenZombieAliases());
        user.getGameProgress().addSeenZombies(ChapterZombieCatalog.zombiesFor(id));
        if (delta != null) {
            for (String alias : ChapterZombieCatalog.zombiesFor(id)) {
                if (!before.contains(alias)) {
                    delta.newlyDiscoveredZombies.add(alias);
                }
            }
        }
    }

    public static ProgressionDelta onStageWon(User user, Chapter chapter) {
        ProgressionDelta delta = new ProgressionDelta();
        if (user == null || chapter == null || chapter.getSeason() == null) {
            return delta;
        }
        SeasonType season = chapter.getSeason().getSeasonType();
        if (season == SeasonType.MINIGAME) {
            recordMinigameResult(user, true);
            ProgressService.saveUserProgress(user);
            return delta;
        }

        GameProgress progress = user.getGameProgress();
        StageProgress stageProgress = progress.getOrCreateStageProgress(season, chapter.getChapterNumber());
        delta.firstClear = !stageProgress.isFirstClearRewardClaimed();

        stageProgress.markWin();
        recordGamePlayed(user);
        progress.incrementTotalWon();
        progress.markLevelCompleted(season, chapter.getChapterNumber());

        int max = ChapterRegistry.countForSeason(season);
        int next = chapter.getChapterNumber() + 1;
        boolean unlockedNext = progress.unlockNextStage(season, chapter.getChapterNumber());
        // markLevelCompleted/ensureStageEntries may already open N+1; still grant its recommended plants.
        if (next <= max && progress.isStageUnlocked(season, next)) {
            unlockedNext = true;
        }
        delta.unlockedNextStage = unlockedNext;
        if (next <= max) {
            delta.nextStageNumber = next;
            grantUnlockRewardsForStage(user, season, next, delta);
        }

        List<String> beforeZombies = new ArrayList<>(progress.getSeenZombieAliases());
        progress.addSeenZombies(zombieAliases(chapter));
        for (String alias : zombieAliases(chapter)) {
            if (!beforeZombies.contains(alias) && !delta.newlyDiscoveredZombies.contains(alias)) {
                delta.newlyDiscoveredZombies.add(alias);
            }
        }

        AppService.markGameDataDirty();
        ProgressService.saveUserProgress(user);
        app.service.news.NewsPublisher.onProgressionDelta(user, delta, season);
        return delta;
    }

    public static ProgressionDelta applyBuyWin(User user, Chapter chapter) {
        ProgressionDelta delta = new ProgressionDelta();
        if (user == null || chapter == null || chapter.getSeason() == null) {
            return delta;
        }
        SeasonType season = chapter.getSeason().getSeasonType();
        if (season == SeasonType.MINIGAME) {
            return delta;
        }

        GameProgress progress = user.getGameProgress();
        StageProgress stageProgress = progress.getOrCreateStageProgress(season, chapter.getChapterNumber());
        boolean wasCompleted = stageProgress.isCompleted();
        delta.firstClear = !stageProgress.isFirstClearRewardClaimed();

        if (!wasCompleted) {
            stageProgress.markBuyWinConverted();
            progress.markLevelCompleted(season, chapter.getChapterNumber());
            progress.incrementTotalWon();
        } else if (!stageProgress.isLastResultWin()) {
            stageProgress.setLastResultWin(true);
        }

        int max = ChapterRegistry.countForSeason(season);
        int next = chapter.getChapterNumber() + 1;
        boolean unlockedNext = progress.unlockNextStage(season, chapter.getChapterNumber());
        // markLevelCompleted/ensureStageEntries may already open N+1; still grant its recommended plants.
        if (next <= max && progress.isStageUnlocked(season, next)) {
            unlockedNext = true;
        }
        delta.unlockedNextStage = unlockedNext;
        if (next <= max) {
            delta.nextStageNumber = next;
            grantUnlockRewardsForStage(user, season, next, delta);
        }

        List<String> beforeZombies = new ArrayList<>(progress.getSeenZombieAliases());
        progress.addSeenZombies(zombieAliases(chapter));
        for (String alias : zombieAliases(chapter)) {
            if (!beforeZombies.contains(alias)) {
                delta.newlyDiscoveredZombies.add(alias);
            }
        }

        AppService.markGameDataDirty();
        ProgressService.saveUserProgress(user);
        app.service.news.NewsPublisher.onProgressionDelta(user, delta, season);
        return delta;
    }

    public static void onStageLost(User user, Chapter chapter) {
        if (user == null || chapter == null || chapter.getSeason() == null) {
            return;
        }
        SeasonType season = chapter.getSeason().getSeasonType();
        if (season == SeasonType.MINIGAME) {
            recordMinigameResult(user, false);
            ProgressService.saveUserProgress(user);
            return;
        }

        GameProgress progress = user.getGameProgress();
        StageProgress stageProgress = progress.getOrCreateStageProgress(season, chapter.getChapterNumber());
        stageProgress.markLoss();
        recordGamePlayed(user);
        progress.incrementTotalLost();
        AppService.markGameDataDirty();
        ProgressService.saveUserProgress(user);
    }

    private static void recordMinigameResult(User user, boolean won) {
        GameProgress progress = user.getGameProgress();
        recordGamePlayed(user);
        if (won) {
            progress.incrementTotalWon();
        } else {
            progress.incrementTotalLost();
        }
        AppService.markGameDataDirty();
    }

    private static void recordGamePlayed(User user) {
        if (user == null) {
            return;
        }
        GameProgress progress = user.getGameProgress();
        if (progress != null) {
            progress.incrementTotalPlayed();
        }
        user.incrementGamesPlayed();
    }

    public static List<PlantName> suggestedPlants(Chapter chapter, User user) {
        if (chapter == null) {
            return List.of();
        }
        Set<PlantName> stagePool = new LinkedHashSet<>();
        if (chapter.getAvailablePlants() != null) {
            stagePool.addAll(chapter.getAvailablePlants());
        }
        List<PlantName> result = new ArrayList<>();
        for (PlantName plant : chapter.getRecommendedPlants()) {
            if (plant == null) {
                continue;
            }
            if (!stagePool.contains(plant)) {
                continue;
            }
            if (user != null && !user.isUnlocked(plant)) {
                continue;
            }
            result.add(plant);
        }
        return result;
    }

    public static List<String> zombieAliases(Chapter chapter) {
        List<String> aliases = new ArrayList<>();
        if (chapter == null || chapter.getAvailableZombies() == null) {
            return aliases;
        }
        for (Property property : chapter.getAvailableZombies()) {
            if (property == null) {
                continue;
            }
            String alias = property.getAlias();
            if (alias != null && !alias.isBlank()) {
                aliases.add(alias);
            }
        }
        return aliases;
    }
}

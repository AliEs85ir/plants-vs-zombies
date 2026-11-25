package game.world.levelConditions;

import app.enums.GameState;
import app.enums.SeasonType;
import app.enums.plant.PlantName;
import app.enums.quest.EventType;
import app.model.App;
import app.model.user.User;
import app.service.progression.ProgressionService;
import game.entity.quest.GameEvent;
import game.entity.quest.LevelFacts;
import game.entity.quest.Quest;
import game.service.quest.QuestManager;
import game.world.GameWorld;
import game.world.chapter.Chapter;
import game.world.chapter.ChapterRegistry;
import game.world.chapter.rule.scoringgame.ScoringSession;
import game.world.levelConditions.conditions.LevelOutcome;
import game.world.levelConditions.result.StageResult;
import game.world.levelConditions.result.StageRewardGrant;
import game.world.levelConditions.result.StageRewardService;
import game.world.levelConditions.result.StageRunStatistics;
import game.world.minigame.MinigameLevelAdvancer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LevelEndHandler {
    private boolean handling;

    public void handle(GameWorld world, LevelOutcome outcome) {
        if (handling || world == null || outcome == null || outcome == LevelOutcome.NONE) {
            return;
        }
        handling = true;
        try {
            if (outcome == LevelOutcome.PLAYER_WON) {
                onPlayerWon(world, false);
            } else if (outcome == LevelOutcome.PLAYER_LOST) {
                onPlayerLost(world);
            } else if (outcome == LevelOutcome.NEXT_LEVEL) {
                onNextMinigameLevel(world);
            }
        } finally {
            handling = false;
        }
    }

    public StageResult applyBuyWin(GameWorld world) {
        if (world == null || world.getStageResult() == null || !world.getStageResult().isLose()) {
            return world != null ? world.getStageResult() : null;
        }
        if (world.getStageResult().isBoughtWin()) {
            return world.getStageResult();
        }
        return onPlayerWon(world, true);
    }

    private void onNextMinigameLevel(GameWorld world) {
        world.addEvent("Level cleared. Starting the next minigame level.");
        MinigameLevelAdvancer.advanceAndRestart(world);
    }

    private StageResult onPlayerWon(GameWorld world, boolean boughtWin) {
        ScoringSession.finish();
        Chapter chapter = world.getCurrentChapter();
        User user = App.getCurrentUser();
        StageRunStatistics stats = StageRunStatistics.fromWorld(world);

        world.addEvent("Dear humanz, zis is not done yet; we will come back to eat your brainz, humanz.");

        if (user == null || chapter == null) {
            StageResult fallback = StageResult.builder()
                .outcome(LevelOutcome.PLAYER_WON)
                .chapter(chapter)
                .statistics(stats)
                .firstClear(false)
                .boughtWin(boughtWin)
                .stageNumber(chapter != null ? chapter.getChapterNumber() : 0)
                .build();
            world.setStageResult(fallback);
            world.setGameState(GameState.WON);
            return fallback;
        }

        Map<String, Integer> questProgressBefore = snapshotQuestProgress(user);
        QuestManager questManager = user.getQuestManager();
        List<Quest> changedQuests = List.of();
        if (questManager != null) {
            questManager.fireEvent(new GameEvent(
                EventType.LEVEL_WON,
                1,
                null, null, -1, -1,
                world.getDifficultyLevel(),
                false
            ));
            questManager.evaluateLevelEnd(LevelFacts.from(world));
            questManager.saveQuests();
            changedQuests = diffChangedQuests(user, questProgressBefore);
        }

        ProgressionService.ProgressionDelta delta;
        if (boughtWin) {
            delta = ProgressionService.applyBuyWin(user, chapter);
        } else {
            delta = ProgressionService.onStageWon(user, chapter);
        }

        List<StageRewardGrant> rewards = List.of();
        boolean firstClear = delta.isFirstClear();
        if (firstClear
            && chapter.getSeason() != null
            && chapter.getSeason().getSeasonType() != SeasonType.MINIGAME) {
            rewards = StageRewardService.grantFirstClearRewards(
                user,
                chapter.getSeason().getSeasonType(),
                chapter.getChapterNumber()
            );
        }

        List<PlantName> newlyUnlockedPlants = mergeUnlockedPlants(delta.getNewlyUnlockedPlants(), rewards);


        Integer nextStage = delta.getNextStageNumber();
        if (nextStage == null && chapter.getSeason() != null) {
            SeasonType season = chapter.getSeason().getSeasonType();
            int max = ChapterRegistry.countForSeason(season);
            if (chapter.getChapterNumber() < max) {
                nextStage = chapter.getChapterNumber() + 1;
            }
        }

        StageResult result = StageResult.builder()
            .outcome(LevelOutcome.PLAYER_WON)
            .chapter(chapter)
            .statistics(stats)
            .firstClear(firstClear)
            .boughtWin(boughtWin)
            .newlyUnlockedPlants(newlyUnlockedPlants)
            .newlyDiscoveredZombies(delta.getNewlyDiscoveredZombies())
            .changedQuests(changedQuests)
            .rewards(rewards)
            .seasonType(chapter.getSeason() != null ? chapter.getSeason().getSeasonType() : null)
            .stageNumber(chapter.getChapterNumber())
            .nextStageNumber(nextStage)
            .build();

        world.setStageResult(result);
        world.setGameState(GameState.WON);
        app.service.ProgressService.saveUserProgress(user);
        return result;
    }

    private void onPlayerLost(GameWorld world) {
        ScoringSession.finish();
        User user = App.getCurrentUser();
        StageRunStatistics stats = StageRunStatistics.fromWorld(world);
        Chapter chapter = world.getCurrentChapter();

        if (user != null && user.getQuestManager() != null) {
            user.getQuestManager().fireEvent(new GameEvent(EventType.LEVEL_LOST, 1));
            user.getQuestManager().saveQuests();
        }

        world.addEvent("The zombie ate your brain; LOSER!!!");
        if (user != null) {
            ProgressionService.onStageLost(user, chapter);
        }

        StageResult result = StageResult.builder()
            .outcome(LevelOutcome.PLAYER_LOST)
            .chapter(chapter)
            .statistics(stats)
            .firstClear(false)
            .boughtWin(false)
            .seasonType(chapter != null && chapter.getSeason() != null
                ? chapter.getSeason().getSeasonType() : null)
            .stageNumber(chapter != null ? chapter.getChapterNumber() : 0)
            .build();
        world.setStageResult(result);
        if (user != null) {
            app.service.ProgressService.saveUserProgress(user);
        }
    }

    private static List<PlantName> mergeUnlockedPlants(
        List<PlantName> fromProgression,
        List<StageRewardGrant> rewards
    ) {
        List<PlantName> merged = new ArrayList<>();
        if (fromProgression != null) {
            for (PlantName plant : fromProgression) {
                if (plant != null && !merged.contains(plant)) {
                    merged.add(plant);
                }
            }
        }
        if (rewards != null) {
            for (StageRewardGrant grant : rewards) {
                if (grant != null
                    && grant.getKind() == StageRewardGrant.Kind.PLANT_UNLOCK
                    && grant.getPlantName() != null
                    && !merged.contains(grant.getPlantName())) {
                    merged.add(grant.getPlantName());
                }
            }
        }
        return merged;
    }

    private Map<String, Integer> snapshotQuestProgress(User user) {
        Map<String, Integer> snapshot = new HashMap<>();
        if (user == null || user.getQuestManager() == null) {
            return snapshot;
        }
        for (Quest quest : user.getQuestManager().getActiveQuests()) {
            snapshot.put(questKey(quest), questProgress(quest));
        }
        for (Quest quest : user.getQuestManager().getCompletedQuests()) {
            snapshot.put(questKey(quest), questProgress(quest));
        }
        return snapshot;
    }

    private List<Quest> diffChangedQuests(User user, Map<String, Integer> before) {
        List<Quest> changed = new ArrayList<>();
        if (user == null || user.getQuestManager() == null) {
            return changed;
        }
        List<Quest> all = new ArrayList<>();
        all.addAll(user.getQuestManager().getActiveQuests());
        all.addAll(user.getQuestManager().getCompletedQuests());
        for (Quest quest : all) {
            String key = questKey(quest);
            int now = questProgress(quest);
            Integer previous = before.get(key);
            if (previous == null || previous != now) {
                changed.add(quest);
            }
        }
        return changed;
    }

    private static String questKey(Quest quest) {
        return quest.getName() != null ? quest.getName().name() : String.valueOf(System.identityHashCode(quest));
    }

    private static int questProgress(Quest quest) {
        if (quest.getCondition() == null) {
            return quest.isCompleted() ? 1 : 0;
        }
        return quest.getCondition().getCurrentProgress();
    }
}

package game.service.quest;

import app.enums.plant.PlantCategory;
import app.enums.plant.PlantName;
import app.enums.quest.EventType;
import app.enums.quest.QuestCategory;
import app.enums.quest.QuestName;
import app.enums.quest.QuestPriority;
import app.enums.quest.RewardType;
import app.model.user.User;
import game.entity.quest.AccumulativeCondition;
import game.entity.quest.LevelFacts;
import game.entity.quest.Quest;
import game.entity.quest.QuestReward;
import game.entity.quest.SnapshotCondition;

import java.util.Arrays;
import java.util.Collections;

public class QuestFactory {

    public static Quest createDailySunCollector() {
        return new Quest(
            QuestName.DAILY_SUN_COLLECTOR,
            "Daily Sun Collector",
            "Collect sun units throughout a single day.",
            QuestCategory.DAILY,
            QuestPriority.MEDIUM,
            new AccumulativeCondition(EventType.SUN_COLLECTED, 3000),
            Collections.singletonList(new QuestReward(RewardType.COIN, 100, "CURRENCY")),
            Arrays.asList(3000, 4000, 5000)
        );
    }

    public static Quest createChapterHunter(int targetChapter) {
        Quest quest = new Quest(
            QuestName.CHAPTER_HUNTER,
            "Chapter Hunter",
            String.format("Kill 50 zombies in chapter %d.", targetChapter),
            QuestCategory.STORY,
            QuestPriority.HIGH,
            new AccumulativeCondition(EventType.ZOMBIE_KILLED, 50, event -> event.chapterIs(targetChapter)),
            Collections.singletonList(new QuestReward(RewardType.SEED, 10, "SEED_PACKET"))
        );
        quest.setTargetValue(targetChapter);
        return quest;
    }

    public static Quest createPlantExpert(PlantName specificPlant) {
        String plantId = specificPlant.name();
        Quest quest = new Quest(
            QuestName.PLANT_EXPERT,
            "Plant Expert",
            String.format("Kill 10 zombies using only %s.", specificPlant.toString()),
            QuestCategory.DAILY,
            QuestPriority.HIGH,
            new AccumulativeCondition(EventType.ZOMBIE_KILLED, 10, event -> event.plantIs(plantId)),
            Collections.singletonList(new QuestReward(RewardType.PLANT_UNLOCK, 1, "RANDOM_PLANT"))
        );
        quest.setTargetPlant(plantId);
        return quest;
    }

    public static Quest createCactusOnly() {
        Quest quest = new Quest(
            QuestName.CACTUS_ONLY,
            "Cactus Only",
            "Kill 10 zombies using only Cactus.",
            QuestCategory.DAILY,
            QuestPriority.HIGH,
            new AccumulativeCondition(EventType.ZOMBIE_KILLED, 10, event -> event.plantIs("CACTUS")),
            Collections.singletonList(new QuestReward(RewardType.GEM, 20, "CURRENCY"))
        );
        quest.setTargetPlant("CACTUS");
        return quest;
    }

    public static Quest createEconomicalGardener(int maxLostPlants) {
        Quest quest = new Quest(
            QuestName.ECONOMICAL_GARDENER,
            "Economical Gardener",
            String.format("Win a level without losing more than %d plants.", maxLostPlants),
            QuestCategory.EVENT,
            QuestPriority.HIGH,
            new SnapshotCondition(facts -> facts.lostPlantsCount() <= maxLostPlants),
            Collections.singletonList(new QuestReward(RewardType.SEED, 1, "SEED_PACKET"))
        );
        quest.setTargetValue(maxLostPlants);
        return quest;
    }

    public static Quest createDefenseMaster() {
        return new Quest(
            QuestName.DEFENSE_MASTER,
            "Defense Master",
            "Finish the level with exactly zero sun remaining.",
            QuestCategory.EVENT,
            QuestPriority.CRITICAL,
            new SnapshotCondition(facts -> facts.remainingSun() == 0),
            Collections.singletonList(new QuestReward(RewardType.GEM, 200, "CURRENCY"))
        );
    }

    public static Quest createQuickAction() {
        return new Quest(
            QuestName.QUICK_ACTION,
            "Quick Action",
            "Kill 10 zombies in less than 30 seconds from the start of the first wave.",
            QuestCategory.EVENT,
            QuestPriority.MEDIUM,
            new AccumulativeCondition(
                EventType.ZOMBIE_KILLED,
                10,
                event -> event.withinSeconds(30),
                EventType.LEVEL_WON,
                EventType.LEVEL_LOST
            ),
            Collections.singletonList(new QuestReward(RewardType.COIN, 500, "CURRENCY"))
        );
    }

    public static Quest createDemolitionExpert() {
        return new Quest(
            QuestName.DEMOLITION_EXPERT,
            "Demolition Expert",
            "Use 3 explosive plants in a single level.",
            QuestCategory.DAILY,
            QuestPriority.LOW,
            new AccumulativeCondition(
                EventType.PLANT_EXPLODED,
                3,
                event -> true,
                EventType.LEVEL_WON,
                EventType.LEVEL_LOST
            ),
            Collections.singletonList(new QuestReward(RewardType.COIN, 100, "CURRENCY"))
        );
    }

    public static Quest createSymmetry() {
        return new Quest(
            QuestName.SYMMETRY,
            "Symmetry",
            "The garden must be completely symmetric at the end of the level.",
            QuestCategory.DAILY,
            QuestPriority.HIGH,
            new SnapshotCondition(LevelFacts::isSymmetric),
            Collections.singletonList(new QuestReward(RewardType.COIN, 500, "CURRENCY"))
        );
    }

    public static Quest createFamilyHunter(PlantCategory category) {
        Quest quest = new Quest(
            QuestName.FAMILY_HUNTER,
            "Family Hunter",
            String.format("Kill all zombies using plants from only the %s family.", category.toString()),
            QuestCategory.DAILY,
            QuestPriority.MEDIUM,
            new SnapshotCondition(facts -> facts.usedOnlyCombatFamily(category)),
            Collections.singletonList(new QuestReward(RewardType.COIN, 1000, "CURRENCY"))
        );
        quest.setTargetCategory(category.name());
        return quest;
    }

    public static Quest createLimitedBloom(PlantCategory category) {
        Quest quest = new Quest(
            QuestName.LIMITED_BLOOM,
            "Limited Bloom",
            String.format("Complete the level without using any plant from %s family.", category.toString()),
            QuestCategory.DAILY,
            QuestPriority.HIGH,
            new SnapshotCondition(facts -> !facts.usedFamily(category)),
            Collections.singletonList(new QuestReward(RewardType.GEM, 100, "CURRENCY"))
        );
        quest.setTargetCategory(category.name());
        return quest;
    }

    public static Quest createDayOrNight() {
        return new Quest(
            QuestName.DAY_OR_NIGHT,
            "Day or Night",
            "Complete a DAY level using only night plants (Mushrooms).",
            QuestCategory.EVENT,
            QuestPriority.HIGH,
            new SnapshotCondition(facts -> facts.isDayLevel() && facts.usedOnlyMushrooms()),
            Collections.singletonList(new QuestReward(RewardType.GEM, 20, "CURRENCY"))
        );
    }

    public static Quest createWinStreak() {
        return new Quest(
            QuestName.WIN_STREAK,
            "Win Streak",
            "Clear the level on the highest difficulty (" + User.MAX_DIFFICULTY_LEVEL + ") five times in a row.",
            QuestCategory.DAILY,
            QuestPriority.MEDIUM,
            new AccumulativeCondition(
                EventType.LEVEL_WON,
                EventType.LEVEL_LOST,
                5,
                event -> event.difficultyIs(User.MAX_DIFFICULTY_LEVEL)
            ),
            Collections.singletonList(new QuestReward(RewardType.COIN, 5000, "CURRENCY"))
        );
    }

    public static Quest createAlmostVictory() {
        return new Quest(
            QuestName.ALMOST_VICTORY,
            "Almost Victory",
            "Kill 10 zombies in the first column of a row that has no lawn mower.",
            QuestCategory.DAILY,
            QuestPriority.MEDIUM,
            new AccumulativeCondition(EventType.ZOMBIE_KILLED, 10, event -> event.isFirstColNoMower()),
            Collections.singletonList(new QuestReward(RewardType.COIN, 300, "CURRENCY"))
        );
    }

    public static Quest createOcdMaster() {
        return new Quest(
            QuestName.OCD_MASTER,
            "OCD Master",
            "Complete the level with no symmetry in the garden except the middle row.",
            QuestCategory.DAILY,
            QuestPriority.MEDIUM,
            new SnapshotCondition(LevelFacts::isAsymmetricExceptMiddle),
            Collections.singletonList(new QuestReward(RewardType.COIN, 800, "CURRENCY"))
        );
    }

    public static Quest createCloudyDay() {
        return new Quest(
            QuestName.CLOUDY_DAY,
            "Cloudy Day",
            "Complete the level with only 3 sun-producing plants.",
            QuestCategory.DAILY,
            QuestPriority.HIGH,
            new SnapshotCondition(facts -> facts.sunProducerCount() <= 3),
            Collections.singletonList(new QuestReward(RewardType.GEM, 10, "CURRENCY"))
        );
    }

    public static Quest createOneColumnLess(int targetCol) {
        Quest quest = new Quest(
            QuestName.ONE_COLUMN_LESS,
            "One Column Less",
            String.format("Complete the level with no plants placed in column %d.", targetCol + 1),
            QuestCategory.DAILY,
            QuestPriority.HIGH,
            new SnapshotCondition(facts -> facts.neverPlantedInColumn(targetCol)),
            Collections.singletonList(new QuestReward(RewardType.GEM, 10, "CURRENCY"))
        );
        quest.setTargetValue(targetCol);
        return quest;
    }

    public static Quest createDefenselessRow(int targetRow) {
        Quest quest = new Quest(
            QuestName.DEFENSELESS_ROW,
            "Defenseless Row",
            String.format("Complete the level with no plants placed in row %d.", targetRow + 1),
            QuestCategory.DAILY,
            QuestPriority.HIGH,
            new SnapshotCondition(facts -> facts.neverPlantedInRow(targetRow)),
            Collections.singletonList(new QuestReward(RewardType.GEM, 20, "CURRENCY"))
        );
        quest.setTargetValue(targetRow);
        return quest;
    }

    public static Quest createDefenselessCross(int targetIndex) {
        Quest quest = new Quest(
            QuestName.DEFENSELESS_CROSS,
            "Defenseless Cross",
            String.format("Complete the level with row %d and column %d completely empty.", targetIndex + 1, targetIndex + 1),
            QuestCategory.DAILY,
            QuestPriority.HIGH,
            new SnapshotCondition(facts -> facts.neverPlantedInCross(targetIndex)),
            Collections.singletonList(new QuestReward(RewardType.GEM, 25, "CURRENCY"))
        );
        quest.setTargetValue(targetIndex);
        return quest;
    }

    public static Quest createLawnMowerTime(int targetKills) {
        Quest quest = new Quest(
            QuestName.LAWN_MOWER_TIME,
            "Lawn Mower Time",
            String.format("Destroy at least %d zombies using lawn mowers.", targetKills),
            QuestCategory.EVENT,
            QuestPriority.MEDIUM,
            new AccumulativeCondition(EventType.LAWN_MOWER_TRIGGERED, targetKills),
            Collections.singletonList(new QuestReward(RewardType.GEM, targetKills, "CURRENCY"))
        );
        quest.setTargetValue(targetKills);
        return quest;
    }

    public static void populateNewPlayer(QuestManager manager) {
        if (manager == null) {
            return;
        }
        manager.addQuest(createChapterHunter(1));
        manager.ensureEventQuests();
        manager.resetDailyQuests();
    }
}

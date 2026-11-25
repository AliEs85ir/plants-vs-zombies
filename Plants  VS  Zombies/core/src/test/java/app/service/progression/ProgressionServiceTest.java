package app.service.progression;

import app.enums.SeasonType;
import app.enums.plant.PlantName;
import app.model.user.GameProgress;
import app.model.user.StageProgress;
import app.model.user.User;
import game.world.chapter.definition.ChapterUnlockCatalog;
import game.world.chapter.definition.StartingPlantUnlocks;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ProgressionServiceTest {

    @Test
    void newUserUnlocksOnlyStageOnePerSeason() {
        User user = new User("prog_user", "x", "nick", "p@test.com", "male", 1, "ans", 20);
        GameProgress progress = user.getGameProgress();

        assertTrue(progress.isStageUnlocked(SeasonType.ANCIENT_EGYPT, 1));
        assertFalse(progress.isStageUnlocked(SeasonType.ANCIENT_EGYPT, 2));
        assertFalse(progress.isStageUnlocked(SeasonType.ANCIENT_EGYPT, 3));
        assertFalse(progress.isStageUnlocked(SeasonType.ANCIENT_EGYPT, 4));

        assertTrue(progress.isStageUnlocked(SeasonType.DARK_AGES, 1));
        assertFalse(progress.isStageUnlocked(SeasonType.DARK_AGES, 2));

        for (PlantName starter : StartingPlantUnlocks.initialPlants()) {
            assertTrue(user.isUnlocked(starter));
        }
        assertFalse(user.isUnlocked(PlantName.MEGA_GATLING_PEA));
    }

    @Test
    void winUnlocksNextStageButLossDoesNot() {
        User user = new User("prog_user2", "x", "nick", "p2@test.com", "male", 1, "ans", 20);
        GameProgress progress = user.getGameProgress();

        StageProgress stage1 = progress.getOrCreateStageProgress(SeasonType.ANCIENT_EGYPT, 1);
        stage1.markLoss();
        progress.incrementTotalPlayed();
        progress.incrementTotalLost();
        assertFalse(progress.isStageUnlocked(SeasonType.ANCIENT_EGYPT, 2));
        assertEquals(1, stage1.getLossCount());
        assertEquals(0, stage1.getWinCount());

        stage1.markWin();
        progress.markLevelCompleted(SeasonType.ANCIENT_EGYPT, 1);
        progress.unlockNextStage(SeasonType.ANCIENT_EGYPT, 1);
        ProgressionService.grantUnlockRewardsForStage(user, SeasonType.ANCIENT_EGYPT, 2);

        assertTrue(progress.isStageUnlocked(SeasonType.ANCIENT_EGYPT, 2));
        assertTrue(progress.isLevelCompleted(SeasonType.ANCIENT_EGYPT, 1));
        assertEquals(1, stage1.getWinCount());
        assertTrue(stage1.isCompleted());

        for (PlantName plant : ChapterUnlockCatalog.unlockPlantsFor(SeasonType.ANCIENT_EGYPT, 2)) {
            assertTrue(user.isUnlocked(plant), "Expected unlock for " + plant);
        }
    }

    @Test
    void suggestedPlantsRequireUnlockedAndStagePool() {
        assertTrue(ChapterUnlockCatalog.unlockPlantsFor(SeasonType.ANCIENT_EGYPT, 1)
            .contains(PlantName.SUNFLOWER));
        assertFalse(ChapterUnlockCatalog.unlockPlantsFor(SeasonType.ANCIENT_EGYPT, 1)
            .contains(PlantName.MEGA_GATLING_PEA));
    }
}

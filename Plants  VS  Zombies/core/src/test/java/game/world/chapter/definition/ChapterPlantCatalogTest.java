package game.world.chapter.definition;

import app.enums.SeasonType;
import app.enums.plant.PlantName;
import app.model.user.GameProgress;
import app.model.user.User;
import app.service.progression.ProgressionService;
import game.world.chapter.ChapterId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ChapterPlantCatalogTest {

    @BeforeEach
    @AfterEach
    void resetCatalogCache() {
        ChapterPlantCatalogLoader.clearCache();
    }

    @Test
    void recommendedIsSubsetOfAllowedForEveryStage() {
        var pools = ChapterPlantCatalog.all();
        assertFalse(pools.isEmpty(), "stage_plant_pools.json should load");
        for (var entry : pools.entrySet()) {
            List<PlantName> allowed = entry.getValue().allowedPlants();
            List<PlantName> recommended = entry.getValue().recommendedPlants();
            assertTrue(allowed.size() >= 35 && allowed.size() <= 45,
                entry.getKey() + " allowed size " + allowed.size());
            assertEquals(8, recommended.size(), entry.getKey() + " recommended count");
            assertTrue(allowed.containsAll(recommended),
                entry.getKey() + " recommended must be subset of allowed");
        }
    }

    @Test
    void stageOneOfEachSeasonIsOpenAtStart() {
        User user = new User("pool_user", "x", "nick", "pool@test.com", "male", 1, "ans", 20);
        GameProgress progress = user.getGameProgress();
        for (SeasonType season : SeasonType.values()) {
            if (season == SeasonType.MINIGAME) {
                continue;
            }
            assertTrue(progress.isStageUnlocked(season, 1), season + " stage 1 must be open");
            assertFalse(progress.isStageUnlocked(season, 2), season + " stage 2 locked initially");
        }
    }

    @Test
    void completingStageNUnlocksRecommendedOfStageNPlusOne() {
        User user = new User("pool_user2", "x", "nick", "pool2@test.com", "male", 1, "ans", 20);
        SeasonType season = SeasonType.ANCIENT_EGYPT;
        List<PlantName> stage2Recommended = ChapterPlantCatalog.recommendedFor(season, 2);
        assertEquals(8, stage2Recommended.size());

        List<PlantName> expectNewlyUnlocked = new ArrayList<>();
        for (PlantName plant : stage2Recommended) {
            if (!user.isUnlocked(plant)) {
                expectNewlyUnlocked.add(plant);
            }
        }
        assertFalse(expectNewlyUnlocked.isEmpty(), "need at least one locked stage-2 recommended plant");

        GameProgress progress = user.getGameProgress();
        progress.getOrCreateStageProgress(season, 1).markWin();
        progress.markLevelCompleted(season, 1);
        progress.unlockNextStage(season, 1);
        // Same grant path ProgressionService uses after a win (recommended of N+1).
        ProgressionService.grantUnlockRewardsForStage(user, season, 2);

        assertTrue(progress.isStageUnlocked(season, 2));
        for (PlantName plant : stage2Recommended) {
            assertTrue(user.isUnlocked(plant), "Expected unlock for recommended " + plant);
        }
        assertEquals(stage2Recommended, ChapterUnlockCatalog.unlockPlantsFor(new ChapterId(season, 2)));
    }

    @Test
    void stageOneRecommendedUnlockedForNewUser() {
        User user = new User("pool_user3", "x", "nick", "pool3@test.com", "male", 1, "ans", 20);
        for (SeasonType season : List.of(
            SeasonType.ANCIENT_EGYPT,
            SeasonType.FROSTBITE_CAVES,
            SeasonType.BIG_WAVE_BEACH,
            SeasonType.DARK_AGES
        )) {
            for (PlantName plant : ChapterPlantCatalog.recommendedFor(season, 1)) {
                assertTrue(user.isUnlocked(plant), season + " stage1 recommended " + plant);
            }
        }
    }

    @Test
    void unlockCatalogDelegatesToRecommended() {
        for (SeasonType season : List.of(
            SeasonType.ANCIENT_EGYPT,
            SeasonType.FROSTBITE_CAVES,
            SeasonType.BIG_WAVE_BEACH,
            SeasonType.DARK_AGES
        )) {
            for (int stage = 1; stage <= 4; stage++) {
                assertEquals(
                    ChapterPlantCatalog.recommendedFor(season, stage),
                    ChapterUnlockCatalog.unlockPlantsFor(season, stage)
                );
            }
        }
    }
}

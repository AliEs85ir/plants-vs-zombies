package game.service.plant;

import app.enums.plant.PlantName;
import app.model.App;
import app.model.user.User;
import app.service.ProgressService;
import common.Result;
import game.entity.plant.PlantLevel;
import game.entity.plant.upgrade.EffectivePlantStats;
import game.entity.plant.upgrade.PlantStatPreview;
import game.entity.plant.upgrade.PlantUpgradeCatalog;
import game.entity.plant.upgrade.PlantUpgradeConfigException;
import game.entity.plant.upgrade.PlantUpgradeConfigLoader;
import game.entity.plant.upgrade.PlantUpgradePreview;
import game.entity.plant.upgrade.PlantUpgradeView;
import game.entity.registry.PlantDefinitionLoader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlantUpgradeServiceTest {
    private static final String USERNAME = "upgrade_service_tester";

    @BeforeAll
    static void loadCatalogAndDefinitions() {
        PlantUpgradeConfigLoader.clearCache();
        PlantDefinitionLoader.load();
        PlantUpgradeConfigLoader.get();
    }

    @BeforeEach
    void setUp() {
        User user = new User(USERNAME, "pass", "nick", "u@t.com", "male", 1, "answer", 20);
        user.setCoins(10_000);
        App.setCurrentUser(user);
    }

    @AfterEach
    void tearDown() throws Exception {
        App.setCurrentUser(null);
        Path progress = ProgressService.progressFile(USERNAME);
        if (progress.getParent() != null) {
            deleteQuiet(progress.getParent());
        }
    }

    @Test
    void scenarioA_notEnoughSeedsDoesNotUpgrade() {
        User user = App.getCurrentUser();
        user.getSeedBank().addPacket(PlantName.SUNFLOWER, 5);

        Result result = PlantUpgradeService.upgrade(PlantName.SUNFLOWER);

        assertFalse(result.success());
        assertEquals(PlantUpgradeService.NOT_ENOUGH_SEEDS, result.message());
        assertEquals(1, PlantUpgradeService.getCurrentLevel(PlantName.SUNFLOWER));
        assertEquals(5, user.getSeedBank().getAmount(PlantName.SUNFLOWER));
        assertEquals(10_000, user.getCoins());
    }

    @Test
    void scenarioB_notEnoughCoinsDoesNotConsumeSeeds() {
        User user = App.getCurrentUser();
        user.setCoins(100);
        user.getSeedBank().addPacket(PlantName.SUNFLOWER, 10);

        Result result = PlantUpgradeService.upgrade(PlantName.SUNFLOWER);

        assertFalse(result.success());
        assertEquals(PlantUpgradeService.NOT_ENOUGH_COINS, result.message());
        assertEquals(1, PlantUpgradeService.getCurrentLevel(PlantName.SUNFLOWER));
        assertEquals(10, user.getSeedBank().getAmount(PlantName.SUNFLOWER));
        assertEquals(100, user.getCoins());
    }

    @Test
    void notEnoughSeedsAndCoinsUsesCombinedMessage() {
        User user = App.getCurrentUser();
        user.setCoins(100);
        user.getSeedBank().addPacket(PlantName.SUNFLOWER, 5);

        Result result = PlantUpgradeService.upgrade(PlantName.SUNFLOWER);

        assertFalse(result.success());
        assertEquals(PlantUpgradeService.NOT_ENOUGH_SEEDS_AND_COINS, result.message());
        assertEquals(5, user.getSeedBank().getAmount(PlantName.SUNFLOWER));
        assertEquals(100, user.getCoins());
    }

    @Test
    void scenarioC_successfulUpgradeIsAtomic() {
        User user = App.getCurrentUser();
        user.getSeedBank().addPacket(PlantName.SUNFLOWER, 10);

        Result result = PlantUpgradeService.upgrade(PlantName.SUNFLOWER);

        assertTrue(result.success());
        assertEquals(2, PlantUpgradeService.getCurrentLevel(PlantName.SUNFLOWER));
        assertEquals(0, user.getSeedBank().getAmount(PlantName.SUNFLOWER));
        assertEquals(9_000, user.getCoins());
        assertFalse(PlantUpgradeService.canUpgrade(PlantName.SUNFLOWER));
    }

    @Test
    void scenarioD_seedProgressCapsAtOneAndKeepsOverflowText() {
        User user = App.getCurrentUser();
        user.getSeedBank().addPacket(PlantName.SUNFLOWER, 30);

        PlantUpgradeView view = PlantUpgradeService.getProgress(PlantName.SUNFLOWER);

        assertEquals(30, view.currentSeeds());
        assertEquals(10, view.requiredSeeds());
        assertEquals(1f, view.seedProgress());
        assertEquals("30 / 10", view.seedProgressText());
        assertTrue(view.seedsMet());
        assertEquals(1f, PlantUpgradeService.progressRatio(30, 10));
        assertEquals(0.3f, PlantUpgradeService.progressRatio(3, 10), 0.0001f);
    }

    @Test
    void scenarioE_maxLevelCannotUpgrade() {
        User user = App.getCurrentUser();
        user.ensurePlantLevel(PlantName.SUNFLOWER).setLevel(4);
        user.getSeedBank().addPacket(PlantName.SUNFLOWER, 100);
        user.setCoins(100_000);

        assertTrue(PlantUpgradeService.isMaxLevel(PlantName.SUNFLOWER));
        assertFalse(PlantUpgradeService.canUpgrade(PlantName.SUNFLOWER));
        Result result = PlantUpgradeService.upgrade(PlantName.SUNFLOWER);
        assertFalse(result.success());
        assertEquals(PlantUpgradeService.MAX_LEVEL, result.message());
        assertEquals(4, PlantUpgradeService.getCurrentLevel(PlantName.SUNFLOWER));
        assertEquals(100, user.getSeedBank().getAmount(PlantName.SUNFLOWER));
        assertEquals(100_000, user.getCoins());
        assertEquals("MAX", PlantUpgradeService.getProgress(PlantName.SUNFLOWER).seedProgressText());
    }

    @Test
    void scenarioF_gameplayReadsEffectiveStatsAfterUpgrade() {
        User user = App.getCurrentUser();
        EffectivePlantStats before = PlantUpgradeService.getEffectiveStats(PlantName.PEASHOOTER);
        assertEquals(20, before.attackDamage());
        assertEquals(300, before.maxHealth());
        assertEquals(100, before.sunCost());

        Result result = PlantUpgradeService.upgrade(PlantName.PEASHOOTER);
        assertTrue(result.success());

        EffectivePlantStats after = PlantUpgradeService.getEffectiveStats(user, PlantName.PEASHOOTER);
        assertEquals(2, PlantUpgradeService.getCurrentLevel(user, PlantName.PEASHOOTER));
        assertEquals(30, after.attackDamage());
        assertEquals(300, after.maxHealth());

        user.ensurePlantLevel(PlantName.PEASHOOTER).setLevel(3);
        EffectivePlantStats level3 = PlantUpgradeService.getEffectiveStats(user, PlantName.PEASHOOTER);
        assertEquals(450, level3.maxHealth());

        user.ensurePlantLevel(PlantName.PEASHOOTER).setLevel(4);
        EffectivePlantStats level4 = PlantUpgradeService.getEffectiveStats(user, PlantName.PEASHOOTER);
        assertEquals(75, level4.sunCost());
        assertEquals(30, level4.attackDamage());
        assertEquals(450, level4.maxHealth());
    }

    @Test
    void scenarioGAndH_levelsPersistAcrossSaveAndReload() {
        User user = App.getCurrentUser();
        user.getSeedBank().addPacket(PlantName.SUNFLOWER, 10);
        assertTrue(PlantUpgradeService.upgrade(PlantName.SUNFLOWER).success());
        assertEquals(2, PlantUpgradeService.getCurrentLevel(PlantName.SUNFLOWER));

        User loaded = new User(USERNAME, "pass", "nick", "u@t.com", "male", 1, "answer", 20);
        loaded.setCoins(0);
        assertTrue(ProgressService.loadUserProgress(loaded));
        App.setCurrentUser(loaded);

        assertEquals(2, PlantUpgradeService.getCurrentLevel(PlantName.SUNFLOWER));
        PlantLevel stored = loaded.getPlantLevel(PlantName.SUNFLOWER);
        assertEquals(2, stored.getLevel());
    }

    @Test
    void missingModifierDoesNotChangeUnrelatedStats() {
        EffectivePlantStats stats = PlantUpgradeService.getEffectiveStats(PlantName.WALL_NUT);
        assertEquals(0, stats.attackDamage());
        assertTrue(stats.maxHealth() >= 1);
    }

    @Test
    void invalidCatalogIsRejected() {
        assertThrows(PlantUpgradeConfigException.class, () ->
            PlantUpgradeConfigLoader.parse("""
                {
                  "plants": {
                    "NOT_A_PLANT": { "maxLevel": 2, "levels": [ { "level": 2, "requiredSeedPackets": 1, "requiredCoins": 1 } ] }
                  }
                }
                """)
        );
        assertThrows(PlantUpgradeConfigException.class, () ->
            PlantUpgradeConfigLoader.parse("""
                {
                  "plants": {
                    "PEASHOOTER": { "maxLevel": 3, "levels": [ { "level": 2, "requiredSeedPackets": 0, "requiredCoins": 0 } ] }
                  }
                }
                """)
        );
        assertThrows(PlantUpgradeConfigException.class, () ->
            PlantUpgradeConfigLoader.parse("""
                {
                  "plants": {
                    "PEASHOOTER": {
                      "maxLevel": 2,
                      "levels": [ { "level": 2, "requiredSeedPackets": -1, "requiredCoins": 0 } ]
                    }
                  }
                }
                """)
        );
    }

    @Test
    void allConfiguredPlantsHaveCompleteProgression() {
        PlantUpgradeCatalog catalog = PlantUpgradeConfigLoader.get();
        int configured = 0;
        for (PlantName plant : PlantName.values()) {
            if (catalog.definition(plant) == null) {
                continue;
            }
            configured++;
            assertEquals(4, catalog.maxLevel(plant));
            assertTrue(catalog.stepToReach(plant, 2) != null);
            assertTrue(catalog.stepToReach(plant, 3) != null);
            assertTrue(catalog.stepToReach(plant, 4) != null);
        }
        assertEquals(PlantName.values().length, configured);
    }

    @Test
    void previewLevel1ShowsDamageChangeAndLeavesUnchangedStatsPlain() {
        PlantUpgradePreview preview = PlantUpgradeService.getPreview(PlantName.PEASHOOTER);

        assertEquals(1, preview.currentLevel());
        assertEquals(2, preview.nextLevel());
        assertFalse(preview.atMaxLevel());
        assertEquals(20, preview.currentStats().attackDamage());
        assertEquals(30, preview.nextStats().attackDamage());
        assertEquals(300, preview.currentStats().maxHealth());
        assertEquals(300, preview.nextStats().maxHealth());
        assertEquals(100, preview.currentStats().sunCost());
        assertEquals(100, preview.nextStats().sunCost());

        PlantStatPreview damage = preview.stat("attackDamage");
        assertTrue(damage.changed());
        assertEquals("20", damage.currentValue());
        assertEquals("30", damage.nextValue());

        PlantStatPreview toughness = preview.stat("maxHealth");
        assertFalse(toughness.changed());
        assertEquals("300", toughness.currentValue());
        assertEquals("300", toughness.nextValue());

        PlantStatPreview cost = preview.stat("sunCost");
        assertFalse(cost.changed());
        assertEquals("100", cost.currentValue());
    }

    @Test
    void previewLevel2ShowsHealthChange() {
        App.getCurrentUser().ensurePlantLevel(PlantName.PEASHOOTER).setLevel(2);
        PlantUpgradePreview preview = PlantUpgradeService.getPreview(PlantName.PEASHOOTER);

        assertEquals(2, preview.currentLevel());
        assertEquals(3, preview.nextLevel());
        PlantStatPreview toughness = preview.stat("maxHealth");
        assertTrue(toughness.changed());
        assertEquals("300", toughness.currentValue());
        assertEquals("450", toughness.nextValue());
        assertFalse(preview.stat("attackDamage").changed());
        assertEquals("30", preview.stat("attackDamage").currentValue());
    }

    @Test
    void previewLevel3ShowsCostReduction() {
        App.getCurrentUser().ensurePlantLevel(PlantName.PEASHOOTER).setLevel(3);
        PlantUpgradePreview preview = PlantUpgradeService.getPreview(PlantName.PEASHOOTER);

        assertEquals(3, preview.currentLevel());
        assertEquals(4, preview.nextLevel());
        PlantStatPreview cost = preview.stat("sunCost");
        assertTrue(cost.changed());
        assertEquals("100", cost.currentValue());
        assertEquals("75", cost.nextValue());
        assertEquals(100, preview.currentStats().sunCost());
        assertEquals(75, preview.nextStats().sunCost());
        assertFalse(preview.stat("maxHealth").changed());
        assertEquals("450", preview.stat("maxHealth").currentValue());
    }

    @Test
    void previewAtMaxLevelHasNoNextStats() {
        App.getCurrentUser().ensurePlantLevel(PlantName.PEASHOOTER).setLevel(4);
        PlantUpgradePreview preview = PlantUpgradeService.getPreview(PlantName.PEASHOOTER);

        assertTrue(preview.atMaxLevel());
        assertEquals(4, preview.currentLevel());
        assertEquals(4, preview.nextLevel());
        assertNull(preview.nextStats());
        for (PlantStatPreview stat : preview.stats()) {
            assertFalse(stat.changed());
            assertEquals(stat.currentValue(), stat.nextValue());
        }
    }

    @Test
    void previewUsesEffectiveStatsAtNextLevelWithoutMutatingUserLevel() {
        User user = App.getCurrentUser();
        assertEquals(1, PlantUpgradeService.getCurrentLevel(PlantName.PEASHOOTER));

        EffectivePlantStats atFour = PlantUpgradeService.getEffectiveStatsAtLevel(PlantName.PEASHOOTER, 4);
        assertEquals(75, atFour.sunCost());
        assertEquals(30, atFour.attackDamage());
        assertEquals(450, atFour.maxHealth());
        assertEquals(1, PlantUpgradeService.getCurrentLevel(user, PlantName.PEASHOOTER));
        assertEquals(100, PlantUpgradeService.getEffectiveStats(user, PlantName.PEASHOOTER).sunCost());
    }

    @Test
    void previewPicksUpIntervalChangeFromProductionModifier() {
        PlantUpgradePreview preview = PlantUpgradeService.getPreview(PlantName.SUNFLOWER);
        PlantStatPreview interval = preview.stat("actionIntervalTicks");
        assertTrue(interval.changed());
        assertEquals("24", interval.currentValue());
        assertEquals("22", interval.nextValue());
        assertFalse(preview.stat("attackDamage").changed());
    }

    private static void deleteQuiet(Path path) throws Exception {
        if (path == null || !Files.exists(path)) {
            return;
        }
        if (Files.isDirectory(path)) {
            try (var stream = Files.list(path)) {
                for (Path child : stream.toList()) {
                    deleteQuiet(child);
                }
            }
        }
        Files.deleteIfExists(path);
    }
}

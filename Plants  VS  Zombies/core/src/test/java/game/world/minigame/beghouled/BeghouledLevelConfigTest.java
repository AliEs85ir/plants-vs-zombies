package game.world.minigame.beghouled;

import game.world.chapter.wave.WaveConfigPreset;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BeghouledLevelConfigTest {

    @Test
    void laterLevelsRequireMoreCombosAndIncreaseSpawnPressure() {
        BeghouledLevelConfig level1 = BeghouledLevelConfig.forLevel(1);
        BeghouledLevelConfig level2 = BeghouledLevelConfig.forLevel(2);
        BeghouledLevelConfig level3 = BeghouledLevelConfig.forLevel(3);

        assertTrue(level2.comboTarget() > level1.comboTarget());
        assertTrue(level3.comboTarget() > level2.comboTarget());
        assertTrue(level2.waveConfig().RSGrowthRate() > level1.waveConfig().RSGrowthRate());
        assertTrue(level3.waveConfig().RSGrowthRate() > level2.waveConfig().RSGrowthRate());
        assertTrue(level2.waveConfig().waveIntervalTicks() < level1.waveConfig().waveIntervalTicks());
        assertTrue(level3.waveConfig().waveIntervalTicks() < level2.waveConfig().waveIntervalTicks());
    }

    @Test
    void levelsUnlockMoreUpgradeCards() {
        assertEquals(2, BeghouledUpgradeCatalog.cardPlantsForLevel(1).size());
        assertEquals(4, BeghouledUpgradeCatalog.cardPlantsForLevel(2).size());
        assertEquals(4, BeghouledUpgradeCatalog.cardPlantsForLevel(3).size());
        assertTrue(BeghouledUpgradeCatalog.upgradesForLevel(3).size() > BeghouledUpgradeCatalog.upgradesForLevel(2).size());
    }

    @Test
    void levelConfigsUseNamedWavePresets() {
        assertEquals(
            WaveConfigPreset.RELAXED.toConfig().withEndless(true),
            BeghouledLevelConfig.forLevel(1).waveConfig()
        );
        assertEquals(
            WaveConfigPreset.STANDARD.toConfig().withEndless(true),
            BeghouledLevelConfig.forLevel(2).waveConfig()
        );
        assertEquals(
            WaveConfigPreset.INTENSE.toConfig().withEndless(true),
            BeghouledLevelConfig.forLevel(3).waveConfig()
        );
    }
}

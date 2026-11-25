package game.world.minigame.plantzombies;

import game.world.chapter.wave.WaveConfigPreset;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PlantZombiesLevelConfigTest {

    @Test
    void laterLevelsIncreaseWavePressure() {
        PlantZombiesLevelConfig level1 = PlantZombiesLevelConfig.forLevel(1);
        PlantZombiesLevelConfig level2 = PlantZombiesLevelConfig.forLevel(2);
        PlantZombiesLevelConfig level3 = PlantZombiesLevelConfig.forLevel(3);

        assertTrue(level2.totalWaves() > level1.totalWaves());
        assertTrue(level3.totalWaves() > level2.totalWaves());
        assertTrue(level2.waveConfig().RSGrowthRate() > level1.waveConfig().RSGrowthRate());
        assertTrue(level3.waveConfig().RSGrowthRate() > level2.waveConfig().RSGrowthRate());
        assertTrue(level2.waveConfig().waveIntervalTicks() < level1.waveConfig().waveIntervalTicks());
        assertTrue(level3.waveConfig().waveIntervalTicks() < level2.waveConfig().waveIntervalTicks());
    }

    @Test
    void levelConfigsUseNamedPresets() {
        assertEquals(WaveConfigPreset.RELAXED.toConfig(), PlantZombiesLevelConfig.forLevel(1).waveConfig());
        assertEquals(WaveConfigPreset.STANDARD.toConfig(), PlantZombiesLevelConfig.forLevel(2).waveConfig());
        assertEquals(WaveConfigPreset.INTENSE.toConfig(), PlantZombiesLevelConfig.forLevel(3).waveConfig());
    }
}

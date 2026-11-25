package game.world.minigame.wallnut_bowling;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WallNutBowlingLevelConfigTest {

    @Test
    void laterLevelsAddWavesAndTightenWaveTiming() {
        WallNutBowlingLevelConfig level1 = WallNutBowlingLevelConfig.forLevel(1);
        WallNutBowlingLevelConfig level2 = WallNutBowlingLevelConfig.forLevel(2);
        WallNutBowlingLevelConfig level3 = WallNutBowlingLevelConfig.forLevel(3);

        assertEquals(3, level1.totalWaves());
        assertEquals(5, level2.totalWaves());
        assertEquals(7, level3.totalWaves());
        assertTrue(level2.waveConfig().waveIntervalTicks() < level1.waveConfig().waveIntervalTicks());
        assertTrue(level3.waveConfig().waveIntervalTicks() < level2.waveConfig().waveIntervalTicks());
        assertTrue(level2.waveConfig().RSInitialDeposit() > level1.waveConfig().RSInitialDeposit());
        assertTrue(level3.waveConfig().RSInitialDeposit() > level2.waveConfig().RSInitialDeposit());
    }
}

package game.world.chapter.wave;

import game.engine.time.GameClock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WaveConfigTest {
    @Test
    void firstWaveIsTenSecondsByDefault() {
        WaveConfig config = WaveConfig.getDefaultWaveConfig();
        assertEquals(10L * GameClock.TICKS_PER_SECOND, config.firstWaveTicks());
        assertEquals(-1, config.secondsSinceFirstWave(0));
        assertEquals(-1, config.secondsSinceFirstWave(config.firstWaveTicks() - 1));
        assertEquals(0, config.secondsSinceFirstWave(config.firstWaveTicks()));
        assertEquals(29, config.secondsSinceFirstWave(
            config.firstWaveTicks() + 29L * GameClock.TICKS_PER_SECOND
        ));
        assertEquals(30, config.secondsSinceFirstWave(
            config.firstWaveTicks() + 30L * GameClock.TICKS_PER_SECOND
        ));
        assertEquals(false, config.endless());
        assertEquals(true, WaveConfig.endlessDefault().endless());
    }

    @Test
    void presetsIncreaseSpawnPressure() {
        WaveConfig relaxed = WaveConfig.forPreset(WaveConfigPreset.RELAXED);
        WaveConfig standard = WaveConfig.forPreset(WaveConfigPreset.STANDARD);
        WaveConfig intense = WaveConfig.forPreset(WaveConfigPreset.INTENSE);
        WaveConfig extreme = WaveConfig.forPreset(WaveConfigPreset.EXTREME);

        assertTrue(relaxed.waveIntervalTicks() > standard.waveIntervalTicks());
        assertTrue(standard.waveIntervalTicks() > intense.waveIntervalTicks());
        assertTrue(intense.waveIntervalTicks() > extreme.waveIntervalTicks());
        assertTrue(relaxed.RSInitialDeposit() > standard.RSInitialDeposit());
        assertTrue(intense.RSGrowthRate() > standard.RSGrowthRate());
    }

    @Test
    void builderCanScaleSpawnPressure() {
        WaveConfig harder = WaveConfig.builderFrom(WaveConfig.relaxed()).spawnPressure(1.5).build();
        assertTrue(harder.RSGrowthRate() > WaveConfig.relaxed().RSGrowthRate());
        assertTrue(harder.waveIntervalTicks() < WaveConfig.relaxed().waveIntervalTicks());
    }
}

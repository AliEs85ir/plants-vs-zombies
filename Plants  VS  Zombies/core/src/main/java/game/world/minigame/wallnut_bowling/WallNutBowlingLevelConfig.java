package game.world.minigame.wallnut_bowling;

import game.engine.time.GameClock;
import game.world.chapter.wave.DefaultWaveFormula;
import game.world.chapter.wave.ScaledWaveFormula;
import game.world.chapter.wave.WaveConfig;
import game.world.chapter.wave.WaveFormula;
import game.world.minigame.MultiLevelMinigame;

public record WallNutBowlingLevelConfig(
    int level,
    int totalWaves,
    WaveConfig waveConfig,
    WaveFormula waveFormula
) {
    public static WallNutBowlingLevelConfig forLevel(int level) {
        int clamped = clamp(level);
        return switch (clamped) {
            case 2 -> new WallNutBowlingLevelConfig(
                2,
                5,
                new WaveConfig(
                    8L * GameClock.TICKS_PER_SECOND,
                    32L * GameClock.TICKS_PER_SECOND,
                    0.65,
                    1200,
                    5.5,
                    0.6,
                    6.5
                ),
                new ScaledWaveFormula(1.3)
            );
            case 3 -> new WallNutBowlingLevelConfig(
                3,
                7,
                new WaveConfig(
                    6L * GameClock.TICKS_PER_SECOND,
                    22L * GameClock.TICKS_PER_SECOND,
                    0.85,
                    1550,
                    6.5,
                    0.75,
                    8
                ),
                new ScaledWaveFormula(1.65)
            );
            default -> new WallNutBowlingLevelConfig(
                1,
                3,
                WaveConfig.getDefaultWaveConfig(),
                new DefaultWaveFormula()
            );
        };
    }

    public static int clamp(int level) {
        if (level < 1) {
            return 1;
        }
        if (level > MultiLevelMinigame.TOTAL_LEVELS) {
            return MultiLevelMinigame.TOTAL_LEVELS;
        }
        return level;
    }
}

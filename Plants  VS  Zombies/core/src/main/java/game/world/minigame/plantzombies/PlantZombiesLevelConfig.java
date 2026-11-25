package game.world.minigame.plantzombies;

import game.world.chapter.wave.DefaultWaveFormula;
import game.world.chapter.wave.ScaledWaveFormula;
import game.world.chapter.wave.WaveConfig;
import game.world.chapter.wave.WaveConfigPreset;
import game.world.chapter.wave.WaveFormula;
import game.world.minigame.MultiLevelMinigame;

public record PlantZombiesLevelConfig(
    int level,
    int totalWaves,
    WaveConfig waveConfig,
    WaveFormula waveFormula
) {
    public static PlantZombiesLevelConfig forLevel(int level) {
        int clamped = clamp(level);
        return switch (clamped) {
            case 2 -> new PlantZombiesLevelConfig(
                2,
                5,
                WaveConfig.forPreset(WaveConfigPreset.STANDARD),
                new ScaledWaveFormula(1.25)
            );
            case 3 -> new PlantZombiesLevelConfig(
                3,
                6,
                WaveConfig.forPreset(WaveConfigPreset.INTENSE),
                new ScaledWaveFormula(1.55)
            );
            default -> new PlantZombiesLevelConfig(
                1,
                4,
                WaveConfig.forPreset(WaveConfigPreset.RELAXED),
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

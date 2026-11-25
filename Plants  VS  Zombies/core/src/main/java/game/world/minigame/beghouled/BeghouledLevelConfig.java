package game.world.minigame.beghouled;

import game.world.chapter.wave.DefaultWaveFormula;
import game.world.chapter.wave.ScaledWaveFormula;
import game.world.chapter.wave.WaveConfig;
import game.world.chapter.wave.WaveConfigPreset;
import game.world.chapter.wave.WaveFormula;
import game.world.minigame.MultiLevelMinigame;

public record BeghouledLevelConfig(
    int level,
    int comboTarget,
    WaveConfig waveConfig,
    WaveFormula waveFormula
) {
    public static BeghouledLevelConfig forLevel(int level) {
        int clamped = clamp(level);
        return switch (clamped) {
            case 2 -> new BeghouledLevelConfig(
                2,
                15,
                WaveConfig.forPreset(WaveConfigPreset.STANDARD).withEndless(true),
                new ScaledWaveFormula(1.25)
            );
            case 3 -> new BeghouledLevelConfig(
                3,
                20,
                WaveConfig.forPreset(WaveConfigPreset.INTENSE).withEndless(true),
                new ScaledWaveFormula(1.55)
            );
            default -> new BeghouledLevelConfig(
                1,
                10,
                WaveConfig.forPreset(WaveConfigPreset.RELAXED).withEndless(true),
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

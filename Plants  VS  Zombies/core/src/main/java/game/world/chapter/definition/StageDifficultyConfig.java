package game.world.chapter.definition;

import game.engine.time.GameClock;
import game.world.chapter.wave.WaveConfig;

public record StageDifficultyConfig(
    double costMultiplier,
    double spawnIntervalFactor,
    double randomSpawnDepositFactor,
    int waveCountBonus
) {
    public static StageDifficultyConfig forOrder(int stageOrder) {
        int order = Math.max(1, stageOrder);
        double cost = 1.0 + 0.18 * (order - 1);
        double interval = Math.max(0.65, 1.0 - 0.08 * (order - 1));
        double deposit = 1.0 + 0.12 * (order - 1);
        int waveBonus = Math.max(0, order - 1);
        return new StageDifficultyConfig(cost, interval, deposit, waveBonus);
    }

    public static StageDifficultyConfig identity() {
        return new StageDifficultyConfig(1.0, 1.0, 1.0, 0);
    }

    public WaveConfig applyTo(WaveConfig base) {
        WaveConfig source = base != null ? base : WaveConfig.getDefaultWaveConfig();
        long interval = Math.max(
            15L * GameClock.TICKS_PER_SECOND,
            Math.round(source.waveIntervalTicks() * spawnIntervalFactor)
        );
        return new WaveConfig(
            source.firstWaveTicks(),
            interval,
            source.RSGrowthRate(),
            source.RSInitialDeposit() * randomSpawnDepositFactor,
            source.RSDepositGrowthFactor(),
            source.RSTimeScaling(),
            source.RSMaxDepositFactor()
        );
    }

    public int applyWaveCount(int baseWaves) {
        return Math.max(1, baseWaves + waveCountBonus);
    }
}

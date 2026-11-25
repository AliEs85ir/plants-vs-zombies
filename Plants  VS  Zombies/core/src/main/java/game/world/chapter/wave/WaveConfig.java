package game.world.chapter.wave;

import game.engine.time.GameClock;

public record WaveConfig(
    long firstWaveTicks,
    long waveIntervalTicks,
    double RSGrowthRate,
    double RSInitialDeposit,
    double RSDepositGrowthFactor,
    double RSTimeScaling,
    double RSMaxDepositFactor,
    boolean endless
) {
    public WaveConfig(
        long firstWaveTicks,
        long waveIntervalTicks,
        double RSGrowthRate,
        double RSInitialDeposit,
        double RSDepositGrowthFactor,
        double RSTimeScaling,
        double RSMaxDepositFactor
    ) {
        this(
            firstWaveTicks,
            waveIntervalTicks,
            RSGrowthRate,
            RSInitialDeposit,
            RSDepositGrowthFactor,
            RSTimeScaling,
            RSMaxDepositFactor,
            false
        );
    }

    public static WaveConfig getDefaultWaveConfig() {
        return standard();
    }

    public static WaveConfig standard() {
        return new WaveConfig(
            10L * GameClock.TICKS_PER_SECOND,
            45L * GameClock.TICKS_PER_SECOND,
            0.5,
            900,
            5,
            0.5,
            5
        );
    }

    public static WaveConfig relaxed() {
        return new WaveConfig(
            15L * GameClock.TICKS_PER_SECOND,
            55L * GameClock.TICKS_PER_SECOND,
            0.35,
            1200,
            4.5,
            0.35,
            4
        );
    }

    public static WaveConfig intense() {
        return new WaveConfig(
            8L * GameClock.TICKS_PER_SECOND,
            32L * GameClock.TICKS_PER_SECOND,
            0.65,
            750,
            5.5,
            0.6,
            6
        );
    }

    public static WaveConfig extreme() {
        return new WaveConfig(
            6L * GameClock.TICKS_PER_SECOND,
            22L * GameClock.TICKS_PER_SECOND,
            0.85,
            600,
            6.5,
            0.75,
            8
        );
    }

    public static WaveConfig forPreset(WaveConfigPreset preset) {
        if (preset == null) {
            return standard();
        }
        return preset.toConfig();
    }

    public static WaveConfigBuilder builder() {
        return WaveConfigBuilder.from(standard());
    }

    public static WaveConfigBuilder builderFrom(WaveConfig base) {
        return WaveConfigBuilder.from(base);
    }

    public WaveConfig withSpawnPressure(double factor) {
        return WaveConfigBuilder.from(this).spawnPressure(factor).build();
    }

    public WaveConfig withEndless(boolean endlessMode) {
        return new WaveConfig(
            firstWaveTicks,
            waveIntervalTicks,
            RSGrowthRate,
            RSInitialDeposit,
            RSDepositGrowthFactor,
            RSTimeScaling,
            RSMaxDepositFactor,
            endlessMode
        );
    }

    public static WaveConfig endlessDefault() {
        return standard().withEndless(true);
    }

    public int secondsSinceFirstWave(long currentTick) {
        if (currentTick < firstWaveTicks) {
            return -1;
        }
        return (int) ((currentTick - firstWaveTicks) / GameClock.TICKS_PER_SECOND);
    }
}

package game.world.chapter.wave;

import game.engine.time.GameClock;

public final class WaveConfigBuilder {
    private long firstWaveTicks;
    private long waveIntervalTicks;
    private double rsGrowthRate;
    private double rsInitialDeposit;
    private double rsDepositGrowthFactor;
    private double rsTimeScaling;
    private double rsMaxDepositFactor;
    private boolean endless;

    private WaveConfigBuilder() {
        WaveConfig defaults = WaveConfig.getDefaultWaveConfig();
        firstWaveTicks = defaults.firstWaveTicks();
        waveIntervalTicks = defaults.waveIntervalTicks();
        rsGrowthRate = defaults.RSGrowthRate();
        rsInitialDeposit = defaults.RSInitialDeposit();
        rsDepositGrowthFactor = defaults.RSDepositGrowthFactor();
        rsTimeScaling = defaults.RSTimeScaling();
        rsMaxDepositFactor = defaults.RSMaxDepositFactor();
        endless = defaults.endless();
    }

    public static WaveConfigBuilder from(WaveConfig base) {
        WaveConfigBuilder builder = new WaveConfigBuilder();
        if (base == null) {
            return builder;
        }
        builder.firstWaveTicks = base.firstWaveTicks();
        builder.waveIntervalTicks = base.waveIntervalTicks();
        builder.rsGrowthRate = base.RSGrowthRate();
        builder.rsInitialDeposit = base.RSInitialDeposit();
        builder.rsDepositGrowthFactor = base.RSDepositGrowthFactor();
        builder.rsTimeScaling = base.RSTimeScaling();
        builder.rsMaxDepositFactor = base.RSMaxDepositFactor();
        builder.endless = base.endless();
        return builder;
    }

    public static WaveConfigBuilder fromPreset(WaveConfigPreset preset) {
        return from(preset != null ? preset.toConfig() : WaveConfig.standard());
    }

    public WaveConfigBuilder firstWaveSeconds(long seconds) {
        this.firstWaveTicks = Math.max(0L, seconds) * GameClock.TICKS_PER_SECOND;
        return this;
    }

    public WaveConfigBuilder waveIntervalSeconds(long seconds) {
        this.waveIntervalTicks = Math.max(1L, seconds) * GameClock.TICKS_PER_SECOND;
        return this;
    }

    public WaveConfigBuilder rsGrowthRate(double value) {
        this.rsGrowthRate = Math.max(0d, value);
        return this;
    }

    public WaveConfigBuilder rsInitialDeposit(double value) {
        this.rsInitialDeposit = Math.max(1d, value);
        return this;
    }

    public WaveConfigBuilder rsDepositGrowthFactor(double value) {
        this.rsDepositGrowthFactor = Math.max(1d, value);
        return this;
    }

    public WaveConfigBuilder rsTimeScaling(double value) {
        this.rsTimeScaling = Math.max(0d, value);
        return this;
    }

    public WaveConfigBuilder rsMaxDepositFactor(double value) {
        this.rsMaxDepositFactor = Math.max(1d, value);
        return this;
    }

    public WaveConfigBuilder endless(boolean endless) {
        this.endless = endless;
        return this;
    }

    /**
     * Values above 1 increase spawn pressure; below 1 make waves gentler.
     */
    public WaveConfigBuilder spawnPressure(double factor) {
        double pressure = Math.max(0.1d, factor);
        rsGrowthRate *= pressure;
        rsInitialDeposit /= pressure;
        rsTimeScaling *= pressure;
        rsMaxDepositFactor *= 0.85d + (0.15d * pressure);
        waveIntervalTicks = Math.max(
            15L * GameClock.TICKS_PER_SECOND,
            Math.round(waveIntervalTicks / Math.sqrt(pressure))
        );
        firstWaveTicks = Math.max(
            5L * GameClock.TICKS_PER_SECOND,
            Math.round(firstWaveTicks / Math.sqrt(pressure))
        );
        return this;
    }

    public WaveConfig build() {
        return new WaveConfig(
            firstWaveTicks,
            waveIntervalTicks,
            rsGrowthRate,
            rsInitialDeposit,
            rsDepositGrowthFactor,
            rsTimeScaling,
            rsMaxDepositFactor,
            endless
        );
    }
}

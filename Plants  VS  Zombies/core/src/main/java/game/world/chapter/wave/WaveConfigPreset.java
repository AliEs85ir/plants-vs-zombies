package game.world.chapter.wave;

public enum WaveConfigPreset {
    RELAXED,
    STANDARD,
    INTENSE,
    EXTREME;

    public WaveConfig toConfig() {
        return switch (this) {
            case RELAXED -> WaveConfig.relaxed();
            case STANDARD -> WaveConfig.standard();
            case INTENSE -> WaveConfig.intense();
            case EXTREME -> WaveConfig.extreme();
        };
    }
}

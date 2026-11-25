package game.world.chapter.wave;

public class ScaledWaveFormula implements WaveFormula {
    private final WaveFormula delegate;
    private final double costMultiplier;

    public ScaledWaveFormula(WaveFormula delegate, double costMultiplier) {
        this.delegate = delegate != null ? delegate : new DefaultWaveFormula();
        this.costMultiplier = costMultiplier > 0 ? costMultiplier : 1.0;
    }

    public ScaledWaveFormula(double costMultiplier) {
        this(new DefaultWaveFormula(), costMultiplier);
    }

    @Override
    public double calculateWaveCost(int waveNumber, int totalWaves, int userDifficultyLevel) {
        return delegate.calculateWaveCost(waveNumber, totalWaves, userDifficultyLevel) * costMultiplier;
    }
}

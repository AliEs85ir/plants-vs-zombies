package game.world.chapter.wave;

public class DefaultWaveFormula implements WaveFormula{

    @Override
    public double calculateWaveCost(int waveNumber, int totalWaves, int userDifficultyLevel) {
        double baseCost = 300.0 * (userDifficultyLevel / 3.0);

        if (waveNumber == 1) return baseCost;

        double cost = baseCost;
        for (int i = 2; i <= waveNumber; i++) {
            if (i == totalWaves) {
                cost *= 2.0;
            } else {
                cost *= 1.25;
            }
        }
        return cost;
    }
}

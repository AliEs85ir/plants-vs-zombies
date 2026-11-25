package game.world.chapter.wave;

public interface WaveFormula {
    double calculateWaveCost(int waveNumber, int totalWaves, int userDifficultyLevel);
}

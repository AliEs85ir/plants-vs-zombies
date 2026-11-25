package game.engine.system.wave;

import app.enums.GameState;
import game.audio.AudioCatalog;
import game.engine.time.TickSystem;
import game.service.Zombie.WaveService;
import game.service.Zombie.ZombieService;
import game.world.GameWorld;
import game.world.chapter.Chapter;
import game.world.chapter.wave.WaveConfig;

import java.util.ArrayList;
import java.util.List;

// >>> Handles wave progression and spawning logic.
public class WaveSimulationSystem implements TickSystem
{
    private final GameWorld world;
    private final WaveService waveService;
    private final WaveConfig config;


    private final int totalWaves;
    private final boolean endless;
    private final long[] waveStartTicks;
    private int nextWaveIndex = 0;
    private long nextEndlessWaveTick;

    private double spawnDeposit = 0;
    private double spawnThreshold;

    // State
    private boolean finalWaveStarted = false;
    private boolean victoryTriggered = false;

    public WaveSimulationSystem(GameWorld world, ZombieService zombieService) {
        this.world = world;
        Chapter chapter = world.getCurrentChapter();
        this.waveService = new WaveService(world, zombieService, chapter);
        this.config = chapter.getWaveConfig() != null
            ? chapter.getWaveConfig()
            : WaveConfig.getDefaultWaveConfig();
        this.totalWaves = Math.max(0, chapter.getTotalWaves());
        this.endless = this.config.endless();

        waveStartTicks = new long[totalWaves];
        long nextTick = config.firstWaveTicks();
        for (int i = 0; i < totalWaves; i++) {
            waveStartTicks[i] = nextTick;
            nextTick += config.waveIntervalTicks();
        }
        nextEndlessWaveTick = totalWaves > 0
            ? waveStartTicks[totalWaves - 1] + config.waveIntervalTicks()
            : config.firstWaveTicks();

        this.spawnThreshold = config.RSInitialDeposit();
    }

    public int getNextWaveIndex() {
        return nextWaveIndex;
    }

    public double getSpawnDeposit() {
        return spawnDeposit;
    }

    public double getSpawnThreshold() {
        return spawnThreshold;
    }

    public boolean isFinalWaveStarted() {
        return finalWaveStarted;
    }

    public boolean isVictoryTriggered() {
        return victoryTriggered;
    }

    public void restore(
        int restoredNextWaveIndex,
        double restoredSpawnDeposit,
        double restoredSpawnThreshold,
        boolean restoredFinalWaveStarted,
        boolean restoredVictoryTriggered
    ) {
        this.nextWaveIndex = Math.max(0, endless ? restoredNextWaveIndex : Math.min(totalWaves, restoredNextWaveIndex));
        this.spawnDeposit = Math.max(0d, restoredSpawnDeposit);
        this.spawnThreshold = restoredSpawnThreshold > 0d ? restoredSpawnThreshold : config.RSInitialDeposit();
        this.finalWaveStarted = restoredFinalWaveStarted;
        this.victoryTriggered = restoredVictoryTriggered;
    }

    @Override
    public void update(long currentTick) {
        if (world.getGameState() != GameState.PLAYING || victoryTriggered) return;

        while (nextWaveIndex < totalWaves && currentTick >= waveStartTicks[nextWaveIndex]) {
            startWave(nextWaveIndex + 1);
            nextWaveIndex++;
            if (nextWaveIndex == totalWaves && !endless) {
                finalWaveStarted = true;
            }
        }
        if (endless && nextWaveIndex >= totalWaves && currentTick >= nextEndlessWaveTick) {
            startWave(nextWaveIndex + 1);
            nextWaveIndex++;
            nextEndlessWaveTick += config.waveIntervalTicks();
        }

        updateRandomSpawner(currentTick);
    }

    private void startWave(int waveNumber) {
        waveService.startNextWave(waveNumber);
        world.setCurrentWaveNumber(waveNumber);
        world.getCurrentSeason().onWaveStart(world, waveNumber);
        AudioCatalog.playWave(world.getCurrentChapter(), waveNumber);
    }

    private void updateRandomSpawner(long currentTick) {
        if (finalWaveStarted && world.getZombies().isEmpty()) return;

        double timeFactor = 1.0 + currentTick * config.RSTimeScaling();
        spawnDeposit += config.RSGrowthRate() * timeFactor;

        // Cap deposit to avoid excessive buildup
        double maxDeposit = spawnThreshold * config.RSMaxDepositFactor();
        if (spawnDeposit > maxDeposit) spawnDeposit = maxDeposit;

        // Spend deposit on zombies
        while (spawnDeposit >= spawnThreshold) {
            if (waveService.spawnRandomZombie() != null) {
                spawnDeposit -= spawnThreshold;
                spawnThreshold *= config.RSDepositGrowthFactor();
            } else {
                break;
            }
        }
    }

}

package game.service.Zombie;

import app.model.App;
import com.badlogic.gdx.math.Vector2;
import common.model.Position;
import game.entity.wave.Wave;
import game.entity.zombie.Zombie;
import game.entity.zombie.data.properties.Property;
import game.world.GameWorld;
import game.world.chapter.Chapter;
import game.world.playground.Grid;

import java.util.List;
import java.util.Random;
import java.util.Vector;

public class WaveService {
    private final GameWorld world;
    private final ZombieService zombieService;
    private final Chapter chapter;
    private final Random random;

    public WaveService(GameWorld world, ZombieService zombieService, Chapter chapter) {
        this.world = world;
        this.zombieService = zombieService;
        this.chapter = chapter;
        this.random = new Random();
    }


    public Wave startNextWave(int waveNumber) {
        Wave wave = new Wave();
        wave.setNumber(waveNumber);
        wave.setFinalWave(!isEndless() && waveNumber == getTotalWavesCount());

        double cost = chapter.getWaveFormula().calculateWaveCost(
            waveNumber,
            getTotalWavesCount(),
            App.getCurrentUser().getDifficultyLevel()
        );

        spawnZombiesForWave(cost, wave);
        world.getMap().setCurrentWave(wave);
        return wave;
    }


    public Zombie spawnRandomZombie() {
        List<Property> availableTypes = chapter.getAvailableZombies();
        if (availableTypes.isEmpty()) return null;

        int totalWeight = 0;
        for (Property p : availableTypes) totalWeight += p.getZombieWeight();
        if (totalWeight <= 0) return null;

        int rand = random.nextInt(totalWeight);
        Property selected = null;
        for (Property p : availableTypes) {
            rand -= p.getZombieWeight();
            if (rand < 0) { selected = p; break; }
        }
        if (selected == null) return null;

        List<Vector2> spawnPoints = world.getMap().getZombieSpawns();
        if (spawnPoints.isEmpty()) return null;

        Vector2 spawnPos = spawnPoints.get(random.nextInt(spawnPoints.size()));
        return zombieService.spawnZombie(selected.getAlias(), new Position(spawnPos.x, spawnPos.y));
    }

    private void spawnZombiesForWave(double cost, Wave wave) {
        double accumulatedCost = 0;
        List<Property> availableTypes = chapter.getAvailableZombies();

        while (accumulatedCost < cost) {
            Property selected = selectWeightedRandom(availableTypes);
            if (selected == null) break;

            List<Vector2> spawnPoints = world.getMap().getZombieSpawns();
            Vector2 pos = spawnPoints.get(random.nextInt(spawnPoints.size()));

            Zombie zombie = zombieService.spawnZombie(selected.getAlias(), new Position(pos.x, pos.y));
            if (zombie != null) {
                wave.addZombie(zombie);
                accumulatedCost += selected.getZombieWaveCost();
            }
        }
    }

    private Property selectWeightedRandom(List<Property> types) {
        int total = 0;
        for (Property p : types) total += p.getZombieWeight();
        int rand = random.nextInt(total);
        for (Property p : types) {
            rand -= p.getZombieWeight();
            if (rand < 0) return p;
        }
        return types.get(0);
    }

    public int getTotalWavesCount() {
        return chapter.getTotalWaves();
    }

    private boolean isEndless() {
        return chapter.getWaveConfig() != null && chapter.getWaveConfig().endless();
    }


    public boolean isCurrentWaveFinished() {
        Wave wave = world.getMap().getCurrentWave();
        if (wave == null) return true;
        for (Zombie z : wave.getZombies()) {
            if (!z.isDead() && z.getCurrentHealth() > 0) return false;
        }
        return true;
    }
}

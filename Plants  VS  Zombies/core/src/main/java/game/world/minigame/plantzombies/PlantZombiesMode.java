package game.world.minigame.plantzombies;

import app.enums.plant.PlantName;
import game.component.physic.PhysicService;
import game.engine.GameSimulationEngine;
import game.world.GameWorld;
import game.world.minigame.Minigame;
import game.world.minigame.MinigameLevelAnnouncer;
import game.world.minigame.MultiLevelMinigame;
import graphics.GraphicsEngine;

import java.util.List;

public class PlantZombiesMode implements Minigame, MultiLevelMinigame {
    private final PlantZombiesLevelConfig levelConfig;

    public PlantZombiesMode() {
        this(PlantZombiesLevelConfig.forLevel(1));
    }

    public PlantZombiesMode(int level) {
        this(PlantZombiesLevelConfig.forLevel(level));
    }

    public PlantZombiesMode(PlantZombiesLevelConfig levelConfig) {
        this.levelConfig = levelConfig != null ? levelConfig : PlantZombiesLevelConfig.forLevel(1);
    }

    @Override
    public int getLevel() {
        return levelConfig.level();
    }

    public PlantZombiesLevelConfig getLevelConfig() {
        return levelConfig;
    }

    @Override
    public void onMinigameStart(GameWorld world, GraphicsEngine graphicsEngine, PhysicService physicService) {
        MinigameLevelAnnouncer.announce(world, getLevel(), getTotalLevels());
    }

    @Override
    public void onTick(GameWorld world, long currentTick) {
    }

    @Override
    public void onEngineStart(GameSimulationEngine engine) {
    }

    @Override
    public void setWorld(GameWorld world) {
    }

    @Override
    public List<PlantName> getPlants() {
        return List.of();
    }

    @Override
    public List<String> getAvailableZombies() {
        return List.of();
    }
}

package game.world.minigame.wallnut_bowling;

import app.enums.plant.PlantName;
import game.component.physic.PhysicService;
import game.engine.GameSimulationEngine;
import game.world.GameWorld;
import game.world.minigame.Minigame;
import game.world.minigame.MinigameLevelAnnouncer;
import game.world.minigame.MultiLevelMinigame;
import graphics.GraphicsEngine;

import java.util.List;

public class WallNutBowlingMode implements Minigame, MultiLevelMinigame {
    private final WallNutBowlingLevelConfig levelConfig;
    private WallNutBowingWorld world;

    public WallNutBowlingMode() {
        this(WallNutBowlingLevelConfig.forLevel(1));
    }

    public WallNutBowlingMode(int level) {
        this(WallNutBowlingLevelConfig.forLevel(level));
    }

    public WallNutBowlingMode(WallNutBowlingLevelConfig levelConfig) {
        this.levelConfig = levelConfig != null ? levelConfig : WallNutBowlingLevelConfig.forLevel(1);
    }

    @Override
    public int getLevel() {
        return levelConfig.level();
    }

    public WallNutBowlingLevelConfig getLevelConfig() {
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
        if (world instanceof WallNutBowingWorld bowlingWorld) {
            this.world = bowlingWorld;
        }
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

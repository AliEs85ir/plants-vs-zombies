package game.world.minigame.vasebreaker;

import app.enums.obstacle.ObstacleType;
import app.enums.plant.PlantName;
import game.component.physic.PhysicService;
import game.engine.GameSimulationEngine;
import game.engine.system.sun.SunSimulationSystem;
import game.engine.system.wave.WaveSimulationSystem;
import game.entity.obstacle.ObstacleFactory;
import game.entity.obstacle.ability.VaseAbility;
import game.world.GameWorld;
import game.world.minigame.Minigame;
import game.world.minigame.MinigameLevelAnnouncer;
import game.world.minigame.MultiLevelMinigame;
import game.world.playground.Cell;
import graphics.GraphicsEngine;

import java.util.List;
import java.util.Random;

public class VaseBreakerMode implements Minigame, MultiLevelMinigame {
    private final List<PlantName> availablePlants;
    private final List<String> availableZombieIds;
    private final VaseBreakerLevelConfig levelConfig;
    private final Random random = new Random();
    private VaseBreakerWorld world;

    public VaseBreakerMode(List<PlantName> availablePlants, List<String> availableZombieIds) {
        this(availablePlants, availableZombieIds, VaseBreakerLevelConfig.forLevel(1));
    }

    public VaseBreakerMode(List<PlantName> availablePlants, List<String> availableZombieIds, int level) {
        this(availablePlants, availableZombieIds, VaseBreakerLevelConfig.forLevel(level));
    }

    public VaseBreakerMode(
        List<PlantName> availablePlants,
        List<String> availableZombieIds,
        VaseBreakerLevelConfig levelConfig
    ) {
        this.availablePlants = availablePlants;
        this.availableZombieIds = availableZombieIds;
        this.levelConfig = levelConfig != null ? levelConfig : VaseBreakerLevelConfig.forLevel(1);
    }

    @Override
    public int getLevel() {
        return levelConfig.level();
    }

    public VaseBreakerLevelConfig getLevelConfig() {
        return levelConfig;
    }

    public int getRandomVaseZombieChance() {
        return levelConfig.randomVaseZombieChance();
    }

    @Override
    public void onMinigameStart(GameWorld world, GraphicsEngine graphicsEngine, PhysicService physicService) {
        MinigameLevelAnnouncer.announce(world, getLevel(), getTotalLevels());
        for (Cell cell : world.getGrid().getLastColumns(4)) {
            spawnVase(cell, graphicsEngine, physicService);
        }
    }

    private void spawnVase(Cell cell, GraphicsEngine graphicsEngine, PhysicService physicService) {
        ObstacleType randomType = giveRandomType();

        ObstacleFactory.getInstance().createRuntimeObstacle(
            randomType,
            40,
            40,
            cell.getWorldX(),
            cell.getWorldY(),
            List.of(new VaseAbility(cell))
        );
    }

    private ObstacleType giveRandomType() {
        return levelConfig.rollVaseType(random.nextInt(100));
    }

    @Override
    public void onTick(GameWorld world, long currentTick) {

    }

    @Override
    public void onEngineStart(GameSimulationEngine engine) {
        engine.removeSystem(SunSimulationSystem.class);
        engine.removeSystem(WaveSimulationSystem.class);
    }

    @Override
    public void setWorld(GameWorld world) {
        this.world = (VaseBreakerWorld) world;
    }

    public List<PlantName> getAvailablePlants() {
        return availablePlants;
    }

    @Override
    public List<PlantName> getPlants() {
        return List.of();
    }

    @Override
    public List<String> getAvailableZombies() {
        return availableZombieIds != null ? availableZombieIds : List.of();
    }

    public boolean hasWorld() {
        return world != null;
    }

    public boolean areAllVasesBroken() {
        if (world == null) {
            return false;
        }
        for (Vase vase : world.getVases()) {
            if (!vase.isBroken()) {
                return false;
            }
        }
        return true;
    }
}

package game.world.minigame.beghouled;

import app.enums.plant.PlantName;
import game.component.physic.PhysicService;
import game.engine.GameSimulationEngine;
import game.engine.system.sun.SunSimulationSystem;
import game.world.GameWorld;
import game.world.levelConditions.conditions.BeghouledCondition;
import game.world.minigame.Minigame;
import game.world.minigame.MinigameLevelAnnouncer;
import game.world.minigame.MultiLevelMinigame;
import game.world.playground.Cell;
import game.world.playground.Grid;
import graphics.GraphicsEngine;

import java.util.List;
import java.util.Random;

public class BeghouledMode implements Minigame, MultiLevelMinigame {
    private final BeghouledLevelConfig levelConfig;
    private final Random random = new Random();
    private BeghouledWorld world;

    public BeghouledMode() {
        this(BeghouledLevelConfig.forLevel(1));
    }

    public BeghouledMode(int level) {
        this(BeghouledLevelConfig.forLevel(level));
    }

    public BeghouledMode(BeghouledLevelConfig levelConfig) {
        this.levelConfig = levelConfig != null ? levelConfig : BeghouledLevelConfig.forLevel(1);
    }

    public BeghouledLevelConfig getLevelConfig() {
        return levelConfig;
    }

    public int getComboTarget() {
        return levelConfig.comboTarget();
    }

    @Override
    public int getLevel() {
        return levelConfig.level();
    }

    @Override
    public void onMinigameStart(GameWorld world, GraphicsEngine graphicsEngine, PhysicService physicService) {
        world.setLevelCondition(new BeghouledCondition());
        world.setSunAmount(0);
        MinigameLevelAnnouncer.announce(world, getLevel(), getTotalLevels());
        if (!(world instanceof BeghouledWorld beghouledWorld)) {
            return;
        }
        this.world = beghouledWorld;
        fillBoard(beghouledWorld, graphicsEngine);
    }

    private void fillBoard(BeghouledWorld world, GraphicsEngine graphicsEngine) {
        Grid grid = world.getGrid();
        if (grid == null) {
            return;
        }
        clearBoardPlants(world, grid);
        BeghouledBoard layout = BeghouledMatcher.fillWithoutMatches(
            grid.getRowCount(),
            grid.getColCount(),
            BeghouledPool.BOARD_TYPES,
            random
        );
        for (int row = 0; row < grid.getRowCount(); row++) {
            for (int col = 0; col < grid.getColCount(); col++) {
                Cell cell = grid.getCell(row, col);
                if (cell == null || cell.hasObstacle() || layout.isCrater(row, col)) {
                    continue;
                }
                PlantName name = layout.get(row, col);
                if (name != null) {
                    BeghouledPlantOps.spawn(name, cell, world, graphicsEngine);
                }
            }
        }
    }

    private static void clearBoardPlants(BeghouledWorld world, Grid grid) {
        for (int row = 0; row < grid.getRowCount(); row++) {
            for (int col = 0; col < grid.getColCount(); col++) {
                Cell cell = grid.getCell(row, col);
                if (cell == null || cell.hasObstacle()) {
                    continue;
                }
                for (var plant : new java.util.ArrayList<>(cell.getPlants())) {
                    if (plant != null) {
                        cell.removePlant(plant);
                        world.getPlants().remove(plant);
                    }
                }
            }
        }
    }

    @Override
    public void onTick(GameWorld world, long currentTick) {
    }

    @Override
    public void onEngineStart(GameSimulationEngine engine) {
        engine.removeSystem(SunSimulationSystem.class);
    }

    @Override
    public void setWorld(GameWorld world) {
        if (world instanceof BeghouledWorld beghouledWorld) {
            this.world = beghouledWorld;
        }
    }

    @Override
    public List<PlantName> getPlants() {
        return BeghouledUpgradeCatalog.availablePlantsForLevel(getLevel());
    }

    @Override
    public List<String> getAvailableZombies() {
        return List.of();
    }
}

package game.engine.system.beghouled;

import app.enums.plant.PlantName;
import app.model.App;
import game.engine.time.TickSystem;
import game.entity.plant.Plant;
import game.service.plant.PlantService;
import game.world.GameWorld;
import game.world.minigame.beghouled.BeghouledBoard;
import game.world.minigame.beghouled.BeghouledCell;
import game.world.minigame.beghouled.BeghouledMatch;
import game.world.minigame.beghouled.BeghouledMatcher;
import game.world.minigame.beghouled.BeghouledPlantOps;
import game.world.minigame.beghouled.BeghouledPool;
import game.world.minigame.beghouled.BeghouledScore;
import game.world.minigame.beghouled.BeghouledScorer;
import game.world.minigame.beghouled.BeghouledWorld;
import game.world.playground.Cell;
import game.world.playground.Grid;
import graphics.GraphicsEngine;

import java.util.List;
import java.util.Random;

public class BeghouledSimulationSystem implements TickSystem {
    private final GameWorld world;
    private final PlantService plantService;
    private final Random random = new Random();

    public BeghouledSimulationSystem(GameWorld world) {
        this.world = world;
        this.plantService = new PlantService(world);
    }

    @Override
    public void update(long currentTick) {
        if (!(world instanceof BeghouledWorld beghouled) || world.getGrid() == null) {
            return;
        }
        if (beghouled.getBoardState() == BeghouledWorld.BoardState.ANIMATING
            || beghouled.hasActiveSlides()) {
            return;
        }
        if (beghouled.getBoardState() != BeghouledWorld.BoardState.RESOLVING) {
            return;
        }
        resolveStep(beghouled);
    }

    public void beginResolve(BeghouledWorld beghouled) {
        beghouled.setBoardState(BeghouledWorld.BoardState.RESOLVING);
        beghouled.setCascadeDepth(0);
        beghouled.setSelectedCell(null);
        beghouled.setPendingResolve(false);
        resolveStep(beghouled);
    }

    private void resolveStep(BeghouledWorld beghouled) {
        BeghouledBoard snapshot = snapshot(beghouled);
        List<BeghouledMatch> matches = BeghouledMatcher.findAllMatches(snapshot);
        if (matches.isEmpty()) {
            if (!BeghouledMatcher.hasAnyValidMove(snapshot)) {
                shuffleLiveBoard(beghouled);
                if (beghouled.hasActiveSlides()) {
                    return;
                }
            }
            snapshot = snapshot(beghouled);
            if (BeghouledMatcher.findAllMatches(snapshot).isEmpty()) {
                beghouled.setBoardState(BeghouledWorld.BoardState.IDLE);
                beghouled.setCascadeDepth(0);
            }
            return;
        }
        destroyMatches(beghouled, matches);
        boolean cascade = beghouled.getCascadeDepth() > 0;
        BeghouledScore score = BeghouledScorer.score(matches, cascade);
        world.addSunAmount(score.sun());
        beghouled.addCombos(score.combos());
        compactLiveBoard(beghouled);
        refillLiveBoard(beghouled);
        beghouled.setCascadeDepth(beghouled.getCascadeDepth() + 1);
        if (!beghouled.hasActiveSlides()) {
            beghouled.setBoardState(BeghouledWorld.BoardState.RESOLVING);
        }
    }

    private BeghouledBoard snapshot(BeghouledWorld beghouled) {
        Grid grid = beghouled.getGrid();
        BeghouledBoard board = new BeghouledBoard(grid.getRowCount(), grid.getColCount());
        for (int row = 0; row < grid.getRowCount(); row++) {
            for (int col = 0; col < grid.getColCount(); col++) {
                Cell cell = grid.getCell(row, col);
                if (cell == null) {
                    continue;
                }
                if (isBlocked(cell)) {
                    board.setCrater(row, col, true);
                    continue;
                }
                Plant plant = livingPlant(cell);
                if (plant != null && plant.getDefinition() != null) {
                    board.set(row, col, plant.getDefinition().getName());
                }
            }
        }
        return board;
    }

    private void destroyMatches(BeghouledWorld beghouled, List<BeghouledMatch> matches) {
        Grid grid = beghouled.getGrid();
        for (BeghouledMatch match : matches) {
            for (BeghouledCell cell : match.cells()) {
                Cell worldCell = grid.getCell(cell.row(), cell.col());
                Plant plant = livingPlant(worldCell);
                if (plant != null) {
                    plantService.removePlant(plant);
                }
                if (worldCell != null) {
                    for (Plant extra : new java.util.ArrayList<>(worldCell.getPlants())) {
                        if (extra != null && !extra.isDead() && extra != plant) {
                            plantService.removePlant(extra);
                        }
                    }
                }
            }
        }
    }

    private void compactLiveBoard(BeghouledWorld beghouled) {
        Grid grid = beghouled.getGrid();
        for (int col = 0; col < grid.getColCount(); col++) {
            java.util.ArrayList<Plant> falling = new java.util.ArrayList<>();
            for (int row = 0; row < grid.getRowCount(); row++) {
                Cell cell = grid.getCell(row, col);
                if (isBlocked(cell)) {
                    continue;
                }
                for (Plant plant : new java.util.ArrayList<>(cell.getPlants())) {
                    if (plant != null && !plant.isDead()) {
                        falling.add(plant);
                    }
                }
            }
            int index = 0;
            for (int row = 0; row < grid.getRowCount() && index < falling.size(); row++) {
                Cell cell = grid.getCell(row, col);
                if (isBlocked(cell)) {
                    continue;
                }
                Plant plant = falling.get(index++);
                float startX = (float) plant.getPosition().getX();
                float startY = (float) plant.getPosition().getY();
                BeghouledPlantOps.slideTo(plant, cell, beghouled, startX, startY);
            }
        }
    }

    private void refillLiveBoard(BeghouledWorld beghouled) {
        Grid grid = beghouled.getGrid();
        GraphicsEngine graphics = graphics();
        float cellHeight = grid.getConfig() != null ? grid.getConfig().getCellHeight() : 80f;
        for (int row = 0; row < grid.getRowCount(); row++) {
            for (int col = 0; col < grid.getColCount(); col++) {
                Cell cell = grid.getCell(row, col);
                if (cell == null || isBlocked(cell) || livingPlant(cell) != null) {
                    continue;
                }
                PlantName name = BeghouledPool.BOARD_TYPES.get(random.nextInt(BeghouledPool.BOARD_TYPES.size()));
                Plant plant = BeghouledPlantOps.spawn(name, cell, beghouled, graphics);
                if (plant == null) {
                    continue;
                }
                float startX = cell.getWorldX();
                float startY = cell.getWorldY() + cellHeight;
                BeghouledPlantOps.slideTo(plant, cell, beghouled, startX, startY);
            }
        }
    }

    private void shuffleLiveBoard(BeghouledWorld beghouled) {
        BeghouledBoard snapshot = snapshot(beghouled);
        BeghouledMatcher.shuffleNonCraterPlants(snapshot, random);
        Grid grid = beghouled.getGrid();
        GraphicsEngine graphics = graphics();
        for (int row = 0; row < grid.getRowCount(); row++) {
            for (int col = 0; col < grid.getColCount(); col++) {
                Cell cell = grid.getCell(row, col);
                if (cell == null || isBlocked(cell)) {
                    continue;
                }
                PlantName desired = snapshot.get(row, col);
                Plant plant = livingPlant(cell);
                PlantName current = plant == null || plant.getDefinition() == null
                    ? null
                    : plant.getDefinition().getName();
                if (desired == current) {
                    continue;
                }
                if (plant != null) {
                    plantService.removePlant(plant);
                }
                if (desired != null) {
                    BeghouledPlantOps.spawn(desired, cell, beghouled, graphics);
                }
            }
        }
    }

    private static Plant livingPlant(Cell cell) {
        if (cell == null) {
            return null;
        }
        Plant plant = cell.getDamageTarget();
        if (plant == null || plant.isDead()) {
            return null;
        }
        return plant;
    }

    private static boolean isBlocked(Cell cell) {
        return cell == null || cell.hasObstacle();
    }

    private static GraphicsEngine graphics() {
        if (App.getRealTimeEngine() == null) {
            return null;
        }
        return App.getRealTimeEngine().getGraphicsEngine();
    }
}

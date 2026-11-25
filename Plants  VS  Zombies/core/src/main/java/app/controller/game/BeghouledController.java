package app.controller.game;

import app.controller.game.interaction.InteractionType;
import app.enums.plant.PlantName;
import app.model.App;
import app.view.game.PlayScreen;
import game.audio.AudioId;
import game.audio.AudioManager;
import game.engine.system.beghouled.BeghouledSimulationSystem;
import game.entity.plant.Plant;
import game.service.plant.PlantService;
import game.world.minigame.beghouled.BeghouledBoard;
import game.world.minigame.beghouled.BeghouledMatcher;
import game.world.minigame.beghouled.BeghouledPlantOps;
import game.world.minigame.beghouled.BeghouledMode;
import game.world.minigame.beghouled.BeghouledUpgradeCatalog;
import game.world.minigame.beghouled.BeghouledWorld;
import game.world.playground.Cell;
import graphics.GraphicsEngine;

public class BeghouledController extends GameController {
    public BeghouledController(PlayScreen screen) {
        super(screen);
        hud.setForwardListener(this);
    }

    public BeghouledController() {
        super();
    }

    @Override
    public void initialize(PlayScreen screen) {
        super.initialize(screen);
        hud.setForwardListener(this);
    }

    @Override
    protected boolean handleGameplayClick(float worldX, float worldY) {
        if (!(world instanceof BeghouledWorld beghouled) || world.getGrid() == null) {
            return false;
        }
        Cell cell = world.getGrid().getCellAt(worldX, worldY);
        if (interaction.is(InteractionType.UPGRADE)) {
            return handleUpgradeClick(beghouled, cell);
        }
        return handleSwapClick(beghouled, cell);
    }

    private boolean handleSwapClick(BeghouledWorld beghouled, Cell cell) {
        if (beghouled.isBoardLocked()) {
            fail(EventResult.BEGHOULED_BOARD_BUSY);
            return true;
        }
        if (cell == null) {
            beghouled.setSelectedCell(null);
            return false;
        }
        if (cell.hasObstacle()) {
            fail(EventResult.BEGHOULED_CRATER);
            beghouled.setSelectedCell(null);
            return true;
        }
        Cell selected = beghouled.getSelectedCell();
        if (selected == null || selected == cell) {
            if (livingPlant(cell) == null) {
                fail(EventResult.BEGHOULED_NO_PLANT);
                return true;
            }
            beghouled.setSelectedCell(cell);
            return true;
        }
        if (!adjacent(selected, cell)) {
            fail(EventResult.BEGHOULED_NOT_ADJACENT);
            beghouled.setSelectedCell(cell);
            return true;
        }
        if (selected.hasObstacle() || livingPlant(selected) == null || livingPlant(cell) == null) {
            fail(EventResult.BEGHOULED_CRATER);
            beghouled.setSelectedCell(null);
            return true;
        }
        BeghouledBoard snapshot = snapshot(beghouled);
        if (!BeghouledMatcher.wouldSwapCreateMatch(
            snapshot,
            selected.getRow(),
            selected.getCol(),
            cell.getRow(),
            cell.getCol()
        )) {
            fail(EventResult.BEGHOULED_INVALID_SWAP);
            beghouled.setSelectedCell(null);
            return true;
        }
        BeghouledPlantOps.swap(livingPlant(selected), livingPlant(cell), beghouled);
        beghouled.setSelectedCell(null);
        beghouled.setPendingResolve(true);
        if (beghouled.hasActiveSlides()) {
            return true;
        }
        if (App.getCurrentGameEngine() != null) {
            BeghouledSimulationSystem system = App.getCurrentGameEngine().findSystem(BeghouledSimulationSystem.class);
            if (system != null) {
                system.beginResolve(beghouled);
            }
        }
        return true;
    }

    private boolean handleUpgradeClick(BeghouledWorld beghouled, Cell cell) {
        if (beghouled.isBoardLocked()) {
            fail(EventResult.BEGHOULED_BOARD_BUSY);
            return true;
        }
        Plant plant = livingPlant(cell);
        if (plant == null || plant.getDefinition() == null) {
            fail(EventResult.BEGHOULED_NO_PLANT);
            return true;
        }
        if (cell.hasObstacle()) {
            fail(EventResult.BEGHOULED_CRATER);
            return true;
        }
        PlantName selectedUpgrade = interaction.selectedPlant();
        int level = beghouledLevel(beghouled);
        var upgrade = BeghouledUpgradeCatalog.findForLevel(level, plant.getDefinition().getName(), selectedUpgrade);
        if (upgrade.isEmpty()) {
            fail(EventResult.BEGHOULED_UPGRADE_INVALID);
            return true;
        }
        if (world.getSunAmount() < upgrade.get().cost()) {
            fail(EventResult.NOT_ENOUGH_SUN);
            return true;
        }
        world.removeSunAmount(upgrade.get().cost());
        new PlantService(world).removePlant(plant);
        GraphicsEngine graphics = App.getRealTimeEngine() == null ? null : App.getRealTimeEngine().getGraphicsEngine();
        BeghouledPlantOps.spawn(upgrade.get().to(), cell, world, graphics);
        cancelActivePlacement();
        return true;
    }

    @Override
    public void onPlantCardSelected(PlantName plantName) {
        if (plantName == null || interaction.blocksSeedSelection()) {
            return;
        }
        if (world instanceof BeghouledWorld beghouled && beghouled.isBoardLocked()) {
            fail(EventResult.BEGHOULED_BOARD_BUSY);
            return;
        }
        interaction.beginUpgrade(plantName);
    }

    @Override
    public void onPlantCardDeselected(PlantName plantName) {
        if (interaction.is(InteractionType.UPGRADE) && interaction.selectedPlant() == plantName) {
            cancelActivePlacement();
        }
    }

    @Override
    public void onShovelToggled() {
    }

    @Override
    public void onPlantFoodBankClicked() {
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if (!(world instanceof BeghouledWorld beghouled) || hud == null) {
            return;
        }
        beghouled.advanceSlides(delta);
        if (world.getCurrentChapter() != null
            && world.getCurrentChapter().getMinigameLogic() instanceof BeghouledMode mode) {
            hud.updateWaveDisplay(mode.getLevel(), mode.getTotalLevels());
        }
    }

    private static int beghouledLevel(BeghouledWorld beghouled) {
        if (beghouled.getCurrentChapter() != null
            && beghouled.getCurrentChapter().getMinigameLogic() instanceof BeghouledMode mode) {
            return mode.getLevel();
        }
        return 1;
    }

    private BeghouledBoard snapshot(BeghouledWorld beghouled) {
        var grid = beghouled.getGrid();
        BeghouledBoard board = new BeghouledBoard(grid.getRowCount(), grid.getColCount());
        for (int row = 0; row < grid.getRowCount(); row++) {
            for (int col = 0; col < grid.getColCount(); col++) {
                Cell worldCell = grid.getCell(row, col);
                if (worldCell == null || worldCell.hasObstacle()) {
                    board.setCrater(row, col, true);
                    continue;
                }
                Plant plant = livingPlant(worldCell);
                if (plant != null && plant.getDefinition() != null) {
                    board.set(row, col, plant.getDefinition().getName());
                }
            }
        }
        return board;
    }

    private static Plant livingPlant(Cell cell) {
        if (cell == null) {
            return null;
        }
        Plant plant = cell.getDamageTarget();
        return plant == null || plant.isDead() ? null : plant;
    }

    private static boolean adjacent(Cell first, Cell second) {
        return Math.abs(first.getRow() - second.getRow()) + Math.abs(first.getCol() - second.getCol()) == 1;
    }

    private void fail(EventResult result) {
        if (hud == null || result == null) {
            return;
        }
        AudioManager.play(AudioId.UI_ERROR);
        hud.showErrorPopup(result.getMessage());
    }
}

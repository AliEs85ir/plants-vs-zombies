package app.controller.game;

import app.controller.game.interaction.HoverFeedback;
import app.controller.game.interaction.InteractionType;
import app.controller.game.interaction.hover.PlantingHoverHandler;
import app.controller.game.interaction.hover.ZombiePlacementHoverHandler;
import app.enums.plant.PlantName;
import app.view.game.PlayScreen;
import app.view.hud.IZombieCouchPlayHud;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import game.audio.AudioId;
import game.audio.AudioManager;
import game.engine.pause.PauseReason;
import game.service.Zombie.ZombiePlacementService;
import game.service.plant.IZombiePlantPlacementService;
import game.service.plant.PlantPlacementService;
import game.world.minigame.izombie.IZombieWorld;
import game.world.playground.Cell;
import game.world.playground.Grid;

public class IZombieCouchPlayController extends GameController {
    private static final Color ZOMBIE_VALID = new Color(0.45f, 0.92f, 0.38f, 1f);
    private static final Color ZOMBIE_INVALID = new Color(1f, 0.32f, 0.22f, 1f);

    private PlantPlacementService plantPlacement;
    private PlantingHoverHandler plantingHoverHandler;
    private ZombiePlacementService zombiePlacement;
    private ZombiePlacementHoverHandler zombieHoverHandler;
    private String selectedZombieAlias;
    private int cursorRow;
    private int cursorCol;
    private boolean cursorReady;

    public IZombieCouchPlayController() {
        super();
    }

    @Override
    public void initialize(PlayScreen screen) {
        super.initialize(screen);
        bindPlacement();
        hud.setForwardListener(this);
        initializeCursor();
    }

    private void bindPlacement() {
        this.plantPlacement = new IZombiePlantPlacementService(screen);
        this.plantingHoverHandler = new PlantingHoverHandler(plantPlacement);
        this.zombiePlacement = new ZombiePlacementService((IZombieWorld) world, engine);
        this.zombieHoverHandler = new ZombiePlacementHoverHandler(zombiePlacement);
    }

    private void initializeCursor() {
        if (world == null || world.getGrid() == null) {
            return;
        }
        Grid grid = world.getGrid();
        cursorRow = Math.max(0, grid.getRowCount() / 2);
        cursorCol = Math.max(0, grid.getColCount() - 1);
        cursorReady = true;
    }

    @Override
    protected boolean handleGameplayClick(float worldX, float worldY) {
        return tryCollectSun(worldX, worldY);
    }

    public void cancelPlacement() {
        if (interaction.is(InteractionType.SHOVEL) || interaction.is(InteractionType.PLANT_FOOD)) {
            return;
        }
        interaction.clear();
        hoveredCell = null;
        selectedZombieAlias = null;
        if (hud instanceof IZombieCouchPlayHud couchHud) {
            couchHud.deselectAllCards();
            couchHud.deselectZombieCards();
        } else {
            hud.deselectAllCards();
        }
    }

    @Override
    public void onPlantCardSelected(PlantName plantName) {
        if (plantName == null || interaction.blocksSeedSelection()) {
            return;
        }
        plantingHoverHandler.setPlantName(plantName);
        interaction.beginPlanting(plantName, plantingHoverHandler);
    }

    @Override
    public void onPlantCardDeselected(PlantName plantName) {
        if (interaction.selectedPlant() == plantName) {
            interaction.clear();
            hoveredCell = null;
            if (hud instanceof IZombieCouchPlayHud couchHud && couchHud.getPlantCardBar() != null) {
                couchHud.getPlantCardBar().deselectAll();
            }
        }
    }

    @Override
    public void onZombieCardSelected(String alias) {
        if (alias == null || interaction.blocksSeedSelection()) {
            return;
        }
        selectedZombieAlias = alias;
        zombieHoverHandler.setAlias(alias);
        if (!cursorReady) {
            initializeCursor();
        }
    }

    @Override
    public void onZombieCardDeselected(String alias) {
        if (alias != null && alias.equals(selectedZombieAlias)) {
            selectedZombieAlias = null;
            zombieHoverHandler.setAlias(null);
        }
    }

    @Override
    public Cell getKeyboardHoveredCell() {
        if (selectedZombieAlias == null || !cursorReady || world == null || world.getGrid() == null) {
            return null;
        }
        return world.getGrid().getCell(cursorRow, cursorCol);
    }

    @Override
    public HoverFeedback evaluateKeyboardHover() {
        Cell cell = getKeyboardHoveredCell();
        if (cell == null || zombieHoverHandler == null) {
            return HoverFeedback.none();
        }
        HoverFeedback feedback = zombieHoverHandler.evaluate(cell);
        if (!feedback.shouldHighlight()) {
            return HoverFeedback.none();
        }
        return HoverFeedback.cell(feedback.isValid(), ZOMBIE_VALID, ZOMBIE_INVALID, feedback.message());
    }

    @Override
    protected boolean handleKeyDown(int keycode) {
        if (engine != null && engine.isPaused() && !engine.isPausedBy(PauseReason.DIALOG)) {
            return false;
        }
        if (interaction.blocksSeedSelection()) {
            return false;
        }
        if (handleZombieCardHotkey(keycode)) {
            return true;
        }
        return switch (keycode) {
            case Input.Keys.UP, Input.Keys.W -> moveCursor(0, 1);
            case Input.Keys.DOWN, Input.Keys.S -> moveCursor(0, -1);
            case Input.Keys.LEFT, Input.Keys.A -> moveCursor(-1, 0);
            case Input.Keys.RIGHT, Input.Keys.D -> moveCursor(1, 0);
            case Input.Keys.Q -> cycleZombieCard(-1);
            case Input.Keys.E -> cycleZombieCard(1);
            case Input.Keys.ENTER, Input.Keys.SPACE -> placeSelectedZombie();
            case Input.Keys.ESCAPE, Input.Keys.X -> {
                cancelZombieSelection();
                yield true;
            }
            default -> false;
        };
    }

    private boolean handleZombieCardHotkey(int keycode) {
        int index = switch (keycode) {
            case Input.Keys.NUM_1, Input.Keys.NUMPAD_1 -> 0;
            case Input.Keys.NUM_2, Input.Keys.NUMPAD_2 -> 1;
            case Input.Keys.NUM_3, Input.Keys.NUMPAD_3 -> 2;
            case Input.Keys.NUM_4, Input.Keys.NUMPAD_4 -> 3;
            case Input.Keys.NUM_5, Input.Keys.NUMPAD_5 -> 4;
            case Input.Keys.NUM_6, Input.Keys.NUMPAD_6 -> 5;
            case Input.Keys.NUM_7, Input.Keys.NUMPAD_7 -> 6;
            case Input.Keys.NUM_8, Input.Keys.NUMPAD_8 -> 7;
            default -> -1;
        };
        if (index < 0) {
            return false;
        }
        if (!(hud instanceof IZombieCouchPlayHud couchHud)) {
            return false;
        }
        if (index >= couchHud.getZombieCardCount()) {
            return true;
        }
        couchHud.activateZombieCardAt(index);
        return true;
    }

    private boolean cycleZombieCard(int delta) {
        if (!(hud instanceof IZombieCouchPlayHud couchHud)) {
            return false;
        }
        int count = couchHud.getZombieCardCount();
        if (count <= 0) {
            return true;
        }
        int current = couchHud.getSelectedZombieIndex();
        int next = current < 0 ? 0 : Math.floorMod(current + delta, count);
        couchHud.activateZombieCardAt(next);
        return true;
    }

    private boolean moveCursor(int colDelta, int rowDelta) {
        if (world == null || world.getGrid() == null) {
            return true;
        }
        if (!cursorReady) {
            initializeCursor();
        }
        Grid grid = world.getGrid();
        cursorCol = Math.max(0, Math.min(grid.getColCount() - 1, cursorCol + colDelta));
        cursorRow = Math.max(0, Math.min(grid.getRowCount() - 1, cursorRow + rowDelta));
        return true;
    }

    private boolean placeSelectedZombie() {
        if (selectedZombieAlias == null) {
            return true;
        }
        Cell cell = getKeyboardHoveredCell();
        EventResult result = zombiePlacement.placeZombie(selectedZombieAlias, cell);
        if (result == null) {
            return true;
        }
        if (result.isSuccess()) {
            cancelZombieSelection();
            return true;
        }
        AudioManager.play(AudioId.UI_ERROR);
        if (hud != null) {
            hud.showErrorPopup(result.getMessage());
        }
        return true;
    }

    private void cancelZombieSelection() {
        selectedZombieAlias = null;
        zombieHoverHandler.setAlias(null);
        if (hud instanceof IZombieCouchPlayHud couchHud) {
            couchHud.deselectZombieCards();
        }
    }
}

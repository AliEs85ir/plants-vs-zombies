package app.controller.game;

import app.controller.game.interaction.HoverFeedback;
import app.controller.game.interaction.InteractionController;
import app.controller.game.interaction.InteractionType;
import app.controller.game.interaction.hover.PlantFoodHoverHandler;
import app.controller.game.interaction.hover.ShovelHoverHandler;
import app.enums.plant.PlantName;
import app.model.App;
import app.view.cursor.GameCursor;
import app.view.game.PlayScreen;
import app.view.hud.Hud;
import app.view.hud.notification.GameplayNotifications;
import app.view.input.PointerCoordinates;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import game.audio.AudioId;
import game.audio.AudioManager;
import game.engine.RealTimeGameEngine;
import game.service.plant.PlantFoodService;
import game.service.plant.PlantService;
import game.service.plant.ShovelService;
import game.service.drop.DropCollector;
import game.service.sun.SunCollector;
import game.world.GameWorld;
import game.world.playground.Cell;

public abstract class GameController implements WorldListener {
    protected PlayScreen screen;
    protected GameWorld world;
    protected RealTimeGameEngine engine;
    protected Hud hud;
    protected SunCollector sunCollector;
    protected DropCollector dropCollector;
    protected Cell hoveredCell;
    protected InteractionController interaction;
    protected ShovelService shovelService;
    protected ShovelHoverHandler shovelHoverHandler;
    protected PlantFoodService plantFoodService;
    protected PlantFoodHoverHandler plantFoodHoverHandler;
    protected GameCursor gameCursor;
    private PointerCoordinates pointer;
    private boolean pointerPressed;
    private final Vector3 worldPointer = new Vector3();
    private long shovelToggleFrame = -1;
    private long plantFoodToggleFrame = -1;

    public GameController(PlayScreen screen) {
        this.screen = screen;
        this.world = screen.getEngine().getWorld();
        this.engine = screen.getEngine();
        this.hud = screen.getHud();
        this.sunCollector = new SunCollector(world);
        this.dropCollector = new DropCollector(world);
        bindInteraction();
    }

    public GameController() {}

    public void initialize(PlayScreen screen) {
        this.screen = screen;
        this.world = screen.getEngine().getWorld();
        this.engine = screen.getEngine();
        this.hud = screen.getHud();
        this.sunCollector = new SunCollector(world);
        this.dropCollector = new DropCollector(world);
        bindInteraction();
    }

    private void bindInteraction() {
        this.gameCursor = screen.getGameCursor();
        this.pointer = screen.getPointerCoordinates();
        this.interaction = new InteractionController();
        this.shovelService = new ShovelService(new PlantService(world));
        this.shovelHoverHandler = new ShovelHoverHandler(shovelService);
        this.plantFoodService = new PlantFoodService(world);
        this.plantFoodHoverHandler = new PlantFoodHoverHandler(plantFoodService);
    }

    public void update(float delta) {
        if (!screen.isGameplayInputBlocked()) {
            PointerCoordinates coordinates = pointer();
            if (coordinates != null && !interaction.blocksUnrelatedWorldActions()) {
                collectSunAtMouse(
                    coordinates.screenX(),
                    coordinates.screenY()
                );
            }
            syncCursor();
        }
        if (gameCursor != null) {
            gameCursor.act(delta);
        }
        hud.update(delta);
    }

    public InputProcessor createGameInputProcessor() {
        return new InputAdapter() {
            @Override
            public boolean touchDown(
                int screenX,
                int screenY,
                int pointer,
                int button
            ) {
                pointerPressed = true;
                if (screen.isGameplayInputBlocked()) {
                    return true;
                }
                Actor hudHit = hitHud(screenX, screenY);
                if (hudHit != null) {
                    if (hud.isShovelActor(hudHit)) {
                        onShovelToggled();
                    } else if (hud.isPlantFoodBankActor(hudHit)) {
                        onPlantFoodBankClicked();
                    } else if (hud.isPauseActor(hudHit)) {
                        hud.requestPause();
                    }
                    syncCursor();
                    return true;
                }
                Vector3 position = screenToWorld(screenX, screenY);
                boolean handled = handleWorldClick(position.x, position.y);
                if(!handled) {
                    App.getCameraController().registerClick(screenX, screenY);
                    handled = true;
                }
                syncCursor();
                return handled;
            }

            @Override
            public boolean touchUp(
                int screenX,
                int screenY,
                int pointer,
                int button
            ) {
                pointerPressed = false;
                syncCursor();
                return false;
            }

            @Override
            public boolean mouseMoved(
                int screenX,
                int screenY
            ) {
                if (screen.isGameplayInputBlocked()) {
                    return false;
                }
                if (!interaction.blocksUnrelatedWorldActions()) {
                    collectSunAtMouse(screenX, screenY);
                }
                updateHoveredCell(screenX, screenY);
                syncCursor();
                return false;
            }

            @Override
            public boolean touchDragged(
                int screenX,
                int screenY,
                int pointer
            ) {
                if (screen.isGameplayInputBlocked()) {
                    return false;
                }
                if (!interaction.blocksUnrelatedWorldActions()) {
                    collectSunAtMouse(screenX, screenY);
                }
                updateHoveredCell(screenX, screenY);
                syncCursor();
                return false;
            }

            @Override
            public boolean keyDown(int keycode) {
                if (screen.isGameplayInputBlocked()) {
                    return false;
                }
                return handleKeyDown(keycode);
            }
        };
    }

    protected boolean handleKeyDown(int keycode) {
        return false;
    }

    protected boolean tryCollectSun(float worldX, float worldY) {
        return sunCollector.collect(
            new Vector2(worldX, worldY)
        ) != null;
    }

    protected boolean tryCollectDrop(float worldX, float worldY) {
        if (dropCollector == null) {
            return false;
        }
        return dropCollector.collect(new Vector2(worldX, worldY)) != null;
    }


    private void updateHoveredCell(
        int screenX,
        int screenY
    ) {
        if (hitHud(screenX, screenY) != null) {
            hoveredCell = null;
            return;
        }
        Vector3 position =
            screenToWorld(screenX, screenY);

        hoveredCell =
            world.getMap()
                .getGrid()
                .getCellAt(position.x, position.y);
    }


    private void collectSunAtMouse(
        int screenX,
        int screenY
    ) {
        Vector3 position =
            screenToWorld(screenX, screenY);

        sunCollector.collect(
            new Vector2(
                position.x,
                position.y
            )
        );
    }

    public final boolean handleWorldClick(float worldX, float worldY) {
        if (hud != null && hud.trySelectWorldPlantCard(worldX, worldY)) {
            return true;
        }
        if (tryCollectDrop(worldX, worldY)) {
            return true;
        }
        if (interaction.hasCellHover()) {
            return handleCellInteraction(worldX, worldY);
        }
        return handleGameplayClick(worldX, worldY);
    }

    protected abstract boolean handleGameplayClick(float worldX, float worldY);

    private boolean handleCellInteraction(float worldX, float worldY) {
        Cell targetCell = world.getMap().getGrid().getCellAt(worldX, worldY);
        EventResult result = interaction.execute(targetCell);
        if (result == null) {
            return handleGameplayClick(worldX, worldY);
        }
        if (result.isSuccess()) {
            if (interaction.endsAfterSuccess()) {
                endCurrentInteraction();
            }
            return true;
        }
        showInteractionFailure(result);
        if (interaction.endsAfterFailure()) {
            endCurrentInteraction();
        }
        return true;
    }

    private void showInteractionFailure(EventResult result) {
        if (hud == null || result == null) {
            return;
        }
        AudioManager.play(AudioId.UI_ERROR);
        if (result == EventResult.SHOVEL_NO_TARGET) {
            hud.showNotification(GameplayNotifications.shovelNoTarget());
            return;
        }
        if (result == EventResult.SHOVEL_NOT_ALLOWED) {
            hud.showNotification(GameplayNotifications.shovelNotAllowed());
            return;
        }
        hud.showErrorPopup(result.getMessage());
    }

    @Override
    public void onShovelToggled() {
        long frameId = Gdx.graphics.getFrameId();
        if (frameId == shovelToggleFrame) {
            return;
        }
        shovelToggleFrame = frameId;
        if (interaction.is(InteractionType.SHOVEL)) {
            cancelShovel();
            return;
        }
        startShovel();
    }

    @Override
    public void onPlantFoodBankClicked() {
        long frameId = Gdx.graphics.getFrameId();
        if (frameId == plantFoodToggleFrame) {
            return;
        }
        plantFoodToggleFrame = frameId;
        if (interaction.is(InteractionType.PLANT_FOOD)) {
            cancelPlantFood();
            return;
        }
        if (plantFoodService == null
            || plantFoodService.getInventory() == null
            || !plantFoodService.getInventory().hasPlantFood()) {
            AudioManager.play(AudioId.UI_ERROR);
            if (hud != null) {
                hud.showErrorPopup(EventResult.PLANT_FOOD_EMPTY.getMessage());
            }
            return;
        }
        startPlantFood();
    }

    protected void startPlantFood() {
        cancelActivePlacement();
        interaction.beginPlantFood(plantFoodHoverHandler);
        if (hud != null) {
            hud.setPlantFoodSelecting(true);
            hud.setSeedSelectionEnabled(false);
        }
        PointerCoordinates coordinates = pointer();
        if (coordinates != null) {
            updateHoveredCell(coordinates.screenX(), coordinates.screenY());
        }
        syncCursor();
    }

    protected void cancelPlantFood() {
        if (!interaction.is(InteractionType.PLANT_FOOD)) {
            return;
        }
        interaction.clear();
        hoveredCell = null;
        if (hud != null) {
            hud.setPlantFoodSelecting(false);
            hud.setSeedSelectionEnabled(true);
        }
        syncCursor();
    }

    protected void startShovel() {
        cancelActivePlacement();
        interaction.beginShovel(shovelHoverHandler);
        hud.setShovelActive(true);
        hud.setSeedSelectionEnabled(false);
        PointerCoordinates coordinates = pointer();
        if (coordinates != null) {
            updateHoveredCell(coordinates.screenX(), coordinates.screenY());
        }
        syncCursor();
    }

    protected void cancelShovel() {
        if (!interaction.is(InteractionType.SHOVEL)) {
            return;
        }
        interaction.clear();
        hoveredCell = null;
        hud.setShovelActive(false);
        hud.setSeedSelectionEnabled(true);
        syncCursor();
    }

    private void endCurrentInteraction() {
        if (interaction.is(InteractionType.SHOVEL)) {
            cancelShovel();
            return;
        }
        if (interaction.is(InteractionType.PLANT_FOOD)) {
            cancelPlantFood();
            return;
        }
        interaction.clear();
        hoveredCell = null;
        if (hud != null) {
            hud.deselectAllCards();
            hud.setSeedSelectionEnabled(true);
            hud.setPlantFoodSelecting(false);
        }
        syncCursor();
    }

    protected void cancelActivePlacement() {
        interaction.clear();
        hoveredCell = null;
        if (hud != null) {
            hud.deselectAllCards();
            hud.setShovelActive(false);
            hud.setSeedSelectionEnabled(true);
            hud.setPlantFoodSelecting(false);
        }
        syncCursor();
    }

    protected Vector3 screenToWorld(int screenX, int screenY) {
        PointerCoordinates coordinates = pointer();
        if (coordinates == null) {
            worldPointer.set(screenX, screenY, 0f);
            if (screen != null && screen.getViewport() != null) {
                screen.getViewport().unproject(worldPointer);
            }
            return worldPointer;
        }
        return coordinates.toWorld(screenX, screenY, worldPointer);
    }

    @Override
    public void onPlantCardSelected(PlantName plantName) {

    }

    @Override
    public void onPlantCardDeselected(PlantName plantName) {

    }

    @Override
    public void onZombieCardDeselected(String alias) {

    }

    @Override
    public void onZombieCardSelected(String alias) {

    }

    public Cell getHoveredCell() {
        return hoveredCell;
    }

    public HoverFeedback evaluateHover() {
        return interaction.evaluate(hoveredCell);
    }

    public Cell getKeyboardHoveredCell() {
        return null;
    }

    public HoverFeedback evaluateKeyboardHover() {
        return HoverFeedback.none();
    }

    public boolean isPlacingMode() {
        return interaction.hasCellHover();
    }

    public boolean canPlaceOnCell(Cell cell) {
        return interaction.evaluate(cell).isValid();
    }

    private void syncCursor() {
        if (gameCursor == null) {
            return;
        }
        gameCursor.setType(interaction.resolveCursor(pointerPressed, isHoveringInteractive()));
        if (interaction.is(InteractionType.PLANTING)
            || interaction.is(InteractionType.BOWLING)
            || interaction.is(InteractionType.ZOMBIE_PLACEMENT)) {
            float width = 80f;
            float height = 80f;
            if (world != null && world.getGrid() != null && world.getGrid().getConfig() != null) {
                width = world.getGrid().getConfig().getCellWidth();
                height = world.getGrid().getConfig().getCellHeight();
            }
            gameCursor.syncPlacement(
                interaction.selectedPlant(),
                interaction.selectedZombie(),
                width,
                height
            );
        } else {
            gameCursor.clearPlacement();
        }
    }

    private boolean isHoveringInteractive() {
        PointerCoordinates coordinates = pointer();
        if (coordinates != null && hitHud(coordinates.screenX(), coordinates.screenY()) != null) {
            return true;
        }
        return hoveredCell != null && hoveredCell.hasPlant();
    }

    private Actor hitHud(int screenX, int screenY) {
        if (hud == null || hud.getStage() == null || pointer() == null) {
            return null;
        }
        return pointer().hitHud(hud.getStage(), screenX, screenY, true);
    }

    private PointerCoordinates pointer() {
        if (pointer == null && screen != null) {
            pointer = screen.getPointerCoordinates();
        }
        return pointer;
    }
}

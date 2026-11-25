package app.view.game;

import app.controller.game.GameController;
import app.controller.game.ScoringGameController;
import app.controller.game.interaction.HoverFeedback;
import app.controller.game.izombie.IZombieMatchContext;
import app.controller.game.izombie.IZombieNetworkController;
import app.model.App;
import app.view.cursor.CursorRenderer;
import app.view.cursor.GameCursor;
import app.view.dialogue.DialogueOverlay;
import app.view.game.pause.PauseMenuController;
import app.view.game.pregame.PregameLayout;
import app.view.game.result.StageResultPresenter;
import app.view.game.states.ReadyState;
import app.view.hud.Hud;
import app.view.hud.VaseBreakerHud;
import app.view.input.PointerCoordinates;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

import game.audio.AudioCatalog;
import game.audio.AudioManager;
import game.component.AssetManager;
import game.component.physic.PhysicService;
import game.dialogue.DialogueManager;
import game.engine.RealTimeGameEngine;
import game.engine.pause.PauseReason;
import game.save.LevelSaveData;
import game.save.SaveService;
import game.settings.GameSettings;
import game.world.chapter.Chapter;
import game.world.chapter.rule.ChapterRule;
import game.world.chapter.rule.NpcDialogueRule;
import game.world.chapter.rule.scoringgame.ScoringSession;
import game.world.playground.Cell;
import game.world.playground.Grid;
import graphics.GraphicsEngine;
import graphics.map.hover.CellHoverRenderer;
import graphics.map.hover.GridOverlayRenderer;
import plants_vs_zombies.PlantsVSZombies;

public class PlayScreen implements Screen {


    private StateMachine<PlayScreen, State<PlayScreen>> stateMachine;

    private final PregameLayout pregameLayout = PregameLayout.defaults();

    private GameViewport          viewport;
    private GameCameraController  cameraController;
    private Stage                 stage;
    private OrthographicCamera    camera;
    private PlantsVSZombies       game;
    private RealTimeGameEngine    engine;
    private OrthogonalTiledMapRenderer renderer;
    private Rectangle             playgroundBounds;
    private GraphicsEngine        graphicsEngine;
    private Box2DDebugRenderer    b2dr;
    private InputMultiplexer      inputMultiplexer;
    private GameController controller;
    private Hud hud;
    private CellHoverRenderer     cellHoverRenderer;
    private GridOverlayRenderer   gridOverlayRenderer;
    private Chapter               chapter;
    private DialogueOverlay       dialogueOverlay;
    private DialogueManager       dialogueManager;
    private PauseMenuController   pauseMenuController;
    private StageResultPresenter  stageResultPresenter;
    private final GameCursor      gameCursor = new GameCursor();
    private final CursorRenderer  cursorRenderer = new CursorRenderer(gameCursor);
    private final PointerCoordinates pointerCoordinates = new PointerCoordinates();
    private static final ScreenShake     screenShake = new ScreenShake();
    private Cursor hiddenCursor;
    private int scoringSessionId;

    public PlayScreen(Chapter chapter) {
        if (chapter == null) return;
        this.chapter = chapter;

        game             = PlantsVSZombies.getPvz();
        renderer         = new OrthogonalTiledMapRenderer(chapter.getMap());
        playgroundBounds = extractPlaygroundBounds(chapter.getMap());
        hud              = chapter.getHud();
        cellHoverRenderer = new CellHoverRenderer();
        gridOverlayRenderer = new GridOverlayRenderer();

        initializeGraphics();
        initializeEngine(chapter);
        hud.initialize(engine);
        if (hud instanceof VaseBreakerHud vaseBreakerHud) {
            vaseBreakerHud.attachWorldStage(stage);
        }
        bindPointerSpaces();
        initializeDialogue();

        engine.pause(PauseReason.PRE_GAME);

        this.controller = chapter.getScreenController();
        this.controller.initialize(this);
        if (this.controller instanceof ScoringGameController scoring) {
            scoringSessionId = scoring.scoringSessionId();
        }

        pauseMenuController = new PauseMenuController(this);
        stageResultPresenter = new StageResultPresenter(this);
        hud.setPauseHandler(pauseMenuController::open);

        initializeInputHandling();

        b2dr = new Box2DDebugRenderer();

        LevelSaveData restore = SaveService.consumePendingRestore();
        if (restore != null) {
            SaveService.restore(this, restore);
            stateMachine = new DefaultStateMachine<>(this, new ReadyState(true));
            if (!engine.isRunning()) {
                engine.start();
            }
            engine.resume(PauseReason.PRE_GAME);
        } else {
            stateMachine = new DefaultStateMachine<>(this, chapter.getFirstState());
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(inputMultiplexer);
        hideSystemCursor();
        AudioManager.play(AudioCatalog.musicFor(chapter));
    }

    @Override
    public void render(float delta) {
        clearScreen();

        AssetManager.textureBank.update();
        if (!engine.isRunning() || !engine.isPaused()) {
            screenShake.update(delta);
        }
        cameraController.setShakeOffset(screenShake.offset().x, screenShake.offset().y);
        cameraController.update(delta);

        engine.update(delta);
        controller.update(delta);
        if (stageResultPresenter != null) {
            stageResultPresenter.update(delta);
        }
        if (dialogueManager != null) {
            dialogueManager.update(delta);
        }
        stateMachine.update();

        pointerCoordinates.applyWorld();
        renderer.setView(camera);
        renderer.render();

        renderHover(delta);
        renderPhysics();

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        graphicsEngine.updateAndRender(game.batch, graphicsDelta(delta));
        game.batch.end();

        stage.act(delta);
        stage.draw();

        pointerCoordinates.applyHud();
        hud.render();
        cursorRenderer.render(game.batch, pointerCoordinates);
    }

    public void pauseGameplay(PauseReason reason) {
        engine.pause(reason);
    }

    public void resumeGameplay(PauseReason reason) {
        engine.resume(reason);
    }

    private void clearScreen() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void renderHover(float delta) {
        Grid grid = engine.getWorld().getGrid();
        renderGridOverlay(grid);
        renderHoverTarget(delta, grid, controller.getHoveredCell(), controller.evaluateHover());
        renderHoverTarget(delta, grid, controller.getKeyboardHoveredCell(), controller.evaluateKeyboardHover());
    }

    private void renderGridOverlay(Grid grid) {
        if (!GameSettings.gameplay().gridOverlayEnabled() || grid == null) {
            return;
        }
        gridOverlayRenderer.render(camera, grid);
    }

    private void renderHoverTarget(float delta, Grid grid, Cell cell, HoverFeedback feedback) {
        if (feedback == null || !feedback.shouldHighlight() || cell == null) {
            return;
        }
        cellHoverRenderer.setHighlightColor(feedback.validColor());
        cellHoverRenderer.setInvalidColor(feedback.invalidColor());
        cellHoverRenderer.render(camera, grid, cell, feedback.isValid(), delta);
    }

    private void renderPhysics() {
        if (!GameSettings.gameplay().debugMode()) {
            return;
        }
        Gdx.gl.glLineWidth(2.5f);
//        b2dr.render(engine.getB2dWorld(),
//            camera.combined.cpy().scl(PhysicService.PIXEL_PER_METER));
        Gdx.gl.glLineWidth(1f);
    }

    @Override
    public void resize(int width, int height) {
        if (width <= 0 || height <= 0) return;
        viewport.update(width, height, false);
        cameraController.onResize();
        hud.resize(width, height);
        bindPointerSpaces();
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        restoreSystemCursor();
        AudioManager.stopGameplayAudio();
    }

    @Override
    public void dispose() {
        ScoringSession.finishGeneration(scoringSessionId);
        restoreSystemCursor();
        if (IZombieMatchContext.isActive()) {
            IZombieNetworkController controller = IZombieNetworkController.get();
            if (controller != null) {
                controller.leaveActiveMatch();
            } else {
                IZombieMatchContext.clear();
            }
        }
        if (dialogueManager != null) {
            dialogueManager.dispose();
            dialogueManager = null;
        }
        stage.dispose();
        b2dr.dispose();
        cellHoverRenderer.dispose();
        if (gridOverlayRenderer != null) {
            gridOverlayRenderer.dispose();
        }
        renderer.dispose();
    }

    public boolean isDialogueBlocking() {
        return dialogueManager != null && dialogueManager.isBlockingGameplayInput();
    }

    public boolean isPauseMenuOpen() {
        return pauseMenuController != null && pauseMenuController.isOpen();
    }

    public boolean isGameplayInputBlocked() {
        return isDialogueBlocking() || isPauseMenuOpen() || isResultOpen();
    }

    public boolean isResultOpen() {
        return stageResultPresenter != null && stageResultPresenter.isOpen();
    }

    private float graphicsDelta(float delta) {
        float speed = GameSettings.gameplay().gameSpeedMultiplier();
        if (!engine.isRunning() || !engine.isPaused()) {
            return delta * speed;
        }
        if (engine.isPausedBy(PauseReason.PAUSE_MENU)) {
            return 0f;
        }
        if (engine.isPausedBy(PauseReason.DIALOG)) {
            return delta;
        }
        return 0f;
    }

    private Rectangle extractPlaygroundBounds(TiledMap map) {
        MapLayer layer = map.getLayers().get("map");
        if (layer == null) throw new IllegalStateException("Map layer not found: map");
        MapObjects objects = layer.getObjects();
        Array<RectangleMapObject> mapObjects = objects.getByType(RectangleMapObject.class);
        if (mapObjects.size == 0) throw new IllegalStateException("No rectangle found in map layer");
        return mapObjects.first().getRectangle();
    }

    private void initializeGraphics() {
        camera   = new OrthographicCamera();
        viewport = new GameViewport(playgroundBounds.height * (16f / 9f), playgroundBounds.height, camera);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);

        cameraController = new GameCameraController(camera, viewport, playgroundBounds);
        cameraController.initialize();
        App.setCameraController(cameraController);

        stage = new Stage(viewport);
    }

    private void initializeEngine(Chapter chapter) {
        graphicsEngine = new GraphicsEngine(AssetManager.player);
        graphicsEngine.prepareLevelAssets(chapter);

        engine = new RealTimeGameEngine(graphicsEngine);
        App.setRealTimeEngine(engine);
    }

    private void initializeInputHandling() {
        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(hud.getStage());
        if (hud instanceof VaseBreakerHud && stage != null) {
            inputMultiplexer.addProcessor(stage);
        }
        inputMultiplexer.addProcessor(controller.createGameInputProcessor());
    }

    private void initializeDialogue() {
        if (!chapterHasNpcDialogue()) {
            return;
        }
        dialogueOverlay = new DialogueOverlay();
        dialogueManager = new DialogueManager(engine, graphicsEngine, viewport, dialogueOverlay);
        dialogueOverlay.setAdvanceListener(dialogueManager::requestAdvance);
        hud.getStage().addActor(dialogueOverlay);
    }

    private boolean chapterHasNpcDialogue() {
        if (chapter == null || chapter.getRules() == null) {
            return false;
        }
        for (ChapterRule rule : chapter.getRules()) {
            if (rule instanceof NpcDialogueRule npcRule && npcRule.isEnabled()) {
                return true;
            }
        }
        return false;
    }

    private void hideSystemCursor() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0f, 0f, 0f, 0f);
        pixmap.fill();
        if (hiddenCursor != null) {
            hiddenCursor.dispose();
        }
        hiddenCursor = Gdx.graphics.newCursor(pixmap, 0, 0);
        Gdx.graphics.setCursor(hiddenCursor);
        pixmap.dispose();
    }

    private void restoreSystemCursor() {
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
        if (hiddenCursor != null) {
            hiddenCursor.dispose();
            hiddenCursor = null;
        }
    }

    public Chapter getChapter() { return chapter; }
    public PregameLayout getPregameLayout() { return pregameLayout; }
    public Stage getStage() { return stage; }
    public OrthographicCamera getCamera() { return camera; }
    public RealTimeGameEngine getEngine() { return engine; }
    public GraphicsEngine getGraphicsEngine() { return graphicsEngine; }
    public Hud getHud() { return hud; }
    public GameViewport getViewport() { return viewport; }
    public GameCameraController getCameraController() { return cameraController; }
    public StateMachine<PlayScreen, State<PlayScreen>> getStateMachine() { return stateMachine; }
    public GameCursor getGameCursor() { return gameCursor; }

    public GameController getController() {
        return controller;
    }

    public DialogueManager getDialogueManager() {
        return dialogueManager;
    }

    public PointerCoordinates getPointerCoordinates() {
        return pointerCoordinates;
    }

    private void bindPointerSpaces() {
        pointerCoordinates.bindWorld(viewport);
        if (hud != null && hud.getStage() != null) {
            pointerCoordinates.bindHud(hud.getStage().getViewport());
        }
    }

    public static void shake(float duration, float intensity) {
        if (!GameSettings.gameplay().screenShakeEnabled()) {
            return;
        }
        screenShake.shake(duration, intensity);
    }
}

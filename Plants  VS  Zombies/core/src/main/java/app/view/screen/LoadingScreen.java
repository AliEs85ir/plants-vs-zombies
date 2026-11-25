package app.view.screen;

import app.bootstrap.StartupPipeline;
import app.enums.Menu;
import app.model.App;
import app.repository.Repository;
import app.service.AppService;
import app.view.cursor.CursorRenderer;
import app.view.cursor.GameCursor;
import app.view.input.PointerCoordinates;
import app.view.loading.CoverImage;
import app.view.loading.InternalFiles;
import app.view.loading.LoadingGroup;
import app.view.loading.LoadingProgressModel;
import app.view.loading.config.LoadingScreenConfig;
import app.view.loading.config.LoadingScreenConfigLoader;
import app.view.loading.icons.StartIconActionRouter;
import app.view.loading.icons.StartIconBar;
import app.view.loading.icons.StartScreenStore;
import app.view.loading.icons.config.StartIconsConfig;
import app.view.loading.icons.config.StartIconsConfigLoader;
import app.view.loading.icons.popup.StartPopupHost;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import game.component.AssetManager;
import plants_vs_zombies.PlantsVSZombies;

public final class LoadingScreen implements Screen {
    private final PlantsVSZombies game;
    private final LoadingScreenConfig config;
    private final LoadingProgressModel progressModel = new LoadingProgressModel();
    private final GameCursor gameCursor = new GameCursor();
    private final CursorRenderer cursorRenderer = new CursorRenderer(gameCursor);
    private final PointerCoordinates pointerCoordinates = new PointerCoordinates();
    private Stage stage;
    private CoverImage background;
    private LoadingGroup loadingGroup;
    private StartIconBar iconBar;
    private StartPopupHost popupHost;
    private StartScreenStore startScreenStore;
    private StartupPipeline pipeline;
    private Texture backgroundTexture;
    private Texture logoTexture;
    private BitmapFont bootstrapFont;
    private Cursor hiddenCursor;
    private boolean systemCursorHidden;
    private boolean firstFrameDrawn;
    private boolean skinAttached;
    private boolean entryPresented;
    private boolean holdStarted;
    private float holdElapsed;
    private boolean customCursorReady;
    private boolean assetsAlreadyReady;
    private boolean waitingForSession;
    private boolean sessionRestoreAttempted;

    public LoadingScreen(PlantsVSZombies game) {
        this.game = game;
        this.config = LoadingScreenConfigLoader.load();
    }

    @Override
    public void show() {
        stage = new Stage(new ExtendViewport(1280, 720));
        pointerCoordinates.bindHud(stage.getViewport());
        Gdx.input.setInputProcessor(stage);
        bootstrapFont = new BitmapFont();
        background = new CoverImage();
        background.setFillParent(true);
        backgroundTexture = loadTexture(config.background.path);
        if (backgroundTexture != null) {
            background.setTexture(
                backgroundTexture,
                config.background.sourceWidth > 0f ? config.background.sourceWidth : backgroundTexture.getWidth(),
                config.background.sourceHeight > 0f ? config.background.sourceHeight : backgroundTexture.getHeight()
            );
            background.setOffsets(config.background.offsetX, config.background.offsetY);
        }
        logoTexture = loadTexture(config.logo.path);
        loadingGroup = new LoadingGroup(config, logoTexture, gameCursor, bootstrapFont);
        Table root = new Table();
        root.setFillParent(true);
        root.add(loadingGroup)
            .expand()
            .center()
            .padLeft(config.group.resolvedX())
            .padBottom(config.group.resolvedY());
        stage.addActor(background);
        stage.addActor(root);
        StartIconsConfig iconsConfig = StartIconsConfigLoader.load();
        startScreenStore = new StartScreenStore();
        popupHost = new StartPopupHost(stage, iconsConfig, startScreenStore, gameCursor);
        StartIconActionRouter router = new StartIconActionRouter(popupHost);
        iconBar = new StartIconBar(iconsConfig, gameCursor, router::activate);
        stage.addActor(iconBar);
        if (AssetManager.isCoreReady()) {
            assetsAlreadyReady = true;
            if (AssetManager.skin != null) {
                loadingGroup.attachSkin(AssetManager.skin);
                skinAttached = true;
            }
            progressModel.markComplete();
            loadingGroup.setProgress(1f);
        } else {
            pipeline = new StartupPipeline(config);
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        AssetManager.pump();
        if (!firstFrameDrawn) {
            firstFrameDrawn = true;
            Gdx.app.log("LoadingScreen", "First graphical frame shown");
        } else if (assetsAlreadyReady) {
            beginCompletion(delta);
            if (!customCursorReady && AssetManager.hasCursorTextures()) {
                hideSystemCursor();
                customCursorReady = true;
            }
        } else if (pipeline != null) {
            pipeline.update();
            syncPipelineState(delta);
        }
        stage.act(delta);
        stage.draw();
        if (customCursorReady) {
            cursorRenderer.render(game.batch, pointerCoordinates);
        }
    }

    @Override
    public void resize(int width, int height) {
        if (stage != null) {
            stage.getViewport().update(width, height, true);
        }
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        disposeOwned();
    }

    @Override
    public void dispose() {
        disposeOwned();
    }

    private void disposeOwned() {
        restoreSystemCursor();
        if (pipeline != null) {
            pipeline.shutdown();
            pipeline = null;
        }
        if (iconBar != null) {
            iconBar.dispose();
            iconBar = null;
        }
        if (stage != null) {
            stage.dispose();
            stage = null;
        }
        if (loadingGroup != null) {
            loadingGroup.dispose();
            loadingGroup = null;
        }
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
            backgroundTexture = null;
        }
        if (logoTexture != null) {
            logoTexture.dispose();
            logoTexture = null;
        }
        if (bootstrapFont != null) {
            bootstrapFont.dispose();
            bootstrapFont = null;
        }
    }

    private void syncPipelineState(float delta) {
        if (pipeline.isFailed()) {
            progressModel.markFailed();
            loadingGroup.showError(config.error.title);
            return;
        }
        if (pipeline.isSkinReady() && !skinAttached) {
            loadingGroup.attachSkin(AssetManager.skin);
            skinAttached = true;
        }
        if (pipeline.isComplete()) {
            progressModel.markComplete();
        } else {
            progressModel.setActual(pipeline.actualProgress());
        }
        float displayed = progressModel.update(delta, config.progress.lerpSpeed, config.progress.incompleteCap);
        loadingGroup.setProgress(displayed);
        if (progressModel.isComplete() && displayed >= 0.999f) {
            beginCompletion(delta);
        }
        if (!customCursorReady && AssetManager.isCoreReady() && AssetManager.hasCursorTextures()) {
            hideSystemCursor();
            customCursorReady = true;
        }
    }

    private void beginCompletion(float delta) {
        if (!holdStarted) {
            holdStarted = true;
            holdElapsed = 0f;
            if (startScreenStore != null) {
                startScreenStore.beginSession(0);
            }
        }
        holdElapsed += delta;
        if (entryPresented) {
            return;
        }
        if (!assetsAlreadyReady
            && config.transition.enabled
            && holdElapsed < config.transition.completionHoldSeconds) {
            return;
        }
        if (waitingForSession) {
            return;
        }
        if (!sessionRestoreAttempted
            && !AppService.isLoggedIn()
            && Repository.hasRememberedLogin()) {
            sessionRestoreAttempted = true;
            waitingForSession = true;
            AppService.restoreSession(result -> waitingForSession = false);
            return;
        }
        boolean loggedIn = AppService.isLoggedIn();
        Gdx.app.log("LoadingScreen", loggedIn ? "Entry button: login" : "Entry button: sign up");
        loadingGroup.revealEntryButton(loggedIn, () -> {
            if (AppService.isLoggedIn()) {
                App.setCurrentMenu(Menu.MainMenu);
                ScreenManager.showMainMenu(game);
            } else {
                App.setCurrentMenu(Menu.SignupMenu);
                ScreenManager.showSignup(game);
            }
        });
        entryPresented = true;
    }

    private Texture loadTexture(String path) {
        var file = InternalFiles.resolve(path);
        if (file == null || !file.exists()) {
            Gdx.app.error("LoadingScreen", "Missing texture: " + path);
            return null;
        }
        try {
            Texture texture = new Texture(file);
            texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            return texture;
        } catch (RuntimeException exception) {
            Gdx.app.error("LoadingScreen", "Could not load texture: " + path, exception);
            return null;
        }
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
        systemCursorHidden = true;
        pixmap.dispose();
    }

    private void restoreSystemCursor() {
        if (!systemCursorHidden) {
            return;
        }
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
        if (hiddenCursor != null) {
            hiddenCursor.dispose();
            hiddenCursor = null;
        }
        systemCursorHidden = false;
    }
}

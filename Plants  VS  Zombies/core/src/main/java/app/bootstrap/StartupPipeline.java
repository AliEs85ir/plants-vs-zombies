package app.bootstrap;

import app.controller.game.izombie.IZombieNetworkController;
import app.enums.Menu;
import app.model.App;
import app.service.AppService;
import app.view.AppLoop;
import app.view.loading.config.LoadingScreenConfig;
import com.badlogic.gdx.Gdx;
import game.audio.AudioManager;
import game.audio.MenuMusic;
import game.component.AssetManager;
import game.settings.GameSettings;
import server.EmbeddedServer;
import server.network.client.GameClient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

public final class StartupPipeline {
    private volatile boolean sessionCheckFinished = false;
    private enum Phase {
        FIRST_FRAME,
        START_CORE_ASSETS,
        WAIT_CORE_ASSETS,
        SKIN_AND_PLAYER,
        START_GAME_DATA,
        WAIT_GAME_DATA,
        AUDIO_AND_SETTINGS,
        START_EMBEDDED_SERVER,
        WAIT_EMBEDDED_SERVER,
        START_SESSION,
        WAIT_SESSION,
        CURSORS,
        OVERLAY_PAM,
        CONSOLE,
        COMPLETE,
        FAILED
    }

    private final LoadingScreenConfig config;
    private final ExecutorService executor = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable, "startup-loader");
        thread.setDaemon(true);
        return thread;
    });
    private final AtomicReference<String> failure = new AtomicReference<>();
    private volatile Phase phase = Phase.FIRST_FRAME;
    private Future<?> coreAssetsFuture;
    private Future<?> gameDataFuture;
    private Future<?> embeddedServerFuture;
    private float actualProgress;
    private boolean skinReady;

    public StartupPipeline(LoadingScreenConfig config) {
        this.config = config;
    }

    public void update() {
        if (phase == Phase.COMPLETE || phase == Phase.FAILED) {
            return;
        }
        try {
            switch (phase) {
                case FIRST_FRAME -> phase = Phase.START_CORE_ASSETS;
                case START_CORE_ASSETS -> startCoreAssets();
                case WAIT_CORE_ASSETS -> waitCoreAssets();
                case SKIN_AND_PLAYER -> initializeSkinAndPlayer();
                case START_GAME_DATA -> startGameData();
                case WAIT_GAME_DATA -> waitGameData();
                case AUDIO_AND_SETTINGS -> initializeAudioAndSettings();
                case START_EMBEDDED_SERVER -> startEmbeddedServer();
                case WAIT_EMBEDDED_SERVER -> waitEmbeddedServer();
                case START_SESSION -> startRestoreSession();
                case WAIT_SESSION -> waitRestoreSession();
                case CURSORS -> loadCursors();
                case OVERLAY_PAM -> loadOverlayPam();
                case CONSOLE -> startConsole();
                default -> {
                }
            }
        } catch (RuntimeException exception) {
            fail("Startup failed.", exception);
        }
    }

    public float actualProgress() {
        return actualProgress;
    }

    public boolean isComplete() {
        return phase == Phase.COMPLETE;
    }

    public boolean isFailed() {
        return phase == Phase.FAILED;
    }

    public boolean isSkinReady() {
        return skinReady;
    }

    public String failureMessage() {
        return failure.get();
    }

    public void shutdown() {
        executor.shutdownNow();
    }

    private void startCoreAssets() {
        actualProgress = 0.02f;
        coreAssetsFuture = executor.submit(() -> {
            try {
                AssetManager.createTextureBankOffThread();
            } catch (RuntimeException exception) {
                fail("Could not prepare core textures.", exception);
                throw exception;
            }
        });
        phase = Phase.WAIT_CORE_ASSETS;
    }

    private void waitCoreAssets() {
        actualProgress = toward(config.progress.coreAssetsWeight, 0.92f);
        if (coreAssetsFuture == null || !coreAssetsFuture.isDone()) {
            return;
        }
        if (isFailed()) {
            return;
        }
        try {
            coreAssetsFuture.get();
            AssetManager.attachTextureBankOnGlThread();
            actualProgress = config.progress.coreAssetsWeight;
            phase = Phase.SKIN_AND_PLAYER;
        } catch (Exception exception) {
            fail("Could not prepare core textures.", exception);
        }
    }

    private void initializeSkinAndPlayer() {
        AssetManager.initializePlayerAndSkin();
        skinReady = true;
        actualProgress = config.progress.coreAssetsWeight + config.progress.skinUiWeight;
        phase = Phase.START_GAME_DATA;
    }

    private void startGameData() {
        gameDataFuture = executor.submit(() -> {
            try {
                AppLoop.loadGameData();
            } catch (RuntimeException exception) {
                fail("Could not load game data.", exception);
                throw exception;
            }
        });
        phase = Phase.WAIT_GAME_DATA;
    }

    private void waitGameData() {
        float base = config.progress.coreAssetsWeight + config.progress.skinUiWeight;
        actualProgress = toward(base + config.progress.gameDataWeight, 0.92f);
        if (gameDataFuture == null || !gameDataFuture.isDone()) {
            return;
        }
        if (isFailed()) {
            return;
        }
        try {
            gameDataFuture.get();
            actualProgress = base + config.progress.gameDataWeight;
            phase = Phase.AUDIO_AND_SETTINGS;
        } catch (Exception exception) {
            fail("Could not load game data.", exception);
        }
    }

    private void initializeAudioAndSettings() {
        AudioManager.init();
        GameSettings.load();
        MenuMusic.ensurePlaying();
        actualProgress += config.progress.audioSettingsWeight;
        phase = Phase.START_EMBEDDED_SERVER;
    }

    private void startEmbeddedServer() {
        if (!EmbeddedServer.shouldEmbed()) {
            phase = Phase.START_SESSION;
            return;
        }
        embeddedServerFuture = executor.submit(EmbeddedServer::startIfNeeded);
        phase = Phase.WAIT_EMBEDDED_SERVER;
    }

    private void waitEmbeddedServer() {
        if (embeddedServerFuture == null || !embeddedServerFuture.isDone()) {
            return;
        }
        try {
            boolean ready = Boolean.TRUE.equals(embeddedServerFuture.get());
            if (!ready || EmbeddedServer.failureMessage() != null) {
                fail(EmbeddedServer.failureMessage() == null
                    ? "Could not start server."
                    : EmbeddedServer.failureMessage(), null);
                return;
            }
        } catch (Exception exception) {
            fail("Could not start server.", exception);
            return;
        }
        phase = Phase.START_SESSION;
    }

    private void startRestoreSession() {
        if (AppService.client() == null) {
            AppService.init(new GameClient());
        }
        AppService.client().connect();
        IZombieNetworkController.init(AppService.client());

        sessionCheckFinished = false;

        AppService.restoreSession(result -> {
            if (AppService.isLoggedIn()) {
                App.setCurrentMenu(Menu.MainMenu);
                Gdx.app.log("StartupPipeline", "Remembered session restored.");
            } else {
                App.setCurrentMenu(Menu.SignupMenu);
                Gdx.app.log("StartupPipeline", "No remembered session: " + result.message());
            }
            sessionCheckFinished = true;
        });

        phase = Phase.WAIT_SESSION;
    }

    private void waitRestoreSession() {
        if (!sessionCheckFinished) {
            return;
        }
        actualProgress += config.progress.sessionWeight;
        phase = Phase.CURSORS;
    }

    private void loadCursors() {
        AssetManager.ensureCursorsLoaded();
        actualProgress += config.progress.cursorsWeight;
        phase = Phase.OVERLAY_PAM;
    }

    private void loadOverlayPam() {
        try {
            AssetManager.player.loadAsync(config.inGameOverlay.pamPath, null);
        } catch (RuntimeException exception) {
            Gdx.app.error("StartupPipeline", "In-game loading icon could not be preloaded", exception);
        }
        actualProgress += config.progress.overlayPamWeight;
        phase = Phase.CONSOLE;
    }

    private void startConsole() {
        AppLoop.startConsoleThread();
        actualProgress = 1f;
        phase = Phase.COMPLETE;
        executor.shutdown();
        Gdx.app.log("StartupPipeline", "Startup initialization complete");
    }

    private float toward(float cap, float ratio) {
        return Math.min(cap * ratio, actualProgress + 0.01f);
    }

    private void fail(String message, Throwable throwable) {
        if (phase == Phase.FAILED) {
            return;
        }
        phase = Phase.FAILED;
        failure.set(message);
        Gdx.app.error("StartupPipeline", message, throwable);
        executor.shutdownNow();
    }
}

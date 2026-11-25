package app.view.screen;

import app.enums.Menu;
import app.model.App;
import app.view.cursor.CursorRenderer;
import app.view.cursor.GameCursor;
import app.view.input.PointerCoordinates;
import app.view.loading.CoverImage;
import app.view.mainmenu.MainMenuTab;
import app.view.mainmenu.MenuTextures;
import app.view.mainmenu.config.MainMenuConfig;
import app.view.mainmenu.config.MainMenuConfigLoader;
import app.view.mainmenu.content.MainMenuContentHost;
import app.view.mainmenu.content.NewsMenuContent;
import app.view.mainmenu.content.ProfileMenuContent;
import app.view.mainmenu.content.SettingsMenuContent;
import app.view.mainmenu.game.GameCardRouter;
import app.view.mainmenu.game.GameMenuPanel;
import app.view.mainmenu.navigation.MainMenuNavBar;
import app.view.mainmenu.navigation.MainMenuNavRouter;
import app.view.mainmenu.title.MainMenuTabTitle;
import app.view.ui.UiSoundBinder;
import game.audio.MenuMusic;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import game.component.AssetManager;
import plants_vs_zombies.PlantsVSZombies;

public final class MainMenuScreen implements Screen {
    private final PlantsVSZombies game;
    private final MainMenuConfig config;
    private final GameCursor gameCursor = new GameCursor();
    private final CursorRenderer cursorRenderer = new CursorRenderer(gameCursor);
    private final PointerCoordinates pointerCoordinates = new PointerCoordinates();
    private Stage stage;
    private CoverImage background;
    private Texture backgroundTexture;
    private MainMenuContentHost contentHost;
    private MainMenuTabTitle tabTitle;
    private MainMenuNavBar navBar;
    private GameMenuPanel gamePanel;
    private Cursor hiddenCursor;
    private boolean systemCursorHidden;
    private boolean customCursorReady;

    public MainMenuScreen(PlantsVSZombies game) {
        this.game = game;
        this.config = MainMenuConfigLoader.load();
    }

    @Override
    public void show() {
        App.setCurrentMenu(Menu.MainMenu);
        stage = new Stage(new ExtendViewport(1280, 720));
        pointerCoordinates.bindHud(stage.getViewport());
        Gdx.input.setInputProcessor(stage);
        UiSoundBinder.bind(stage);
        MenuMusic.ensurePlaying();

        background = new CoverImage();
        background.setFillParent(true);
        backgroundTexture = MenuTextures.load(config.background.path, "MainMenuScreen");
        if (backgroundTexture != null) {
            background.setTexture(
                backgroundTexture,
                config.background.sourceWidth > 0f ? config.background.sourceWidth : backgroundTexture.getWidth(),
                config.background.sourceHeight > 0f ? config.background.sourceHeight : backgroundTexture.getHeight()
            );
            background.setOffsets(config.background.offsetX, config.background.offsetY);
        }

        contentHost = new MainMenuContentHost(config.content, config.tabTitle);
        tabTitle = new MainMenuTabTitle(config.tabTitle);
        GameCardRouter cardRouter = new GameCardRouter(game);
        gamePanel = new GameMenuPanel(config.gameMenu, gameCursor, cardRouter::activate);
        MainMenuNavRouter navRouter = new MainMenuNavRouter(game, this::showTab);
        navBar = new MainMenuNavBar(config.navigation, gameCursor, navRouter::activate);

        stage.addActor(background);
        stage.addActor(contentHost);
        stage.addActor(tabTitle);
        stage.addActor(navBar);

        showTab(MainMenuTab.fromId(config.defaultTab));
        if (AssetManager.hasCursorTextures()) {
            hideSystemCursor();
            customCursorReady = true;
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        AssetManager.pump();
        if (!customCursorReady && AssetManager.hasCursorTextures()) {
            hideSystemCursor();
            customCursorReady = true;
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

    private void showTab(MainMenuTab tab) {
        if (tab == null) {
            tab = MainMenuTab.GAME;
        }
        if (navBar != null) {
            navBar.setSelectedId(tab.id());
        }
        if (tabTitle != null) {
            tabTitle.showTab(tab.id());
        }
        if (contentHost == null) {
            return;
        }
        if (tab == MainMenuTab.GAME) {
            contentHost.setContent(gamePanel, false);
            return;
        }
        Skin skin = AssetManager.skin;
        if (skin == null) {
            Gdx.app.error("MainMenuScreen", "Skin is not ready for tab " + tab.id());
            contentHost.setContent(null);
            return;
        }
        switch (tab) {
            case NEWS -> contentHost.setContent(new NewsMenuContent(skin));
            case PROFILE -> contentHost.setContent(new ProfileMenuContent(skin, stage, game));
            case SETTINGS -> contentHost.setContent(new SettingsMenuContent(skin));
            default -> contentHost.setContent(gamePanel, false);
        }
    }

    private void disposeOwned() {
        restoreSystemCursor();
        if (navBar != null) {
            navBar.dispose();
            navBar = null;
        }
        if (tabTitle != null) {
            tabTitle.dispose();
            tabTitle = null;
        }
        if (gamePanel != null) {
            gamePanel.dispose();
            gamePanel = null;
        }
        if (stage != null) {
            stage.dispose();
            stage = null;
        }
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
            backgroundTexture = null;
        }
        contentHost = null;
        background = null;
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

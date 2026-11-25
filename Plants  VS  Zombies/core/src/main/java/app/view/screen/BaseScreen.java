package app.view.screen;

import app.enums.cursor.CursorType;
import app.view.cursor.CursorRenderer;
import app.view.cursor.GameCursor;
import app.view.input.PointerCoordinates;
import app.view.ui.UiSoundBinder;
import game.audio.AudioId;
import game.audio.MenuMusic;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import game.component.AssetManager;
import plants_vs_zombies.PlantsVSZombies;

public abstract class BaseScreen implements Screen {
    protected final PlantsVSZombies game;
    protected Stage stage;
    protected Skin skin;
    private final GameCursor gameCursor = new GameCursor();
    private final CursorRenderer cursorRenderer = new CursorRenderer(gameCursor);
    private final PointerCoordinates pointerCoordinates = new PointerCoordinates();
    private final Vector2 pointerHud = new Vector2();
    private Cursor hiddenCursor;
    private boolean systemCursorHidden;
    private boolean customCursorReady;

    public BaseScreen(PlantsVSZombies game) {
        this.game = game;
    }

    @Override
    public void show() {
        ExtendViewport extendViewport = new ExtendViewport(1280, 720);
        stage = new Stage(extendViewport);
        skin = AssetManager.skin;
        pointerCoordinates.bindHud(stage.getViewport());
        UiSoundBinder.bind(stage);
        MenuMusic.ensurePlaying(menuMusicId());
        Gdx.input.setInputProcessor(stage);
        if (AssetManager.hasCursorTextures()) {
            hideSystemCursor();
            customCursorReady = true;
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (!customCursorReady && AssetManager.hasCursorTextures()) {
            hideSystemCursor();
            customCursorReady = true;
        }
        updateGameCursor();
        stage.act(delta);
        stage.draw();
        if (customCursorReady) {
            cursorRenderer.render(game.batch, pointerCoordinates);
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        restoreSystemCursor();
        if (stage != null) {
            stage.dispose();
        }
    }

    private void updateGameCursor() {
        if (stage == null) {
            return;
        }
        pointerCoordinates.toHud(pointerHud);
        Actor hit = stage.hit(pointerHud.x, pointerHud.y, true);
        boolean interactive = isInteractive(hit);
        boolean pressed = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
        gameCursor.setType(resolveCursorType(interactive, pressed));
    }

    protected GameCursor gameCursor() {
        return gameCursor;
    }

    protected void setForcedCursor(CursorType type) {
        gameCursor.setType(type == null ? CursorType.DEFAULT : type);
    }

    public Stage getStage() {
        return stage;
    }

    protected AudioId menuMusicId() {
        return AudioId.MENU_MUSIC;
    }

    protected CursorType resolveCursorType(boolean interactive, boolean pressed) {
        if (interactive && pressed) {
            return CursorType.PRESSED;
        }
        if (interactive) {
            return CursorType.HOVER;
        }
        return CursorType.DEFAULT;
    }

    private static boolean isInteractive(Actor actor) {
        for (Actor current = actor; current != null; current = current.getParent()) {
            if (current instanceof Button
                || current instanceof TextField
                || current instanceof SelectBox
                || current instanceof Slider
                || "AuthSecondaryLink".equals(current.getClass().getSimpleName())
                || current.getName() != null && (
                    current.getName().startsWith("auth-")
                        || current.getName().startsWith("menu-")
                        || current.getName().startsWith("greenhouse-")
                        || current.getName().startsWith("shop-")
                        || current.getName().startsWith("collection-")
                )) {
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

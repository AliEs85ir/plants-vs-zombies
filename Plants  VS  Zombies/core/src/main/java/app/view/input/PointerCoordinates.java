package app.view.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public final class PointerCoordinates {
    private Viewport worldViewport;
    private Viewport hudViewport;
    private final ScreenViewport windowViewport = new ScreenViewport();
    private final Vector3 scratch3 = new Vector3();
    private final Vector2 hudHit = new Vector2();

    public PointerCoordinates bindWorld(Viewport viewport) {
        this.worldViewport = viewport;
        return this;
    }

    public PointerCoordinates bindHud(Viewport viewport) {
        this.hudViewport = viewport;
        return this;
    }

    public int screenX() {
        return Gdx.input.getX();
    }

    public int screenY() {
        return Gdx.input.getY();
    }

    public void applyWorld() {
        if (worldViewport != null) {
            worldViewport.apply();
        }
    }

    public void applyHud() {
        if (hudViewport != null) {
            hudViewport.apply();
        }
    }

    public void applyWindow() {
        syncWindowViewport();
        windowViewport.apply();
    }

    public Vector2 toWindow(Vector2 out) {
        return toWindow(screenX(), screenY(), out);
    }

    public Vector2 toWindow(int screenX, int screenY, Vector2 out) {
        Vector2 target = out == null ? new Vector2() : out;
        syncWindowViewport();
        scratch3.set(screenX, screenY, 0f);
        windowViewport.unproject(scratch3);
        return target.set(scratch3.x, scratch3.y);
    }

    public Vector2 toHud(Vector2 out) {
        return toHud(screenX(), screenY(), out);
    }

    public Vector2 toHud(int screenX, int screenY, Vector2 out) {
        Vector2 target = out == null ? new Vector2() : out;
        if (hudViewport == null) {
            return toWindow(screenX, screenY, target);
        }
        scratch3.set(screenX, screenY, 0f);
        hudViewport.unproject(scratch3);
        return target.set(scratch3.x, scratch3.y);
    }

    public Vector3 toWorld(Vector3 out) {
        return toWorld(screenX(), screenY(), out);
    }

    public Vector3 toWorld(int screenX, int screenY, Vector3 out) {
        Vector3 target = out == null ? new Vector3() : out;
        target.set(screenX, screenY, 0f);
        if (worldViewport != null) {
            worldViewport.unproject(target);
        }
        return target;
    }

    public Actor hitHud(Stage stage, boolean touchable) {
        return hitHud(stage, screenX(), screenY(), touchable);
    }

    public Actor hitHud(Stage stage, int screenX, int screenY, boolean touchable) {
        if (stage == null) {
            return null;
        }
        toHud(screenX, screenY, hudHit);
        return stage.hit(hudHit.x, hudHit.y, touchable);
    }

    public Viewport worldViewport() {
        return worldViewport;
    }

    public Viewport hudViewport() {
        return hudViewport;
    }

    public Viewport windowViewport() {
        return windowViewport;
    }

    private void syncWindowViewport() {
        int width = Math.max(1, Gdx.graphics.getWidth());
        int height = Math.max(1, Gdx.graphics.getHeight());
        windowViewport.setWorldSize(width, height);
        windowViewport.setScreenBounds(0, 0, width, height);
        Camera camera = windowViewport.getCamera();
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.position.set(width / 2f, height / 2f, 0f);
        camera.update();
    }
}

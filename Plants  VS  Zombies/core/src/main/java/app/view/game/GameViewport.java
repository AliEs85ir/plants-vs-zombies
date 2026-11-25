package app.view.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class GameViewport extends FitViewport {

    public GameViewport(float worldWidth, float worldHeight, OrthographicCamera camera) {
        super(worldWidth, worldHeight, camera);
    }

    public float getWorldLeft() {
        return getCamera().position.x - getWorldWidth() / 2f;
    }

    public float getWorldRight() {
        return getCamera().position.x + getWorldWidth() / 2f;
    }

    public float getWorldBottom() {
        return getCamera().position.y - getWorldHeight() / 2f;
    }

    public float getWorldTop() {
        return getCamera().position.y + getWorldHeight() / 2f;
    }
}

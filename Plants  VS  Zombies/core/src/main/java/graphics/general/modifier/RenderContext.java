package graphics.general.modifier;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Rectangle;

public class RenderContext {
    private final Camera camera;
    private final Rectangle worldBounds;

    public RenderContext(Camera camera, Rectangle worldBounds) {
        this.camera = camera;
        this.worldBounds = worldBounds;
    }

    public Camera getCamera() {
        return camera;
    }

    public Rectangle getWorldBounds() {
        return worldBounds;
    }
}

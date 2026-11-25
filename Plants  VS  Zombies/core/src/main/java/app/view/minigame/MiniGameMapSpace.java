package app.view.minigame;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public final class MiniGameMapSpace {
    private final float mapPixelWidth;
    private final float mapPixelHeight;
    private final Rectangle mapBounds;

    public MiniGameMapSpace(float mapPixelWidth, float mapPixelHeight, Rectangle mapBounds) {
        this.mapPixelWidth = mapPixelWidth <= 0f ? 1f : mapPixelWidth;
        this.mapPixelHeight = mapPixelHeight <= 0f ? 1f : mapPixelHeight;
        this.mapBounds = mapBounds == null
            ? new Rectangle(0f, 0f, this.mapPixelWidth, this.mapPixelHeight)
            : new Rectangle(mapBounds);
    }

    public float mapPixelWidth() {
        return mapPixelWidth;
    }

    public float mapPixelHeight() {
        return mapPixelHeight;
    }

    public Rectangle mapBounds() {
        return new Rectangle(mapBounds);
    }

    public boolean containsMapPoint(float mapX, float mapY) {
        return mapBounds.contains(mapX, mapY);
    }

    public float scaleX(float viewWidth) {
        return viewWidth / mapPixelWidth;
    }

    public float scaleY(float viewHeight) {
        return viewHeight / mapPixelHeight;
    }

    public float uniformScale(float viewWidth, float viewHeight) {
        return Math.min(scaleX(viewWidth), scaleY(viewHeight));
    }

    public Vector2 toMap(float localX, float localY, float viewWidth, float viewHeight, Vector2 out) {
        Vector2 target = out == null ? new Vector2() : out;
        float width = viewWidth <= 0f ? 1f : viewWidth;
        float height = viewHeight <= 0f ? 1f : viewHeight;
        return target.set(localX / width * mapPixelWidth, localY / height * mapPixelHeight);
    }

    public Vector2 toStage(float mapX, float mapY, float viewWidth, float viewHeight, Vector2 out) {
        Vector2 target = out == null ? new Vector2() : out;
        return target.set(mapX * scaleX(viewWidth), mapY * scaleY(viewHeight));
    }

    public Rectangle toStage(Rectangle mapRect, float viewWidth, float viewHeight, Rectangle out) {
        Rectangle target = out == null ? new Rectangle() : out;
        if (mapRect == null) {
            return target.set(0f, 0f, 0f, 0f);
        }
        return target.set(
            mapRect.x * scaleX(viewWidth),
            mapRect.y * scaleY(viewHeight),
            mapRect.width * scaleX(viewWidth),
            mapRect.height * scaleY(viewHeight)
        );
    }
}

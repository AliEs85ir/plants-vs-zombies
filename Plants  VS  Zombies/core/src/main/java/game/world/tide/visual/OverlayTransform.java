package game.world.tide.visual;

public final class OverlayTransform {
    private final float offsetX;
    private final float offsetY;
    private final float scaleX;
    private final float scaleY;

    public OverlayTransform(float offsetX, float offsetY, float scaleX, float scaleY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }

    public static OverlayTransform identity() {
        return new OverlayTransform(0f, 0f, 1f, 1f);
    }

    public float getOffsetX() {
        return offsetX;
    }

    public float getOffsetY() {
        return offsetY;
    }

    public float getScaleX() {
        return scaleX;
    }

    public float getScaleY() {
        return scaleY;
    }

    public OverlayTransform withOffset(float x, float y) {
        return new OverlayTransform(x, y, scaleX, scaleY);
    }

    public OverlayTransform withScale(float x, float y) {
        return new OverlayTransform(offsetX, offsetY, x, y);
    }
}

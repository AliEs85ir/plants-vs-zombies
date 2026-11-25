package game.component;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public final class ResolvedIcon {
    private final TextureRegion region;
    private final float width;
    private final float height;
    private final float scale;

    public ResolvedIcon(TextureRegion region, float width, float height, float scale) {
        this.region = region;
        this.width = width;
        this.height = height;
        this.scale = scale;
    }

    public TextureRegion region() {
        return region;
    }

    public float width() {
        return width;
    }

    public float height() {
        return height;
    }

    public float scale() {
        return scale;
    }

    public boolean isPresent() {
        return region != null && width > 0f && height > 0f;
    }
}

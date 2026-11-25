package app.view.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.util.HashMap;
import java.util.Map;

public final class Solid {

    private static final Map<String, Texture> CACHE = new HashMap<>();

    private Solid() {}

    public static TextureRegionDrawable drawable(Color color, float minWidth, float minHeight) {
        TextureRegionDrawable drawable = new TextureRegionDrawable(texture(color));
        drawable.setMinWidth(minWidth);
        drawable.setMinHeight(minHeight);
        return drawable;
    }

    private static Texture texture(Color color) {
        String key = color.toString();
        Texture cached = CACHE.get(key);
        if (cached != null) return cached;

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture created = new Texture(pixmap);
        pixmap.dispose();

        CACHE.put(key, created);
        return created;
    }
}

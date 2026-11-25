package app.view.hud.notification.emoji_sticker;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.util.HashMap;
import java.util.Map;

public class EmojiAssetLoader {
    private static final Map<String, Texture> textures = new HashMap<>();
    private static final Map<String, TextureRegion> regions = new HashMap<>();

    private EmojiAssetLoader() {
    }

    public static TextureRegion getRegion(String assetPath) {
        if (assetPath == null || assetPath.isBlank()) {
            return null;
        }

        TextureRegion cachedRegion = regions.get(assetPath);
        if (cachedRegion != null) {
            return cachedRegion;
        }

        if (Gdx.files == null) {
            return null;
        }

        FileHandle file = Gdx.files.internal(assetPath);

        if (!file.exists()) {
            Gdx.app.error(
                "EmojiAssetLoader",
                "Emoji file not found: " + file.path()
            );
            return null;
        }

        try {
            Texture texture = new Texture(file);
            texture.setFilter(
                Texture.TextureFilter.Linear,
                Texture.TextureFilter.Linear
            );
            texture.setWrap(
                Texture.TextureWrap.ClampToEdge,
                Texture.TextureWrap.ClampToEdge
            );

            TextureRegion region = new TextureRegion(texture);

            textures.put(assetPath, texture);
            regions.put(assetPath, region);

            return region;
        } catch (RuntimeException exception) {
            Gdx.app.error(
                "EmojiAssetLoader",
                "Could not load emoji: " + assetPath,
                exception
            );
            return null;
        }
    }

    public static void dispose() {
        for (Texture texture : textures.values()) {
            texture.dispose();
        }

        textures.clear();
        regions.clear();
    }
}

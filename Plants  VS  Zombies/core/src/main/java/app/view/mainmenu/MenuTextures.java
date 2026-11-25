package app.view.mainmenu;

import app.view.loading.InternalFiles;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;

public final class MenuTextures {
    private MenuTextures() {
    }

    public static Texture load(String path, String logTag) {
        if (path == null || path.isBlank()) {
            Gdx.app.error(logTag, "Empty texture path");
            return null;
        }
        FileHandle file = InternalFiles.resolve(path);
        if (file == null || !file.exists()) {
            Gdx.app.error(logTag, "Missing texture: " + path);
            return null;
        }
        try {
            Texture texture = new Texture(file);
            texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            return texture;
        } catch (RuntimeException exception) {
            Gdx.app.error(logTag, "Could not load texture: " + path, exception);
            return null;
        }
    }
}

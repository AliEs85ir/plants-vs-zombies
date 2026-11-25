package app.view.shop;

import app.view.mainmenu.MenuTextures;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;

public final class ShopTextureCache implements Disposable {
    private final ObjectMap<String, Texture> textures = new ObjectMap<>();

    public Texture get(String path) {
        if (path == null || path.isBlank()) {
            return null;
        }
        String key = path.replace('\\', '/');
        if (textures.containsKey(key)) {
            return textures.get(key);
        }
        Texture loaded = MenuTextures.load(key, "ShopTextureCache");
        textures.put(key, loaded);
        return loaded;
    }

    @Override
    public void dispose() {
        for (Texture texture : textures.values()) {
            if (texture != null) {
                texture.dispose();
            }
        }
        textures.clear();
    }
}

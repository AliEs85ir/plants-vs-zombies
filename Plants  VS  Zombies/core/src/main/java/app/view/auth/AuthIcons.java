package app.view.auth;

import app.view.mainmenu.MenuTextures;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public final class AuthIcons {
    private AuthIcons() {
    }

    public static Texture load(String path) {
        if (path == null || path.isBlank()) {
            return null;
        }
        Texture texture = MenuTextures.load(path, "AuthMenu");
        if (texture != null) {
            return texture;
        }
        if (!path.contains(".")) {
            texture = MenuTextures.load(path + ".png", "AuthMenu");
            if (texture != null) {
                return texture;
            }
            return MenuTextures.load(path + ".jpg", "AuthMenu");
        }
        return null;
    }

    public static Image image(Texture texture, float size) {
        Image image = texture == null ? new Image() : new Image(new TextureRegionDrawable(texture));
        image.setSize(size, size);
        return image;
    }
}

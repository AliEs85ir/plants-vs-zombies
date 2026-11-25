package app.view.mainmenu.settings;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import game.component.AssetManager;

public final class SettingsUiFonts {
    private SettingsUiFonts() {
    }

    public static BitmapFont resolve(String name, String fallback) {
        BitmapFont font = tryFont(name);
        if (font != null) {
            return font;
        }
        if (name != null) {
            font = tryFont(name.replace('-', '_'));
            if (font != null) {
                return font;
            }
            font = tryFont(name.endsWith("_") ? name : name + "_");
            if (font != null) {
                return font;
            }
        }
        font = tryFont(fallback);
        if (font != null) {
            return font;
        }
        return AssetManager.font(AssetManager.DEFAULT_FONT);
    }

    private static BitmapFont tryFont(String name) {
        if (name == null || name.isBlank() || AssetManager.skin == null) {
            return null;
        }
        if (AssetManager.skin.has(name, BitmapFont.class)) {
            return AssetManager.skin.getFont(name);
        }
        return null;
    }
}

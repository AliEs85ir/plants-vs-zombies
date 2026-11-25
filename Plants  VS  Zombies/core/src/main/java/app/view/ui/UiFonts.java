package app.view.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import game.component.AssetManager;

public final class UiFonts {
    private static BitmapFont errorFont;
    private static Label.LabelStyle errorStyle;

    private UiFonts() {
    }

    public static Label.LabelStyle errorPopupStyle() {
        if (errorStyle == null) {
            errorStyle = new Label.LabelStyle(errorFont(), Color.BLACK);
        }
        return errorStyle;
    }

    private static BitmapFont errorFont() {
        if (errorFont != null) {
            return errorFont;
        }
        FileHandle file = resolveFontFile();
        if (file == null) {
            errorFont = AssetManager.font(AssetManager.DEFAULT_FONT);
            return errorFont;
        }
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(file);
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 26;
        parameter.color = Color.BLACK;
        parameter.borderWidth = 1.4f;
        parameter.borderColor = Color.WHITE;
        parameter.minFilter = Texture.TextureFilter.Linear;
        parameter.magFilter = Texture.TextureFilter.Linear;
        errorFont = generator.generateFont(parameter);
        generator.dispose();
        return errorFont;
    }

    private static FileHandle resolveFontFile() {
        String[] paths = {
            "C:/Windows/Fonts/tahoma.ttf",
            "C:/Windows/Fonts/arial.ttf",
            "C:/Windows/Fonts/segoeui.ttf"
        };
        for (String path : paths) {
            FileHandle file = Gdx.files.absolute(path);
            if (file.exists()) {
                return file;
            }
        }
        return null;
    }
}

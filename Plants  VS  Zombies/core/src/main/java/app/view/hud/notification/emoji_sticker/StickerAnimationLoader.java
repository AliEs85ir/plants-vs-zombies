package app.view.hud.notification.emoji_sticker;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class StickerAnimationLoader {
    public static Animation<TextureRegion> createAnimation(String assetPath, int cols, int rows, float frameDuration) {
        Texture sheet = new Texture(Gdx.files.internal(assetPath));
        TextureRegion[][] grid = TextureRegion.split(
            sheet,
            sheet.getWidth() / cols,
            sheet.getHeight() / rows
        );

        TextureRegion[] frames = new TextureRegion[cols * rows];
        int index = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                frames[index++] = grid[r][c];
            }
        }

        return new Animation<>(frameDuration, frames);
    }
}

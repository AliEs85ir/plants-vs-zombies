package app.view.cursor;

import app.enums.cursor.CursorType;
import app.view.input.PointerCoordinates;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import game.component.AssetManager;

public final class CursorRenderer {
    private final GameCursor cursor;
    private final Vector2 window = new Vector2();

    public CursorRenderer(GameCursor cursor) {
        this.cursor = cursor;
    }

    public void render(SpriteBatch batch, PointerCoordinates pointer) {
        if (batch == null || cursor == null || pointer == null) {
            return;
        }

        pointer.applyWindow();
        pointer.toWindow(window);

        batch.setProjectionMatrix(pointer.windowViewport().getCamera().combined);
        batch.begin();
        batch.setColor(1f, 1f, 1f, 1f);

        Actor preview = cursor.placementActor();
        if (preview != null && preview.getWidth() > 0f && preview.getHeight() > 0f) {
            preview.setPosition(
                window.x - preview.getWidth() * 0.5f,
                window.y - preview.getHeight() * 0.5f
            );
            preview.draw(batch, 1f);
        } else {
            drawCursorTexture(batch);
        }

        batch.end();
    }

    private void drawCursorTexture(SpriteBatch batch) {
        CursorType type = cursor.type();
        Texture texture = AssetManager.cursor(type);
        if (texture == null) {
            return;
        }

        float scale = cursor.scale().current();
        float width = texture.getWidth() * scale;
        float height = texture.getHeight() * scale;
        float x = window.x - type.hotspotX() * width;
        float y = window.y - (1f - type.hotspotY()) * height;
        batch.draw(texture, x, y, width, height);
    }
}

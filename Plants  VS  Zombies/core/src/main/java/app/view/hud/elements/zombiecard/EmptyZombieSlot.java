package app.view.hud.elements.zombiecard;

import app.view.ui.UiTheme;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import game.component.AssetManager;

public class EmptyZombieSlot extends Actor {

    private static final String SLOT_TEXTURE_KEY = "IMAGE_UI_PACKETS_MODERNDAY";

    private final TextureRegion region;

    public EmptyZombieSlot(float width, float height) {
        this.region = AssetManager.textureBank.region(SLOT_TEXTURE_KEY);
        setSize(width, height);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (region == null) return;

        Color tint = UiTheme.SLOT_EMPTY;
        batch.setColor(tint.r, tint.g, tint.b, tint.a * parentAlpha * getColor().a);
        batch.draw(region, getX(), getY(), getWidth(), getHeight());
        batch.setColor(Color.WHITE);
    }
}

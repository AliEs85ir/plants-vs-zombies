package app.view.hud.elements;

import app.controller.game.WorldListener;
import app.view.ui.UiTheme;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import game.audio.AudioId;
import game.audio.AudioManager;
import game.component.AssetManager;

public class ShovelButton extends Widget {
    private static final float MAX_HEIGHT = 64f;

    private final TextureRegion up;
    private final TextureRegion down;
    private final float prefWidth;
    private final float prefHeight;
    private WorldListener listener;
    private boolean selected;

    public ShovelButton() {
        up = resolveRegion(true);
        down = resolveRegion(false);
        TextureRegion region = up != null ? up : down;
        float width = 64f;
        float height = 64f;
        if (region != null) {
            float scale = region.getRegionHeight() > MAX_HEIGHT
                ? MAX_HEIGHT / region.getRegionHeight()
                : 1f;
            width = region.getRegionWidth() * scale;
            height = region.getRegionHeight() * scale;
        }
        prefWidth = width;
        prefHeight = height;
        setSize(width, height);
        setTouchable(Touchable.enabled);
        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                AudioManager.play(AudioId.UI_CLICK);
                if (listener != null) {
                    listener.onShovelToggled();
                }
                return true;
            }
        });
    }

    public void setListener(WorldListener listener) {
        this.listener = listener;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    @Override
    public float getPrefWidth() {
        return prefWidth;
    }

    @Override
    public float getPrefHeight() {
        return prefHeight;
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        if (touchable && getTouchable() != Touchable.enabled) {
            return null;
        }
        if (!isVisible()) {
            return null;
        }
        if (x >= 0f && x < getWidth() && y >= 0f && y < getHeight()) {
            return this;
        }
        return null;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        TextureRegion region = selected && down != null ? down : up;
        if (region == null) {
            return;
        }
        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        batch.draw(region, getX(), getY(), getWidth(), getHeight());
        batch.setColor(Color.WHITE);
    }

    private static TextureRegion resolveRegion(boolean upState) {
        String id = upState
            ? "IMAGE_UI_HUD_INGAME_SHOVEL_BUTTON"
            : "IMAGE_UI_HUD_INGAME_SHOVEL_BUTTON_DOWN";
        TextureRegion region = AssetManager.textureBank.region(id);
        if (region != null) {
            return region;
        }
        if (!UiTheme.hasImageButtonStyle(UiTheme.IMG_BTN_SHOVEL)) {
            return null;
        }
        ImageButton.ImageButtonStyle style = AssetManager.skin.get(
            UiTheme.IMG_BTN_SHOVEL,
            ImageButton.ImageButtonStyle.class
        );
        Object drawable = upState ? style.imageUp : style.imageDown;
        if (drawable instanceof TextureRegionDrawable textureDrawable) {
            return textureDrawable.getRegion();
        }
        return null;
    }
}

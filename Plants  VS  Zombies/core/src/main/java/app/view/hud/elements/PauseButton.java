package app.view.hud.elements;

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

public class PauseButton extends Widget {
    private static final float MAX_HEIGHT = 56f;
    private static final Color HOVER_TINT = new Color(1.12f, 1.12f, 1.05f, 1f);
    private static final Color PRESSED_TINT = new Color(0.88f, 0.88f, 0.88f, 1f);
    private static final Color ACTIVE_TINT = new Color(1.08f, 0.98f, 0.72f, 1f);

    private final TextureRegion up;
    private final TextureRegion down;
    private final float prefWidth;
    private final float prefHeight;
    private Runnable onPause;
    private HudButtonState state = HudButtonState.IDLE;
    private boolean hovered;
    private boolean pressed;
    private boolean menuOpen;

    public PauseButton() {
        up = resolveRegion(true);
        down = resolveRegion(false);
        TextureRegion region = up != null ? up : down;
        float width = 56f;
        float height = 56f;
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
                if (menuOpen || !isVisible()) {
                    return false;
                }
                pressed = true;
                refreshState();
                AudioManager.play(AudioId.UI_CLICK);
                if (onPause != null) {
                    onPause.run();
                }
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                pressed = false;
                refreshState();
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer != -1) {
                    return;
                }
                hovered = true;
                refreshState();
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (pointer != -1) {
                    return;
                }
                hovered = false;
                pressed = false;
                refreshState();
            }
        });
        refreshState();
    }

    public void setOnPause(Runnable onPause) {
        this.onPause = onPause;
    }

    public void setMenuOpen(boolean open) {
        this.menuOpen = open;
        pressed = false;
        if (open) {
            hovered = false;
            setVisible(false);
            setTouchable(Touchable.disabled);
        } else {
            setVisible(true);
            setTouchable(Touchable.enabled);
        }
        refreshState();
    }

    public boolean isMenuOpen() {
        return menuOpen;
    }

    public HudButtonState state() {
        return state;
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
        if (!isVisible()) {
            return null;
        }
        if (touchable && getTouchable() != Touchable.enabled) {
            return null;
        }
        if (x < 0f || x >= getWidth() || y < 0f || y >= getHeight()) {
            return null;
        }
        return this;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (!isVisible()) {
            return;
        }
        TextureRegion region = (state == HudButtonState.PRESSED || state == HudButtonState.ACTIVE)
            && down != null ? down : up;
        if (region == null) {
            return;
        }
        Color tint = tintForState();
        Color color = getColor();
        batch.setColor(
            color.r * tint.r,
            color.g * tint.g,
            color.b * tint.b,
            color.a * tint.a * parentAlpha
        );
        batch.draw(region, getX(), getY(), getWidth(), getHeight());
        batch.setColor(Color.WHITE);
    }

    private void refreshState() {
        if (menuOpen) {
            state = HudButtonState.ACTIVE;
            return;
        }
        if (pressed) {
            state = HudButtonState.PRESSED;
            return;
        }
        if (hovered) {
            state = HudButtonState.HOVER;
            return;
        }
        state = HudButtonState.IDLE;
    }

    private Color tintForState() {
        return switch (state) {
            case HOVER -> HOVER_TINT;
            case PRESSED -> PRESSED_TINT;
            case ACTIVE -> ACTIVE_TINT;
            case IDLE -> Color.WHITE;
        };
    }

    private static TextureRegion resolveRegion(boolean upState) {
        String id = upState
            ? "IMAGE_UI_HUD_INGAME_PAUSE_BUTTON"
            : "IMAGE_UI_HUD_INGAME_PAUSE_BUTTON_DOWN";
        TextureRegion region = AssetManager.textureBank.region(id);
        if (region != null) {
            return region;
        }
        if (!UiTheme.hasImageButtonStyle(UiTheme.IMG_BTN_PAUSE)) {
            return null;
        }
        ImageButton.ImageButtonStyle style = AssetManager.skin.get(
            UiTheme.IMG_BTN_PAUSE,
            ImageButton.ImageButtonStyle.class
        );
        Object drawable = upState ? style.imageUp : style.imageDown;
        if (drawable instanceof TextureRegionDrawable textureDrawable) {
            return textureDrawable.getRegion();
        }
        return null;
    }
}

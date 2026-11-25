package app.view.adventure;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import game.audio.AudioId;
import game.audio.AudioManager;

public final class AdventureHudIconButton extends Image {
    private final AdventureHudConfig.HudIconConfig style;
    private boolean hovered;
    private boolean pressed;
    private boolean enabled = true;
    private Runnable action = () -> {
    };

    public AdventureHudIconButton(Texture texture, AdventureHudConfig.HudIconConfig style, String name) {
        super();
        this.style = style;
        setName(name);
        setOrigin(Align.center);
        if (texture != null) {
            setDrawable(new TextureRegionDrawable(new TextureRegion(texture)));
            setTouchable(Touchable.enabled);
        } else {
            setTouchable(Touchable.disabled);
        }
        addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                if (pointer != -1 || !enabled) {
                    return;
                }
                hovered = true;
                applyVisual(true);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                hovered = false;
                pressed = false;
                applyVisual(true);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                boolean handled = super.touchDown(event, x, y, pointer, button);
                if (enabled) {
                    pressed = true;
                    applyVisual(false);
                }
                return handled;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                pressed = false;
                applyVisual(true);
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!enabled || !isTouchableEnabled()) {
                    return;
                }
                AudioManager.play(AudioId.UI_CLICK);
                action.run();
            }
        });
    }

    public void setAction(Runnable action) {
        this.action = action != null ? action : () -> {
        };
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        setTouchable(enabled && getDrawable() != null ? Touchable.enabled : Touchable.disabled);
        if (!enabled) {
            hovered = false;
            pressed = false;
        }
        applyVisual(false);
    }

    public void place(float x, float y, float width, float height) {
        setBounds(x, y, Math.max(1f, width), Math.max(1f, height));
        setOrigin(getWidth() * 0.5f, getHeight() * 0.5f);
        setRotation(style.rotation);
        Color color = getColor();
        color.a = style.alpha;
        setColor(color);
    }

    public void applyVisual(boolean animate) {
        float target = 1f;
        if (!enabled) {
            target = 1f;
        } else if (pressed) {
            target = style.pressedScale;
        } else if (hovered) {
            target = style.hoverScale;
        }
        clearActions();
        float duration = style.hoverDuration;
        Interpolation easing = style.interpolation();
        if (!animate || duration <= 0f) {
            setScale(target);
        } else {
            addAction(Actions.scaleTo(target, target, duration, easing));
        }
    }

    private boolean isTouchableEnabled() {
        return getTouchable() == Touchable.enabled;
    }
}

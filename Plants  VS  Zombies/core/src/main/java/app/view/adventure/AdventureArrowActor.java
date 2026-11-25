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

public final class AdventureArrowActor extends Image {
    private final boolean right;
    private boolean hovered;
    private boolean pressed;
    private boolean enabled = true;
    private Runnable action = () -> {
    };

    public AdventureArrowActor(Texture texture, boolean right) {
        super();
        this.right = right;
        setName("menu-adventure-arrow");
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
                if (!enabled) {
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

    public boolean right() {
        return right;
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

    public void place(float x, float y, float hitWidth, float hitHeight, float alpha, float rotationDegrees) {
        setBounds(x, y, Math.max(1f, hitWidth), Math.max(1f, hitHeight));
        setOrigin(getWidth() * 0.5f, getHeight() * 0.5f);
        setRotation(rotationDegrees);
        Color color = getColor();
        color.a = alpha;
        setColor(color);
    }

    public void applyVisual(boolean animate) {
        AdventureMenuConfig.ArrowsConfig style = style();
        float target = 1f;
        if (!enabled) {
            target = 1f;
        } else if (pressed) {
            target = style == null ? 0.96f : style.pressedScale;
        } else if (hovered) {
            target = style == null ? 1.08f : style.hoverScale;
        }
        clearActions();
        float duration = style == null ? 0f : style.hoverDuration;
        Interpolation easing = style == null ? Interpolation.sineOut : style.interpolation();
        if (!animate || duration <= 0f) {
            setScale(target);
        } else {
            addAction(Actions.scaleTo(target, target, duration, easing));
        }
    }

    private AdventureMenuConfig.ArrowsConfig style() {
        if (getParent() instanceof AdventureCarousel carousel) {
            return carousel.config().arrows;
        }
        return null;
    }
}

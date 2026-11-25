package app.view.adventure;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import game.audio.AudioId;
import game.audio.AudioManager;
import game.component.AssetManager;

public final class AdventureChapterActor extends Image {
    public enum VisualState {
        NORMAL,
        HOVERED,
        TRANSITIONING,
        LOCKED,
        COMPLETED,
        NEW,
        DISABLED
    }

    private final AdventureMenuConfig.ChapterConfig definition;
    private final float nativeWidth;
    private final float nativeHeight;
    private boolean hovered;
    private boolean front;
    private boolean rotating;
    private float hoverFactor;
    private float bobTime;
    private float shadeAlpha;
    private VisualState visualState = VisualState.NORMAL;
    private Runnable action = () -> {
    };
    private Runnable hoverChanged = () -> {
    };

    public AdventureChapterActor(AdventureMenuConfig.ChapterConfig definition) {
        this.definition = definition;
        setName("menu-adventure-chapter");
        setOrigin(Align.center);
        setScaling(Scaling.fit);
        TextureRegion region = region(definition == null ? "" : definition.worldAsset);
        if (region != null) {
            setDrawable(new TextureRegionDrawable(region));
            nativeWidth = Math.max(1f, region.getRegionWidth());
            nativeHeight = Math.max(1f, region.getRegionHeight());
        } else {
            nativeWidth = 128f;
            nativeHeight = 128f;
        }
        setTouchable(Touchable.disabled);
        addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                if (pointer != -1 || !front || rotating) {
                    return;
                }
                hovered = true;
                updateVisualState();
                hoverChanged.run();
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                if (hovered) {
                    hovered = false;
                    updateVisualState();
                    hoverChanged.run();
                }
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!front || rotating || definition == null || !definition.interactive()) {
                    return;
                }
                AudioManager.play(AudioId.UI_CLICK);
                action.run();
            }
        });
        updateVisualState();
    }

    public void setAction(Runnable action) {
        this.action = action != null ? action : () -> {
        };
    }

    public void setHoverChanged(Runnable hoverChanged) {
        this.hoverChanged = hoverChanged != null ? hoverChanged : () -> {
        };
    }

    public AdventureMenuConfig.ChapterConfig definition() {
        return definition;
    }

    public boolean hovered() {
        return hovered && front && !rotating;
    }

    public void updateHover(float delta, float duration) {
        float target = hovered() ? 1f : 0f;
        if (duration <= 0f) {
            hoverFactor = target;
        } else {
            float step = Math.max(0f, delta) / duration;
            if (hoverFactor < target) {
                hoverFactor = Math.min(target, hoverFactor + step);
            } else if (hoverFactor > target) {
                hoverFactor = Math.max(target, hoverFactor - step);
            }
        }
        if (hoverFactor > 0.001f) {
            bobTime += Math.max(0f, delta);
        } else {
            bobTime = 0f;
        }
    }

    public VisualState visualState() {
        return visualState;
    }

    public void setFront(boolean front) {
        this.front = front;
        if (!front && hovered) {
            hovered = false;
            hoverFactor = 0f;
            bobTime = 0f;
        }
        setTouchable(front && !rotating ? Touchable.enabled : Touchable.disabled);
        updateVisualState();
    }

    public void setRotating(boolean rotating) {
        this.rotating = rotating;
        if (rotating && hovered) {
            hovered = false;
            hoverFactor = 0f;
            bobTime = 0f;
        }
        setTouchable(front && !rotating ? Touchable.enabled : Touchable.disabled);
        updateVisualState();
    }

    public void clearHover() {
        hoverFactor = 0f;
        bobTime = 0f;
        if (!hovered) {
            return;
        }
        hovered = false;
        updateVisualState();
        hoverChanged.run();
    }

    public void applySlot(
        AdventureCarouselMath.Slot slot,
        float targetWidth,
        float targetHeight,
        AdventureMenuConfig.ChapterStyleConfig style,
        Color tint
    ) {
        if (slot == null || !slot.render) {
            setVisible(false);
            shadeAlpha = 0f;
            return;
        }
        float hoverScale = style == null ? 1.08f : style.hoverScale;
        float hoverDarkness = style == null ? 0.05f : style.hoverDarkness;
        float bobAmplitude = style == null ? 8f : style.hoverBobAmplitude;
        float bobSpeed = style == null ? 5.5f : style.hoverBobSpeed;
        setVisible(true);
        float visualScale = slot.scale * (1f + (hoverScale - 1f) * hoverFactor);
        float fit = Math.min(targetWidth / nativeWidth, targetHeight / nativeHeight);
        float drawWidth = Math.max(1f, nativeWidth * fit * visualScale);
        float drawHeight = Math.max(1f, nativeHeight * fit * visualScale);
        float bobOffset = hoverFactor <= 0.001f
            ? 0f
            : (float) Math.sin(bobTime * bobSpeed) * bobAmplitude * hoverFactor;
        setBounds(
            slot.centerX - drawWidth * 0.5f,
            slot.centerY - drawHeight * 0.5f + bobOffset,
            drawWidth,
            drawHeight
        );
        setOrigin(drawWidth * 0.5f, drawHeight * 0.5f);
        Color color = tint == null ? Color.WHITE : tint;
        float darkness = Math.max(0f, Math.min(1f, hoverDarkness)) * hoverFactor;
        float tintValue = 1f - darkness;
        setColor(color.r * tintValue, color.g * tintValue, color.b * tintValue, slot.alpha * color.a);
        shadeAlpha = Math.max(0f, Math.min(1f, slot.shade));
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (shadeAlpha <= 0.001f || !isVisible()) {
            return;
        }
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }
        Color previous = batch.getColor();
        float overlayAlpha = getColor().a * parentAlpha * shadeAlpha;
        batch.setColor(0f, 0f, 0f, overlayAlpha);
        drawable.draw(batch, getX(), getY(), getWidth(), getHeight());
        batch.setColor(previous);
    }

    private void updateVisualState() {
        if (rotating) {
            visualState = VisualState.TRANSITIONING;
            return;
        }
        if (definition != null && !definition.unlocked) {
            visualState = VisualState.LOCKED;
            return;
        }
        if (definition != null && !definition.selectable) {
            visualState = VisualState.DISABLED;
            return;
        }
        visualState = hovered && front ? VisualState.HOVERED : VisualState.NORMAL;
    }

    private static TextureRegion region(String id) {
        if (id == null || id.isBlank() || AssetManager.textureBank == null) {
            return null;
        }
        try {
            return AssetManager.textureBank.region(id);
        } catch (RuntimeException exception) {
            Gdx.app.error("AdventureChapterActor", "Missing libPVZ region: " + id, exception);
            return null;
        }
    }
}

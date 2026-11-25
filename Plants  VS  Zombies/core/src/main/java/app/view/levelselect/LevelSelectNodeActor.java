package app.view.levelselect;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public final class LevelSelectNodeActor extends LevelSelectPamActor {
    private final LevelSelectMenuConfig.LevelAnimationConfig animation;
    private final LevelSelectMenuConfig.HoverConfig hover;
    private LevelSelectProgress.NodeState state = LevelSelectProgress.NodeState.LOCKED;
    private boolean hovered;
    private float hoverFactor;
    private Runnable clickAction = () -> {
    };

    public LevelSelectNodeActor(
        LevelSelectMenuConfig.LevelAnimationConfig animation,
        LevelSelectMenuConfig.HoverConfig hover
    ) {
        super(
            animation == null ? "" : animation.pamPath,
            animation == null ? "locked_idle" : animation.lockedIdleClip,
            true
        );
        this.animation = animation == null ? new LevelSelectMenuConfig.LevelAnimationConfig() : animation;
        this.hover = hover == null ? new LevelSelectMenuConfig.HoverConfig() : hover;
        setName("menu-level-select-node");
        setTouchable(Touchable.enabled);
        addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                if (pointer != -1) {
                    return;
                }
                hovered = true;
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                hovered = false;
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                clickAction.run();
            }
        });
        applyState(LevelSelectProgress.NodeState.LOCKED, true);
    }

    public void setClickAction(Runnable clickAction) {
        this.clickAction = clickAction != null ? clickAction : () -> {
        };
    }

    public LevelSelectProgress.NodeState state() {
        return state;
    }

    public void applyState(LevelSelectProgress.NodeState next, boolean restartUnlock) {
        LevelSelectProgress.NodeState resolved = next == null ? LevelSelectProgress.NodeState.LOCKED : next;
        boolean same = resolved == state;
        state = resolved;
        switch (state) {
            case LOCKED -> setClip(animation.lockedIdleClip, true, !same);
            case UNLOCKED -> setClip(animation.unlockedIdleClip, true, !same);
            case NEWLY_UNLOCKED -> {
                setClip(animation.unlockAnimationClip, false, restartUnlock || !same);
                if (!ready()) {
                    state = LevelSelectProgress.NodeState.UNLOCKED;
                    setClip(animation.unlockedIdleClip, true, true);
                }
            }
        }
    }

    public void updateHover(float delta) {
        float duration = hover.levelDuration;
        float target = hovered && hover.levelEnabled ? 1f : 0f;
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
        if (state == LevelSelectProgress.NodeState.NEWLY_UNLOCKED && finished()) {
            applyState(LevelSelectProgress.NodeState.UNLOCKED, true);
        }
    }

    public void applySlot(
        LevelSelectLayoutMath.Slot slot,
        float time,
        float hitPad
    ) {
        if (slot == null || !slot.render) {
            setVisible(false);
            return;
        }
        setVisible(true);
        float baseScale = Math.max(0.01f, slot.scale * animation.scale);
        float hoverScale = hover.levelEnabled ? 1f + (hover.levelScale - 1f) * hoverFactor : 1f;
        float visual = baseScale * hoverScale;
        float floatY = slot.floatAmplitude <= 0f
            ? 0f
            : (float) Math.sin(time * slot.floatSpeed + slot.floatPhase) * slot.floatAmplitude;
        float hoverY = hover.levelEnabled ? hover.levelOffsetY * hoverFactor : 0f;
        applyVisualSize(visual);
        setCenter(slot.centerX + animation.offsetX * baseScale, slot.centerY + animation.offsetY * baseScale + floatY + hoverY);
        float pad = Math.max(0f, hitPad);
        if (pad > 0f) {
            float cx = getX() + getOriginX();
            float cy = getY() + getOriginY();
            setSize(getWidth() + pad * 2f, getHeight() + pad * 2f);
            setOrigin(getWidth() * 0.5f, getHeight() * 0.5f);
            setPosition(cx - getOriginX(), cy - getOriginY());
        }
        boolean flipX = animation.flipX || (slot.side < 0 && animation.flipLeft) || (slot.side > 0 && animation.flipRight);
        if (animation.flipTowardCenter) {
            flipX = slot.side < 0 || animation.flipX;
        }
        setFlip(flipX, animation.flipY);
        setVisualRotation(animation.rotation);
        float alpha = slot.alpha * animation.alpha;
        if (hover.levelEnabled) {
            alpha *= (1f + (hover.levelAlpha - 1f) * hoverFactor);
        }
        float darkness = Math.max(0f, Math.min(1f, slot.darkness));
        if (hover.levelEnabled) {
            darkness = Math.max(0f, Math.min(1f, darkness + hover.levelDarkness * hoverFactor));
        }
        float tint = 1f - darkness;
        setColor(tint, tint, tint, Math.max(0f, Math.min(1f, alpha)));
    }
}

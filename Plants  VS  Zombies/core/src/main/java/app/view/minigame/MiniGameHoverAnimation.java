package app.view.minigame;

import app.view.levelselect.LevelSelectPamActor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;

public final class MiniGameHoverAnimation {
    private final MiniGameDefinition definition;
    private final MiniGameMenuConfig.HoverAnimationConfig config;
    private final MiniGameHoverLifecycle lifecycle;
    private final LevelSelectPamActor actor;
    private final Vector2 scratch = new Vector2();

    public MiniGameHoverAnimation(
        MiniGameDefinition definition,
        MiniGameMenuConfig.HoverAnimationConfig config
    ) {
        this.definition = definition;
        this.config = config == null ? new MiniGameMenuConfig.HoverAnimationConfig() : config;
        this.config.sanitize();
        this.lifecycle = new MiniGameHoverLifecycle(this.config);
        String initialClip = firstNonBlank(this.config.startClip, this.config.resolvedLoopClip(), this.config.clip);
        this.actor = new LevelSelectPamActor(this.config.pamPath, initialClip, this.config.loop);
        this.actor.setTouchable(Touchable.disabled);
        this.actor.setVisible(false);
        this.actor.setName("menu-minigame-hover");
    }

    public Actor actor() {
        return actor;
    }

    public MiniGameHoverLifecycle.State state() {
        return lifecycle.state();
    }

    public void enter() {
        lifecycle.enter();
        applyClip();
    }

    public void exit() {
        lifecycle.exit();
        applyClip();
    }

    public void advance() {
        if (!lifecycle.visible()) {
            return;
        }
        MiniGameHoverLifecycle.State state = lifecycle.state();
        if (state == MiniGameHoverLifecycle.State.HOVER_START
            || state == MiniGameHoverLifecycle.State.HOVER_END) {
            if (actor.finished() || !actor.hasClip()) {
                lifecycle.onClipFinished();
                applyClip();
            }
        }
    }

    public void layout(MiniGameMapSpace space, float viewWidth, float viewHeight) {
        if (space == null) {
            return;
        }
        float mapX = definition.centerX() + config.offsetX;
        float mapY = definition.centerY() + config.offsetY;
        space.toStage(mapX, mapY, viewWidth, viewHeight, scratch);
        float mapScale = config.scaleWithMap ? space.uniformScale(viewWidth, viewHeight) : 1f;
        float drawScaleX = config.resolvedScaleX() * mapScale;
        float drawScaleY = config.resolvedScaleY() * mapScale;
        actor.setVisualScale(drawScaleX, drawScaleY);
        actor.setFlip(config.flipX, config.flipY);
        actor.setVisualRotation(config.rotation);
        actor.setColor(1f, 1f, 1f, config.alpha);
        float width = Math.max(1f, actor.nativeWidth() * Math.abs(drawScaleX));
        float height = Math.max(1f, actor.nativeHeight() * Math.abs(drawScaleY));
        actor.setSize(width, height);
        float originX = width * config.originX + config.originOffsetX * mapScale;
        float originY = height * config.originY + config.originOffsetY * mapScale;
        actor.setOrigin(originX, originY);
        actor.setPosition(scratch.x - originX, scratch.y - originY);
    }

    private void applyClip() {
        if (!lifecycle.visible()) {
            actor.setVisible(false);
            return;
        }
        actor.setVisible(true);
        String clip = lifecycle.clip();
        if (clip == null || clip.isBlank()) {
            actor.setVisible(false);
            return;
        }
        actor.setClip(clip, lifecycle.loop(), lifecycle.consumeRestart());
        if (!actor.hasClip()
            && (lifecycle.state() == MiniGameHoverLifecycle.State.HOVER_START
            || lifecycle.state() == MiniGameHoverLifecycle.State.HOVER_END)) {
            lifecycle.onClipFinished();
            if (!lifecycle.visible()) {
                actor.setVisible(false);
                return;
            }
            actor.setClip(lifecycle.clip(), lifecycle.loop(), lifecycle.consumeRestart());
        }
    }

    private static String firstNonBlank(String... values) {
        if (values == null) {
            return "";
        }
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }
        return "";
    }
}

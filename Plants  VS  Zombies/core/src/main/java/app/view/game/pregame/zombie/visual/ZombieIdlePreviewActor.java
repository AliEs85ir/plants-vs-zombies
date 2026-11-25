package app.view.game.pregame.zombie.visual;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import game.component.AssetManager;
import graphics.general.animation.PamClipResolver;
import pvz.libpvz.pam.PamPlayer;

public class ZombieIdlePreviewActor extends Actor {
    private static final float FIT_PADDING = 0.85f;

    private final ZombieIdlePreviewConfigLoader.ZombieIdlePreviewConfig config;
    private final PamPlayer player;
    private final String clip;
    private final Matrix4 originalTransform = new Matrix4();
    private final Matrix4 scaledTransform = new Matrix4();
    private float stateTime;
    private float nativeWidth = 1f;
    private float nativeHeight = 1f;

    public ZombieIdlePreviewActor(ZombieIdlePreviewConfigLoader.ZombieIdlePreviewConfig config) {
        this.config = config;
        this.player = AssetManager.player;
        this.clip = PamClipResolver.resolve(player, config.pamPath, config.clip);
        if (clip == null) {
            return;
        }
        Rectangle bounds = player.bounds(config.pamPath, clip);
        if (bounds != null && bounds.width > 1f && bounds.height > 1f) {
            nativeWidth = bounds.width;
            nativeHeight = bounds.height;
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (clip == null) {
            return;
        }
        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);

        float fit = Math.min(getWidth() / nativeWidth, getHeight() / nativeHeight) * FIT_PADDING;
        float scale = Math.max(0.01f, fit * config.scale);
        float cx = getX() + getWidth() * 0.5f + config.offsetX;
        float cy = getY() + getHeight() * 0.5f + config.offsetY;

        batch.flush();
        originalTransform.set(batch.getTransformMatrix());
        scaledTransform.set(originalTransform);
        scaledTransform.translate(cx, cy, 0f);
        scaledTransform.scale(scale, scale, 1f);
        scaledTransform.translate(-cx, -cy, 0f);
        batch.setTransformMatrix(scaledTransform);

        player.draw(
            batch,
            config.pamPath,
            clip,
            stateTime,
            cx,
            cy,
            config.loop
        );

        batch.flush();
        batch.setTransformMatrix(originalTransform);
        batch.setColor(Color.WHITE);
    }
}

package app.view.hud.elements;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import game.component.AssetManager;
import graphics.general.animation.PamClipResolver;
import pvz.libpvz.pam.PamPlayer;

public class HudPamActor extends Actor {
    private final PamPlayer player;
    private final String pamPath;
    private final String clip;
    private final boolean loop;
    private final float drawScale;
    private final float offsetX;
    private final float offsetY;
    private final Matrix4 originalTransform = new Matrix4();
    private final Matrix4 scaledTransform = new Matrix4();
    private float stateTime;
    private float nativeWidth = 1f;
    private float nativeHeight = 1f;

    public HudPamActor(String pamPath, String requestedClip, boolean loop, float drawScale, float offsetX, float offsetY) {
        this.player = AssetManager.player;
        this.pamPath = pamPath;
        this.clip = PamClipResolver.resolve(player, pamPath, requestedClip);
        this.loop = loop;
        this.drawScale = drawScale <= 0f ? 1f : drawScale;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        if (clip != null) {
            Rectangle bounds = player.bounds(pamPath, clip);
            if (bounds != null && bounds.width > 1f && bounds.height > 1f) {
                nativeWidth = bounds.width;
                nativeHeight = bounds.height;
            }
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (isVisible()) {
            stateTime += delta;
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (!isVisible() || clip == null) {
            return;
        }
        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);

        float fit = Math.min(getWidth() / nativeWidth, getHeight() / nativeHeight);
        float scale = Math.max(0.01f, fit * drawScale);
        float cx = getX() + getWidth() * 0.5f + offsetX;
        float cy = getY() + getHeight() * 0.5f + offsetY;

        batch.flush();
        originalTransform.set(batch.getTransformMatrix());
        scaledTransform.set(originalTransform);
        scaledTransform.translate(cx, cy, 0f);
        scaledTransform.scale(scale, scale, 1f);
        scaledTransform.translate(-cx, -cy, 0f);
        batch.setTransformMatrix(scaledTransform);

        player.draw(batch, pamPath, clip, stateTime, cx, cy, loop);

        batch.flush();
        batch.setTransformMatrix(originalTransform);
        batch.setColor(Color.WHITE);
    }

    public void restart() {
        stateTime = 0f;
    }
}

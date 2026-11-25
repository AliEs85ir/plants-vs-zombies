package app.view.levelselect;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import game.component.AssetManager;
import pvz.libpvz.pam.ClipRef;
import pvz.libpvz.pam.PamPlayer;

public class LevelSelectPamActor extends Actor {
    private final PamPlayer player;
    private final String pamPath;
    private final Matrix4 oldTransform = new Matrix4();
    private final Matrix4 drawTransform = new Matrix4();
    private String requestedClip = "";
    private String resolvedClip;
    private ClipRef clipRef;
    private boolean loop = true;
    private boolean flipX;
    private boolean flipY;
    private float visualScale = 1f;
    private float visualScaleX = 1f;
    private float visualScaleY = 1f;
    private float visualRotation;
    private float stateTime;
    private float nativeWidth = 128f;
    private float nativeHeight = 128f;
    private boolean loaded;

    public LevelSelectPamActor(String pamPath, String clip, boolean loop) {
        this.player = AssetManager.player;
        this.pamPath = pamPath == null ? "" : pamPath;
        this.loop = loop;
        setTouchable(Touchable.disabled);
        loaded = LevelSelectClipResolver.ensureLoaded(player, this.pamPath);
        setClip(clip, loop, true);
        measure();
        setSize(nativeWidth, nativeHeight);
        setOrigin(nativeWidth * 0.5f, nativeHeight * 0.5f);
    }

    public boolean ready() {
        return loaded && resolvedClip != null && player != null;
    }

    public boolean hasClip() {
        return resolvedClip != null && clipRef != null;
    }

    public String pamPath() {
        return pamPath;
    }

    public void setClip(String clip, boolean loop, boolean restart) {
        this.loop = loop;
        String requested = clip == null ? "" : clip.trim();
        boolean same = requested.equals(requestedClip) && resolvedClip != null;
        requestedClip = requested;
        if (!loaded || player == null) {
            resolvedClip = null;
            clipRef = null;
            return;
        }
        if (!same) {
            resolvedClip = requestedClip.isBlank()
                ? null
                : LevelSelectClipResolver.resolveOrLog(player, pamPath, requestedClip);
            clipRef = null;
            if (resolvedClip != null) {
                try {
                    clipRef = player.getClip(pamPath, resolvedClip);
                } catch (RuntimeException exception) {
                    Gdx.app.error("LevelSelectPamActor", "Invalid clip " + resolvedClip + " in " + pamPath, exception);
                    resolvedClip = null;
                }
            }
            measure();
        }
        if (restart || !same) {
            stateTime = 0f;
        }
    }

    public void setVisualScale(float scale) {
        setVisualScale(scale, scale);
    }

    public void setVisualScale(float scaleX, float scaleY) {
        visualScaleX = finiteScale(scaleX);
        visualScaleY = finiteScale(scaleY);
        visualScale = visualScaleX;
    }

    public void setVisualRotation(float rotation) {
        visualRotation = Float.isNaN(rotation) ? 0f : rotation;
    }

    public void setFlip(boolean flipX, boolean flipY) {
        this.flipX = flipX;
        this.flipY = flipY;
    }

    public float nativeWidth() {
        return nativeWidth;
    }

    public float nativeHeight() {
        return nativeHeight;
    }

    public boolean loop() {
        return loop;
    }

    public boolean finished() {
        if (loop || clipRef == null) {
            return false;
        }
        return clipRef.duration > 0f && stateTime >= clipRef.duration;
    }

    public void setCenter(float x, float y) {
        setPosition(x - getOriginX(), y - getOriginY());
    }

    public void applyVisualSize(float scale) {
        float safe = scale <= 0f || Float.isNaN(scale) ? 1f : scale;
        float width = Math.max(1f, nativeWidth * safe);
        float height = Math.max(1f, nativeHeight * safe);
        float cx = getX() + getOriginX();
        float cy = getY() + getOriginY();
        setSize(width, height);
        setOrigin(width * 0.5f, height * 0.5f);
        setPosition(cx - getOriginX(), cy - getOriginY());
        setVisualScale(safe);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (isVisible() && ready()) {
            stateTime += Math.max(0f, delta);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (!isVisible() || !ready()) {
            return;
        }
        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        float cx = getX() + getOriginX();
        float cy = getY() + getOriginY();
        float scaleX = visualScaleX * (flipX ? -1f : 1f);
        float scaleY = visualScaleY * (flipY ? -1f : 1f);
        boolean rotate = visualRotation != 0f;
        if (rotate) {
            batch.flush();
            oldTransform.set(batch.getTransformMatrix());
            drawTransform.set(oldTransform);
            drawTransform.translate(cx, cy, 0f);
            drawTransform.rotate(0f, 0f, 1f, visualRotation);
            drawTransform.translate(-cx, -cy, 0f);
            batch.setTransformMatrix(drawTransform);
        }
        try {
            player.draw(batch, pamPath, resolvedClip, stateTime, cx, cy, scaleX, scaleY, loop);
        } catch (RuntimeException exception) {
            Gdx.app.error("LevelSelectPamActor", "Failed to draw " + pamPath + " clip " + resolvedClip, exception);
            resolvedClip = null;
            clipRef = null;
        }
        if (rotate) {
            batch.flush();
            batch.setTransformMatrix(oldTransform);
        }
        batch.setColor(Color.WHITE);
    }

    private static float finiteScale(float value) {
        if (Float.isNaN(value)) {
            return 1f;
        }
        return value;
    }

    private void measure() {
        if (!loaded || player == null) {
            return;
        }
        try {
            Rectangle bounds = resolvedClip == null
                ? player.bounds(pamPath)
                : player.bounds(pamPath, resolvedClip);
            if (bounds != null && bounds.width > 1f && bounds.height > 1f) {
                nativeWidth = bounds.width;
                nativeHeight = bounds.height;
            }
        } catch (RuntimeException ignored) {
            nativeWidth = 128f;
            nativeHeight = 128f;
        }
    }
}

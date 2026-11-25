package graphics.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import graphics.general.animation.AnimationLibrary;
import graphics.general.animation.PamClipResolver;
import pvz.libpvz.pam.PamPlayer;

public final class ScaledPamOverlay {
    private final String pamPath;
    private final String clip;
    private final boolean loop;
    private String resolvedClip;
    private float stateTime;
    private float x;
    private float y;
    private float scaleX = 1f;
    private float scaleY = 1f;

    public ScaledPamOverlay(String pamPath, String clip, boolean loop) {
        this.pamPath = pamPath;
        this.clip = clip;
        this.loop = loop;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setScale(float scaleX, float scaleY) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }

    public String getPamPath() {
        return pamPath;
    }

    public void update(float delta) {
        stateTime += delta;
    }

    public void render(SpriteBatch batch, PamPlayer pamPlayer) {
        if (pamPath == null || clip == null || pamPlayer == null) {
            return;
        }
        if (resolvedClip == null) {
            resolvedClip = PamClipResolver.resolve(pamPlayer, pamPath, clip);
            if (resolvedClip == null) {
                return;
            }
        }
        Matrix4 original = batch.getTransformMatrix().cpy();
        boolean scaled = scaleX != 1f || scaleY != 1f;
        if (scaled) {
            batch.end();
            batch.getTransformMatrix()
                .translate(x, y, 0)
                .scale(scaleX, scaleY, 1f)
                .translate(-x, -y, 0);
            batch.begin();
        }
        pamPlayer.draw(batch, pamPath, resolvedClip, stateTime, x, y, loop);
        if (scaled) {
            batch.end();
            batch.setTransformMatrix(original);
            batch.begin();
        }
    }

    public void ensureLoaded(AnimationLibrary library) {
        if (library != null && pamPath != null) {
            library.ensureLoaded(pamPath);
        }
    }
}

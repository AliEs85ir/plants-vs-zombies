package app.view.game;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public final class ScreenShake {
    private float duration;
    private float remaining;
    private float intensity;
    private final Vector2 offset = new Vector2();

    public void shake(float duration, float intensity) {
        this.duration = Math.max(0f, duration);
        this.remaining = this.duration;
        this.intensity = Math.max(0f, intensity);
        if (this.remaining <= 0f || this.intensity <= 0f) {
            offset.setZero();
        }
    }

    public void update(float delta) {
        if (remaining <= 0f) {
            offset.setZero();
            return;
        }
        remaining = Math.max(0f, remaining - Math.max(0f, delta));
        if (remaining <= 0f || duration <= 0f) {
            remaining = 0f;
            offset.setZero();
            return;
        }
        float falloff = remaining / duration;
        float magnitude = intensity * falloff;
        offset.set(
            (MathUtils.random() * 2f - 1f) * magnitude,
            (MathUtils.random() * 2f - 1f) * magnitude
        );
    }

    public Vector2 offset() {
        return offset;
    }

    public boolean isActive() {
        return remaining > 0f;
    }

    public float getDuration() {
        return duration;
    }

    public float getIntensity() {
        return intensity;
    }

    public float getRemaining() {
        return remaining;
    }

    public void stop() {
        remaining = 0f;
        offset.setZero();
    }
}

package app.view.hud.notification;

import com.badlogic.gdx.math.Interpolation;

public class NotificationMotion {
    public float duration = 0.35f;
    public float delay = 0f;
    public float offsetX = 0f;
    public float offsetY = 0f;
    public float startAlpha = 1f;
    public float endAlpha = 1f;
    public float startScale = 1f;
    public float endScale = 1f;
    public Interpolation interpolation = Interpolation.pow2Out;

    public NotificationMotion duration(float duration) {
        this.duration = Math.max(0f, duration);
        return this;
    }

    public NotificationMotion delay(float delay) {
        this.delay = Math.max(0f, delay);
        return this;
    }

    public NotificationMotion offset(float offsetX, float offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        return this;
    }

    public NotificationMotion alpha(float startAlpha, float endAlpha) {
        this.startAlpha = startAlpha;
        this.endAlpha = endAlpha;
        return this;
    }

    public NotificationMotion scale(float startScale, float endScale) {
        this.startScale = startScale;
        this.endScale = endScale;
        return this;
    }

    public NotificationMotion interpolation(Interpolation interpolation) {
        if (interpolation != null) {
            this.interpolation = interpolation;
        }
        return this;
    }

    public NotificationMotion copy() {
        NotificationMotion copy = new NotificationMotion();
        copy.duration = duration;
        copy.delay = delay;
        copy.offsetX = offsetX;
        copy.offsetY = offsetY;
        copy.startAlpha = startAlpha;
        copy.endAlpha = endAlpha;
        copy.startScale = startScale;
        copy.endScale = endScale;
        copy.interpolation = interpolation;
        return copy;
    }
}

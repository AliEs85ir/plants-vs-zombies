package app.view.hud.notification;

import com.badlogic.gdx.math.Interpolation;

public class NotificationAnimationSpec {
    public NotificationMotion iconEnter = new NotificationMotion()
        .duration(0.36f)
        .delay(0f)
        .offset(-26f, 8f)
        .alpha(0f, 1f)
        .scale(0.88f, 1f)
        .interpolation(Interpolation.pow2Out);

    public NotificationMotion textEnter = new NotificationMotion()
        .duration(0.34f)
        .delay(0.08f)
        .offset(-18f, 0f)
        .alpha(0f, 1f)
        .scale(0.96f, 1f)
        .interpolation(Interpolation.pow2Out);

    public NotificationMotion contentIdle = new NotificationMotion()
        .duration(1.55f)
        .offset(0f, 2.4f)
        .scale(1f, 1.018f)
        .interpolation(Interpolation.sine);

    public NotificationMotion iconExit = new NotificationMotion()
        .duration(0.28f)
        .delay(0f)
        .offset(-16f, 6f)
        .alpha(1f, 0f)
        .scale(1f, 0.9f)
        .interpolation(Interpolation.pow2In);

    public NotificationMotion textExit = new NotificationMotion()
        .duration(0.3f)
        .delay(0.05f)
        .offset(-22f, 0f)
        .alpha(1f, 0f)
        .scale(1f, 0.94f)
        .interpolation(Interpolation.pow2In);

    public static NotificationAnimationSpec defaults() {
        return new NotificationAnimationSpec();
    }

    public static NotificationAnimationSpec fadeOnly(float enterSeconds, float exitSeconds) {
        NotificationAnimationSpec spec = new NotificationAnimationSpec();
        NotificationMotion enter = new NotificationMotion()
            .duration(enterSeconds)
            .offset(0f, 0f)
            .alpha(0f, 1f)
            .scale(1f, 1f)
            .interpolation(Interpolation.fade);
        NotificationMotion exit = new NotificationMotion()
            .duration(exitSeconds)
            .offset(0f, 0f)
            .alpha(1f, 0f)
            .scale(1f, 1f)
            .interpolation(Interpolation.fade);
        spec.iconEnter(enter.copy());
        spec.textEnter(enter.copy());
        spec.iconExit(exit.copy());
        spec.textExit(exit.copy());
        spec.contentIdle(new NotificationMotion().duration(0f).scale(1f, 1f).offset(0f, 0f));
        return spec;
    }

    public static NotificationAnimationSpec scoringPop() {
        NotificationAnimationSpec spec = new NotificationAnimationSpec();
        NotificationMotion enter = new NotificationMotion()
            .duration(0.32f)
            .offset(0f, -64f)
            .alpha(0f, 1f)
            .scale(0.28f, 1.24f)
            .interpolation(Interpolation.swingOut);
        NotificationMotion idle = new NotificationMotion()
            .duration(0.26f)
            .offset(0f, 10f)
            .scale(1f, 1.1f)
            .interpolation(Interpolation.sine);
        NotificationMotion exit = new NotificationMotion()
            .duration(0.4f)
            .offset(0f, 56f)
            .alpha(1f, 0f)
            .scale(1f, 1.4f)
            .interpolation(Interpolation.pow2In);
        spec.iconEnter(enter.copy());
        spec.textEnter(enter.copy().delay(0.06f));
        spec.contentIdle(idle);
        spec.iconExit(exit.copy());
        spec.textExit(exit.copy().delay(0.05f));
        return spec;
    }

    public NotificationAnimationSpec iconEnter(NotificationMotion motion) {
        if (motion != null) {
            this.iconEnter = motion;
        }
        return this;
    }

    public NotificationAnimationSpec textEnter(NotificationMotion motion) {
        if (motion != null) {
            this.textEnter = motion;
        }
        return this;
    }

    public NotificationAnimationSpec contentIdle(NotificationMotion motion) {
        if (motion != null) {
            this.contentIdle = motion;
        }
        return this;
    }

    public NotificationAnimationSpec iconExit(NotificationMotion motion) {
        if (motion != null) {
            this.iconExit = motion;
        }
        return this;
    }

    public NotificationAnimationSpec textExit(NotificationMotion motion) {
        if (motion != null) {
            this.textExit = motion;
        }
        return this;
    }

    public float enterDuration() {
        return Math.max(
            iconEnter.delay + iconEnter.duration,
            textEnter.delay + textEnter.duration
        );
    }

    public float exitDuration() {
        return Math.max(
            iconExit.delay + iconExit.duration,
            textExit.delay + textExit.duration
        );
    }

    public NotificationAnimationSpec copy() {
        NotificationAnimationSpec copy = new NotificationAnimationSpec();
        copy.iconEnter = iconEnter.copy();
        copy.textEnter = textEnter.copy();
        copy.contentIdle = contentIdle.copy();
        copy.iconExit = iconExit.copy();
        copy.textExit = textExit.copy();
        return copy;
    }
}

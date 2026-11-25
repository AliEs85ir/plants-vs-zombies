package graphics.general.modifier.visualModifiers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class FadeModifier implements VisualModifier{
    private float time;
    private final float duration;
    private final float fromAlpha;
    private final float toAlpha;

    public FadeModifier(float duration) {
        this(duration, 1f, 0f);
    }

    public FadeModifier(float duration, float fromAlpha, float toAlpha) {
        this.duration = duration;
        this.fromAlpha = fromAlpha;
        this.toAlpha = toAlpha;
        this.time = 0f;
    }

    @Override
    public void update(float delta) {
        if (!isFinished()) {
            time += delta;
        }
    }

    @Override
    public void apply(SpriteBatch batch) {
        if (isFinished()) {
            return;
        }

        float progress = duration > 0f
            ? Math.min(1f, time / duration)
            : 1f;

        float alpha = fromAlpha + (toAlpha - fromAlpha) * progress;

        Color color = batch.getColor();
        batch.setColor(color.r, color.g, color.b, alpha);
    }

    @Override
    public boolean isFinished() {
        return duration <= 0f || time >= duration;
    }
}

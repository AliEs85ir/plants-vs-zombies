package graphics.general.modifier.visualModifiers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class RedDamageModifier implements VisualModifier{

    private float time;
    private final float duration;

    private final float startR = 1f;
    private final float startG = 0.7f;
    private final float startB = 0.4f;

    public RedDamageModifier(float duration) {
        this.duration = duration;
        this.time = 0f;
    }

    @Override
    public void update(float delta) {
        time += delta;
    }

    @Override
    public void apply(SpriteBatch batch) {
        if (isFinished()) {
            batch.setColor(1f, 1f, 1f, 1f);
            return;
        }

        float progress = time / duration;

        float currentR = startR + (1f - startR) * progress;
        float currentG = startG + (1f - startG) * progress;
        float currentB = startB + (1f - startB) * progress;

        batch.setColor(currentR, currentG, currentB, 1f);
    }

    @Override
    public boolean isFinished() {
        return time >= duration;
    }
}

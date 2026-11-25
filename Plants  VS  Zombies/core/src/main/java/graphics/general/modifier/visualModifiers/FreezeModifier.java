package graphics.general.modifier.visualModifiers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class FreezeModifier implements VisualModifier{
    private float time;
    private final float duration;

    private final float iceR = 0.35f;
    private final float iceG = 0.60f;
    private final float iceB = 1.00f;

    public FreezeModifier(float duration) {
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

        batch.setColor(iceR, iceG, iceB, 1f);
    }

    @Override
    public boolean isFinished() {
        return time >= duration;
    }
}

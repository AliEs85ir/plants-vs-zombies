package graphics.general.modifier.visualModifiers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GreenModifier implements VisualModifier {
    private static final float GREEN_RED = 0.35f;
    private static final float GREEN_GREEN = 1f;
    private static final float GREEN_BLUE = 0.35f;

    private final float pulsesPerSecond;
    private float time;
    private boolean finished;

    public GreenModifier() {
        this(1.4f);
    }

    public GreenModifier(float pulsesPerSecond) {
        this.pulsesPerSecond = pulsesPerSecond <= 0f ? 1.4f : pulsesPerSecond;
    }

    @Override
    public void update(float delta) {
        time += delta;
    }

    @Override
    public void apply(SpriteBatch batch) {
        if (finished) {
            return;
        }
        float wave = (float) ((Math.sin(time * pulsesPerSecond * Math.PI * 2.0) + 1.0) * 0.5);
        batch.setColor(
            1f + (GREEN_RED - 1f) * wave,
            1f + (GREEN_GREEN - 1f) * wave,
            1f + (GREEN_BLUE - 1f) * wave,
            1f
        );
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    public void stop() {
        finished = true;
    }
}

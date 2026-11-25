package graphics.general.modifier.visualModifiers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class PulseTintModifier implements VisualModifier {
    private final float red;
    private final float green;
    private final float blue;
    private final float alpha;
    private final float pulsesPerSecond;
    private float time;
    private boolean finished;

    public PulseTintModifier(float red, float green, float blue, float alpha, float pulsesPerSecond) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
        this.pulsesPerSecond = pulsesPerSecond <= 0f ? 2f : pulsesPerSecond;
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
        float wave = 0.55f + 0.45f * (float) ((Math.sin(time * pulsesPerSecond * Math.PI * 2.0) + 1.0) * 0.5);
        batch.setColor(red * wave, green * wave, Math.min(1f, blue * (0.75f + 0.25f * wave)), alpha);
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    public void stop() {
        finished = true;
    }
}

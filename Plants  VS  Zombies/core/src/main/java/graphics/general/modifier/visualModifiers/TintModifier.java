package graphics.general.modifier.visualModifiers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TintModifier implements VisualModifier {
    private final float red;
    private final float green;
    private final float blue;
    private final float alpha;
    private boolean finished;

    public TintModifier(float red, float green, float blue, float alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    @Override
    public void update(float delta) {
    }

    @Override
    public void apply(SpriteBatch batch) {
        if (finished) {
            return;
        }
        batch.setColor(red, green, blue, alpha);
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    public void stop() {
        finished = true;
    }
}

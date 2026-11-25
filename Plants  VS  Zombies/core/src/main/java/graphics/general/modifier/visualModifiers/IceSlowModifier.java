package graphics.general.modifier.visualModifiers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class IceSlowModifier implements VisualModifier {
    private static final float RED = 0.62f;
    private static final float GREEN = 0.82f;
    private static final float BLUE = 1f;

    private boolean finished;

    @Override
    public void update(float delta) {
    }

    @Override
    public void apply(SpriteBatch batch) {
        if (finished) {
            return;
        }
        batch.setColor(RED, GREEN, BLUE, 1f);
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    public void stop() {
        finished = true;
    }
}

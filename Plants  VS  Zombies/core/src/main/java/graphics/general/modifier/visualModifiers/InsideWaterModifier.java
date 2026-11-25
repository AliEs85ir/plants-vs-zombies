package graphics.general.modifier.visualModifiers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import graphics.general.modifier.RenderContext;

public class InsideWaterModifier implements VisualModifier{
    private final float visibleTopPercent;

    private final Rectangle scissorArea = new Rectangle();
    private final Rectangle calculatedScissor = new Rectangle();

    private boolean scissorPushed = false;

    public InsideWaterModifier(float visibleTopPercent) {
        this.visibleTopPercent = MathUtils.clamp(visibleTopPercent, 0f, 1f);
    }

    @Override
    public void update(float delta) {
    }

    @Override
    public void beforeRender(SpriteBatch batch, RenderContext context) {
        Rectangle bounds = context.getWorldBounds();

        // 1. Flush any pending draws before changing scissor state
        batch.flush();

        float visibleHeight = bounds.height * visibleTopPercent;

        // Scissor only the top portion of the bounds
        scissorArea.set(
            bounds.x,
            bounds.y + bounds.height - visibleHeight,
            bounds.width,
            visibleHeight
        );

        ScissorStack.calculateScissors(
            context.getCamera(),
            batch.getTransformMatrix(),
            scissorArea,
            calculatedScissor
        );

        scissorPushed = ScissorStack.pushScissors(calculatedScissor);
    }

    @Override
    public void afterRender(SpriteBatch batch, RenderContext context) {
        if (scissorPushed) {
            // 2. CRUCIAL: Flush zombie draw calls BEFORE popping scissor!
            batch.flush();
            ScissorStack.popScissors();
            scissorPushed = false;
        }
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public void apply(SpriteBatch batch) {
    }
}

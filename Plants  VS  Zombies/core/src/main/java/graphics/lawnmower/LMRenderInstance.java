package graphics.lawnmower;
import game.entity.lawnmower.LawnMower;
import graphics.general.event.LMVisualEventType;
import graphics.general.event.VisualEvent;
import graphics.general.instance.RenderInstance;

public class LMRenderInstance extends RenderInstance<LawnMower, LMVisualState, LMVisualEventType, LMAnimationVariant> {

    private float hitStateTimer = 0f;
    private static final float HIT_DURATION = 0.5f;

    public LMRenderInstance(LawnMower lawnMower) {
        super(lawnMower, LMVisualState.IDLE);
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        if (getCurrentVisualState() == LMVisualState.ATTACK) {
            hitStateTimer += delta;
            if (hitStateTimer >= HIT_DURATION) {
                pushEvent(new VisualEvent<>(LMVisualEventType.HIT_FINISHED));
                hitStateTimer = 0f;
            }
        } else {
            hitStateTimer = 0f;
        }
    }

    @Override
    protected float generateRandomVariationInterval() {
        return 3.0f + (float) (Math.random() * 4.0f);
    }

    public LawnMower getlawnmower() { return getModel(); }
}

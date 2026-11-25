package graphics.general.resolver;

import game.entity.lawnmower.LawnMower;
import graphics.general.event.LMVisualEventType;
import graphics.general.event.VisualEvent;
import graphics.general.resolver.general.StatusConditionEvaluator;
import graphics.lawnmower.LMRenderInstance;
import graphics.lawnmower.LMVisualState;

public class LawnMowerVisualStateResolver implements VisualStateResolver<LawnMower, LMVisualState, LMRenderInstance> {
    private final StatusConditionEvaluator conditionEvaluator;

    public LawnMowerVisualStateResolver() {
        this.conditionEvaluator = new StatusConditionEvaluator();
    }

    @Override
    public LMVisualState resolve(LawnMower lm, LMRenderInstance instance,
                                 LMVisualState currentState, boolean isAnimationFinished) {
        VisualEvent<LMVisualEventType> event = instance.pollEvent();
        if (event != null) {
            if (event.getType() == LMVisualEventType.HIT_ZOMBIE) {
                return LMVisualState.ATTACK;
            }
            if (event.getType() == LMVisualEventType.HIT_FINISHED) {
                return lm.isUsed() ? LMVisualState.TRANSITION : LMVisualState.IDLE;
            }
        }

        if (currentState == LMVisualState.ATTACK) {
            if (isAnimationFinished) {
                return lm.isUsed() ? LMVisualState.TRANSITION : LMVisualState.IDLE;
            }
            return LMVisualState.ATTACK;
        }

        LMVisualState forced = conditionEvaluator.evaluateEntityConditions(lm);
        if (forced != null) {
            return forced;
        }

        return currentState;
    }
}

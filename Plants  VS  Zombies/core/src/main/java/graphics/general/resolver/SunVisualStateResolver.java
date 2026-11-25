package graphics.general.resolver;

import game.entity.sun.Sun;
import graphics.general.event.LMVisualEventType;
import graphics.general.event.SunVisualEventType;
import graphics.general.event.VisualEvent;
import graphics.general.resolver.general.StatusConditionEvaluator;
import graphics.lawnmower.LMVisualState;
import graphics.sun.SunRenderInstance;
import graphics.sun.SunVisualState;

public class SunVisualStateResolver implements VisualStateResolver<Sun, SunVisualState, SunRenderInstance> {
    private final StatusConditionEvaluator conditionEvaluator;

    public SunVisualStateResolver() {
        this.conditionEvaluator = new StatusConditionEvaluator();
    }

    @Override
    public SunVisualState resolve(Sun model, SunRenderInstance instance,
                                  SunVisualState currentState, boolean animationFinished) {
        VisualEvent<SunVisualEventType> event = instance.pollEvent();
        if (event == null) return currentState;

        if (event.getType() == SunVisualEventType.EXPLODE) {
            return SunVisualState.EXPLODE;
        }
        if (event.getType() == SunVisualEventType.TO_NORMAL_SUN) {
            return SunVisualState.NORMAL_SUN;
        }

        SunVisualState forced = conditionEvaluator.evaluateEntityConditions(model);
        if (forced != null) {
            return forced;
        }

        return SunVisualState.IDLE;
    }
}

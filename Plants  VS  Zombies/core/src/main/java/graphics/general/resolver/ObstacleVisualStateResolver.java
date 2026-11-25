package graphics.general.resolver;

import game.entity.obstacle.Obstacle;
import graphics.general.event.ObstacleVisualEventType;
import graphics.general.event.VisualEvent;
import graphics.general.resolver.general.EventStateTransitionEvaluator;
import graphics.general.resolver.general.StatusConditionEvaluator;
import graphics.obstacle.ObstacleRenderInstance;
import graphics.obstacle.ObstacleVisualState;

public class ObstacleVisualStateResolver implements VisualStateResolver<Obstacle, ObstacleVisualState, ObstacleRenderInstance>{
    private final EventStateTransitionEvaluator eventEvaluator;
    private final StatusConditionEvaluator conditionEvaluator;

    public ObstacleVisualStateResolver() {
        this.eventEvaluator = new EventStateTransitionEvaluator();
        this.conditionEvaluator = new StatusConditionEvaluator();
    }

    @Override
    public ObstacleVisualState resolve(Obstacle obstacle, ObstacleRenderInstance instance, ObstacleVisualState currentState, boolean isAnimationFinished) {
        ObstacleVisualState forced = conditionEvaluator.evaluateEntityConditions(obstacle);
        if (forced != null) return forced;

        VisualEvent<ObstacleVisualEventType> nextEvent = instance.topEvent();

        if (nextEvent != null && nextEvent.getType().isInterruptable()) {
            return eventEvaluator.evaluateNextEventState(instance);
        }


        if (currentState.isActionState()) {
            boolean isLooping = instance.getAnimationController().getCurrentVariant() != null
                && instance.getAnimationController().getCurrentVariant().isLoop();

            if (!isLooping && !isAnimationFinished) {
                return currentState;
            }
        }

        ObstacleVisualState eventState = eventEvaluator.evaluateNextEventState(instance);
        if (eventState != null) {
            instance.getAnimationController().setFinished(false);
            return eventState;
        }

        if (isAnimationFinished && currentState.isActionState()) {
            return ObstacleVisualState.IDLE;
        }

        return currentState;
    }
}

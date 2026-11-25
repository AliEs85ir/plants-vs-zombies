package graphics.general.resolver;

import game.entity.zombie.Zombie;
import graphics.general.event.VisualEvent;
import graphics.general.event.ZombieVisualEventType;
import graphics.general.resolver.general.EventStateTransitionEvaluator;
import graphics.general.resolver.general.StatusConditionEvaluator;
import graphics.zombie.ZombieRenderInstance;
import graphics.zombie.ZombieVisualState;

public class ZombieVisualStateResolver implements VisualStateResolver<Zombie, ZombieVisualState, ZombieRenderInstance>{
    private final StatusConditionEvaluator conditionEvaluator;
    private final EventStateTransitionEvaluator eventEvaluator;

    public ZombieVisualStateResolver() {
        this.conditionEvaluator = new StatusConditionEvaluator();
        this.eventEvaluator = new EventStateTransitionEvaluator();
    }

    @Override
    public ZombieVisualState resolve(Zombie zombie, ZombieRenderInstance instance, ZombieVisualState currentState, boolean isAnimationFinished) {
        ZombieVisualState forced = conditionEvaluator.evaluateEntityConditions(zombie);
        if (forced != null) return forced;

        VisualEvent<ZombieVisualEventType> nextEvent = instance.topEvent();

        // 2. Interrupt logic (TACKLE shuld interrupt RUN)
        if (nextEvent != null && nextEvent.getType().isInterruptable()) {
            return eventEvaluator.evaluateNextEventState(instance);
        }

        // 3. Blocking Logic
        // If we are in an action state...
        if (currentState.isActionState()) {
            boolean isLooping = instance.getAnimationController().getCurrentVariant() != null
                && instance.getAnimationController().getCurrentVariant().isLoop();

            // If it's NOT looping and NOT finished, we MUST stay here (block)
            if (!isLooping && !isAnimationFinished) {
                return currentState;
            }
        }

        // 4. If we aren't blocked, try to get the next event
        ZombieVisualState eventState = eventEvaluator.evaluateNextEventState(instance);
        if (eventState != null) {
            // Clear the finished flag for the new animation
            instance.getAnimationController().setFinished(false);
            return eventState;
        }

        // 5. Fallback: If action (Tackle/Smash) finished and no new event, go to WALK
        if (isAnimationFinished && currentState.isActionState()) {
            return ZombieVisualState.WALK;
        }

        return currentState;
    }
}

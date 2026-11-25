package graphics.general.resolver;

import game.entity.plant.Plant;
import graphics.general.instance.RenderInstance;
import graphics.general.resolver.general.EventStateTransitionEvaluator;
import graphics.general.resolver.general.StatusConditionEvaluator;
import graphics.plant.PlantRenderInstance;
import graphics.plant.PlantVisualState;

public class PlantVisualStateResolver implements VisualStateResolver<Plant, PlantVisualState, PlantRenderInstance>{

    private final StatusConditionEvaluator conditionEvaluator;
    private final EventStateTransitionEvaluator eventEvaluator;

    public PlantVisualStateResolver() {
        this.conditionEvaluator = new StatusConditionEvaluator();
        this.eventEvaluator = new EventStateTransitionEvaluator();
    }

    @Override
    public PlantVisualState resolve(Plant plant, PlantRenderInstance instance, PlantVisualState currentState, boolean isAnimationFinished) {
        PlantVisualState forcedConditionState = conditionEvaluator.evaluateEntityConditions(plant);
        if (forcedConditionState != null) {
            return forcedConditionState;
        }

        boolean plantFoodActive = plant.getPlantFoodState() != null && plant.getPlantFoodState().isActive();
        if (currentState == PlantVisualState.PLANT_FOOD && plantFoodActive) {
            var variant = instance.getAnimationController().getCurrentVariant();
            boolean looping = variant != null && variant.isLoop();
            if (!looping && isAnimationFinished) {
                PlantVisualState eventState = eventEvaluator.evaluateNextEventState(instance);
                if (eventState != null) {
                    return eventState;
                }
                return PlantVisualState.IDLE;
            }
            return currentState;
        }

        PlantVisualState eventState = eventEvaluator.evaluateNextEventState(instance);
        if (eventState != null) {
            return eventState;
        }

        if (currentState == PlantVisualState.PLANT_FOOD) {
            var variant = instance.getAnimationController().getCurrentVariant();
            if (isAnimationFinished || variant == null || variant.isLoop()) {
                return PlantVisualState.IDLE;
            }
            return currentState;
        }

        if (isAnimationFinished && currentState.isActionState()) {
            return PlantVisualState.IDLE;
        }
        return currentState;
    }

}

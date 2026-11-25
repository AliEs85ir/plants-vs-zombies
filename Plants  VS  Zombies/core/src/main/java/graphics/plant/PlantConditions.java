package graphics.plant;

import game.entity.plant.HealthSegments;
import game.entity.plant.Plant;
import graphics.general.animation.condition.AnimationCondition;

public final class PlantConditions {

    private PlantConditions() {
    }

    public static AnimationCondition<Plant> stage(int expectedStage) {
        return plant -> plant.getStage() == expectedStage;
    }

    public static AnimationCondition<Plant> healthSegment(int expectedSegment, int segmentCount) {
        return plant -> plant != null
            && HealthSegments.matches(plant.getHealth(), expectedSegment, segmentCount);
    }

    public static AnimationCondition<Plant> phase(String expectedPhase) {
        return plant -> plant.getGameplayPhase() != null
            && plant.getGameplayPhase().name().equalsIgnoreCase(expectedPhase);
    }

    public static AnimationCondition<Plant> facingLeft() {
        return Plant::facesLeft;
    }

    public static AnimationCondition<Plant> facingRight() {
        return plant -> !plant.facesLeft();
    }

    public static AnimationCondition<Plant> plantFoodActive() {
        return plant -> plant != null
            && plant.getPlantFoodState() != null
            && plant.getPlantFoodState().isActive();
    }

    public static AnimationCondition<Plant> plantFoodInactive() {
        return plant -> plant == null
            || plant.getPlantFoodState() == null
            || !plant.getPlantFoodState().isActive();
    }
}

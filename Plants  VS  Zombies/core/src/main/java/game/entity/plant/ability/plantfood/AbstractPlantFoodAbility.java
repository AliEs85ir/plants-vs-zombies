package game.entity.plant.ability.plantfood;

import game.entity.plant.Plant;
import game.entity.plant.ability.AbstractPlantAbility;
import game.entity.plant.ability.PlantAbility;
import game.entity.plant.ability.PlantAbilitySupport;
import game.entity.plant.plantfood.PlantFoodDefinition;
import game.service.plant.PlantService;
import graphics.general.event.PlantVisualEventType;
import graphics.plant.PlantRenderInstance;
import graphics.plant.PlantVisualState;

public abstract class AbstractPlantFoodAbility extends AbstractPlantAbility {
    protected final PlantFoodDefinition definition;
    private boolean started;
    private boolean completed;
    private long startTick;

    protected AbstractPlantFoodAbility(PlantFoodDefinition definition) {
        this.definition = definition;
    }

    @Override
    public boolean isPlantFoodAbility() {
        return true;
    }

    @Override
    protected void doUpdate(Plant plant, PlantService plantService, long currentTick) {
        if (plant == null || plantService == null || !plant.getPlantFoodState().isActive()) {
            reset();
            return;
        }
        if (!started) {
            started = true;
            startTick = currentTick;
            PlantAbilitySupport.play(plant, PlantVisualEventType.PLANT_FOOD_ACTIVATED);
            onStart(plant, plantService, currentTick);
        }
        onTick(plant, plantService, currentTick, currentTick - startTick);
        if (completed) {
            resetNormalAbilities(plant, currentTick);
            plant.getPlantFoodState().deactivate();
            reset();
        }
    }

    protected abstract void onStart(Plant plant, PlantService plantService, long currentTick);

    protected abstract void onTick(Plant plant, PlantService plantService, long currentTick, long elapsedTicks);

    protected void complete() {
        this.completed = true;
    }

    protected boolean isAnimationLifecycleFinished(Plant plant, long elapsedTicks) {
        PlantRenderInstance instance = PlantAbilitySupport.instanceOf(plant);
        if (instance == null) {
            return true;
        }
        if (instance.getCurrentVisualState() != PlantVisualState.PLANT_FOOD) {
            return false;
        }
        int last = instance.getSequenceLength() > 0 ? instance.getSequenceLength() - 1 : 0;
        if (instance.getAnimationStage() < last) {
            return false;
        }
        var variant = instance.getAnimationController().getCurrentVariant();
        if (variant != null && variant.isLoop()) {
            int animationDurationTicks = definition.getAnimationDurationTicks();
            if (animationDurationTicks > 0) {
                return elapsedTicks >= animationDurationTicks;
            }
            return true;
        }
        return instance.getAnimationController().isFinished();
    }

    protected void requestAnimationStage(Plant plant, int stage) {
        if (plant.getPlantFoodState() != null) {
            plant.getPlantFoodState().requestAnimationStage(stage);
        }
    }

    private void resetNormalAbilities(Plant plant, long currentTick) {
        if (plant.getAbilities() == null) {
            return;
        }
        for (PlantAbility ability : plant.getAbilities()) {
            if (ability != null && !ability.isPlantFoodAbility()) {
                ability.resetCycle(currentTick);
            }
        }
    }

    private void reset() {
        started = false;
        completed = false;
        startTick = 0;
    }
}

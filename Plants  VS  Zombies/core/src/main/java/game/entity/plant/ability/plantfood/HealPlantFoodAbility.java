package game.entity.plant.ability.plantfood;

import game.entity.plant.Plant;
import game.entity.plant.plantfood.PlantFoodDefinition;
import game.service.plant.PlantService;

public final class HealPlantFoodAbility extends AbstractPlantFoodAbility {
    public HealPlantFoodAbility(PlantFoodDefinition definition) {
        super(definition);
    }

    @Override
    protected void onStart(Plant plant, PlantService plantService, long currentTick) {
        if (plant == null || plant.getHealth() == null) {
            return;
        }
        if (definition.isRestoreFullHealth()) {
            plant.heal(plant.getHealth().getMaxHealth());
            return;
        }
        int amount = definition.getHealAmount();
        if (amount > 0) {
            plant.heal(amount);
        }
    }

    @Override
    protected void onTick(Plant plant, PlantService plantService, long currentTick, long elapsedTicks) {
        if (isAnimationLifecycleFinished(plant, elapsedTicks)) {
            complete();
            return;
        }
        int duration = definition.getDurationTicks();
        if (duration > 0 && elapsedTicks >= duration) {
            complete();
        }
    }
}

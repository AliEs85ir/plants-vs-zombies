package game.entity.plant.ability.plantfood;

import game.entity.plant.Plant;
import game.entity.plant.ability.PlantAbility;
import game.entity.plant.ability.SunProductionAbility;
import game.entity.plant.plantfood.PlantFoodDefinition;
import game.service.plant.PlantService;

public final class SunBurstPlantFoodAbility extends AbstractPlantFoodAbility {
    private boolean effectTriggered;

    public SunBurstPlantFoodAbility(PlantFoodDefinition definition) {
        super(definition);
    }

    @Override
    protected void onStart(Plant plant, PlantService plantService, long currentTick) {
        effectTriggered = false;
    }

    @Override
    protected void onTick(Plant plant, PlantService plantService, long currentTick, long elapsedTicks) {
        if (!effectTriggered && elapsedTicks >= definition.getEffectDelayTicks()) {
            effectTriggered = true;
            if (definition.isForceMaxGrowth()) {
                forceMaxGrowth(plant);
            }
            if (definition.getSunAmount() > 0) {
                plantService.getSunSpawner().spawnPlantSun(plant, definition.getSunAmount());
            }
        }
        if (effectTriggered && isAnimationLifecycleFinished(plant, elapsedTicks)) {
            complete();
        }
    }

    private void forceMaxGrowth(Plant plant) {
        plant.setGrowthStage(3);
        if (plant.getAbilities() == null) {
            return;
        }
        for (PlantAbility ability : plant.getAbilities()) {
            if (ability instanceof SunProductionAbility sunAbility) {
                sunAbility.forceGrowthStage(3);
            }
        }
    }
}

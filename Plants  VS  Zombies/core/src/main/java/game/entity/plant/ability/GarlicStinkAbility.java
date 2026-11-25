package game.entity.plant.ability;

import game.entity.plant.Plant;
import game.service.plant.PlantService;
import graphics.general.effects.EffectConditions;
import graphics.general.effects.EffectLayer;
import graphics.general.effects.GameplayEffects;

public class GarlicStinkAbility extends AbstractPlantAbility {
    private boolean spawned;

    @Override
    protected void doUpdate(Plant plant, PlantService plantService, long currentTick) {
        if (spawned || plant == null || plant.isDead()) {
            return;
        }
        GameplayEffects.spawnFollowingPlant(
            "garlic stink",
            plant,
            EffectLayer.FRONT,
            EffectConditions.plantAlive(plant),
            0f
        );
        spawned = true;
    }
}

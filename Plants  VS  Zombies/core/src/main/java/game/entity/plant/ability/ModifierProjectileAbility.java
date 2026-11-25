package game.entity.plant.ability;

import game.entity.plant.Plant;
import game.service.plant.PlantService;

public class ModifierProjectileAbility extends AbstractPlantAbility {
    @Override
    protected void doUpdate(Plant plant, PlantService plantService, long currentTick) {
        // Crossing is applied in ProjectileSimulationSystem via ProjectileFireTransform
        // so fast projectiles that skip cells are still transformed once.
    }
}

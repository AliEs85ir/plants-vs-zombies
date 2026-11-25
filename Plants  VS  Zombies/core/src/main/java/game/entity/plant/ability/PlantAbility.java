package game.entity.plant.ability;

import game.entity.plant.Plant;
import game.entity.zombie.Zombie;
import game.service.plant.PlantService;


public interface PlantAbility
{
    void update(
            Plant plant,
            PlantService plantService,
            long currentTick);

    default boolean handleDeath(Plant plant, PlantService plantService, long currentTick) {
        return false;
    }

    default void syncFrame(Plant plant, float interpolationAlpha) {
    }

    default boolean isPlantFoodAbility() {
        return false;
    }

    default void resetCycle(long currentTick) {
    }

    default boolean acceptsDamageFrom(Zombie attacker) {
        return true;
    }

    default void onDamaged(Plant plant, Zombie attacker, long currentTick) {
    }
}

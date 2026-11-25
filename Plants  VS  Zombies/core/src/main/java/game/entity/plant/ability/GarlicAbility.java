package game.entity.plant.ability;

import app.model.App;
import game.entity.plant.Plant;
import game.entity.plant.PlantDamage;
import game.entity.zombie.Zombie;
import game.entity.zombie.ZombieLaneSwitch;
import game.service.plant.PlantService;
import game.world.GameWorld;
import game.world.playground.Grid;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

public class GarlicAbility extends AbstractPlantAbility {
    private static final int DIVERT_DELAY_AFTER_BITE_TICKS = 1;

    private final Set<Zombie> diverted = Collections.newSetFromMap(new IdentityHashMap<>());

    @Override
    public boolean acceptsDamageFrom(Zombie attacker) {
        return attacker == null || !diverted.contains(attacker);
    }

    @Override
    public void onDamaged(Plant plant, Zombie attacker, long currentTick) {
        if (plant == null || !PlantTargeting.isEligible(attacker) || diverted.contains(attacker)) {
            return;
        }
        if (!hasGarlicBiteRecorded(plant, attacker, currentTick)) {
            return;
        }
        if (ZombieLaneSwitch.isDivertInProgress(attacker)) {
            return;
        }

        diverted.add(attacker);
        Grid grid = gridOf(attacker, plant);
        PlantAbilitySupport.schedule(currentTick, DIVERT_DELAY_AFTER_BITE_TICKS, () -> {
            if (!PlantTargeting.isEligible(attacker)) {
                return;
            }
            attacker.setTargetPlant(null);
            if (!ZombieLaneSwitch.divertToAdjacentLane(attacker, grid)) {
                attacker.setTargetPlant(null);
            }
        });
    }

    @Override
    public boolean handleDeath(Plant plant, PlantService plantService, long currentTick) {
        diverted.clear();
        return false;
    }

    @Override
    protected void doUpdate(Plant plant, PlantService plantService, long currentTick) {
        if (plant == null || plantService == null || plantService.getGameWorld() == null) {
            return;
        }
        pruneDead();
        releaseIgnoredEaters(plant, plantService.getGameWorld());
    }

    private static boolean hasGarlicBiteRecorded(Plant plant, Zombie attacker, long currentTick) {
        PlantDamage damageState = plant.getPlantDamage();
        return damageState != null
            && damageState.getZombie() == attacker
            && damageState.getLastDamageTick() == currentTick;
    }

    private void pruneDead() {
        diverted.removeIf(zombie -> zombie == null || zombie.isDead());
    }

    private void releaseIgnoredEaters(Plant plant, GameWorld world) {
        if (world.getZombies() == null || diverted.isEmpty()) {
            return;
        }
        for (Zombie zombie : world.getZombies()) {
            if (zombie != null
                && diverted.contains(zombie)
                && zombie.getTargetPlant() == plant
                && !ZombieLaneSwitch.isDivertInProgress(zombie)) {
                zombie.setTargetPlant(null);
            }
        }
    }

    private static Grid gridOf(Zombie attacker, Plant plant) {
        if (attacker != null && attacker.getWorld() != null) {
            return attacker.getWorld().getGrid();
        }
        if (plant != null && App.getCurrentGameEngine() != null
            && App.getCurrentGameEngine().getGameWorld() != null) {
            return App.getCurrentGameEngine().getGameWorld().getGrid();
        }
        return null;
    }
}

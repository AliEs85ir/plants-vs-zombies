package game.service.Zombie;

import app.controller.game.EventResult;
import game.entity.registry.ZombieRegistry;
import game.entity.zombie.data.properties.Property;
import game.world.minigame.izombie.IZombieWorld;
import game.world.minigame.izombie_couchplay.IZombieCouchPlayWorld;
import game.world.playground.Cell;

public class ZombiePlacementValidator {
    private final IZombieWorld world;

    public ZombiePlacementValidator(IZombieWorld world) {
        this.world = world;
    }

    public EventResult validate(String alias, Cell targetCell) {
        if (alias == null) {
            return EventResult.ZOMBIE_PLACEMENT_ZOMBIE_NOT_SELECTED;
        }

        if (targetCell == null) {
            return EventResult.INVALID_CELL;
        }

        Property zombieProp = ZombieRegistry.getZombie(alias);

        if (zombieProp == null) {
            return EventResult.INVALID_ZOMBIE;
        }

        if (!isCooldownAvailable(alias)) {
            return EventResult.ZOMBIE_PLACEMENT_COOLDOWN;
        }

        if (!hasEnoughSun(zombieProp)) {
            return EventResult.NOT_ENOUGH_SUN;
        }

        if (targetCell.hasPlant() || targetCell.hasObstacle()) {
            return EventResult.ZOMBIE_PLACEMENT_CELL_OCCUPIED;
        }

        return EventResult.ZOMBIE_PLACEMENT_SUCCESS;
    }

    public boolean canPlaceZombie(String alias, Cell targetCell) {
        return validate(alias, targetCell).isSuccess();
    }

    private boolean isCooldownAvailable(String alias) {
        return !world.isOnCooldown(alias);
    }

    private boolean hasEnoughSun(Property property) {
        return currentZombieSun() >= property.getZombieWaveCost();
    }

    private int currentZombieSun() {
        if (world instanceof IZombieCouchPlayWorld couchWorld) {
            return couchWorld.getZombieSunAmount();
        }
        return world.getSunAmount();
    }
}

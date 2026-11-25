package game.service.Zombie;

import app.controller.game.EventResult;
import app.controller.game.izombie.IZombieMatchContext;
import app.controller.game.izombie.IZombieNetworkController;
import common.model.Position;
import game.engine.RealTimeGameEngine;
import game.entity.registry.ZombieRegistry;
import game.entity.zombie.data.properties.Property;
import game.world.minigame.izombie.IZombieWorld;
import game.world.minigame.izombie.ZombieCooldown;
import game.world.minigame.izombie_couchplay.IZombieCouchPlayWorld;
import game.world.playground.Cell;

public class ZombiePlacementService {
    private final IZombieWorld world;
    private final RealTimeGameEngine engine;
    private final ZombiePlacementValidator validator;

    public ZombiePlacementService(IZombieWorld world, RealTimeGameEngine engine) {
        this.world = world;
        this.engine = engine;
        this.validator = new ZombiePlacementValidator(world);
    }

    public EventResult placeZombie(
        String alias,
        Cell targetCell
    ) {
        IZombieMatchContext context = IZombieMatchContext.get();
        boolean remote = context != null && context.isApplyingRemotePlacement();
        if (!remote) {
            EventResult validation = validator.validate(alias, targetCell);
            if (!validation.isSuccess()) {
                return validation;
            }
            if (context != null) {
                IZombieNetworkController.publishPlacement(
                    alias,
                    targetCell.getRow(),
                    targetCell.getCol()
                );
                return EventResult.ZOMBIE_PLACEMENT_SUCCESS;
            }
        }
        return spawnZombie(alias, targetCell);
    }

    private EventResult spawnZombie(String alias, Cell targetCell) {
        if (alias == null || targetCell == null) {
            return EventResult.INVALID_CELL;
        }

        ZombieService service = new ZombieService(world);
        Position position = new Position(targetCell.getWorldX(), targetCell.getWorldY());
        if (service.spawnZombie(alias, position) == null) {
            return EventResult.INVALID_ZOMBIE;
        }
        world.markZombieSpawned();

        Property property = ZombieRegistry.getZombie(alias);
        if (property != null) {
            spendZombieSun(property.getZombieWaveCost());
        }
        ZombieCooldown cooldown = world.getCoolDownZombie(alias);
        if (cooldown != null) {
            cooldown.startCooldown(engine.getClock().getCurrentTick());
        }
        return EventResult.ZOMBIE_PLACEMENT_SUCCESS;
    }

    private void spendZombieSun(int amount) {
        if (world instanceof IZombieCouchPlayWorld couchWorld) {
            couchWorld.removeZombieSunAmount(amount);
            return;
        }
        world.removeSunAmount(amount);
    }

    public ZombiePlacementValidator getValidator() {
        return validator;
    }
}

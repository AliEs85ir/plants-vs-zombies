package game.entity.zombie.ability;

import app.enums.plant.PlantName;
import game.entity.plant.Plant;
import game.entity.zombie.Zombie;
import game.entity.zombie.data.properties.OrdinaryZombieProperty;
import game.entity.zombie.data.properties.ProspectorProperty;
import game.entity.zombie.strategy.movement.FlyingMovement;
import game.entity.zombie.strategy.movement.GroundTrackedMovement;
import game.entity.zombie.strategy.movement.MovementStrategy;
import game.entity.zombie.strategy.movement.ReverseMovement;
import game.service.Zombie.ZombieService;
import graphics.general.event.VisualEvent;
import graphics.general.event.ZombieVisualEventType;

import java.util.List;

public class FlyingAbility extends AbstractZombieAbility{
    private boolean done = false;
    private boolean launched = false;

    @Override
    public void update(Zombie zombie, ZombieService zombieService, long currentTick) {
        if (done) return;

        MovementStrategy strategy = zombie.getMovementStrategy();
        if (!(strategy instanceof FlyingMovement)) {
            return;
        }

        if (!launched) {
            launched = true;
            game.audio.Sfx.flyingStarted(zombie);
        }

        FlyingMovement fly = (FlyingMovement) strategy;
        if (fly.hasLanded()) {
            if(zombie.getPropertyData() instanceof ProspectorProperty) {
                zombie.setMovementStrategy(new ReverseMovement());
            }
            else {
                zombie.setMovementStrategy(GroundTrackedMovement.defaultWalkStrategy(zombie));
            }
            done = true;
        }

    }

    @Override
    public void onDeath(Zombie zombie) {
        if (!done) {
            zombie.setMovementStrategy(GroundTrackedMovement.defaultWalkStrategy(zombie));
        }
    }
}

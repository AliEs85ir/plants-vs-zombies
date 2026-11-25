package game.entity.zombie.strategy.movement;

import app.model.App;
import game.entity.zombie.Zombie;
import game.service.Zombie.ZombieService;
import game.world.seasons.Color;
import graphics.general.event.VisualEvent;
import graphics.general.event.ZombieVisualEventType;
import graphics.zombie.ZombieRenderInstance;

public class ChargeMovement implements MovementStrategy{

    @Override
    public boolean move(Zombie zombie, ZombieService zombieService, long currentTick) {
        if (zombie.getBody() != null) {
            float vx = (float) -(zombie.getSpeed() * zombie.getMoveSpeedMultiplier());
            zombie.getBody().setLinearVelocity(vx, 0f);


            // if not run , push run :
            zombie.pushEvent(ZombieVisualEventType.RUN);
            return true;
        }
        return false;
    }
}

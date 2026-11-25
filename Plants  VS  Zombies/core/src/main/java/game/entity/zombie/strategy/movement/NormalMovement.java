package game.entity.zombie.strategy.movement;

import app.model.App;
import common.model.Position;
import game.entity.zombie.Zombie;
import game.service.Zombie.ZombieService;
import graphics.general.event.VisualEvent;
import graphics.general.event.ZombieVisualEventType;
import graphics.zombie.ZombieRenderInstance;

public class NormalMovement implements MovementStrategy{

    @Override
    public boolean move(Zombie zombie, ZombieService zombieService, long currentTick) {
        if (zombie.getBody() != null) {
            float vx = (float) -(zombie.getSpeed() * zombie.getMoveSpeedMultiplier());
            zombie.getBody().setLinearVelocity(vx, 0f);


            zombie.pushEvent(ZombieVisualEventType.WALK);
            return true;
        }
        return false;
    }
}

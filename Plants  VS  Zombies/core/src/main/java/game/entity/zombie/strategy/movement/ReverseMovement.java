package game.entity.zombie.strategy.movement;

import game.entity.zombie.Zombie;
import game.service.Zombie.ZombieService;
import graphics.general.event.ZombieVisualEventType;

public class ReverseMovement implements MovementStrategy{

    @Override
    public boolean move(Zombie zombie, ZombieService zombieService, long currentTick) {
        if (zombie.getBody() != null) {
            float vx = (float) (zombie.getSpeed() * zombie.getMoveSpeedMultiplier());
            zombie.getBody().setLinearVelocity(vx, 0f);

            if(!zombie.getWorld().getGrid().isInsidePlayGround(zombie.getPosition())) {
                zombie.die();
            }

            zombie.pushEvent(ZombieVisualEventType.WALK);
            return true;
        }
        return false;
    }
}

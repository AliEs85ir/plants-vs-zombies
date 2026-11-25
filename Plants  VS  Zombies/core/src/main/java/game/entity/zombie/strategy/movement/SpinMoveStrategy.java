package game.entity.zombie.strategy.movement;

import game.entity.zombie.Zombie;
import game.service.Zombie.ZombieService;

public class SpinMoveStrategy implements MovementStrategy{
    @Override
    public boolean move(Zombie zombie, ZombieService zombieService, long currentTick) {
        if (zombie.getBody() != null) {
            float vx = (float) -(zombie.getSpeed() * zombie.getMoveSpeedMultiplier());
            zombie.getBody().setLinearVelocity(vx, 0f);
            return true;
        }
        return false;
    }
}

package game.entity.zombie.strategy.movement;

import game.entity.zombie.Zombie;
import game.service.Zombie.ZombieService;

public class NoMovement implements MovementStrategy{
    @Override
    public boolean move(Zombie zombie, ZombieService zombieService, long currentTick) {
        return false;
    }
}

package game.entity.zombie.strategy.movement;

import game.entity.zombie.Zombie;
import game.service.Zombie.ZombieService;

public interface MovementStrategy {

    boolean move(Zombie zombie, ZombieService zombieService, long currentTick);
}

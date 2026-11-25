package game.entity.zombie.strategy.attack;

import game.entity.plant.Plant;
import game.entity.zombie.Zombie;
import game.service.Zombie.ZombieService;

public interface AttackStrategy {

    void executeAttack(Zombie zombie,
                       Plant targetPlant,
                       ZombieService zombieService,
                       long currentTick);
}

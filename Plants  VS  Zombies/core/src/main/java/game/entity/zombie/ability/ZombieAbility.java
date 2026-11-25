package game.entity.zombie.ability;

import game.entity.zombie.Zombie;
import game.service.Zombie.ZombieService;

public interface ZombieAbility {

    void update(
            Zombie zombie,
            ZombieService zombieService,
            long currentTick);

    void onDeath(Zombie zombie);
}

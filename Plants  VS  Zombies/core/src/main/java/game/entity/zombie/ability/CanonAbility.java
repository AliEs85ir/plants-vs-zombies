package game.entity.zombie.ability;

import game.entity.zombie.Zombie;
import game.service.Zombie.ZombieService;

public class CanonAbility extends AbstractZombieAbility{
    private final int CANON_COOLDOWN = 40;
    private int lastFire = -1;

    @Override
    public void update(Zombie zombie, ZombieService zombieService, long currentTick) {
        if(!canFire(currentTick)) {
            return;
        }

    }

    @Override
    public void onDeath(Zombie zombie) {

    }

    private boolean canFire(long currentTick) {
        return lastFire < 0 || currentTick - lastFire > CANON_COOLDOWN;
    }
}

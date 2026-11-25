package game.entity.zombie.ability;

import game.entity.DeathStyle;
import game.entity.zombie.Zombie;
import game.entity.zombie.part.ZombieBodyPart;
import game.entity.zombie.part.ZombieBodyPartDropper;
import game.service.Zombie.ZombieService;

public class HandDroppingAbility extends AbstractZombieAbility {
    private static final double HAND_DROP_HEALTH_RATIO = 0.5;

    private boolean handDropped;
    private boolean headDropped;

    @Override
    public void update(Zombie zombie, ZombieService zombieService, long currentTick) {
        if (handDropped || zombie == null || zombie.isDead()) {
            return;
        }
        if (belowHandDropThreshold(zombie)) {
            dropHand(zombie);
        }
    }

    @Override
    public void onDeath(Zombie zombie) {
        if (zombie == null) {
            return;
        }
        if (!handDropped) {
            dropHand(zombie);
        }
        if (headDropped) {
            return;
        }
        if (zombie.getPendingDeathStyle() == DeathStyle.ASH) {
            return;
        }
        ZombieBodyPartDropper.drop(zombie, ZombieBodyPart.HEAD);
        headDropped = true;
    }

    private void dropHand(Zombie zombie) {
        ZombieBodyPartDropper.drop(zombie, ZombieBodyPart.HAND);
        handDropped = true;
    }

    private static boolean belowHandDropThreshold(Zombie zombie) {
        int initialHealth = zombie.getPropertyData() == null ? 0 : zombie.getPropertyData().getInitialHealth();
        if (initialHealth <= 0) {
            return false;
        }
        return zombie.getCurrentHealth() / initialHealth <= HAND_DROP_HEALTH_RATIO;
    }
}

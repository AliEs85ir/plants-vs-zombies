package game.entity.zombie.ability;

import app.model.App;
import game.entity.zombie.Zombie;
import game.service.Zombie.ZombieService;
import game.world.GameWorld;
import graphics.general.effects.anchers.EntityAnchor;
import graphics.general.event.ZombieVisualEventType;

import java.util.ArrayList;
import java.util.Collections;

public class KingAuraAbility extends AbstractZombieAbility {

    private static final long ACTIVATION_INTERVAL = 50;
    private boolean intro;
    private long lastActivationTick = -1;

    @Override
    public void update(Zombie zombie, ZombieService zombieService, long currentTick) {
        if(!intro) {
            zombie.pushEvent(ZombieVisualEventType.INTRO);
            intro = true;
            return;
        }
        if (lastActivationTick < 0) {
            lastActivationTick = currentTick;
            return;
        }
        ArrayList<Zombie> zombies = zombie.getWorld().getZombies();

        if (currentTick - lastActivationTick < ACTIVATION_INTERVAL ||
            zombies.size() < 2 || canCast(zombie.getWorld(), zombie)) {
            return;
        }
        zombie.pushEvent(ZombieVisualEventType.GIVE_ARMOR);
        App.getCurrentGameEngine().getTaskManager().scheduleRelative(
            currentTick,
            10,
            () -> apply(zombie, currentTick, zombies)
        );
    }

    private void apply(Zombie zombie, long currentTick, ArrayList<Zombie> zombies) {
        int initialHealth = zombie.getPropertyData().getInitialHealth();
        if(zombie.getCurrentHealth() < initialHealth * 0.5) {
            zombie.setCurrentHealth(initialHealth);
            zombie.pushEffect("king heal",
                new EntityAnchor<Zombie>(zombie, zombie1 -> {
                    return zombie1.getPosition().toVector();
                }));
        }
        else {
            Collections.shuffle(zombies);
            for (Zombie zombie1 : zombies) {
                if(zombie.equals(zombie1)) {
                    return;
                }
                zombie1.setCurrentHealth(zombie1.getPropertyData().getInitialHealth());
                zombie1.pushEffect("king heal",
                    new EntityAnchor<Zombie>(zombie, zombie2 -> {
                        return zombie2.getPosition().toVector();
                    }));
                break;
            }
        }
        lastActivationTick = currentTick;
    }


    @Override
    public void onDeath(Zombie zombie) {
    }

    private boolean canCast(GameWorld world, Zombie king) {
        for(Zombie zombie : world.getZombies()) {
            if(king.equals(zombie)) {
                continue;
            }
            if(zombie.getCurrentHealth() < zombie.getPropertyData().getInitialHealth() * 0.8) {
                return true;
            }
        }
        return false;
    }
}

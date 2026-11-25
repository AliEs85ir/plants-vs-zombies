package game.entity.zombie.strategy.takedamage;

import game.entity.DamageContext;
import game.entity.Entity;
import game.entity.plant.Plant;
import game.entity.projectile.Projectile;
import game.entity.zombie.Zombie;
import game.entity.zombie.ZombieArmor;
import game.entity.zombie.ability.SpinAbility;
import graphics.general.event.ZombieVisualEventType;

import java.util.Iterator;

public class SpinTakingDamage implements TakingDamageStrategy {
    private final SpinAbility spinAbility;

    public SpinTakingDamage(SpinAbility spinAbility) {
        this.spinAbility = spinAbility;
    }

    @Override
    public boolean takeDamage(Zombie zombie, long currentTick, DamageContext context) {
        if(spinAbility.getState() != SpinAbility.SpinState.SPINNING) {
            spinAbility.setState(SpinAbility.SpinState.SPINNING);
        }
        if(!(context.attacker() instanceof Projectile projectile)) {
            takeNormalDamage(zombie, currentTick, context);
            return true;
        }
        if (projectile.isUnstoppable() || projectile.isInstantKill()) {
            takeNormalDamage(zombie, currentTick, context);
            return true;
        }
        if(!spinAbility.spawnProjectile(projectile.getPlantName())) {
            takeNormalDamage(zombie, currentTick, context);
        }
        return true;
    }


    private boolean takeNormalDamage(Zombie zombie, long currentTick, DamageContext damageContext) {
        if (damageContext.amount() <= 0 || zombie.isDead()) {
            return false;
        }

        double remainingDamage = damageContext.amount();
        Iterator<ZombieArmor> iterator = zombie.getArmors().iterator();

        while (iterator.hasNext() && remainingDamage > 0) {
            ZombieArmor armor = iterator.next();
            remainingDamage = armor.takeDamage(remainingDamage, zombie);

            if (armor.isBroken()) {
                iterator.remove();
                zombie.getWorld().addEvent(
                    "Armor " + armor.getName() + " broke on " + zombie.getName()
                );
            }
        }
        if (remainingDamage > 0) {
            double health = zombie.getCurrentHealth();
            zombie.setCurrentHealth(health - remainingDamage);
        }
        return true;
    }
}

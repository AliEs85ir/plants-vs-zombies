package game.entity.zombie.strategy.takedamage;

import game.entity.DamageContext;
import game.entity.zombie.Zombie;
import game.entity.zombie.ZombieArmor;

import java.util.Iterator;

public class StandardTakingDamage implements TakingDamageStrategy{
    @Override
    public boolean takeDamage(Zombie zombie, long currentTick, DamageContext damageContext) {
        if (damageContext.amount() <= 0 || zombie.isDead()) {
            return false;
        }

        double remainingDamage = damageContext.amount();
        if (!damageContext.ignoresArmor()) {
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
        }
        if (remainingDamage > 0) {
            double health = zombie.getCurrentHealth();
            zombie.setCurrentHealth(health - remainingDamage);
        }
        return true;
    }

}
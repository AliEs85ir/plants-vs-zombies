package game.entity.zombie.strategy.takedamage;

import game.entity.DamageContext;
import game.entity.zombie.Zombie;

public interface TakingDamageStrategy {

    boolean takeDamage(Zombie zombie,
                       long currentTick,
                       DamageContext damageContext);
}

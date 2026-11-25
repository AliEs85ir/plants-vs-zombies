package game.entity.zombie.strategy.takedamage;

import game.entity.DamageContext;
import game.entity.zombie.Zombie;

public class InvulnerableTakingDamage implements TakingDamageStrategy{
    @Override
    public boolean takeDamage(Zombie zombie, long currentTick, DamageContext context) {
        return false;
    }

    // >>> taking damage for imp dragon gets handled here (it ignores fire shots)
}

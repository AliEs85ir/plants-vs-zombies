package game.entity.zombie.strategy.takedamage;

import game.entity.DamageContext;
import game.entity.zombie.Zombie;

public class ElementalTakingDamage implements TakingDamageStrategy{

    @Override
    public boolean takeDamage(Zombie zombie, long currentTick, DamageContext context) {
        return false;
    }

    /*
        TODO : If projectile's type is Ice remove dynamite ability from explorer.
               taking damage for imp dragon gets handled here (it ignores fire shots).
               taking damage for explorer gets handled here
               (ice -> removes its torch) (fire -> lightens up its torch).

     */
}

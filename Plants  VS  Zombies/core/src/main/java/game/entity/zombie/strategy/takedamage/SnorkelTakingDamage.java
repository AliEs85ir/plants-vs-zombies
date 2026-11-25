package game.entity.zombie.strategy.takedamage;

import game.entity.DamageContext;
import game.entity.zombie.Zombie;
import game.world.playground.Cell;
import game.world.playground.celltypes.WaterCellType;

public class SnorkelTakingDamage implements TakingDamageStrategy{

    @Override
    public boolean takeDamage(Zombie zombie, long currentTick, DamageContext context) {
        return false;
    }

}

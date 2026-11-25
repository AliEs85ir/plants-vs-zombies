package game.entity.effect;

import app.enums.effect.EffectType;
import game.entity.DamageContext;
import game.entity.plant.Plant;
import game.entity.zombie.Zombie;
import game.service.Zombie.ZombieService;
import game.service.plant.PlantService;

public class BurnEffect
        extends TimedEffect
{
    private final int damagePerTick;

    public BurnEffect(float durationInSecond, int damagePerTick) {
        super(durationInSecond , EffectType.BURN);
        this.damagePerTick = damagePerTick;
    }

    @Override
    public void tick(Zombie zombie, ZombieService zombieService, long currentTick) {
        zombie.getTakingDamageStrategy().takeDamage(zombie, currentTick, new DamageContext(null, 1, false));
    }

    @Override
    public void tick(Plant plant, PlantService plantService, long currentTick) {
        // TODO
        Zombie zombie = null;
        plant.takeDamage(damagePerTick ,zombie , currentTick );
    }


    public int getDamagePerTick()
    {
        return damagePerTick;
    }

    @Override
    public void onApply(Plant plant) {

    }
}

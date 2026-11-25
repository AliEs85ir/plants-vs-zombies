package game.entity.effect;

import app.enums.effect.EffectType;
import game.entity.DamageContext;
import game.entity.plant.Plant;
import game.entity.zombie.Zombie;
import game.service.Zombie.ZombieService;

public class PoisonEffect
        extends TimedEffect
{
    private final int damagePerTick;


    public PoisonEffect(float durationInSecond, int damagePerTick) {
        super(durationInSecond, EffectType.POISON);
        this.damagePerTick = damagePerTick;
    }

    @Override
    public void tick(Zombie zombie, ZombieService zombieService, long currentTick) {
        zombie.getTakingDamageStrategy().takeDamage(zombie, currentTick, new DamageContext(null, 2, false));
    }

    @Override
    public void applyModifier(Zombie zombie) {
        if(!applied) {
            zombie.getWorld().addEvent("Zombie at position " + zombie.getPosition() + " got poison effect");
            applied = true;
        }
    }

    public int getDamagePerTick()
    {
        return damagePerTick;
    }

    @Override
    public void onApply(Plant plant) {

    }

    @Override
    public void onRemove(Zombie zombie) {
        zombie.getWorld().addEvent("poison effect got removed from zombie at position : " + zombie.getPosition());
    }
}

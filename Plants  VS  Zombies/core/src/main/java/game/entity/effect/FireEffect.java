package game.entity.effect;

import app.enums.effect.EffectType;
import game.entity.plant.Plant;
import game.entity.zombie.Zombie;

public class FireEffect extends TimedEffect{
    public FireEffect(float durationInSecond) {
        super(durationInSecond, EffectType.FIRE);
    }

    @Override
    public void applyModifier(Zombie zombie) {

    }

    @Override
    public void onApply(Plant plant)
    {
    }
}

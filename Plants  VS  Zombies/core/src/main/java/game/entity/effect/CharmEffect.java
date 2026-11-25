package game.entity.effect;

import app.enums.effect.EffectType;
import game.entity.plant.Plant;
import game.entity.zombie.Zombie;
import game.entity.zombie.strategy.movement.GroundTrackedMovement;
import game.entity.zombie.strategy.movement.ReverseMovement;

public class CharmEffect
        extends TimedEffect
{


    public CharmEffect(float durationInSeconds) {
        super(durationInSeconds , EffectType.CHARM);
    }

    @Override
    public void onApply(Plant plant) {

    }

    @Override
    public void onApply(Zombie zombie) {
        zombie.setMovementStrategy(new ReverseMovement());
        game.audio.Sfx.play(game.audio.AudioId.SFX_DANCER);
    }

    @Override
    public void onRemove(Zombie zombie) {
        zombie.setMovementStrategy(GroundTrackedMovement.defaultWalkStrategy(zombie));
    }
}

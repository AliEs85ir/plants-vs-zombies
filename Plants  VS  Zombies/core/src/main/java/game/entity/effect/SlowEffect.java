package game.entity.effect;

import app.enums.effect.EffectType;
import app.model.App;
import game.entity.plant.Plant;
import game.entity.zombie.Zombie;
import graphics.general.animation.AnimationController;
import graphics.general.modifier.visualModifiers.IceSlowModifier;
import graphics.zombie.ZombieRenderInstance;

public class SlowEffect
        extends TimedEffect
{
    private final double slowFactor;


    public SlowEffect(float durationInSecond, double slowFactor , EffectType type) {
        super(durationInSecond, type);
        this.slowFactor = slowFactor;
    }

    @Override
    public void applyModifier(Zombie zombie) {
        if(!applied) {
            zombie.setMoveSpeedMultiplier(
                    zombie.getMoveSpeedMultiplier() * slowFactor
            );
            zombie.setEatSpeedMultiplier(
                    zombie.getEatSpeedMultiplier() * slowFactor
            );
            zombie.getWorld().addEvent("Zombie at position " + zombie.getPosition().toString() + " got slow effect.");
            applied = true;
        }
        applyVisuals(zombie, true);
    }

    public double getSlowFactor()
    {
        return slowFactor;
    }

    @Override
    public void onApply(Zombie zombie) {
        applyVisuals(zombie, true);
    }

    @Override
    public void onApply(Plant plant)
    {

    }

    @Override
    public void onRemove(Zombie zombie) {
        zombie.setMoveSpeedMultiplier(zombie.getMoveSpeedMultiplier() * (1f/slowFactor));
        zombie.setEatSpeedMultiplier(zombie.getEatSpeedMultiplier() * (1f/slowFactor));
        applyVisuals(zombie, false);
        zombie.getWorld().addEvent("Slow effect got removed from zombie at position : " + zombie.getPosition().toString());
    }

    private void applyVisuals(Zombie zombie, boolean active) {
        ZombieRenderInstance instance = renderInstance(zombie);
        if (instance == null) {
            return;
        }
        if (active) {
            instance.getModifierController().add(new IceSlowModifier());
            if (!zombie.hasEffect(EffectType.FREEZE) && !zombie.hasEffect(EffectType.STUN)) {
                instance.getAnimationController().setTimeScale((float) slowFactor);
            }
            return;
        }
        instance.getModifierController().remove(IceSlowModifier.class);
        restoreTimeScale(instance.getAnimationController(), zombie);
    }

    private static void restoreTimeScale(AnimationController<?> animation, Zombie zombie) {
        if (zombie.hasEffect(EffectType.FREEZE) || zombie.hasEffect(EffectType.STUN)) {
            animation.setTimeScale(0f);
            return;
        }
        animation.setTimeScale(1f);
    }

    private static ZombieRenderInstance renderInstance(Zombie zombie) {
        if (zombie == null || App.getRealTimeEngine() == null || App.getRealTimeEngine().getGraphicsEngine() == null) {
            return null;
        }
        return App.getRealTimeEngine().getGraphicsEngine().getZombieInstance(zombie);
    }
}

package game.entity.effect;

import app.enums.effect.EffectType;
import app.model.App;
import game.entity.plant.Plant;
import game.entity.zombie.Zombie;
import graphics.general.instance.VisibilityController;
import graphics.zombie.ZombieRenderInstance;

public class StunEffect
        extends TimedEffect
{
    private static final String BUTTER_LAYER = "butter";

    public StunEffect(float durationInSecond) {
        super(durationInSecond, EffectType.STUN);
    }

    @Override
    public void applyModifier(Zombie zombie) {
        zombie.setCanMove(false);
        applyButter(zombie, true);
    }

    @Override
    public void onApply(Zombie zombie) {
        zombie.setCanMove(false);
        applyButter(zombie, true);
        game.audio.Sfx.play(game.audio.AudioId.SFX_BUTTER);
    }

    @Override
    public void onRemove(Zombie zombie) {
        zombie.setCanMove(true);
        applyButter(zombie, false);
    }

    @Override
    public void onApply(Plant plant) {

    }

    private static void applyButter(Zombie zombie, boolean visible) {
        ZombieRenderInstance instance = renderInstance(zombie);
        if (instance == null) {
            return;
        }
        VisibilityController visibility = instance.getVisibilityController();
        if (visible) {
            visibility.setVisible(BUTTER_LAYER);
            instance.getAnimationController().pauseAnimation();
            return;
        }
        visibility.setInvisible(BUTTER_LAYER);
        if (!zombie.hasEffect(EffectType.FREEZE)) {
            SlowEffect slow = slowEffect(zombie);
            if (slow != null) {
                instance.getAnimationController().setTimeScale((float) slow.getSlowFactor());
            } else {
                instance.getAnimationController().resumeAnimation();
            }
        }
    }

    private static SlowEffect slowEffect(Zombie zombie) {
        Effect ice = zombie.findEffect(EffectType.ICE);
        if (ice instanceof SlowEffect slow) {
            return slow;
        }
        Effect slow = zombie.findEffect(EffectType.SLOW);
        return slow instanceof SlowEffect found ? found : null;
    }

    private static ZombieRenderInstance renderInstance(Zombie zombie) {
        if (zombie == null || App.getRealTimeEngine() == null || App.getRealTimeEngine().getGraphicsEngine() == null) {
            return null;
        }
        return App.getRealTimeEngine().getGraphicsEngine().getZombieInstance(zombie);
    }
}

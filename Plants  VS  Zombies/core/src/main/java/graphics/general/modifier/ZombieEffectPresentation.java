package graphics.general.modifier;

import app.enums.effect.EffectType;
import app.model.App;
import game.entity.effect.Effect;
import game.entity.effect.SlowEffect;
import game.entity.zombie.Zombie;
import graphics.general.modifier.visualModifiers.IceSlowModifier;
import graphics.general.modifier.visualModifiers.PulseTintModifier;
import graphics.general.modifier.visualModifiers.TintModifier;
import graphics.zombie.ZombieRenderInstance;

public final class ZombieEffectPresentation {
    private static final String BUTTER_LAYER = "butter";

    private ZombieEffectPresentation() {
    }

    public static void sync(Zombie zombie) {
        if (zombie == null || App.getRealTimeEngine() == null || App.getRealTimeEngine().getGraphicsEngine() == null) {
            return;
        }
        ZombieRenderInstance instance = App.getRealTimeEngine().getGraphicsEngine().getZombieInstance(zombie);
        if (instance == null) {
            return;
        }
        VisualEffectController controller = instance.getModifierController();
        boolean frozen = zombie.hasEffect(EffectType.FREEZE);
        boolean stunned = zombie.hasEffect(EffectType.STUN);
        boolean burning = zombie.hasEffect(EffectType.FIRE) || zombie.hasEffect(EffectType.BURN);
        SlowEffect slow = slowEffect(zombie);
        if (frozen) {
            if (!controller.has(PulseTintModifier.class)) {
                controller.add(new PulseTintModifier(0.22f, 0.55f, 1f, 1f, 2.2f));
            }
        } else {
            controller.remove(PulseTintModifier.class);
        }
        if (slow != null && !frozen) {
            if (!controller.has(IceSlowModifier.class)) {
                controller.add(new IceSlowModifier());
            }
        } else {
            controller.remove(IceSlowModifier.class);
        }
        if (burning && !frozen) {
            if (!controller.has(TintModifier.class)) {
                controller.add(new TintModifier(1f, 0.28f, 0.12f, 1f));
            }
        } else {
            controller.remove(TintModifier.class);
        }
        if (frozen || stunned) {
            instance.getAnimationController().setTimeScale(0f);
        } else if (slow != null) {
            instance.getAnimationController().setTimeScale((float) slow.getSlowFactor());
        } else {
            instance.getAnimationController().setTimeScale(1f);
        }
        if (stunned) {
            instance.getVisibilityController().setVisible(BUTTER_LAYER);
        } else {
            instance.getVisibilityController().setInvisible(BUTTER_LAYER);
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
}

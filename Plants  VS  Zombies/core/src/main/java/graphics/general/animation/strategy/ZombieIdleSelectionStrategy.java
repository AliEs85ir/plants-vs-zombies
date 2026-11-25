package graphics.general.animation.strategy;

import graphics.general.animation.AnimationController;
import graphics.zombie.ZombieAnimationTag;
import graphics.zombie.ZombieAnimationVariant;
import graphics.zombie.ZombieRenderInstance;
import graphics.zombie.ZombieVisualState;

import java.util.List;

public class ZombieIdleSelectionStrategy implements VariantSelectionStrategy<ZombieVisualState, ZombieAnimationVariant, ZombieRenderInstance> {

    @Override
    public ZombieAnimationVariant select(List<ZombieAnimationVariant> variants, ZombieRenderInstance instance) {
        if (isVariantsInvalid(variants)) {
            return null;
        }

        AnimationController<ZombieAnimationVariant> controller = instance.getAnimationController();
        ZombieAnimationVariant currentVariant = controller.getCurrentVariant();

        if (currentVariant != null && isVariation(currentVariant)) {
            if (!controller.isFinished()) {
                return currentVariant;
            }
        }

        if (instance.shouldTriggerVariation()) {
            for (ZombieAnimationVariant variant : variants) {
                if (isVariation(variant)) {
                    instance.resetVariationTimer();
                    return variant;
                }
            }
        }

        for (ZombieAnimationVariant variant : variants) {
            if (variant.getTag() != null && "DEFAULT".equals(variant.getTag().name())) {
                return variant;
            }
        }

        return variants.get(0);
    }

    private boolean isVariation(ZombieAnimationVariant variant) {
        if (variant.getTag() == null) return false;
        ZombieAnimationTag tag = variant.getTag();
        return tag == ZombieAnimationTag.BLINK;
    }
}

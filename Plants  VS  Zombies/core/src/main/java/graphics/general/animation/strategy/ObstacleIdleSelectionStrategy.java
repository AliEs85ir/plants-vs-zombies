package graphics.general.animation.strategy;

import graphics.general.animation.AnimationController;
import graphics.obstacle.ObstacleAnimationTag;
import graphics.obstacle.ObstacleAnimationVariant;
import graphics.obstacle.ObstacleRenderInstance;
import graphics.obstacle.ObstacleVisualState;

import java.util.List;

public class ObstacleIdleSelectionStrategy implements VariantSelectionStrategy<ObstacleVisualState, ObstacleAnimationVariant, ObstacleRenderInstance> {

    @Override
    public ObstacleAnimationVariant select(List<ObstacleAnimationVariant> variants, ObstacleRenderInstance instance) {
        if (isVariantsInvalid(variants)) {
            return null;
        }

        AnimationController<ObstacleAnimationVariant> controller = instance.getAnimationController();
        ObstacleAnimationVariant currentVariant = controller.getCurrentVariant();

        if (currentVariant != null && isVariation(currentVariant)) {
            if (!controller.isFinished()) {
                return currentVariant;
            }
        }

        if (instance.shouldTriggerVariation()) {
            for (ObstacleAnimationVariant variant : variants) {
                if (isVariation(variant)) {
                    instance.resetVariationTimer();
                    return variant;
                }
            }
        }

        for (ObstacleAnimationVariant variant : variants) {
            if (variant.getTag() != null && "DEFAULT".equals(variant.getTag().name())) {
                return variant;
            }
        }

        return variants.get(0);
    }

    private boolean isVariation(ObstacleAnimationVariant variant) {
        if (variant.getTag() == null) return false;
        ObstacleAnimationTag tag = variant.getTag();
        return tag == ObstacleAnimationTag.SHINE;
    }
}

package graphics.general.animation.strategy;

import graphics.plant.PlantAnimationTag;
import graphics.plant.PlantAnimationVariant;
import graphics.plant.PlantRenderInstance;
import graphics.plant.PlantVisualState;

import java.util.ArrayList;
import java.util.List;

public class PlantIdleSelectionStrategy
    implements VariantSelectionStrategy<
    PlantVisualState,
    PlantAnimationVariant,
    PlantRenderInstance
    > {

    @Override
    public PlantAnimationVariant select(
        List<PlantAnimationVariant> variants,
        PlantRenderInstance instance
    ) {
        if (isVariantsInvalid(variants)) {
            return null;
        }

        PlantAnimationVariant current =
            instance.getAnimationController()
                .getCurrentVariant();

        if (shouldKeepCurrentVariation(current, instance)) {
            return current;
        }

        if (instance.shouldTriggerIdleVariation()) {
            PlantAnimationVariant variation =
                findPlantVariationVariant(
                    variants,
                    instance
                );

            instance.resetIdleTimer();

            if (variation != null) {
                return variation;
            }
        }

        return findPlantDefaultVariant(
            variants,
            instance
        );
    }

    private boolean shouldKeepCurrentVariation(
        PlantAnimationVariant current,
        PlantRenderInstance instance
    ) {
        if (current == null || !isVariationTag(current.getTag())) {
            return false;
        }
        if (!current.matchesCondition(instance.getPlant())) {
            return false;
        }
        if (!current.isLoop()) {
            return !instance.getAnimationController().isFinished();
        }
        return instance.getAnimationController().getLoopCount() < 1;
    }

    private PlantAnimationVariant findPlantVariationVariant(
        List<PlantAnimationVariant> variants,
        PlantRenderInstance instance
    ) {
        ArrayList<PlantAnimationVariant> variations = new ArrayList<>();
        for (PlantAnimationVariant variant : variants) {
            if (isVariationTag(variant.getTag())
                && variant.matchesCondition(instance.getPlant())) {
                variations.add(variant);
            }
        }
        if (variations.isEmpty()) {
            return null;
        }
        return variations.get((int) (Math.random() * variations.size()));
    }

    private boolean isVariationTag(PlantAnimationTag tag) {
        return tag == PlantAnimationTag.BLINK
            || tag == PlantAnimationTag.SPECIAL;
    }

    private PlantAnimationVariant findPlantDefaultVariant(
        List<PlantAnimationVariant> variants,
        PlantRenderInstance instance
    ) {
        for (PlantAnimationVariant variant : variants) {
            if (variant.getTag() == PlantAnimationTag.DEFAULT
                && variant.hasCondition()
                && variant.matchesCondition(instance.getPlant())) {
                return variant;
            }
        }

        for (PlantAnimationVariant variant : variants) {
            if (variant.getTag() == PlantAnimationTag.DEFAULT
                && !variant.hasCondition()) {
                return variant;
            }
        }

        return null;
    }
}

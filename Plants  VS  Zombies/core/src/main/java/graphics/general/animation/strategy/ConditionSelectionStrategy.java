package graphics.general.animation.strategy;

import graphics.general.animation.AnimationVariant;
import graphics.general.instance.RenderInstance;

import java.util.List;

public class ConditionSelectionStrategy<
    M,
    S extends Enum<S>,
    V extends AnimationVariant<M, ?>,
    I extends RenderInstance<M, S, ?, V>
    > implements VariantSelectionStrategy<S, V, I> {

    @Override
    public V select(List<V> variants, I instance) {
        if (isVariantsInvalid(variants)) {
            return null;
        }

        for (V variant : variants) {
            if (variant.hasCondition()
                && variant.matchesCondition(instance.getModel())) {
                return variant;
            }
        }

        for (V variant : variants) {
            if (!variant.hasCondition()) {
                return variant;
            }
        }

        return null;
    }
}

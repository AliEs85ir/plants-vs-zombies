package graphics.general.animation.strategy;

import graphics.general.animation.AnimationVariant;
import graphics.general.instance.RenderInstance;

import java.util.List;

public interface VariantSelectionStrategy<
    S extends Enum<S>,
    V extends AnimationVariant<?, ?>,
    I extends RenderInstance<?, S, ?, V>
    > {
    V select(List<V> variants, I instance);

    default boolean isVariantsInvalid(List<V> variants) {
        return variants == null || variants.isEmpty();
    }
}

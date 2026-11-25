package graphics.general.animation.strategy;

import graphics.general.animation.AnimationVariant;
import graphics.general.instance.RenderInstance;

import java.util.List;

public class FirstSelectionStrategy<
    S extends Enum<S>,
    V extends AnimationVariant<?, ?>,
    I extends RenderInstance<?, S, ?, V>
    > implements VariantSelectionStrategy<S, V, I> {

    @Override
    public V select(List<V> variants, I instance) {
        if (isVariantsInvalid(variants)) {
            return null;
        }
        return variants.get(0);
    }
}

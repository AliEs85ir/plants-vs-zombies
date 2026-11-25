package graphics.general.animation.strategy;

import graphics.general.animation.AnimationVariant;
import graphics.general.instance.RenderInstance;
import java.util.List;

public class LoopSequenceSelectionStrategy<
    S extends Enum<S>,
    V extends AnimationVariant<?, ?>,
    I extends RenderInstance<?, S, ?, V>
    > implements VariantSelectionStrategy<S, V, I> {

    @Override
    public V select(List<V> variants, I instance) {
        if (isVariantsInvalid(variants)) {
            return null;
        }
        int stage = instance.getAnimationStage();
        int index = stage % variants.size();
        return variants.get(index);
    }
}

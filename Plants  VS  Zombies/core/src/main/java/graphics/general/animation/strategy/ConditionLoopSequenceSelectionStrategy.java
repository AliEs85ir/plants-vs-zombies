package graphics.general.animation.strategy;

import graphics.general.animation.AnimationVariant;
import graphics.general.instance.RenderInstance;

import java.util.ArrayList;
import java.util.List;

public class ConditionLoopSequenceSelectionStrategy<
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
        List<V> matching = new ArrayList<>();
        for (V variant : variants) {
            if (!variant.hasCondition() || variant.matchesCondition(instance.getModel())) {
                matching.add(variant);
            }
        }
        if (matching.isEmpty()) {
            matching.addAll(variants);
        }
        List<V> specific = new ArrayList<>();
        for (V variant : matching) {
            if (variant.hasCondition()) {
                specific.add(variant);
            }
        }
        List<V> cycle = specific.isEmpty() ? matching : specific;
        int stage = instance.getAnimationStage();
        int index = stage % cycle.size();
        return cycle.get(index);
    }
}

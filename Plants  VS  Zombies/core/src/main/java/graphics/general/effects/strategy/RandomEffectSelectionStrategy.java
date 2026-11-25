package graphics.general.effects.strategy;

import graphics.general.effects.EffectDefinition;
import graphics.general.effects.EffectInstance;
import graphics.general.effects.EffectTag;
import graphics.general.effects.EffectVariant;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomEffectSelectionStrategy<M> implements EffectSelectionStrategy<M> {

    @Override
    public List<EffectVariant<M, EffectTag>> select(EffectDefinition<M> definition, EffectInstance<M> instance) {
        List<EffectVariant<M, EffectTag>> variants = definition.getVariants();
        if (variants.isEmpty()) {
            return List.of();
        }
        int index = ThreadLocalRandom.current().nextInt(variants.size());
        return List.of(variants.get(index));
    }
}

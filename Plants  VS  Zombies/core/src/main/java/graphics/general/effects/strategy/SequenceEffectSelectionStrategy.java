package graphics.general.effects.strategy;

import graphics.general.effects.EffectDefinition;
import graphics.general.effects.EffectInstance;
import graphics.general.effects.EffectTag;
import graphics.general.effects.EffectVariant;

import java.util.List;

public class SequenceEffectSelectionStrategy<M> implements EffectSelectionStrategy<M> {

    @Override
    public List<EffectVariant<M, EffectTag>> select(EffectDefinition<M> definition, EffectInstance<M> instance) {
        return List.copyOf(definition.getVariants());
    }
}

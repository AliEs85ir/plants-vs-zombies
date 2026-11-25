package graphics.general.effects.strategy;

import graphics.general.effects.EffectDefinition;
import graphics.general.effects.EffectInstance;
import graphics.general.effects.EffectTag;
import graphics.general.effects.EffectVariant;

import java.util.List;

public class FirstEffectSelectionStrategy<M> implements EffectSelectionStrategy<M> {

    @Override
    public List<EffectVariant<M, EffectTag>> select(EffectDefinition<M> definition, EffectInstance<M> instance) {
        if (definition.getVariants().isEmpty()) {
            return List.of();
        }
        return List.of(definition.getVariants().get(0));
    }
}

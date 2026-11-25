package graphics.general.effects.strategy;

import graphics.general.effects.EffectDefinition;
import graphics.general.effects.EffectInstance;
import graphics.general.effects.EffectTag;
import graphics.general.effects.EffectVariant;

import java.util.List;

public class ConditionEffectSelectionStrategy<M> implements EffectSelectionStrategy<M> {

    @Override
    public List<EffectVariant<M, EffectTag>> select(EffectDefinition<M> definition, EffectInstance<M> instance) {
        for (EffectVariant<M, EffectTag> variant : definition.getVariants()) {
            if (variant.matchesCondition(instance.getContext())) {
                return List.of(variant);
            }
        }
        if (definition.getVariants().isEmpty()) {
            return List.of();
        }
        return List.of(definition.getVariants().get(0));
    }
}

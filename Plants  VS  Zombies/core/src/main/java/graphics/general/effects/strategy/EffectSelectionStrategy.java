package graphics.general.effects.strategy;

import graphics.general.effects.EffectDefinition;
import graphics.general.effects.EffectInstance;
import graphics.general.effects.EffectTag;
import graphics.general.effects.EffectVariant;

import java.util.List;

public interface EffectSelectionStrategy<M> {
    List<EffectVariant<M, EffectTag>> select(EffectDefinition<M> definition, EffectInstance<M> instance);
}

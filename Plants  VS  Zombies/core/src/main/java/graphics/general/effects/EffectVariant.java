package graphics.general.effects;

import graphics.general.animation.AnimationVariant;
import graphics.general.animation.condition.AnimationCondition;

import java.util.Map;

public final class EffectVariant<M, T extends Enum<T>> extends AnimationVariant<M, T> {

    public EffectVariant(String clipName,
                         boolean loop,
                         int priority,
                         T tag,
                         AnimationCondition<M> condition,
                         String pamPath, Map<String, Boolean> visibility) {
        super(clipName, loop, priority, tag, condition, pamPath, visibility);
    }

    public EffectVariant(String clipName, boolean loop, int priority, T tag) {
        super(clipName, loop, priority, tag);
    }


}

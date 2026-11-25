package graphics.sun;

import game.entity.sun.Sun;
import graphics.general.animation.AnimationVariant;
import graphics.general.animation.condition.AnimationCondition;

public class SunAnimationVariant extends AnimationVariant<Sun, SunAnimationTag> {
    public SunAnimationVariant(String clipName, boolean loop, int priority, SunAnimationTag tag,
                               AnimationCondition<Sun> condition) {
        super(clipName, loop, priority, tag, condition);
    }

    public SunAnimationVariant(String clipName, boolean loop, int priority, SunAnimationTag tag) {
        super(clipName, loop, priority, tag);
    }
}

package graphics.lawnmower;
import game.entity.lawnmower.LawnMower;
import graphics.general.animation.AnimationVariant;
import graphics.general.animation.condition.AnimationCondition;

public class LMAnimationVariant extends AnimationVariant<LawnMower, LMAnimationTag> {
    public LMAnimationVariant(String clipName, boolean loop, int priority, LMAnimationTag tag, AnimationCondition<LawnMower> condition) {
        super(clipName, loop, priority, tag, condition);
    }

    public LMAnimationVariant(String clipName, boolean loop, int priority, LMAnimationTag tag) {
        super(clipName, loop, priority, tag);
    }
}

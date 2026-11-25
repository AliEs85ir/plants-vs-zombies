package graphics.zombie;
import game.entity.zombie.Zombie;
import graphics.general.animation.AnimationVariant;
import graphics.general.animation.condition.AnimationCondition;


public class ZombieAnimationVariant extends AnimationVariant<Zombie, ZombieAnimationTag> {
    public ZombieAnimationVariant(String clipName, boolean loop, int priority, ZombieAnimationTag tag, AnimationCondition<Zombie> condition) {
        super(clipName, loop, priority, tag, condition);
    }

    public ZombieAnimationVariant(String clipName, boolean loop, int priority, ZombieAnimationTag tag) {
        super(clipName, loop, priority, tag);
    }
}

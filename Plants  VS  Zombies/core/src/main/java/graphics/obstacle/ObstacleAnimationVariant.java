package graphics.obstacle;

import game.entity.obstacle.Obstacle;
import graphics.general.animation.AnimationVariant;
import graphics.general.animation.condition.AnimationCondition;

public class ObstacleAnimationVariant extends AnimationVariant<Obstacle, ObstacleAnimationTag> {
    public ObstacleAnimationVariant(String clipName, boolean loop, int priority, ObstacleAnimationTag tag, AnimationCondition<Obstacle> condition) {
        super(clipName, loop, priority, tag, condition);
    }

    public ObstacleAnimationVariant(String clipName, boolean loop, int priority, ObstacleAnimationTag tag) {
        super(clipName, loop, priority, tag);
    }
}

package graphics.plant;

import game.entity.plant.Plant;
import graphics.general.animation.AnimationVariant;
import graphics.general.animation.condition.AnimationCondition;
public class PlantAnimationVariant extends AnimationVariant<Plant, PlantAnimationTag> {
    public PlantAnimationVariant(String clipName, boolean loop, int priority, PlantAnimationTag tag, AnimationCondition<Plant> condition) {
        super(clipName, loop, priority, tag, condition);
    }

    public PlantAnimationVariant(String clipName, boolean loop, int priority, PlantAnimationTag tag) {
        super(clipName, loop, priority, tag);
    }
}

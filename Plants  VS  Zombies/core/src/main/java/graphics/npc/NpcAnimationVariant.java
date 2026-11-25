package graphics.npc;

import graphics.general.animation.AnimationVariant;
import graphics.general.animation.condition.AnimationCondition;

public class NpcAnimationVariant extends AnimationVariant<NpcPresence, NpcAnimationTag> {
    public NpcAnimationVariant(String clipName, boolean loop, int priority, NpcAnimationTag tag) {
        super(clipName, loop, priority, tag);
    }

    public NpcAnimationVariant(
        String clipName,
        boolean loop,
        int priority,
        NpcAnimationTag tag,
        AnimationCondition<NpcPresence> condition
    ) {
        super(clipName, loop, priority, tag, condition);
    }
}

package graphics.npc;

import game.dialogue.Speaker;
import graphics.general.animation.AnimationSelector;
import graphics.general.animation.strategy.ConditionSelectionStrategy;
import graphics.general.animation.strategy.FirstSelectionStrategy;

public class NpcAnimationSelector
    extends AnimationSelector<Speaker, NpcVisualState, NpcAnimationVariant, NpcRenderInstance> {

    public NpcAnimationSelector() {
        super(NpcVisualState.class, new FirstSelectionStrategy<>());
        registerStrategy(NpcVisualState.ENTERING, new FirstSelectionStrategy<>());
        registerStrategy(NpcVisualState.IDLE, new FirstSelectionStrategy<>());
        registerStrategy(NpcVisualState.LEAVING, new FirstSelectionStrategy<>());
        registerStrategy(NpcVisualState.TALKING, new ConditionSelectionStrategy<>());
    }
}

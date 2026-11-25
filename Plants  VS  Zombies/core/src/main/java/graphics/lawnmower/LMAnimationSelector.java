package graphics.lawnmower;

import app.enums.lawnmower.LawnMowerType;
import graphics.general.animation.AnimationSelector;
import graphics.general.animation.strategy.FirstSelectionStrategy;
import graphics.general.animation.strategy.SequenceSelectionStrategy;

public class LMAnimationSelector extends AnimationSelector<LawnMowerType, LMVisualState, LMAnimationVariant, LMRenderInstance> {
    public LMAnimationSelector() {
        super(LMVisualState.class, new FirstSelectionStrategy<>());
        registerDefaults();
    }

    private void registerDefaults() {
        // defaults :
        registerStrategy(LMVisualState.IDLE, new FirstSelectionStrategy<>());
        registerStrategy(LMVisualState.TRANSITION, new SequenceSelectionStrategy<>());
        registerStrategy(LMVisualState.ATTACK, new FirstSelectionStrategy<>());


        // specifics :

        //registerStrategy(LawnMowerType.ICEAGE, LMVisualState.IDLE, new );
    }
}

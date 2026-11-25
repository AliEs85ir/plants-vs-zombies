package graphics.sun;

import app.enums.sun.SunType;
import graphics.general.animation.AnimationSelector;
import graphics.general.animation.strategy.FirstSelectionStrategy;
import graphics.general.animation.strategy.LoopSequenceSelectionStrategy;
import graphics.general.animation.strategy.SequenceSelectionStrategy;

public class SunAnimationSelector
    extends AnimationSelector<SunType, SunVisualState, SunAnimationVariant, SunRenderInstance> {

    public SunAnimationSelector() {
        super(SunVisualState.class, new FirstSelectionStrategy<>());
        registerDefaults();

        registerStrategy(SunType.RADIOACTIVE, SunVisualState.IDLE,
            new LoopSequenceSelectionStrategy<>());
    }

    private void registerDefaults() {
        registerStrategy(SunVisualState.IDLE, new SequenceSelectionStrategy<>());
        registerStrategy(SunVisualState.EXPLODE, new FirstSelectionStrategy<>());
        registerStrategy(SunVisualState.NORMAL_SUN, new FirstSelectionStrategy<>());

    }
}

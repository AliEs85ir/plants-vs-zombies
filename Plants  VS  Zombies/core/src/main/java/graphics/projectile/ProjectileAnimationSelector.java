package graphics.projectile;

import app.enums.plant.PlantName;
import graphics.general.animation.AnimationSelector;
import graphics.general.animation.strategy.ConditionSelectionStrategy;
import graphics.general.animation.strategy.ConditionSequenceSelectionStrategy;
import graphics.general.animation.strategy.FirstSelectionStrategy;

public class ProjectileAnimationSelector
    extends AnimationSelector<PlantName, ProjectileVisualState, ProjectileAnimationVariant, ProjectileRenderInstance> {

    public ProjectileAnimationSelector() {
        super(ProjectileVisualState.class, new FirstSelectionStrategy<>());
        registerStrategy(ProjectileVisualState.FLYING, new ConditionSequenceSelectionStrategy<>());
        registerStrategy(ProjectileVisualState.IMPACT, new ConditionSelectionStrategy<>());
    }
}
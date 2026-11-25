package graphics.obstacle;

import app.enums.obstacle.ObstacleType;
import graphics.general.animation.AnimationSelector;
import graphics.general.animation.strategy.ConditionSelectionStrategy;
import graphics.general.animation.strategy.FirstSelectionStrategy;
import graphics.general.animation.strategy.ObstacleIdleSelectionStrategy;
import graphics.general.animation.strategy.SequenceSelectionStrategy;

public class ObstacleAnimationSelector extends AnimationSelector<ObstacleType, ObstacleVisualState, ObstacleAnimationVariant, ObstacleRenderInstance> {
    public ObstacleAnimationSelector() {
        super(ObstacleVisualState.class, new FirstSelectionStrategy<>());
        registerDefaults();
    }

    private void registerDefaults() {
        registerStrategy(ObstacleVisualState.IDLE, new ObstacleIdleSelectionStrategy());
        registerStrategy(ObstacleVisualState.ABILITY, new ConditionSelectionStrategy<>());
        registerStrategy(ObstacleVisualState.DEATH, new FirstSelectionStrategy<>());

        registerStrategy(ObstacleType.PERMANENT_SCORCHED, ObstacleVisualState.IDLE, new SequenceSelectionStrategy<>());
        registerStrategy(ObstacleType.MISSILE, ObstacleVisualState.ABILITY, new FirstSelectionStrategy<>());
        registerStrategy(ObstacleType.SLINGSHOT, ObstacleVisualState.ABILITY, new FirstSelectionStrategy<>());
        registerStrategy(ObstacleType.FIREBALL, ObstacleVisualState.ABILITY, new FirstSelectionStrategy<>());
        registerStrategy(ObstacleType.GOLD_TILE, ObstacleVisualState.ABILITY, new FirstSelectionStrategy<>());
        registerStrategy(ObstacleType.NECROMANCY_TILE, ObstacleVisualState.ABILITY, new FirstSelectionStrategy<>());
        registerStrategy(ObstacleType.ICE_BLOCK, ObstacleVisualState.ABILITY, new FirstSelectionStrategy<>());
        registerStrategy(ObstacleType.RANDOM_VASE, ObstacleVisualState.IDLE, new FirstSelectionStrategy<>());
        registerStrategy(ObstacleType.GARGANTUAR_VASE, ObstacleVisualState.IDLE, new FirstSelectionStrategy<>());
        registerStrategy(ObstacleType.HOOK, ObstacleVisualState.IDLE, new FirstSelectionStrategy<>());
        registerStrategy(ObstacleType.CABINET, ObstacleVisualState.IDLE, new FirstSelectionStrategy<>());
        registerStrategy(ObstacleType.PUSHABLE_ICE, ObstacleVisualState.IDLE, new FirstSelectionStrategy<>());
        registerStrategy(ObstacleType.WALLNUT, ObstacleVisualState.IDLE, new FirstSelectionStrategy<>());
        registerStrategy(ObstacleType.TALLNUT, ObstacleVisualState.IDLE, new FirstSelectionStrategy<>());
        registerStrategy(ObstacleType.EXPLODE_O_NUT, ObstacleVisualState.IDLE, new FirstSelectionStrategy<>());
        registerStrategy(ObstacleType.BOSS_ICE, ObstacleVisualState.IDLE, new FirstSelectionStrategy<>());
        registerStrategy(ObstacleType.SQUASH, ObstacleVisualState.IDLE, new FirstSelectionStrategy<>());
        registerStrategy(ObstacleType.PUDDLE, ObstacleVisualState.IDLE, new FirstSelectionStrategy<>());
        registerStrategy(ObstacleType.GARGANTUAR_PUDDLE, ObstacleVisualState.IDLE, new FirstSelectionStrategy<>());
        registerStrategy(ObstacleType.PLANT_VASE, ObstacleVisualState.IDLE, new FirstSelectionStrategy<>());
        registerStrategy(ObstacleType.SNOWBALL, ObstacleVisualState.IDLE, new FirstSelectionStrategy<>());
        registerStrategy(ObstacleType.PEASHOOTER, ObstacleVisualState.IDLE, new FirstSelectionStrategy<>());
        registerStrategy(ObstacleType.SNOW_PEA, ObstacleVisualState.IDLE, new FirstSelectionStrategy<>());
        registerStrategy(ObstacleType.SLIDING_TILE_DOWN, ObstacleVisualState.IDLE, new FirstSelectionStrategy<>());
        registerStrategy(ObstacleType.CRATER, ObstacleVisualState.IDLE, new FirstSelectionStrategy<>());
        registerStrategy(ObstacleType.SLIDING_TILE_UP, ObstacleVisualState.IDLE, new FirstSelectionStrategy<>());
        registerStrategy(ObstacleType.EGYPT_GRAVE, ObstacleVisualState.IDLE, new FirstSelectionStrategy<>());
        registerStrategy(ObstacleType.PLANTFOOD_GRAVE, ObstacleVisualState.IDLE, new FirstSelectionStrategy<>());
        registerStrategy(ObstacleType.SUN_GRAVE, ObstacleVisualState.IDLE, new FirstSelectionStrategy<>());
        registerStrategy(ObstacleType.TUTORIAL_GRAVE, ObstacleVisualState.IDLE, new FirstSelectionStrategy<>());
        registerStrategy(ObstacleType.MAMMOTH_GLACIER, ObstacleVisualState.IDLE, new FirstSelectionStrategy<>());
        registerStrategy(ObstacleType.MAMMOTH_GLACIER, ObstacleVisualState.ABILITY, new FirstSelectionStrategy<>());
        registerStrategy(ObstacleType.BRAIN, ObstacleVisualState.IDLE, new FirstSelectionStrategy<>());

    }
}

package graphics.plant;

import app.enums.plant.PlantName;
import app.enums.sun.SunType;
import graphics.general.animation.AnimationSelector;
import graphics.general.animation.strategy.*;
import graphics.sun.SunVisualState;

public class PlantAnimationSelector
    extends AnimationSelector<
    PlantName,
    PlantVisualState,
    PlantAnimationVariant,
    PlantRenderInstance
    > {

    public PlantAnimationSelector() {
        super(
            PlantVisualState.class,
            new FirstSelectionStrategy<>()
        );

        registerDefaults();

        registerStrategy(PlantName.SUN_SHROOM, PlantVisualState.IDLE,
            new PlantIdleSelectionStrategy());

        registerStrategy(PlantName.SUN_SHROOM, PlantVisualState.ATTACK,
            new ConditionSelectionStrategy<>());

        registerStrategy(PlantName.POTATO_MINE, PlantVisualState.IDLE,
            new ConditionSelectionStrategy<>());
        registerStrategy(PlantName.PRIMAL_POTATO_MINE, PlantVisualState.IDLE,
            new ConditionSelectionStrategy<>());
        registerStrategy(PlantName.DOOM_SHROOM, PlantVisualState.IDLE,
            new ConditionSelectionStrategy<>());
        registerStrategy(PlantName.DOOM_SHROOM, PlantVisualState.ATTACK,
            new ConditionSelectionStrategy<>());
        registerStrategy(PlantName.DOOM_SHROOM, PlantVisualState.ATTACK_2,
            new ConditionSelectionStrategy<>());
        registerStrategy(PlantName.SQUASH, PlantVisualState.ATTACK,
            new ConditionSequenceSelectionStrategy<>());
        registerStrategy(PlantName.TANGLE_KELP, PlantVisualState.IDLE,
            new PlantIdleSelectionStrategy());
        registerStrategy(PlantName.BONK_CHOY, PlantVisualState.IDLE,
            new PlantIdleSelectionStrategy());
        registerStrategy(PlantName.WASABI_WHIP, PlantVisualState.IDLE,
            new PlantIdleSelectionStrategy());
        registerStrategy(PlantName.PHAT_BEET, PlantVisualState.IDLE,
            new PlantIdleSelectionStrategy());
        registerStrategy(PlantName.CHOMPER, PlantVisualState.IDLE,
            new PlantIdleSelectionStrategy());
        registerStrategy(PlantName.KIWIBEAST, PlantVisualState.IDLE,
            new PlantIdleSelectionStrategy());
        registerStrategy(PlantName.KIWIBEAST, PlantVisualState.ATTACK,
            new ConditionSelectionStrategy<>());
        registerStrategy(PlantName.KIWIBEAST, PlantVisualState.ATTACK_2,
            new ConditionSelectionStrategy<>());

        registerStrategy(PlantName.WALL_NUT, PlantVisualState.IDLE,
            new PlantIdleSelectionStrategy());
        registerStrategy(PlantName.TALL_NUT, PlantVisualState.IDLE,
            new PlantIdleSelectionStrategy());
        registerStrategy(PlantName.ENDURIAN, PlantVisualState.IDLE,
            new PlantIdleSelectionStrategy());
        registerStrategy(PlantName.ENDURIAN, PlantVisualState.ATTACK,
            new ConditionSequenceSelectionStrategy<>());
        registerStrategy(PlantName.ENDURIAN, PlantVisualState.RECOVER,
            new ConditionSelectionStrategy<>());
        registerStrategy(PlantName.GARLIC, PlantVisualState.IDLE,
            new PlantIdleSelectionStrategy());
        registerStrategy(PlantName.SWEET_POTATO, PlantVisualState.IDLE,
            new PlantIdleSelectionStrategy());
        registerStrategy(PlantName.EXPLODE_O_NUT, PlantVisualState.IDLE,
            new PlantIdleSelectionStrategy());
        registerStrategy(PlantName.PUMPKIN, PlantVisualState.IDLE,
            new PlantIdleSelectionStrategy());

        registerStrategy(PlantName.PEPPER_PULT, PlantVisualState.IDLE,
            new PlantIdleSelectionStrategy());

        registerStrategy(PlantName.CACTUS, PlantVisualState.IDLE,
            new ConditionLoopSequenceSelectionStrategy<>());
        registerStrategy(PlantName.CACTUS, PlantVisualState.ATTACK,
            new ConditionSequenceSelectionStrategy<>());

    }

    private void registerDefaults() {
        registerStrategy(
            PlantVisualState.IDLE,
            new LoopSequenceSelectionStrategy<>()
        );

        registerStrategy(
            PlantVisualState.ATTACK,
            new SequenceSelectionStrategy<>()
        );

        registerStrategy(
            PlantVisualState.ATTACK_2,
            new SequenceSelectionStrategy<>()
        );

        registerStrategy(
            PlantVisualState.HURT,
            new ConditionSelectionStrategy<>()
        );

        registerStrategy(
            PlantVisualState.PLANT_FOOD,
            new SequenceSelectionStrategy<>()
        );

        registerStrategy(
            PlantVisualState.RECOVER,
            new SequenceSelectionStrategy<>()
        );

        registerStrategy(
            PlantVisualState.TURN,
            new SequenceSelectionStrategy<>()
        );
    }
}

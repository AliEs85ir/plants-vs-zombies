package graphics.zombie;

import graphics.general.animation.AnimationSelector;
import graphics.general.animation.strategy.ConditionSelectionStrategy;
import graphics.general.animation.strategy.FirstSelectionStrategy;
import graphics.general.animation.strategy.SequenceSelectionStrategy;
import graphics.general.animation.strategy.ZombieIdleSelectionStrategy;

public class ZombieAnimationSelector extends AnimationSelector<String, ZombieVisualState, ZombieAnimationVariant, ZombieRenderInstance> {
    public ZombieAnimationSelector() {
        super(ZombieVisualState.class, new FirstSelectionStrategy<>());
        registerDefaults();
    }

    private void registerDefaults() {
        // defaults :
        registerStrategy(ZombieVisualState.IDLE, new FirstSelectionStrategy<>());
        registerStrategy(ZombieVisualState.WALK, new FirstSelectionStrategy<>());
        registerStrategy(ZombieVisualState.ATTACK, new FirstSelectionStrategy<>());
        registerStrategy(ZombieVisualState.DEATH, new FirstSelectionStrategy<>());
        registerStrategy(ZombieVisualState.ABILITY, new FirstSelectionStrategy<>());
        registerStrategy(ZombieVisualState.INTRO, new FirstSelectionStrategy<>());

        // specifics :
        registerStrategy("ZombieModernAllStar", ZombieVisualState.ABILITY, new ConditionSelectionStrategy<>());
        registerStrategy("ZombieBeachFisherman", ZombieVisualState.ABILITY, new ConditionSelectionStrategy<>());
        registerStrategy("ZombieImp", ZombieVisualState.ABILITY, new ConditionSelectionStrategy<>());

        registerStrategy("Gargantuar1", ZombieVisualState.ABILITY, new SequenceSelectionStrategy<>());
        registerStrategy("Gargantuar2", ZombieVisualState.ABILITY, new SequenceSelectionStrategy<>());
        registerStrategy("Gargantuar4", ZombieVisualState.ABILITY, new SequenceSelectionStrategy<>());
        registerStrategy("Gargantuar5", ZombieVisualState.ABILITY, new SequenceSelectionStrategy<>());
        registerStrategy("Gargantuar7", ZombieVisualState.ABILITY, new SequenceSelectionStrategy<>());
        registerStrategy("Gargantuar8", ZombieVisualState.ABILITY, new SequenceSelectionStrategy<>());
        registerStrategy("Gargantuar11", ZombieVisualState.ABILITY, new SequenceSelectionStrategy<>());
        registerStrategy("Gargantuar12", ZombieVisualState.ABILITY, new SequenceSelectionStrategy<>());
        registerStrategy("Gargantuar13", ZombieVisualState.ABILITY, new SequenceSelectionStrategy<>());

        registerStrategy("Gargantuar1", ZombieVisualState.ATTACK, new SequenceSelectionStrategy<>());
        registerStrategy("Gargantuar2", ZombieVisualState.ATTACK, new SequenceSelectionStrategy<>());
        registerStrategy("Gargantuar4", ZombieVisualState.ATTACK, new SequenceSelectionStrategy<>());
        registerStrategy("Gargantuar5", ZombieVisualState.ATTACK, new SequenceSelectionStrategy<>());
        registerStrategy("Gargantuar7", ZombieVisualState.ATTACK, new SequenceSelectionStrategy<>());
        registerStrategy("Gargantuar8", ZombieVisualState.ATTACK, new SequenceSelectionStrategy<>());
        registerStrategy("Gargantuar11", ZombieVisualState.ATTACK, new SequenceSelectionStrategy<>());
        registerStrategy("Gargantuar12", ZombieVisualState.ATTACK, new SequenceSelectionStrategy<>());
        registerStrategy("Gargantuar13", ZombieVisualState.ATTACK, new SequenceSelectionStrategy<>());

        registerStrategy("ZombieNewspaper", ZombieVisualState.WALK, new ConditionSelectionStrategy<>());
        registerStrategy("ZombieNewspaper", ZombieVisualState.ATTACK, new ConditionSelectionStrategy<>());

        registerStrategy("ZombieRa", ZombieVisualState.ABILITY, new SequenceSelectionStrategy<>());
        registerStrategy("SunProducer", ZombieVisualState.ABILITY, new SequenceSelectionStrategy<>());

        registerStrategy("ZombieCrystalSkull", ZombieVisualState.ABILITY, new ConditionSelectionStrategy<>());

        registerStrategy("ZombieProspector", ZombieVisualState.ABILITY, new ConditionSelectionStrategy<>());

        registerStrategy("jester1", ZombieVisualState.ABILITY, new ConditionSelectionStrategy<>());
        registerStrategy("jester2", ZombieVisualState.ABILITY, new ConditionSelectionStrategy<>());
        registerStrategy("jester3", ZombieVisualState.ABILITY, new ConditionSelectionStrategy<>());
        registerStrategy("jester4", ZombieVisualState.ABILITY, new ConditionSelectionStrategy<>());

        registerStrategy("ZombieBossDragon", ZombieVisualState.ABILITY, new ConditionSelectionStrategy<>());
        registerStrategy("ZombieBossDragon", ZombieVisualState.DEATH, new ConditionSelectionStrategy<>());



        registerStrategy("ZombieBossEgypt", ZombieVisualState.ABILITY, new ConditionSelectionStrategy<>());
        registerStrategy("ZombieBossEgypt", ZombieVisualState.DEATH, new ConditionSelectionStrategy<>());
        registerStrategy("ZombieBossEgypt", ZombieVisualState.WALK, new ConditionSelectionStrategy<>());

        registerStrategy("ZombieBossMammoth", ZombieVisualState.ABILITY, new ConditionSelectionStrategy<>());
        registerStrategy("ZombieBossMammoth", ZombieVisualState.DEATH, new ConditionSelectionStrategy<>());

        registerStrategy("ZombieBossBeach", ZombieVisualState.ABILITY, new ConditionSelectionStrategy<>());
        registerStrategy("ZombieBossBeach", ZombieVisualState.DEATH, new ConditionSelectionStrategy<>());

        registerStrategy("ZombieDarkKing", ZombieVisualState.IDLE, new ZombieIdleSelectionStrategy());

    }
}

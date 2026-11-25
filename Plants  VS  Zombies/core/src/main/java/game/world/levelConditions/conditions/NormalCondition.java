package game.world.levelConditions.conditions;

import game.world.GameWorld;

public class NormalCondition implements LevelCondition {
    @Override
    public LevelOutcome evaluate(GameWorld world) {
        if (StageOutcomeRules.zombieReachedLoseLine(world)) {
            return LevelOutcome.PLAYER_LOST;
        }
        if (StageOutcomeRules.allWavesCleared(world)) {
            return LevelOutcome.PLAYER_WON;
        }
        return LevelOutcome.NONE;
    }
}

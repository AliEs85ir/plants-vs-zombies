package game.world.levelConditions.conditions;

import game.world.GameWorld;

public class PlantZombiesCondition implements LevelCondition {
    @Override
    public LevelOutcome evaluate(GameWorld world) {
        if (StageOutcomeRules.zombieReachedLoseLine(world)) {
            return LevelOutcome.PLAYER_LOST;
        }
        if (StageOutcomeRules.allWavesCleared(world)) {
            return MinigameLevelOutcomes.cleared(world);
        }
        return LevelOutcome.NONE;
    }
}

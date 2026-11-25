package game.world.levelConditions.conditions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class StageOutcomeRulesTest {

    @Test
    void nullWorldDoesNotWinOrLose() {
        assertFalse(StageOutcomeRules.zombieReachedLoseLine(null));
        assertFalse(StageOutcomeRules.allWavesCleared(null));
    }
}

package game.world.levelConditions.conditions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MinigameLevelOutcomesTest {

    @Test
    void nullWorldCountsAsFinalWin() {
        assertEquals(LevelOutcome.PLAYER_WON, MinigameLevelOutcomes.cleared(null));
    }
}

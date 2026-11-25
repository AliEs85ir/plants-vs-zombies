package game.entity.quest;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SnapshotConditionTest {
    @Test
    void keepsProgressOnceSatisfied() {
        SnapshotCondition condition = new SnapshotCondition(facts -> false);
        condition.setCurrentProgress(1);
        assertTrue(condition.isReached());
        assertTrue(condition.verifyFinalState(null));
        assertEquals(1, condition.getCurrentProgress());
        assertTrue(condition.isReached());
    }
}

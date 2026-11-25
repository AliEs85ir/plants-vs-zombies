package game.entity.quest;

import app.enums.quest.EventType;
import app.model.user.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AccumulativeConditionTest {
    @Test
    void countsMatchingEventsOnly() {
        AccumulativeCondition condition = new AccumulativeCondition(
            EventType.ZOMBIE_KILLED,
            3,
            event -> event.plantIs("CACTUS")
        );

        condition.handleEvent(new GameEvent(EventType.ZOMBIE_KILLED, 1, "PEASHOOTER", null, -1, -1, -1, false));
        condition.handleEvent(new GameEvent(EventType.ZOMBIE_KILLED, 1, "CACTUS", null, -1, -1, -1, false));
        condition.handleEvent(new GameEvent(EventType.SUN_COLLECTED, 1));
        condition.handleEvent(new GameEvent(EventType.ZOMBIE_KILLED, 2, "CACTUS", null, -1, -1, -1, false));

        assertEquals(3, condition.getCurrentProgress());
        assertTrue(condition.isReached());
    }

    @Test
    void resetEventClearsProgress() {
        AccumulativeCondition condition = new AccumulativeCondition(
            EventType.LEVEL_WON,
            EventType.LEVEL_LOST,
            2,
            event -> event.difficultyIs(3)
        );
        condition.handleEvent(new GameEvent(EventType.LEVEL_WON, 1, null, null, -1, -1, 3, false));
        assertEquals(1, condition.getCurrentProgress());
        condition.handleEvent(new GameEvent(EventType.LEVEL_LOST, 1));
        assertEquals(0, condition.getCurrentProgress());
        assertFalse(condition.isReached());
    }

    @Test
    void doesNotResetAfterGoalIsReached() {
        AccumulativeCondition condition = new AccumulativeCondition(
            EventType.LEVEL_WON,
            EventType.LEVEL_LOST,
            2,
            event -> event.difficultyIs(User.MAX_DIFFICULTY_LEVEL)
        );
        condition.handleEvent(new GameEvent(EventType.LEVEL_WON, 1, null, null, -1, -1, 5, false));
        condition.handleEvent(new GameEvent(EventType.LEVEL_WON, 1, null, null, -1, -1, 5, false));
        assertTrue(condition.isReached());
        condition.handleEvent(new GameEvent(EventType.LEVEL_LOST, 1));
        assertEquals(2, condition.getCurrentProgress());
        assertTrue(condition.isReached());
    }

    @Test
    void unreachedPerLevelProgressResetsOnWin() {
        AccumulativeCondition condition = new AccumulativeCondition(
            EventType.PLANT_EXPLODED,
            3,
            event -> true,
            EventType.LEVEL_WON,
            EventType.LEVEL_LOST
        );
        condition.handleEvent(new GameEvent(EventType.PLANT_EXPLODED, 1));
        condition.handleEvent(new GameEvent(EventType.PLANT_EXPLODED, 1));
        condition.handleEvent(new GameEvent(EventType.LEVEL_WON, 1));
        assertEquals(0, condition.getCurrentProgress());
        assertFalse(condition.isReached());
    }

    @Test
    void ignoresWinsBelowMaxDifficulty() {
        AccumulativeCondition condition = new AccumulativeCondition(
            EventType.LEVEL_WON,
            EventType.LEVEL_LOST,
            5,
            event -> event.difficultyIs(User.MAX_DIFFICULTY_LEVEL)
        );
        condition.handleEvent(new GameEvent(EventType.LEVEL_WON, 1, null, null, -1, -1, 3, false));
        assertEquals(0, condition.getCurrentProgress());
        condition.handleEvent(new GameEvent(EventType.LEVEL_WON, 1, null, null, -1, -1, 5, false));
        assertEquals(1, condition.getCurrentProgress());
    }

    @Test
    void quickActionCountsOnlyKillsBeforeThirtySeconds() {
        AccumulativeCondition condition = new AccumulativeCondition(
            EventType.ZOMBIE_KILLED,
            10,
            event -> event.withinSeconds(30),
            EventType.LEVEL_WON,
            EventType.LEVEL_LOST
        );
        condition.handleEvent(new GameEvent(EventType.ZOMBIE_KILLED, 4, null, null, -1, 0, -1, false));
        condition.handleEvent(new GameEvent(EventType.ZOMBIE_KILLED, 6, null, null, -1, 29, -1, false));
        assertTrue(condition.isReached());
        condition.handleEvent(new GameEvent(EventType.ZOMBIE_KILLED, 10, null, null, -1, 30, -1, false));
        assertEquals(10, condition.getCurrentProgress());
    }

    @Test
    void lawnMowerKillsAccumulateByAmount() {
        AccumulativeCondition condition = new AccumulativeCondition(EventType.LAWN_MOWER_TRIGGERED, 10);
        condition.handleEvent(new GameEvent(EventType.LAWN_MOWER_TRIGGERED, 4));
        condition.handleEvent(new GameEvent(EventType.LAWN_MOWER_TRIGGERED, 6));
        assertEquals(10, condition.getCurrentProgress());
        assertTrue(condition.isReached());
    }
}

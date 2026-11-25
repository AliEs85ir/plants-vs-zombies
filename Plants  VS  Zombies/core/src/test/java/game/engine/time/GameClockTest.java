package game.engine.time;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameClockTest {
    @Test
    void scaledDeltaProducesProportionallyMoreTicks() {
        GameClock normal = new GameClock();
        GameClock fast = new GameClock();
        normal.reset();
        fast.reset();

        int normalTicks = 0;
        int fastTicks = 0;
        float frame = GameClock.SECONDS_PER_TICK;
        for (int i = 0; i < 10; i++) {
            normalTicks += normal.update(frame);
            fastTicks += fast.update(frame * 3f);
        }

        assertEquals(10, normalTicks);
        assertEquals(30, fastTicks);
        assertTrue(fastTicks == normalTicks * 3);
    }
}

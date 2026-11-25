package graphics.zombie;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GroundSwatchTrackerTest {

    @Test
    void movingSwatchBackwardPushesZombieForward() {
        GroundSwatchTracker tracker = GroundSwatchTracker.withCurve(new float[]{0f, 10f, 20f}, 1f);
        assertEquals(0f, tracker.consumeWorldDeltaX(0f), 0.01f);
        assertEquals(-10f, tracker.consumeWorldDeltaX(0.4f), 0.01f);
        assertEquals(-10f, tracker.consumeWorldDeltaX(0.7f), 0.01f);
    }

    @Test
    void clipLoopDoesNotTeleport() {
        GroundSwatchTracker tracker = GroundSwatchTracker.withCurve(new float[]{0f, 10f, 20f}, 1f);
        tracker.consumeWorldDeltaX(0f);
        tracker.consumeWorldDeltaX(0.7f);
        assertEquals(0f, tracker.consumeWorldDeltaX(1.05f), 0.01f);
    }
}

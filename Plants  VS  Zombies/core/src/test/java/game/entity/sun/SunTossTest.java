package game.entity.sun;

import common.model.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SunTossTest {
    @Test
    void interpolatesLinearlyWithParabolicArc() {
        SunToss toss = new SunToss(0, 0, 10, 0, 5, 4);
        assertEquals(0d, toss.arc(0d), 0.0001d);
        assertEquals(0d, toss.arc(1d), 0.0001d);
        assertEquals(5d, toss.arc(0.5d), 0.0001d);
        Position mid = toss.pointAt(0.5d);
        assertEquals(5d, mid.getX(), 0.0001d);
        assertEquals(5d, mid.getY(), 0.0001d);
        toss.advance();
        toss.advance();
        toss.advance();
        toss.advance();
        assertTrue(toss.isFinished());
        assertEquals(10d, toss.pointAt(toss.progress()).getX(), 0.0001d);
        assertEquals(0d, toss.pointAt(toss.progress()).getY(), 0.0001d);
    }
}

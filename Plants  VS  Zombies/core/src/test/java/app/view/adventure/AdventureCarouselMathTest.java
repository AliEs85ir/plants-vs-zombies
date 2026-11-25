package app.view.adventure;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdventureCarouselMathTest {
    @Test
    void wrapIsCircularAndSafeForEmpty() {
        assertEquals(0, AdventureCarouselMath.wrap(0, 0));
        assertEquals(0, AdventureCarouselMath.wrap(5, 1));
        assertEquals(0, AdventureCarouselMath.wrap(4, 4));
        assertEquals(3, AdventureCarouselMath.wrap(-1, 4));
        assertEquals(1, AdventureCarouselMath.wrap(-3, 4));
        assertEquals(0, AdventureCarouselMath.wrap(-4, 4));
    }

    @Test
    void computeKeepsValidStateForZeroOneTwoAndManyChapters() {
        AdventureMenuConfig config = new AdventureMenuConfig().sanitize();
        assertNoCrash(config, 0, 0);
        assertNoCrash(config, 1, 0);
        assertNoCrash(config, 2, 0);
        assertNoCrash(config, 2, 1);
        assertNoCrash(config, 8, 7);
        AdventureCarouselMath.Slot[] four = slots(4);
        AdventureCarouselMath.compute(four, 4, 0f, 0, config, 1280f, 720f);
        assertTrue(four[0].front);
        assertTrue(four[0].depth >= four[1].depth);
        assertTrue(four[0].depth >= four[2].depth);
        assertTrue(four[0].depth >= four[3].depth);
        assertTrue(four[0].scale >= four[2].scale);
        assertTrue(four[0].shade <= four[2].shade);
        assertEquals(0f, four[0].shade, 0.001f);
        assertTrue(four[2].shade >= 0.19f);
        int[] order = AdventureCarouselMath.drawOrder(four, 4);
        assertEquals(4, order.length);
        assertTrue(four[order[0]].depth <= four[order[3]].depth);
        assertEquals(order[3], 0);
    }

    @Test
    void twoChaptersStayOppositeOnThePath() {
        AdventureMenuConfig config = new AdventureMenuConfig().sanitize();
        AdventureCarouselMath.Slot[] slots = slots(2);
        AdventureCarouselMath.compute(slots, 2, 0f, 0, config, 1280f, 720f);
        assertTrue(slots[0].front);
        assertFalse(slots[1].front);
        assertTrue(Math.abs(slots[0].centerX - slots[1].centerX) > 1f || Math.abs(slots[0].depth - slots[1].depth) > 0.2f);
    }

    @Test
    void scaleChangesSmoothlyAlongRotationWithoutFrontBoostJumps() {
        AdventureMenuConfig config = new AdventureMenuConfig().sanitize();
        AdventureCarouselMath.Slot[] slots = slots(4);
        float previous = -1f;
        for (int frame = 0; frame <= 20; frame++) {
            float angle = (float) (Math.PI * 0.5 * frame / 20.0);
            AdventureCarouselMath.compute(slots, 4, angle, 0, config, 1280f, 720f);
            float frontScale = slots[0].scale;
            if (previous >= 0f) {
                assertTrue(Math.abs(frontScale - previous) < 0.12f);
            }
            previous = frontScale;
            assertTrue(slots[0].scale > 0f);
            assertTrue(slots[1].scale > 0f);
            assertTrue(slots[2].scale > 0f);
            assertTrue(slots[3].scale > 0f);
        }
    }

    private static void assertNoCrash(AdventureMenuConfig config, int count, int front) {
        AdventureCarouselMath.Slot[] slots = slots(count);
        AdventureCarouselMath.compute(slots, count, 0f, front, config, 1280f, 720f);
        int[] order = AdventureCarouselMath.drawOrder(slots, count);
        assertEquals(count, order.length);
    }

    private static AdventureCarouselMath.Slot[] slots(int count) {
        AdventureCarouselMath.Slot[] slots = new AdventureCarouselMath.Slot[count];
        for (int i = 0; i < count; i++) {
            slots[i] = new AdventureCarouselMath.Slot();
        }
        return slots;
    }
}

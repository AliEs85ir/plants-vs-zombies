package app.view.levelselect;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LevelSelectLayoutMathTest {
    @Test
    void splitsOddLevelsOntoTheLeft() {
        assertEquals(0, LevelSelectLayoutMath.leftCount(0));
        assertEquals(0, LevelSelectLayoutMath.rightCount(0));
        assertEquals(1, LevelSelectLayoutMath.leftCount(1));
        assertEquals(0, LevelSelectLayoutMath.rightCount(1));
        assertEquals(1, LevelSelectLayoutMath.leftCount(2));
        assertEquals(1, LevelSelectLayoutMath.rightCount(2));
        assertEquals(2, LevelSelectLayoutMath.leftCount(4));
        assertEquals(2, LevelSelectLayoutMath.rightCount(4));
        assertEquals(3, LevelSelectLayoutMath.leftCount(5));
        assertEquals(2, LevelSelectLayoutMath.rightCount(5));
        assertEquals(5, LevelSelectLayoutMath.leftCount(10));
        assertEquals(5, LevelSelectLayoutMath.rightCount(10));
        assertEquals(10, LevelSelectLayoutMath.leftCount(20));
        assertEquals(10, LevelSelectLayoutMath.rightCount(20));
    }

    @Test
    void fourLevelsSitSymmetricallyWithInnerNodesInFront() {
        LevelSelectMenuConfig config = new LevelSelectMenuConfig().sanitize();
        LevelSelectLayoutMath.Slot[] slots = slots(4);
        LevelSelectLayoutMath.compute(slots, 4, config, 1280f, 720f);
        assertEquals(1, slots[0].levelNumber);
        assertEquals(2, slots[1].levelNumber);
        assertEquals(3, slots[2].levelNumber);
        assertEquals(4, slots[3].levelNumber);
        assertEquals(-1, slots[0].side);
        assertEquals(-1, slots[1].side);
        assertEquals(1, slots[2].side);
        assertEquals(1, slots[3].side);
        assertFalse(slots[0].front);
        assertTrue(slots[1].front);
        assertTrue(slots[2].front);
        assertFalse(slots[3].front);
        assertTrue(slots[0].depth < slots[1].depth);
        assertEquals(slots[1].depth, slots[2].depth, 0.001f);
        assertEquals(slots[0].depth, slots[3].depth, 0.001f);
        float axis = config.layout.resolvedAxisX(1280f);
        assertTrue(slots[0].centerX < slots[1].centerX);
        assertTrue(slots[1].centerX < axis);
        assertTrue(axis < slots[2].centerX);
        assertTrue(slots[2].centerX < slots[3].centerX);
        assertEquals(axis - slots[1].centerX, slots[2].centerX - axis, 0.01f);
        assertEquals(axis - slots[0].centerX, slots[3].centerX - axis, 0.01f);
        assertEquals(slots[0].centerY, slots[3].centerY, 0.01f);
    }

    @Test
    void fiveLevelsInsertTheExtraNodeOnTheFarLeft() {
        LevelSelectMenuConfig config = new LevelSelectMenuConfig().sanitize();
        LevelSelectLayoutMath.Slot[] four = slots(4);
        LevelSelectLayoutMath.compute(four, 4, config, 1280f, 720f);
        LevelSelectLayoutMath.Slot[] five = slots(5);
        LevelSelectLayoutMath.compute(five, 5, config, 1280f, 720f);
        assertEquals(1, five[0].levelNumber);
        assertEquals(3, five[2].levelNumber);
        assertTrue(five[0].centerX < four[0].centerX);
        assertEquals(five[3].centerX, four[2].centerX, 0.01f);
        assertEquals(five[4].centerX, four[3].centerX, 0.01f);
        assertTrue(five[0].front);
        assertFalse(five[1].front);
        assertTrue(five[2].front);
    }

    @Test
    void zeroAndOneLevelDoNotCrash() {
        LevelSelectMenuConfig config = new LevelSelectMenuConfig().sanitize();
        LevelSelectLayoutMath.Slot[] empty = slots(0);
        LevelSelectLayoutMath.compute(empty, 0, config, 1280f, 720f);
        assertEquals(0, LevelSelectLayoutMath.drawOrder(empty, 0).length);
        LevelSelectLayoutMath.Slot[] one = slots(1);
        LevelSelectLayoutMath.compute(one, 1, config, 1280f, 720f);
        assertTrue(one[0].render);
        assertTrue(one[0].front);
        assertEquals(1, one[0].levelNumber);
        assertEquals(-1, one[0].side);
    }

    @Test
    void spacingAndLineDistanceMoveEveryNode() {
        LevelSelectMenuConfig config = new LevelSelectMenuConfig().sanitize();
        LevelSelectLayoutMath.Slot[] compact = slots(4);
        LevelSelectLayoutMath.compute(compact, 4, config, 1280f, 720f);
        config.layout.levelLineDistance += 40f;
        config.layout.levelSpacingX += 20f;
        config.layout.levelsOffsetY += 80f;
        LevelSelectLayoutMath.Slot[] expanded = slots(4);
        LevelSelectLayoutMath.compute(expanded, 4, config, 1280f, 720f);
        assertTrue(expanded[0].centerX < compact[0].centerX);
        assertTrue(expanded[3].centerX > compact[3].centerX);
        assertEquals(compact[0].centerY + 80f, expanded[0].centerY, 0.01f);
    }

    @Test
    void frontAndBackScalesAreIndependent() {
        LevelSelectMenuConfig config = new LevelSelectMenuConfig().sanitize();
        config.layout.levelScale = 0.2f;
        config.layout.frontScale = 1.2f;
        config.layout.backScale = 0.5f;
        LevelSelectLayoutMath.Slot[] slots = slots(4);
        LevelSelectLayoutMath.compute(slots, 4, config, 1280f, 720f);
        assertEquals(0.24f, slots[1].scale, 0.0001f);
        assertEquals(0.24f, slots[2].scale, 0.0001f);
        assertEquals(0.1f, slots[0].scale, 0.0001f);
        assertEquals(0.1f, slots[3].scale, 0.0001f);
    }

    @Test
    void twentyLevelsKeepLeftRightBalanceAndOutwardExpansion() {
        LevelSelectMenuConfig config = new LevelSelectMenuConfig().sanitize();
        LevelSelectLayoutMath.Slot[] slots = slots(20);
        LevelSelectLayoutMath.compute(slots, 20, config, 1280f, 720f);
        assertEquals(10, LevelSelectLayoutMath.leftCount(20));
        assertTrue(slots[0].render);
        assertTrue(slots[19].render);
        assertEquals(1, slots[0].levelNumber);
        assertEquals(20, slots[19].levelNumber);
        assertTrue(slots[9].front);
        assertTrue(slots[10].front);
        assertFalse(slots[8].front);
        assertFalse(slots[11].front);
        for (int i = 0; i < 9; i++) {
            assertTrue(slots[i].centerX < slots[i + 1].centerX);
        }
        for (int i = 10; i < 19; i++) {
            assertTrue(slots[i].centerX < slots[i + 1].centerX);
        }
    }

    @Test
    void floatSeedsDifferAcrossLevels() {
        LevelSelectMenuConfig config = new LevelSelectMenuConfig().sanitize();
        LevelSelectLayoutMath.Slot[] slots = slots(6);
        LevelSelectLayoutMath.compute(slots, 6, config, 1280f, 720f);
        assertFalse(
            slots[0].floatPhase == slots[1].floatPhase
                && slots[0].floatSpeed == slots[1].floatSpeed
                && slots[0].floatAmplitude == slots[1].floatAmplitude
        );
        assertFalse(
            slots[2].floatPhase == slots[3].floatPhase
                && slots[2].floatSpeed == slots[3].floatSpeed
        );
    }

    @Test
    void drawOrderPutsBackNodesBeforeFrontNodes() {
        LevelSelectMenuConfig config = new LevelSelectMenuConfig().sanitize();
        LevelSelectLayoutMath.Slot[] slots = slots(4);
        LevelSelectLayoutMath.compute(slots, 4, config, 1280f, 720f);
        int[] order = LevelSelectLayoutMath.drawOrder(slots, 4);
        assertTrue(slots[order[0]].depth <= slots[order[3]].depth);
        assertTrue(slots[order[0]].depth <= slots[order[1]].depth);
    }

    private static LevelSelectLayoutMath.Slot[] slots(int count) {
        LevelSelectLayoutMath.Slot[] slots = new LevelSelectLayoutMath.Slot[count];
        for (int i = 0; i < count; i++) {
            slots[i] = new LevelSelectLayoutMath.Slot();
        }
        return slots;
    }
}

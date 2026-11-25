package game.entity.zombie;

import common.model.Position;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ZombieLaneSwitchTest {
    @Test
    void middleRowOffersBothAdjacentLanes() {
        assertEquals(List.of(1, 3), ZombieLaneSwitch.validAdjacentRows(2, 5));
    }

    @Test
    void topRowOnlyOffersLaneBelow() {
        assertEquals(List.of(1), ZombieLaneSwitch.validAdjacentRows(0, 5));
    }

    @Test
    void bottomRowOnlyOffersLaneAbove() {
        assertEquals(List.of(3), ZombieLaneSwitch.validAdjacentRows(4, 5));
    }

    @Test
    void singleRowMapHasNoValidAdjacentLane() {
        assertTrue(ZombieLaneSwitch.validAdjacentRows(0, 1).isEmpty());
    }

    @Test
    void invalidCurrentRowYieldsNoTargets() {
        assertTrue(ZombieLaneSwitch.validAdjacentRows(-1, 5).isEmpty());
        assertTrue(ZombieLaneSwitch.validAdjacentRows(5, 5).isEmpty());
        assertTrue(ZombieLaneSwitch.validAdjacentRows(0, 0).isEmpty());
    }

    @Test
    void diagonalTargetMovesOneCellForwardAndIntoTargetRow() {
        Position target = ZombieLaneSwitch.computeDiagonalTarget(500f, 160f, 100f);
        assertEquals(400f, target.getX(), 0.0001f);
        assertEquals(160f, target.getY(), 0.0001f);
    }

    @Test
    void lanePullKeepsForwardDisplacementWhileLateralIsFaster() {
        float startX = 500f;
        double forward = 5d;
        double lateral = forward * ZombieLaneSwitch.LANE_PULL_SPEED_MULTIPLIER;
        int duration = ZombieLaneSwitch.computeLanePullDurationTicks(100d, lateral);
        float targetX = ZombieLaneSwitch.computeLanePullTargetX(startX, duration, forward);

        assertEquals(8, duration);
        assertEquals(460f, targetX, 0.0001f);
        assertTrue(startX - targetX > 0f);
        assertTrue(lateral > forward);
        assertEquals(forward * duration, startX - targetX, 0.0001f);
        assertEquals(100d / duration, 100d / 8d, 0.0001d);
        assertTrue(100d / duration > forward);
    }

    @Test
    void lanePullDurationUsesLateralDistanceNotDiagonal() {
        assertEquals(1, ZombieLaneSwitch.computeLanePullDurationTicks(0.5d, 12.5d));
        assertEquals(4, ZombieLaneSwitch.computeLanePullDurationTicks(50d, 12.5d));
    }
}

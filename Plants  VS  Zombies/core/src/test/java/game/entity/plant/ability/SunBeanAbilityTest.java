package game.entity.plant.ability;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SunBeanAbilityTest {
    @Test
    void successiveAnglesStayInConfiguredUpwardCone() {
        for (int i = 0; i < 32; i++) {
            double angle = SunBeanAbility.angleDegrees(i);
            assertTrue(angle >= SunBeanAbility.MIN_ANGLE_DEGREES);
            assertTrue(angle <= SunBeanAbility.MAX_ANGLE_DEGREES);
        }
        for (int i = 0; i < 12; i++) {
            double landing = SunBeanAbility.landingDistanceCells(i);
            assertTrue(landing >= SunBeanAbility.MIN_LANDING_CELLS);
            assertTrue(landing <= SunBeanAbility.MAX_LANDING_CELLS);
        }
    }
}

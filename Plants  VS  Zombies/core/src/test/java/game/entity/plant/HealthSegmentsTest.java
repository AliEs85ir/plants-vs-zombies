package game.entity.plant;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HealthSegmentsTest {

    @Test
    void wallNutAndEndurianUseQuarterThresholds() {
        int max = 4000;
        int count = HealthSegments.QUARTERS;

        assertEquals(0, HealthSegments.index(4000, max, count));
        assertEquals(0, HealthSegments.index(3001, max, count));
        assertEquals(1, HealthSegments.index(3000, max, count));
        assertEquals(1, HealthSegments.index(2001, max, count));
        assertEquals(2, HealthSegments.index(2000, max, count));
        assertEquals(2, HealthSegments.index(1001, max, count));
        assertEquals(3, HealthSegments.index(1000, max, count));
        assertEquals(3, HealthSegments.index(1, max, count));
        assertEquals(3, HealthSegments.index(0, max, count));
    }

    @Test
    void tallNutUsesThirdThresholds() {
        int max = 8000;
        int count = HealthSegments.THIRDS;
        double twoThirds = max * 2.0d / 3.0d;
        double oneThird = max * 1.0d / 3.0d;

        assertEquals(0, HealthSegments.index(8000, max, count));
        assertEquals(0, HealthSegments.index(twoThirds + 0.001d, max, count));
        assertEquals(1, HealthSegments.index(twoThirds, max, count));
        assertEquals(1, HealthSegments.index(oneThird + 0.001d, max, count));
        assertEquals(2, HealthSegments.index(oneThird, max, count));
        assertEquals(2, HealthSegments.index(0, max, count));
    }

    @Test
    void garlicUsesThirdThresholdsFromLiveHealthRatio() {
        int max = 300;
        int count = HealthSegments.THIRDS;
        double twoThirds = max * 2.0d / 3.0d;
        double oneThird = max * 1.0d / 3.0d;

        assertEquals(0, HealthSegments.index(300, max, count));
        assertEquals(0, HealthSegments.index(twoThirds + 0.001d, max, count));
        assertEquals(1, HealthSegments.index(twoThirds, max, count));
        assertEquals(1, HealthSegments.index(oneThird + 0.001d, max, count));
        assertEquals(2, HealthSegments.index(oneThird, max, count));
        assertEquals(2, HealthSegments.index(1, max, count));

        PlantHealth health = new PlantHealth(max);
        health.setCurrentHealth(50);
        health.heal(health.getMaxHealth());
        assertEquals(max, health.getCurrentHealth(), 0.0001);
        assertTrue(HealthSegments.matches(health, 0, HealthSegments.THIRDS));
    }

    @Test
    void plantHealthRatioDrivesIdleDamageClips() {
        PlantHealth health = new PlantHealth(4000);
        assertTrue(HealthSegments.matches(health, 0, HealthSegments.QUARTERS));

        health.setCurrentHealth(3000);
        assertTrue(HealthSegments.matches(health, 1, HealthSegments.QUARTERS));
        assertFalse(HealthSegments.matches(health, 0, HealthSegments.QUARTERS));

        health.setCurrentHealth(2000);
        assertTrue(HealthSegments.matches(health, 2, HealthSegments.QUARTERS));

        health.setCurrentHealth(1000);
        assertTrue(HealthSegments.matches(health, 3, HealthSegments.QUARTERS));
    }

    @Test
    void healDoesNotExceedMaxHealth() {
        PlantHealth health = new PlantHealth(4000);
        health.setCurrentHealth(500);
        health.heal(4000);
        assertEquals(4000.0, health.getCurrentHealth(), 0.0001);

        PlantHealth tall = new PlantHealth(8000);
        tall.setCurrentHealth(100);
        tall.heal(8000);
        assertEquals(8000.0, tall.getCurrentHealth(), 0.0001);

        PlantHealth endurian = new PlantHealth(3000);
        endurian.setCurrentHealth(2500);
        endurian.heal(3000);
        assertEquals(3000.0, endurian.getCurrentHealth(), 0.0001);
    }
}

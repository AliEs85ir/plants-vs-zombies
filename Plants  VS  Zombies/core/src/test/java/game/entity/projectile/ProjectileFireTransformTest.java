package game.entity.projectile;

import app.enums.plant.PlantName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProjectileFireTransformTest {
    @Test
    void detectsLeftToRightAndRightToLeftCrossing() {
        assertTrue(ProjectileFireTransform.crossesCenter(0, 10, 20, 10, 10, 10, 1, 8));
        assertTrue(ProjectileFireTransform.crossesCenter(20, 10, 0, 10, 10, 10, 1, 8));
        assertFalse(ProjectileFireTransform.crossesCenter(0, 10, 5, 10, 10, 10, 1, 8));
        assertFalse(ProjectileFireTransform.crossesCenter(0, 0, 20, 0, 10, 40, 1, 8));
    }

    @Test
    void detectsFastProjectileThatSkipsTheTorchCell() {
        assertTrue(ProjectileFireTransform.crossesCenter(0, 10, 200, 10, 80, 10, 6.4, 36));
    }

    @Test
    void exceptionListIncludesConfiguredSourcesAndNotSnowPea() {
        assertTrue(ProjectileFireTransform.isExcluded(PlantName.FIRE_PEASHOOTER));
        assertTrue(ProjectileFireTransform.isExcluded(PlantName.CABBAGE_PULT));
        assertTrue(ProjectileFireTransform.isExcluded(PlantName.KERNEL_PULT));
        assertTrue(ProjectileFireTransform.isExcluded(PlantName.MELON_PULT));
        assertTrue(ProjectileFireTransform.isExcluded(PlantName.WINTER_MELON));
        assertTrue(ProjectileFireTransform.isExcluded(PlantName.PEPPER_PULT));
        assertTrue(ProjectileFireTransform.isExcluded(PlantName.ELECTRIC_BLUEBERRY));
        assertTrue(ProjectileFireTransform.isExcluded(PlantName.BOWLING_BULB));
        assertTrue(ProjectileFireTransform.isExcluded(PlantName.CITRON));
        assertTrue(ProjectileFireTransform.isExcluded(PlantName.GOO_PEASHOOTER));
        assertFalse(ProjectileFireTransform.isExcluded(PlantName.SNOW_PEA));
        assertFalse(ProjectileFireTransform.isExcluded(PlantName.PEASHOOTER));
        assertEquals(10, ProjectileFireTransform.excludedSources().size());
    }

    @Test
    void multipliersAreTwoAndThreeTimesBase() {
        assertEquals(2, ProjectileFireTransform.NORMAL_MULTIPLIER);
        assertEquals(3, ProjectileFireTransform.EMPOWERED_MULTIPLIER);
    }
}

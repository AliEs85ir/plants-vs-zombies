package game.entity.plant.ability;

import game.entity.plant.Plant;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlantTargetingEatingTest {
    @Test
    void zombiesEatingReturnsEmptyWhenWorldOrPlantIsMissing() {
        assertTrue(PlantTargeting.zombiesEating(null, null).isEmpty());
        assertTrue(PlantTargeting.zombiesEating(null, new Plant()).isEmpty());
        assertFalse(PlantTargeting.isEating(null, new Plant()));
    }
}

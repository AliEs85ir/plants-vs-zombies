package game.service.drop;

import app.enums.drop.DropType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DropTableTest {

    @Test
    void deathRollNeverReturnsPlantFood() {
        boolean sawOtherDrop = false;
        for (int i = 0; i < 20_000; i++) {
            var drop = DropTable.roll();
            drop.ifPresent(type -> assertNotEquals(DropType.PLANT_FOOD, type));
            sawOtherDrop = sawOtherDrop || drop.isPresent();
        }
        assertTrue(sawOtherDrop);
    }
}

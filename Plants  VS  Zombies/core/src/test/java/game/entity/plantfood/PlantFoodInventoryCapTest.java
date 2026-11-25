package game.entity.plantfood;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlantFoodInventoryCapTest {
    @Test
    void neverExceedsConfiguredMaximum() {
        PlantFoodInventory inventory = new PlantFoodInventory();
        inventory.setAmount(4);
        inventory.addPlantFood();
        assertEquals(5, inventory.getAmount());
        inventory.addPlantFood();
        assertEquals(5, inventory.getAmount());
        assertTrue(inventory.isFull());
        inventory.setAmount(99);
        assertEquals(PlantFoodSettings.MAX_AMOUNT, inventory.getAmount());
    }
}

package game.world;

import app.model.App;
import app.model.user.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class PlantFoodUserResourceTest {
    @Test
    void worldUsesCentralUserPlantFoodNotStartingFour() {
        User user = new User("tester", "pass", "nick", "tester@mail.com", "male", 1, "answer");
        user.getFoodInventory().setAmount(2);
        App.setCurrentUser(user);
        try {
            GameWorld world = new GameWorld();
            assertSame(user.getFoodInventory(), world.getPlantFoodInventory());
            assertEquals(2, world.getPlantFoodInventory().getAmount());
            world.getPlantFoodInventory().consumePlantFood();
            assertEquals(1, user.getFoodInventory().getAmount());
        } finally {
            App.setCurrentUser(null);
        }
    }
}

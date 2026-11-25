package game.service.drop;

import app.enums.drop.DropType;
import app.model.App;
import app.service.AppService;
import app.service.greenhouse.GreenhouseService;
import app.model.user.User;
import game.entity.plantfood.PlantFoodInventory;
import game.world.GameWorld;

public final class DropRewardService {
    private DropRewardService() {
    }

    public static void apply(DropType type, GameWorld world) {
        if (type == null) {
            return;
        }
        switch (type) {
            case GOLD_COIN, SILVER_COIN -> addCoins(type.getValue());
            case DIAMOND -> addGems(type.getValue());
            case PLANT_FOOD -> addPlantFood(world);
            case POT -> addGreenhousePot();
        }
    }

    private static void addCoins(int amount) {
        User user = App.getCurrentUser();
        if (user != null) {
            user.addCoins(amount);
        }
    }

    private static void addGems(int amount) {
        User user = App.getCurrentUser();
        if (user != null) {
            user.addGems(amount);
        }
    }

    private static void addPlantFood(GameWorld world) {
        if (world == null || world.getPlantFoodInventory() == null) {
            return;
        }
        PlantFoodInventory inventory = world.getPlantFoodInventory();
        if (inventory.isFull()) {
            return;
        }
        inventory.addPlantFood();
    }

    private static void addGreenhousePot() {
        User user = App.getCurrentUser();
        if (user == null || user.getGreenhouse() == null) {
            return;
        }
        GreenhouseService service = new GreenhouseService(user.getGreenhouse());
        if (service.tryAddDroppedPlantedPot()) {
            AppService.markGameDataDirty();
        }
    }
}

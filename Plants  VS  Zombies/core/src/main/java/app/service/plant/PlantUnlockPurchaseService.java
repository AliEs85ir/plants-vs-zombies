package app.service.plant;

import app.enums.plant.PlantName;
import app.model.App;
import app.model.user.User;
import app.service.AppService;
import app.service.ProgressService;
import common.Result;

public final class PlantUnlockPurchaseService {
    public static final String PLANT_NOT_FOUND = "Plant not found.";
    public static final String ALREADY_OWNED = "You already have this plant.";
    public static final String NOT_ENOUGH_DIAMONDS = "You do not have enough diamonds.";
    public static final String PURCHASE_FAILED = "Could not unlock this plant.";
    public static final String SAVE_FAILED = "Could not save plant unlock progress.";
    public static final String PURCHASE_SUCCESS = "Plant unlocked.";

    private PlantUnlockPurchaseService() {
    }

    public static int priceOf(PlantName plantName) {
        return PlantUnlockPriceCatalogLoader.get().priceOf(plantName);
    }

    public static boolean canAfford(User user, PlantName plantName) {
        if (user == null || plantName == null) {
            return false;
        }
        return user.getGems() >= priceOf(plantName);
    }

    public static Result purchase(PlantName plantName) {
        return purchase(App.getCurrentUser(), plantName);
    }

    public static Result purchase(User user, PlantName plantName) {
        if (plantName == null) {
            return Result.failure(PLANT_NOT_FOUND);
        }
        if (user == null) {
            return Result.failure(PURCHASE_FAILED);
        }
        if (user.isUnlocked(plantName)) {
            return Result.failure(ALREADY_OWNED);
        }

        int price = priceOf(plantName);
        if (price < 0) {
            return Result.failure(PURCHASE_FAILED);
        }
        if (user.getGems() < price) {
            return Result.failure(NOT_ENOUGH_DIAMONDS);
        }

        int previousGems = user.getGems();
        boolean hadBoostEntry = user.getPlantBoostStatus().containsKey(plantName);
        boolean hadLevelEntry = user.getPlantLevelMap().containsKey(plantName);

        if (!user.tryDeductGems(price)) {
            return Result.failure(NOT_ENOUGH_DIAMONDS);
        }
        user.unlockPlant(plantName);
        AppService.markGameDataDirty();

        boolean saved = ProgressService.saveUserProgress(user);
        if (!saved) {
            user.setGems(previousGems);
            if (!hadBoostEntry) {
                user.getPlantBoostStatus().remove(plantName);
            }
            if (!hadLevelEntry) {
                user.getPlantLevelMap().remove(plantName);
            }
            AppService.markGameDataDirty();
            return Result.failure(SAVE_FAILED);
        }
        app.service.news.NewsPublisher.onPlantUnlocked(user, plantName, "diamond purchase");
        return Result.success(PURCHASE_SUCCESS + " (" + price + " diamonds)");
    }
}
package app.service.plant;

import app.enums.plant.PlantName;
import app.model.App;
import app.model.user.User;
import common.Result;
import game.entity.plant.PlantBoostSettings;

public final class PlantBoostService {
    public static final String NOT_ENOUGH_DIAMONDS = "You do not have enough diamonds.";
    public static final String CANNOT_BOOST = "This plant cannot be boosted.";

    private PlantBoostService() {
    }

    public static boolean isEligible(PlantName plantName) {
        return plantName != null && plantName.hasPlantFoodEffect();
    }

    public static boolean isBoosted(PlantName plantName) {
        User user = App.getCurrentUser();
        return user != null && plantName != null && user.isBoosted(plantName);
    }

    public static Result tryBoost(PlantName plantName) {
        if (!isEligible(plantName)) {
            return new Result(false, CANNOT_BOOST);
        }
        User user = App.getCurrentUser();
        if (user == null || !user.isUnlocked(plantName)) {
            return new Result(false, CANNOT_BOOST);
        }
        if (user.isBoosted(plantName)) {
            return new Result(true, plantName + " is already boosted.");
        }
        if (user.getGems() < PlantBoostSettings.COST) {
            return new Result(false, NOT_ENOUGH_DIAMONDS);
        }
        user.deductGems(PlantBoostSettings.COST);
        user.setBoost(plantName, true);
        return new Result(true, plantName + " boosted. (" + PlantBoostSettings.COST + " diamonds spent)");
    }

    public static boolean consumeBoost(PlantName plantName) {
        User user = App.getCurrentUser();
        if (user == null || plantName == null || !user.isBoosted(plantName)) {
            return false;
        }
        user.setBoost(plantName, false);
        return true;
    }
}

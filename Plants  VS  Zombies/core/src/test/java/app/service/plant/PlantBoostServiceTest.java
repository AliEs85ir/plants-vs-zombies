package app.service.plant;

import app.enums.plant.PlantName;
import app.model.App;
import app.model.user.User;
import common.Result;
import game.entity.plant.PlantBoostSettings;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlantBoostServiceTest {

    @BeforeEach
    void setUp() {
        App.setCurrentUser(new User("tester", "pass", "nick", "t@t.com", "male", 1, "answer"));
    }

    @AfterEach
    void tearDown() {
        App.setCurrentUser(null);
    }

    @Test
    void eligibilityFollowsPlantFoodEffectNotImplementation() {
        assertTrue(PlantBoostService.isEligible(PlantName.SUNFLOWER));
        assertTrue(PlantBoostService.isEligible(PlantName.PEASHOOTER));
        assertTrue(PlantBoostService.isEligible(PlantName.WALL_NUT));
        assertFalse(PlantBoostService.isEligible(PlantName.GOLD_BLOOM));
        assertFalse(PlantBoostService.isEligible(PlantName.CHERRY_BOMB));
    }

    @Test
    void boostSpendsDiamondsAndSetsTypeState() {
        User user = App.getCurrentUser();
        int gemsBefore = user.getGems();

        Result result = PlantBoostService.tryBoost(PlantName.SUNFLOWER);

        assertTrue(result.success());
        assertTrue(PlantBoostService.isBoosted(PlantName.SUNFLOWER));
        assertEquals(gemsBefore - PlantBoostSettings.COST, user.getGems());
        assertEquals(PlantBoostSettings.COST, 2);
    }

    @Test
    void alreadyBoostedDoesNotSpendMoreDiamonds() {
        User user = App.getCurrentUser();
        assertTrue(PlantBoostService.tryBoost(PlantName.SUNFLOWER).success());
        int gemsAfterFirstBoost = user.getGems();

        Result second = PlantBoostService.tryBoost(PlantName.SUNFLOWER);

        assertTrue(second.success());
        assertTrue(PlantBoostService.isBoosted(PlantName.SUNFLOWER));
        assertEquals(gemsAfterFirstBoost, user.getGems());
    }

    @Test
    void notEnoughDiamondsDoesNotBoost() {
        User user = App.getCurrentUser();
        user.deductGems(user.getGems());
        user.addGems(1);

        Result result = PlantBoostService.tryBoost(PlantName.SUNFLOWER);

        assertFalse(result.success());
        assertEquals(PlantBoostService.NOT_ENOUGH_DIAMONDS, result.message());
        assertFalse(PlantBoostService.isBoosted(PlantName.SUNFLOWER));
        assertEquals(1, user.getGems());
    }

    @Test
    void ineligiblePlantCannotBeBoosted() {
        User user = App.getCurrentUser();
        int gemsBefore = user.getGems();

        Result result = PlantBoostService.tryBoost(PlantName.GOLD_BLOOM);

        assertFalse(result.success());
        assertFalse(PlantBoostService.isBoosted(PlantName.GOLD_BLOOM));
        assertEquals(gemsBefore, user.getGems());
    }

    @Test
    void boostStateIsIndependentPerPlant() {
        User user = App.getCurrentUser();
        int gemsBefore = user.getGems();
        assertTrue(PlantBoostService.tryBoost(PlantName.SUNFLOWER).success());
        assertTrue(PlantBoostService.tryBoost(PlantName.PEASHOOTER).success());

        assertTrue(PlantBoostService.isBoosted(PlantName.SUNFLOWER));
        assertTrue(PlantBoostService.isBoosted(PlantName.PEASHOOTER));
        assertFalse(PlantBoostService.isBoosted(PlantName.WALL_NUT));
        assertEquals(gemsBefore - (2 * PlantBoostSettings.COST), user.getGems());
    }

    @Test
    void consumeBoostClearsTypeStateOnce() {
        assertTrue(PlantBoostService.tryBoost(PlantName.SUNFLOWER).success());

        assertTrue(PlantBoostService.consumeBoost(PlantName.SUNFLOWER));
        assertFalse(PlantBoostService.isBoosted(PlantName.SUNFLOWER));
        assertFalse(PlantBoostService.consumeBoost(PlantName.SUNFLOWER));
    }

    @Test
    void unimplementedPlantFoodPlantCanStillBeBoosted() {
        Result result = PlantBoostService.tryBoost(PlantName.WALL_NUT);

        assertTrue(result.success());
        assertTrue(PlantBoostService.isBoosted(PlantName.WALL_NUT));
        assertTrue(PlantBoostService.consumeBoost(PlantName.WALL_NUT));
        assertFalse(PlantBoostService.isBoosted(PlantName.WALL_NUT));
    }
}

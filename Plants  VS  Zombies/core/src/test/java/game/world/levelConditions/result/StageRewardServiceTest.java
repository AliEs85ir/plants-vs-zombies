package game.world.levelConditions.result;

import app.enums.SeasonType;
import app.enums.plant.PlantName;
import app.model.user.User;
import app.view.game.result.ResultBalanceConfigLoader;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StageRewardServiceTest {

    @Test
    void firstClearRewardsGrantedOnce() {
        User user = new User("reward_user", "x", "n", "r@test.com", "male", 1, "a", 20);
        user.unlockPlant(PlantName.SUNFLOWER);
        user.unlockPlant(PlantName.PEASHOOTER);

        List<StageRewardGrant> first = StageRewardService.grantFirstClearRewards(
            user, SeasonType.ANCIENT_EGYPT, 1);
        assertFalse(first.isEmpty());
        assertEquals(200, user.getCoins());
        assertTrue(user.getGems() >= 2);

        int coinsAfterFirst = user.getCoins();
        int gemsAfterFirst = user.getGems();
        List<StageRewardGrant> second = StageRewardService.grantFirstClearRewards(
            user, SeasonType.ANCIENT_EGYPT, 1);
        assertTrue(second.isEmpty());
        assertEquals(coinsAfterFirst, user.getCoins());
        assertEquals(gemsAfterFirst, user.getGems());
        assertTrue(user.getGameProgress()
            .getOrCreateStageProgress(SeasonType.ANCIENT_EGYPT, 1)
            .isFirstClearRewardClaimed());
    }

    @Test
    void loseSkipCostIsLoadedFromJson() {
        assertEquals(1000, ResultBalanceConfigLoader.get().loseSkipDiamondCost);
    }

    @Test
    void resultPopupDelayIsLoadedFromJson() {
        assertEquals(3.5f, ResultBalanceConfigLoader.get().popupDelaySeconds, 0.001f);
    }
}

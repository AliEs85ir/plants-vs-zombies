package app.view.network;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NetworkMenuConfigTest {
    @Test
    void sanitizeFillsBackgroundGamesAndRankingDefaults() {
        NetworkMenuConfig config = new NetworkMenuConfig().sanitize();
        assertEquals(1280f, config.layout.designWidth, 0.001f);
        assertEquals(720f, config.layout.designHeight, 0.001f);
        assertEquals("menu/bg/network.png", config.background.path);
        assertEquals("menu/icon/back.png", config.backButton.assetPath);
        assertEquals(3, config.minigames.visibleGames().size());
        assertEquals("izombie_network", config.minigames.visibleGames().get(0).id);
        assertEquals("izombie_couch", config.minigames.visibleGames().get(1).id);
        assertEquals("scoring_game", config.minigames.visibleGames().get(2).id);
        assertEquals("menu/image/network/izombie_network.png", config.minigames.visibleGames().get(0).assetPath);
        assertEquals("menu/image/network/play.png", config.minigames.visibleGames().get(0).playAssetPath);
        assertEquals("menu/image/network/shelf_slot.png", config.minigames.frameAssetPath);
        assertEquals(216f, config.minigames.slotHeight, 0.001f);
        assertEquals("open_izombie", config.minigames.visibleGames().get(0).action);
        assertEquals("I_ZOMBIE", config.minigames.visibleGames().get(0).chapter);
        assertEquals("open_izombie_couch", config.minigames.visibleGames().get(1).action);
        assertEquals("I_ZOMBIE_COUCH", config.minigames.visibleGames().get(1).chapter);
        assertEquals("open_scoring_game", config.minigames.visibleGames().get(2).action);
        assertEquals("SCORING_GAME", config.minigames.visibleGames().get(2).chapter);
        assertEquals("menu/image/network/ranking_panel.png", config.ranking.backgroundPath);
        assertEquals("image_ui_if_bundle_reward_multiplier_bg_10", config.ranking.slotBackground);
        assertEquals("FBUSV8C5EI_2", config.ranking.rankFont);
        assertEquals("120 MP", config.ranking.formatScore(120));
        assertEquals("120", config.ranking.formatScore(120, NetworkRankSort.DIAMOND));
        assertEquals("miopoint", config.ranking.labelFor(NetworkRankSort.MIOPOINT));
        assertEquals("FBUSV8C5EI_1", config.ranking.sort.font);
        assertEquals("FBUSV8C5EI_1_outline", config.ranking.sort.optionFont);
        assertEquals("image_ui_if_bundle_reward1_bg_10", config.ranking.sort.optionSlotBackground);
        assertEquals(58f, config.ranking.sort.height, 0.001f);
        assertEquals(NetworkRankSort.MIOPOINT, config.ranking.sort.initialSort());
        assertEquals("1.  Alice     40 MP", config.ranking.formatRow(1, "Alice", 40));
        assertTrue(config.ranking.entries.isEmpty());
        assertTrue(config.minigames.hoverScale > 1f);
        assertTrue(config.ranking.rowHeight > 0f);
        assertTrue(config.ranking.includeLoggedInUser);
    }

    @Test
    void blankValuesAreRestoredAndEmptyListsUseDefaults() {
        NetworkMenuConfig config = new NetworkMenuConfig();
        config.layout.designWidth = 0f;
        config.background.path = " ";
        config.backButton.assetPath = "";
        config.ranking.backgroundPath = "";
        config.ranking.rowFormat = "";
        config.minigames.games.clear();
        config.ranking.entries.clear();
        config.sanitize();
        assertEquals(1280f, config.layout.designWidth, 0.001f);
        assertEquals("menu/bg/network.png", config.background.path);
        assertEquals("menu/icon/back.png", config.backButton.assetPath);
        assertEquals("menu/image/network/ranking_panel.png", config.ranking.backgroundPath);
        assertEquals("{rank}.  {name}     {score} MP", config.ranking.rowFormat);
        assertEquals(3, config.minigames.visibleGames().size());
        assertTrue(config.ranking.entries.isEmpty());
    }
}

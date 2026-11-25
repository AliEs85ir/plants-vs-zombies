package app.view.mainmenu.game;

import app.view.mainmenu.config.MainMenuConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameCardLayoutTest {
    @Test
    void stackedCardsOverlapWithConfiguredStepAndStayCentered() {
        MainMenuConfig.GameMenuLayoutConfig layout = new MainMenuConfig.GameMenuLayoutConfig();
        layout.firstCardX = 0f;
        layout.firstCardY = 430f;
        layout.cardWidth = 860f;
        layout.cardHeight = 220f;
        layout.scale = 1f;
        layout.cardSpacing = 0f;
        layout.cardOverlap = 78f;
        layout.alignment = "center";
        GameCardLayout.Placement[] placements = GameCardLayout.place(3, 1280f, 720f, layout);
        assertEquals(3, placements.length);
        assertEquals(210f, placements[0].x, 0.001f);
        assertEquals(placements[0].x, placements[1].x, 0.001f);
        assertEquals(placements[1].x, placements[2].x, 0.001f);
        assertEquals(430f, placements[0].y, 0.001f);
        assertEquals(288f, placements[1].y, 0.001f);
        assertEquals(146f, placements[2].y, 0.001f);
        assertTrue(placements[0].y > placements[1].y);
        assertTrue(placements[1].y > placements[2].y);
        assertEquals(142f, placements[0].y - placements[1].y, 0.001f);
    }

    @Test
    void laterCardsStayLowerSoFrontCardCanCoverUpperOnes() {
        MainMenuConfig.GameMenuLayoutConfig layout = new MainMenuConfig.GameMenuLayoutConfig();
        layout.firstCardY = 400f;
        layout.cardWidth = 200f;
        layout.cardHeight = 100f;
        layout.cardOverlap = 40f;
        layout.alignment = "left";
        layout.firstCardX = 12f;
        GameCardLayout.Placement[] placements = GameCardLayout.place(3, 800f, 600f, layout);
        assertEquals(12f, placements[0].x, 0.001f);
        assertEquals(400f, placements[0].y, 0.001f);
        assertEquals(340f, placements[1].y, 0.001f);
        assertEquals(280f, placements[2].y, 0.001f);
    }
}

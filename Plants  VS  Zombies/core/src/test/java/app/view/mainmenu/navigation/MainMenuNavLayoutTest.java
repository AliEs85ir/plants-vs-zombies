package app.view.mainmenu.navigation;

import app.view.mainmenu.config.MainMenuConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MainMenuNavLayoutTest {
    @Test
    void fiveIconsFillStartToEndEvenlyFromLeftToRight() {
        MainMenuConfig.NavigationLayoutConfig layout = new MainMenuConfig.NavigationLayoutConfig();
        layout.startX = 160f;
        layout.endX = 1120f;
        layout.bottomOffset = 18f;
        layout.iconWidth = 72f;
        layout.iconHeight = 72f;
        layout.scale = 1f;
        layout.iconSpacing = 0f;
        layout.alignment = "center";
        MainMenuNavLayout.Placement[] placements = MainMenuNavLayout.place(5, 1280f, 720f, layout);
        assertEquals(5, placements.length);
        assertEquals(160f, placements[0].x, 0.001f);
        assertEquals(382f, placements[1].x, 0.001f);
        assertEquals(604f, placements[2].x, 0.001f);
        assertEquals(826f, placements[3].x, 0.001f);
        assertEquals(1048f, placements[4].x, 0.001f);
        assertEquals(18f, placements[0].y, 0.001f);
        assertEquals(placements[0].y, placements[4].y, 0.001f);
        assertEquals(72f, placements[0].width, 0.001f);
    }

    @Test
    void explicitSpacingKeepsConfiguredGap() {
        MainMenuConfig.NavigationLayoutConfig layout = new MainMenuConfig.NavigationLayoutConfig();
        layout.startX = 100f;
        layout.endX = 900f;
        layout.bottomOffset = 10f;
        layout.iconWidth = 50f;
        layout.iconHeight = 50f;
        layout.iconSpacing = 20f;
        layout.alignment = "left";
        MainMenuNavLayout.Placement[] placements = MainMenuNavLayout.place(3, 1000f, 600f, layout);
        assertEquals(100f, placements[0].x, 0.001f);
        assertEquals(170f, placements[1].x, 0.001f);
        assertEquals(240f, placements[2].x, 0.001f);
        assertEquals(10f, placements[0].y, 0.001f);
    }
}

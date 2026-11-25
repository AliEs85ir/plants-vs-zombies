package app.view.loading.icons;

import app.view.loading.icons.config.StartIconsConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StartIconLayoutTest {
    @Test
    void singleRowStaysCenteredAtBottom() {
        StartIconsConfig.LayoutConfig layout = new StartIconsConfig.LayoutConfig();
        layout.columns = 8;
        layout.iconWidth = 70f;
        layout.iconHeight = 70f;
        layout.scale = 1f;
        layout.spacingX = 10f;
        layout.spacingY = 8f;
        layout.paddingBottom = 16f;
        layout.offsetX = 0f;
        layout.offsetY = 4f;
        layout.alignment = "center";
        layout.anchor = "bottom";
        StartIconLayout.Placement[] placements = StartIconLayout.place(8, 1280f, 720f, layout);
        assertEquals(8, placements.length);
        assertEquals(20f, placements[0].y, 0.001f);
        assertEquals(placements[0].y, placements[7].y, 0.001f);
        float clusterWidth = 8 * 70f + 7 * 10f;
        float expectedFirstX = (1280f - clusterWidth) * 0.5f;
        assertEquals(expectedFirstX, placements[0].x, 0.001f);
        assertEquals(expectedFirstX + 7 * 80f, placements[7].x, 0.001f);
    }

    @Test
    void twoRowsFillLeftToRightTopToBottom() {
        StartIconsConfig.LayoutConfig layout = new StartIconsConfig.LayoutConfig();
        layout.columns = 4;
        layout.iconWidth = 50f;
        layout.iconHeight = 50f;
        layout.spacingX = 10f;
        layout.spacingY = 12f;
        layout.paddingBottom = 0f;
        layout.offsetY = 0f;
        layout.alignment = "left";
        layout.paddingLeft = 8f;
        layout.anchor = "bottom";
        StartIconLayout.Placement[] placements = StartIconLayout.place(8, 800f, 600f, layout);
        assertEquals(8f, placements[0].x, 0.001f);
        assertEquals(62f, placements[0].y, 0.001f);
        assertEquals(0f, placements[4].y, 0.001f);
        assertEquals(placements[0].x, placements[4].x, 0.001f);
        assertEquals(8f + 3 * 60f, placements[3].x, 0.001f);
    }
}

package app.view.adventure;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdventureMenuConfigTest {
    @Test
    void sanitizeFillsFourWorldsAndCarouselDefaults() {
        AdventureMenuConfig config = new AdventureMenuConfig().sanitize();
        assertEquals("menu/bg/adventure.png", config.background.path);
        assertEquals(4, config.visibleChapters().size());
        assertEquals("egypt", config.visibleChapters().get(0).id);
        assertEquals("iceage", config.visibleChapters().get(1).id);
        assertEquals("dark", config.visibleChapters().get(2).id);
        assertEquals("beach", config.visibleChapters().get(3).id);
        assertEquals("IMAGE_UI_UNIVERSE_WORLDS_EGYPT", config.visibleChapters().get(0).worldAsset);
        assertEquals("ANCIENT_EGYPT", config.visibleChapters().get(0).season);
        assertEquals("{played} / {total}", config.progress.format);
        assertEquals("2 / 4", config.progress.formatted(2, 4));
        assertEquals(128f, config.chapter.width, 0.001f);
        assertEquals(128f, config.chapter.height, 0.001f);
        assertTrue(config.rotation.duration > 0f);
        assertTrue(config.carousel.scale > 0f);
        assertEquals(1f, config.hoverOverlay.scale, 0.001f);
        assertEquals("menu/icon/arrow_left.png", config.arrows.left.assetPath);
        assertEquals("menu/icon/arrow_right.png", config.arrows.right.assetPath);
        assertEquals("menu/image/bg_levels.png", config.progress.backgroundPath);
        assertEquals(0, config.indexOf("egypt"));
        assertEquals(3, config.indexOf("beach"));
    }

    @Test
    void blankValuesAreRestoredAndEmptyChaptersUseDefaults() {
        AdventureMenuConfig config = new AdventureMenuConfig();
        config.background.path = " ";
        config.progress.format = "";
        config.arrows.left.assetPath = "";
        config.chapters.clear();
        config.sanitize();
        assertEquals("menu/bg/adventure.png", config.background.path);
        assertEquals("{played} / {total}", config.progress.format);
        assertEquals("menu/icon/arrow_left.png", config.arrows.left.assetPath);
        assertEquals(4, config.visibleChapters().size());
        assertEquals("egypt", config.initialFrontId);
    }
}

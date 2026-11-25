package app.view.mainmenu.news;

import app.enums.NewsOrder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NewsMenuConfigTest {
    @Test
    void sanitizeKeepsRequestedFontsAndUnreadDefault() {
        NewsMenuConfig config = new NewsMenuConfig().sanitize();
        assertEquals("FBUSV8C5EI_1_outline", config.newsTitle.titleFont);
        assertEquals("AVENIRNEXTLTPRO-DEMICN", config.newsMessage.messageFont);
        assertEquals("PICO12", config.newsTime.timeFont);
        assertEquals("E8C84A", config.newsTime.timeColor);
        assertTrue(config.newsTitle.titleFontSize > config.newsMessage.messageFontSize);
        assertTrue(config.newsMessage.messageFontSize > config.newsTime.timeFontSize);
        assertTrue(config.sorting.defaultShowUnreadOnly);
        assertFalse(config.sorting.showAllByDefault());
        assertEquals(NewsOrder.NEWEST_FIRST, config.sorting.order());
        assertEquals("Show all news", config.checkbox.checkboxLabel);
        assertEquals("center", config.checkbox.checkboxAlignment);
        assertEquals(0.20f, config.scrollArea.scrollAreaHorizontalInset, 0.001f);
        assertEquals("No unread news.", config.emptyState.message(false));
        assertEquals("No news to show.", config.emptyState.message(true));
    }

    @Test
    void blankValuesAreReplacedWithSemanticDefaults() {
        NewsMenuConfig config = new NewsMenuConfig();
        config.newsTitle.titleFont = "";
        config.newsMessage.messageFont = null;
        config.newsTime.timeFont = "";
        config.newsTime.timeColor = "";
        config.checkbox.checkboxLabel = " ";
        config.sorting.defaultNewsOrder = "";
        config.emptyState.unreadEmptyMessage = " ";
        config.sanitize();
        assertEquals("FBUSV8C5EI_1_outline", config.newsTitle.titleFont);
        assertEquals("AVENIRNEXTLTPRO-DEMICN", config.newsMessage.messageFont);
        assertEquals("PICO12", config.newsTime.timeFont);
        assertEquals("E8C84A", config.newsTime.timeColor);
        assertEquals("Show all news", config.checkbox.checkboxLabel);
        assertEquals(NewsOrder.NEWEST_FIRST, config.sorting.order());
        assertEquals("No unread news.", config.emptyState.unreadEmptyMessage);
    }
}

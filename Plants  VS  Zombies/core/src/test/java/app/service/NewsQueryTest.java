package app.service;

import app.enums.NewsOrder;
import app.model.News;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NewsQueryTest {
    @Test
    void unreadFilterHidesReadNews() {
        List<News> source = sampleNews();
        List<News> unread = NewsQuery.visible(source, false, NewsOrder.NEWEST_FIRST);
        assertEquals(2, unread.size());
        assertEquals("Newer unread", unread.get(0).getTitle());
        assertEquals("Older unread", unread.get(1).getTitle());
    }

    @Test
    void showAllUsesNewestFirstByDefault() {
        List<News> source = sampleNews();
        List<News> all = NewsQuery.visible(source, true, NewsOrder.NEWEST_FIRST);
        assertEquals(3, all.size());
        assertEquals("Newer unread", all.get(0).getTitle());
        assertEquals("Read news", all.get(1).getTitle());
        assertEquals("Older unread", all.get(2).getTitle());
    }

    @Test
    void oldestFirstReversesTimeOrder() {
        List<News> source = sampleNews();
        List<News> all = NewsQuery.visible(source, true, NewsOrder.OLDEST_FIRST);
        assertEquals("Older unread", all.get(0).getTitle());
        assertEquals("Read news", all.get(1).getTitle());
        assertEquals("Newer unread", all.get(2).getTitle());
    }

    @Test
    void markDisplayedOnlyChangesUnreadItems() {
        List<News> source = sampleNews();
        List<News> unread = NewsQuery.visible(source, false, NewsOrder.NEWEST_FIRST);
        int changed = NewsQuery.markDisplayed(unread);
        assertEquals(2, changed);
        assertTrue(source.get(0).isRead());
        assertTrue(source.get(1).isRead());
        assertTrue(source.get(2).isRead());
        assertEquals(0, NewsQuery.markDisplayed(unread));
    }

    @Test
    void migrateSanitizesLegacyNewsWithoutInjectingPlaceholders() {
        List<News> existing = new ArrayList<>();
        News legacy = new News();
        legacy.setMessage("WELCOME TO PvZ!\nKeep the lawn safe.");
        legacy.setTitle("");
        legacy.setTime(null);
        existing.add(legacy);
        List<News> catalog = NewsCatalog.defaults();
        NewsQuery.migrate(existing, catalog);
        assertEquals("Welcome to PvZ", existing.get(0).getTitle());
        assertTrue(existing.get(0).getTime() != null);
        assertEquals(1, existing.size());
        boolean second = NewsQuery.migrate(existing, catalog);
        assertFalse(second);
        assertEquals(1, existing.size());
    }

    @Test
    void catalogDefaultsAreEmpty() {
        List<News> defaults = NewsCatalog.defaults();
        assertEquals(0, defaults.size());
    }

    @Test
    void purgeRemovesLegacyPlaceholderTitles() {
        List<News> existing = new ArrayList<>();
        existing.add(new News("Welcome to PvZ", "old placeholder", false, LocalDateTime.now()));
        existing.add(News.article("REAL:1", app.enums.NewsCategory.SYSTEM, app.enums.NewsPriority.NORMAL,
            "Real Event", "body", null));
        assertTrue(NewsQuery.purgePlaceholderArticles(existing));
        assertEquals(1, existing.size());
        assertEquals("Real Event", existing.get(0).getTitle());
    }

    @Test
    void unreadCountAndUniqueKeyHelpers() {
        List<News> source = sampleNews();
        assertEquals(2, NewsQuery.unreadCount(source));
        source.get(0).setUniqueKey("K1");
        assertTrue(NewsQuery.containsUniqueKey(source, "K1"));
        assertFalse(NewsQuery.containsUniqueKey(source, "missing"));
    }

    @Test
    void orderingUsesIdTieBreakDeterministically() {
        List<News> source = new ArrayList<>();
        LocalDateTime same = LocalDateTime.of(2026, 9, 1, 12, 0);
        News a = new News("A", "a", false, same);
        News b = new News("B", "b", false, same);
        a.setId("aaa");
        b.setId("bbb");
        source.add(b);
        source.add(a);
        List<News> newest = NewsQuery.visible(source, true, NewsOrder.NEWEST_FIRST);
        assertEquals("bbb", newest.get(0).getId());
        assertEquals("aaa", newest.get(1).getId());
    }

    private static List<News> sampleNews() {
        List<News> news = new ArrayList<>();
        news.add(new News("Older unread", "old", false, LocalDateTime.of(2026, 8, 1, 10, 0)));
        news.add(new News("Read news", "read", true, LocalDateTime.of(2026, 8, 2, 10, 0)));
        news.add(new News("Newer unread", "new", false, LocalDateTime.of(2026, 8, 3, 10, 0)));
        return news;
    }
}

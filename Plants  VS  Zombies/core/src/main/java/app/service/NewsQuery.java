package app.service;

import app.enums.NewsOrder;
import app.model.News;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class NewsQuery {
    private static final Set<String> PLACEHOLDER_TITLES = Set.of(
        "Welcome to PvZ",
        "Game Update 0.9",
        "New Adventure Content",
        "Gameplay Update",
        "Bug Fix",
        "New Mini Game",
        "Network Update",
        "UI Improvement",
        "Community News",
        "Development Update"
    );

    private NewsQuery() {
    }

    public static List<News> visible(List<News> source, boolean showAll, NewsOrder order) {
        List<News> visible = new ArrayList<>();
        if (source == null) {
            return visible;
        }
        for (News news : source) {
            if (news == null) {
                continue;
            }
            news.sanitize();
            if (showAll || !news.isRead()) {
                visible.add(news);
            }
        }
        visible.sort(ordering(order));
        return visible;
    }

    public static Comparator<News> ordering(NewsOrder order) {
        Comparator<News> byTime = Comparator.comparing(
            news -> news.getTime() == null ? LocalDateTime.MIN : news.getTime()
        );
        Comparator<News> byId = Comparator.comparing(
            news -> news.getId() == null ? "" : news.getId(),
            String.CASE_INSENSITIVE_ORDER
        );
        Comparator<News> combined = byTime.thenComparing(byId);
        if (order != NewsOrder.OLDEST_FIRST) {
            combined = combined.reversed();
        }
        return combined;
    }

    public static int unreadCount(List<News> source) {
        if (source == null) {
            return 0;
        }
        int count = 0;
        for (News news : source) {
            if (news != null && !news.sanitize().isRead()) {
                count++;
            }
        }
        return count;
    }

    public static int markDisplayed(List<News> displayed) {
        int changed = 0;
        if (displayed == null) {
            return 0;
        }
        for (News news : displayed) {
            if (news == null || news.isRead()) {
                continue;
            }
            news.setRead(true);
            changed++;
        }
        return changed;
    }

    public static boolean markRead(News news) {
        if (news == null || news.isRead()) {
            return false;
        }
        news.setRead(true);
        return true;
    }

    public static boolean containsUniqueKey(List<News> source, String uniqueKey) {
        if (source == null || uniqueKey == null || uniqueKey.isBlank()) {
            return false;
        }
        String key = uniqueKey.strip();
        for (News news : source) {
            if (news == null) {
                continue;
            }
            String existing = news.getUniqueKey();
            if (existing != null && key.equalsIgnoreCase(existing.strip())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sanitizes existing articles and optionally merges catalog entries that carry a uniqueKey.
     * Placeholder catalog items without uniqueKey are ignored.
     */
    public static boolean migrate(List<News> existing, List<News> catalog) {
        boolean changed = false;
        if (existing == null) {
            return false;
        }
        for (News news : existing) {
            if (news == null) {
                continue;
            }
            String beforeTitle = news.getTitle();
            String beforeId = news.getId();
            news.sanitize();
            if (beforeTitle == null || beforeTitle.isBlank() || beforeId == null || beforeId.isBlank()) {
                changed = true;
            }
        }
        if (catalog == null) {
            return changed;
        }
        for (News catalogNews : catalog) {
            if (catalogNews == null) {
                continue;
            }
            catalogNews.sanitize();
            String key = catalogNews.getUniqueKey();
            if (key == null || key.isBlank()) {
                continue;
            }
            if (containsUniqueKey(existing, key)) {
                continue;
            }
            existing.add(copy(catalogNews));
            changed = true;
        }
        return changed;
    }

    public static boolean purgePlaceholderArticles(List<News> existing) {
        if (existing == null || existing.isEmpty()) {
            return false;
        }
        boolean changed = false;
        Iterator<News> iterator = existing.iterator();
        while (iterator.hasNext()) {
            News news = iterator.next();
            if (news == null) {
                iterator.remove();
                changed = true;
                continue;
            }
            news.sanitize();
            if (isLegacyPlaceholder(news)) {
                iterator.remove();
                changed = true;
            }
        }
        return changed;
    }

    public static boolean isLegacyPlaceholder(News news) {
        if (news == null) {
            return false;
        }
        if (news.getUniqueKey() != null && !news.getUniqueKey().isBlank()) {
            return false;
        }
        String title = news.getTitle() == null ? "" : news.getTitle().strip();
        for (String placeholder : PLACEHOLDER_TITLES) {
            if (placeholder.equalsIgnoreCase(title)) {
                return true;
            }
        }
        return false;
    }

    private static News copy(News source) {
        News copy = News.article(
            source.getUniqueKey(),
            source.getCategory(),
            source.getPriority(),
            source.getTitle(),
            source.getMessage(),
            source.getMetadata()
        );
        copy.setRead(source.isRead());
        copy.setTime(source.getTime());
        if (source.getId() != null && !source.getId().isBlank()) {
            copy.setId(source.getId());
        }
        return copy;
    }
}

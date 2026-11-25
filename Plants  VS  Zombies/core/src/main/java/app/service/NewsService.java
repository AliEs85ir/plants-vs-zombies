package app.service;

import app.enums.NewsCategory;
import app.enums.NewsOrder;
import app.enums.NewsPriority;
import app.model.App;
import app.model.News;
import app.model.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public final class NewsService {
    private static List<News> sessionNews;

    private NewsService() {
    }

    public static List<News> visibleNews(boolean showAll, NewsOrder order) {
        return NewsQuery.visible(source(), showAll, order);
    }

    public static int unreadCount() {
        return NewsQuery.unreadCount(source());
    }

    public static int unreadCount(User user) {
        if (user == null) {
            return 0;
        }
        ensureReady(user);
        return NewsQuery.unreadCount(user.getNewsList());
    }

    public static void markDisplayed(List<News> displayed) {
        if (NewsQuery.markDisplayed(displayed) > 0 && App.getCurrentUser() != null) {
            persist();
        }
    }

    public static void markRead(News news) {
        if (NewsQuery.markRead(news) && App.getCurrentUser() != null) {
            persist();
        }
    }

    public static void ensureReady(User user) {
        if (user == null || user.getNewsList() == null) {
            return;
        }
        boolean changed = false;
        if (NewsQuery.purgePlaceholderArticles(user.getNewsList())) {
            changed = true;
        }
        if (NewsQuery.migrate(user.getNewsList(), NewsCatalog.load())) {
            changed = true;
        }
        if (changed) {
            persist();
        }
    }

    /**
     * Idempotent publish: if {@code uniqueKey} already exists on the user, no article is added.
     * @return true when a new article was stored
     */
    public static boolean publishIfAbsent(User user, String uniqueKey, Supplier<News> articleFactory) {
        if (user == null || user.getNewsList() == null || articleFactory == null) {
            return false;
        }
        if (uniqueKey == null || uniqueKey.isBlank()) {
            return false;
        }
        ensureReady(user);
        if (NewsQuery.containsUniqueKey(user.getNewsList(), uniqueKey)) {
            return false;
        }
        News article = articleFactory.get();
        if (article == null) {
            return false;
        }
        article.setUniqueKey(uniqueKey.strip());
        article.sanitize();
        user.getNewsList().add(article);
        persistUser(user);
        return true;
    }

    public static boolean publishIfAbsent(
        User user,
        String uniqueKey,
        NewsCategory category,
        NewsPriority priority,
        String title,
        String body,
        Map<String, String> metadata
    ) {
        return publishIfAbsent(user, uniqueKey, () -> News.article(uniqueKey, category, priority, title, body, metadata));
    }

    public static boolean publishIfAbsentCurrent(String uniqueKey, Supplier<News> articleFactory) {
        return publishIfAbsent(App.getCurrentUser(), uniqueKey, articleFactory);
    }

    private static List<News> source() {
        User user = App.getCurrentUser();
        if (user != null) {
            ensureReady(user);
            return user.getNewsList();
        }
        if (sessionNews == null) {
            sessionNews = new ArrayList<>();
        }
        return sessionNews;
    }

    private static void persist() {
        AppService.syncGameData();
    }

    private static void persistUser(User user) {
        if (user == null) {
            return;
        }
        if (App.getCurrentUser() == user) {
            AppService.markGameDataDirty();
            AppService.syncGameData();
        } else {
            ProgressService.saveUserProgress(user);
        }
    }

    public static String formatForConsole(List<News> newsList, String timePattern) {
        if (newsList == null || newsList.isEmpty()) {
            return "";
        }
        StringBuilder output = new StringBuilder();
        for (News news : newsList) {
            news.sanitize();
            output.append(news.getTitle()).append('\n');
            output.append(news.getMessage()).append('\n');
            output.append(news.formattedTime(timePattern)).append("\n\n");
        }
        return output.toString();
    }
}

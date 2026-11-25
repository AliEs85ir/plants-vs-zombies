package app.model;

import app.enums.NewsCategory;
import app.enums.NewsPriority;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * Persistent newspaper article. Stored on {@link app.model.user.User#getNewsList()}.
 * {@code message} is the long article body (multi-paragraph). Extra event data lives in {@code metadata}.
 */
public class News {
    private String id;
    private String uniqueKey;
    private NewsCategory category = NewsCategory.SYSTEM;
    private NewsPriority priority = NewsPriority.NORMAL;
    private String title;
    @SerializedName(value = "message", alternate = {"body"})
    private String message;
    @SerializedName(value = "read", alternate = {"haveRead", "isRead"})
    private boolean read;
    private LocalDateTime time;
    private Map<String, String> metadata = new LinkedHashMap<>();

    public News() {
        this("", "", false, LocalDateTime.now());
    }

    public News(String message, boolean haveRead) {
        this("", message, haveRead, LocalDateTime.now());
    }

    public News(String title, String message, boolean read, LocalDateTime time) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.message = message;
        this.read = read;
        this.time = time;
        sanitize();
    }

    public static News article(
        String uniqueKey,
        NewsCategory category,
        NewsPriority priority,
        String title,
        String body,
        Map<String, String> metadata
    ) {
        News news = new News(title, body, false, LocalDateTime.now());
        news.uniqueKey = uniqueKey;
        news.category = category == null ? NewsCategory.SYSTEM : category;
        news.priority = priority == null ? NewsPriority.NORMAL : priority;
        if (metadata != null && !metadata.isEmpty()) {
            news.metadata = new LinkedHashMap<>(metadata);
        }
        return news.sanitize();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        sanitize();
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public NewsCategory getCategory() {
        return category == null ? NewsCategory.SYSTEM : category;
    }

    public void setCategory(NewsCategory category) {
        this.category = category == null ? NewsCategory.SYSTEM : category;
    }

    public NewsPriority getPriority() {
        return priority == null ? NewsPriority.NORMAL : priority;
    }

    public void setPriority(NewsPriority priority) {
        this.priority = priority == null ? NewsPriority.NORMAL : priority;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        sanitize();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
        sanitize();
    }

    /** Long newspaper body (alias of message). */
    public String getBody() {
        return getMessage();
    }

    public void setBody(String body) {
        setMessage(body);
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isHaveRead() {
        return isRead();
    }

    public void setHaveRead(boolean haveRead) {
        setRead(haveRead);
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
        sanitize();
    }

    public LocalDateTime getCreatedAt() {
        return getTime();
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        setTime(createdAt);
    }

    public Map<String, String> getMetadata() {
        if (metadata == null) {
            metadata = new LinkedHashMap<>();
        }
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata == null ? new LinkedHashMap<>() : new LinkedHashMap<>(metadata);
    }

    public String metadata(String key) {
        if (key == null || metadata == null) {
            return null;
        }
        return metadata.get(key);
    }

    public void putMetadata(String key, String value) {
        if (key == null || key.isBlank()) {
            return;
        }
        if (metadata == null) {
            metadata = new LinkedHashMap<>();
        }
        if (value == null) {
            metadata.remove(key);
        } else {
            metadata.put(key, value);
        }
    }

    public String preview(int maxChars) {
        sanitize();
        String body = message == null ? "" : message.strip();
        if (body.isEmpty()) {
            return "";
        }
        String first = body.split("\\R", 2)[0].strip();
        int limit = Math.max(24, maxChars);
        if (first.length() <= limit) {
            return first;
        }
        return first.substring(0, limit - 3).strip() + "...";
    }

    public String formattedTime(String pattern) {
        sanitize();
        String format = pattern == null || pattern.isBlank() ? "yyyy-MM-dd HH:mm" : pattern;
        try {
            return time.format(DateTimeFormatter.ofPattern(format, Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            return time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.ROOT));
        }
    }

    public String statusLabel() {
        return isRead() ? "Read" : "Unread";
    }

    public News sanitize() {
        if (id == null || id.isBlank()) {
            id = UUID.randomUUID().toString();
        }
        if (message == null) {
            message = "";
        }
        if (time == null) {
            time = LocalDateTime.of(2024, 1, 1, 12, 0);
        }
        if (category == null) {
            category = NewsCategory.SYSTEM;
        }
        if (priority == null) {
            priority = NewsPriority.NORMAL;
        }
        if (metadata == null) {
            metadata = new LinkedHashMap<>();
        }
        if (uniqueKey != null) {
            uniqueKey = uniqueKey.strip();
            if (uniqueKey.isEmpty()) {
                uniqueKey = null;
            }
        }
        if (title == null || title.isBlank()) {
            title = derivedTitle(message);
        }
        return this;
    }

    private static String derivedTitle(String message) {
        if (message == null || message.isBlank()) {
            return "News";
        }
        String upper = message.toUpperCase(Locale.ROOT);
        if (upper.contains("WELCOME TO PVZ")) {
            return "Welcome to PvZ";
        }
        String firstLine = message.strip().split("\\R", 2)[0].strip();
        if (firstLine.length() > 48) {
            return firstLine.substring(0, 45).strip() + "...";
        }
        return firstLine.isBlank() ? "News" : firstLine;
    }

    public static Map<String, String> meta(String... keyValues) {
        Map<String, String> map = new LinkedHashMap<>();
        if (keyValues == null) {
            return map;
        }
        for (int i = 0; i + 1 < keyValues.length; i += 2) {
            if (keyValues[i] != null) {
                map.put(keyValues[i], keyValues[i + 1] == null ? "" : keyValues[i + 1]);
            }
        }
        return map;
    }

    public static Map<String, String> emptyMeta() {
        return Collections.emptyMap();
    }
}

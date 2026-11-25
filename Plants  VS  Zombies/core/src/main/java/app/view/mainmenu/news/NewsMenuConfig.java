package app.view.mainmenu.news;

import app.enums.NewsOrder;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Align;

import java.util.Locale;

public class NewsMenuConfig {
    public TitleImageConfig titleImage = new TitleImageConfig();
    public ScrollAreaConfig scrollArea = new ScrollAreaConfig();
    public NewsItemConfig newsItem = new NewsItemConfig();
    public NewsTitleTextConfig newsTitle = new NewsTitleTextConfig();
    public NewsMessageTextConfig newsMessage = new NewsMessageTextConfig();
    public NewsTimeTextConfig newsTime = new NewsTimeTextConfig();
    public SeparatorConfig separator = new SeparatorConfig();
    public CheckboxConfig checkbox = new CheckboxConfig();
    public EmptyStateConfig emptyState = new EmptyStateConfig();
    public SortingConfig sorting = new SortingConfig();
    public ListPaneConfig listPane = new ListPaneConfig();
    public DetailPaneConfig detailPane = new DetailPaneConfig();

    public NewsMenuConfig sanitize() {
        if (titleImage == null) {
            titleImage = new TitleImageConfig();
        }
        if (scrollArea == null) {
            scrollArea = new ScrollAreaConfig();
        }
        if (newsItem == null) {
            newsItem = new NewsItemConfig();
        }
        if (newsTitle == null) {
            newsTitle = new NewsTitleTextConfig();
        }
        if (newsMessage == null) {
            newsMessage = new NewsMessageTextConfig();
        }
        if (newsTime == null) {
            newsTime = new NewsTimeTextConfig();
        }
        if (separator == null) {
            separator = new SeparatorConfig();
        }
        if (checkbox == null) {
            checkbox = new CheckboxConfig();
        }
        if (emptyState == null) {
            emptyState = new EmptyStateConfig();
        }
        if (sorting == null) {
            sorting = new SortingConfig();
        }
        if (listPane == null) {
            listPane = new ListPaneConfig();
        }
        if (detailPane == null) {
            detailPane = new DetailPaneConfig();
        }
        titleImage.sanitize();
        scrollArea.sanitize();
        newsItem.sanitize();
        newsTitle.sanitize();
        newsMessage.sanitize();
        newsTime.sanitize();
        separator.sanitize();
        checkbox.sanitize();
        emptyState.sanitize();
        sorting.sanitize();
        listPane.sanitize();
        detailPane.sanitize();
        return this;
    }

    public static class TitleImageConfig {
        public String assetPath = "menu/image/new_title.png";
        public float titleWidth = 420f;
        public float titleHeight = 72f;
        public float titleScale = 1f;
        public float titleOffsetX;
        public float titleOffsetY;
        public float titleTopPadding = 8f;
        public float titleBottomPadding = 12f;
        public String titleAlignment = "top";
        public boolean keepAspectRatio = true;

        public void sanitize() {
            if (assetPath == null || assetPath.isBlank()) {
                assetPath = "menu/image/new_title.png";
            }
            if (titleWidth <= 0f) {
                titleWidth = 420f;
            }
            if (titleHeight <= 0f) {
                titleHeight = 72f;
            }
            if (titleScale <= 0f) {
                titleScale = 1f;
            }
            if (titleAlignment == null || titleAlignment.isBlank()) {
                titleAlignment = "top";
            }
        }

        public float drawWidth() {
            return titleWidth * titleScale;
        }

        public float drawHeight() {
            return titleHeight * titleScale;
        }
    }

    public static class ScrollAreaConfig {
        public float scrollAreaWidth;
        public float scrollAreaHeight;
        public float scrollAreaOffsetX;
        public float scrollAreaOffsetY;
        public float scrollAreaTopPadding = 8f;
        public float scrollAreaBottomPadding = 12f;
        public float scrollAreaHorizontalInset = 0.20f;
        public float scrollSpeed = 220f;
        public float scrollSensitivity = 1.35f;

        public void sanitize() {
            if (scrollSpeed <= 0f) {
                scrollSpeed = 220f;
            }
            if (scrollSensitivity <= 0f) {
                scrollSensitivity = 1.35f;
            }
            if (scrollAreaHorizontalInset < 0f) {
                scrollAreaHorizontalInset = 0.20f;
            }
            if (scrollAreaHorizontalInset > 0.45f) {
                scrollAreaHorizontalInset = 0.45f;
            }
        }
    }

    public static class NewsItemConfig {
        public float newsItemWidth;
        public float newsItemPaddingLeft = 18f;
        public float newsItemPaddingRight = 18f;
        public float newsItemPaddingTop = 10f;
        public float newsItemPaddingBottom = 8f;
        public float newsItemSpacing = 14f;
        public float firstNewsTopPadding = 4f;
        public float lastNewsBottomPadding = 18f;

        public void sanitize() {
            newsItemPaddingLeft = Math.max(0f, newsItemPaddingLeft);
            newsItemPaddingRight = Math.max(0f, newsItemPaddingRight);
            newsItemPaddingTop = Math.max(0f, newsItemPaddingTop);
            newsItemPaddingBottom = Math.max(0f, newsItemPaddingBottom);
            newsItemSpacing = Math.max(0f, newsItemSpacing);
        }
    }

    public static class NewsTitleTextConfig {
        public String titleFont = "FBUSV8C5EI_1_outline";
        public String titleFontFallback = "FBUSV8C5EI_1";
        public float titleFontSize = 0.88f;
        public String titleColor = "F4E7B0";
        public float titleLineSpacing = 1.05f;
        public float titleMessageSpacing = 6f;
        public String titleAlignment = "left";
        public float titleMaxWidth;

        public void sanitize() {
            if (titleFont == null || titleFont.isBlank()) {
                titleFont = "FBUSV8C5EI_1_outline";
            }
            if (titleFontFallback == null || titleFontFallback.isBlank()) {
                titleFontFallback = "FBUSV8C5EI_1";
            }
            if (titleFontSize <= 0f) {
                titleFontSize = 0.88f;
            }
            if (titleColor == null || titleColor.isBlank()) {
                titleColor = "F4E7B0";
            }
            if (titleLineSpacing <= 0f) {
                titleLineSpacing = 1.05f;
            }
            if (titleAlignment == null || titleAlignment.isBlank()) {
                titleAlignment = "left";
            }
        }
    }

    public static class NewsMessageTextConfig {
        public String messageFont = "AVENIRNEXTLTPRO-DEMICN";
        public String messageFontFallback = "AVENIRNEXTLTPRO_DEMICN";
        public float messageFontSize = 0.78f;
        public String messageColor = "E4DCC4";
        public float messageLineSpacing = 1.08f;
        public float messageTimeSpacing = 8f;
        public String messageAlignment = "left";
        public float messageMaxWidth;

        public void sanitize() {
            if (messageFont == null || messageFont.isBlank()) {
                messageFont = "AVENIRNEXTLTPRO-DEMICN";
            }
            if (messageFontFallback == null || messageFontFallback.isBlank()) {
                messageFontFallback = "AVENIRNEXTLTPRO_DEMICN";
            }
            if (messageFontSize <= 0f) {
                messageFontSize = 0.78f;
            }
            if (messageColor == null || messageColor.isBlank()) {
                messageColor = "E4DCC4";
            }
            if (messageLineSpacing <= 0f) {
                messageLineSpacing = 1.08f;
            }
            if (messageAlignment == null || messageAlignment.isBlank()) {
                messageAlignment = "left";
            }
        }
    }

    public static class NewsTimeTextConfig {
        public String timeFont = "PICO12";
        public String timeFontFallback = "PICO12_";
        public float timeFontSize = 0.70f;
        public String timeColor = "E8C84A";
        public float timeTopPadding = 2f;
        public float timeBottomPadding = 2f;
        public String timeAlignment = "left";
        public String timePattern = "yyyy-MM-dd HH:mm";
        public float timeOffsetX;
        public float timeOffsetY;

        public void sanitize() {
            if (timeFont == null || timeFont.isBlank()) {
                timeFont = "PICO12";
            }
            if (timeFontFallback == null || timeFontFallback.isBlank()) {
                timeFontFallback = "PICO12_";
            }
            if (timeFontSize <= 0f) {
                timeFontSize = 0.70f;
            }
            if (timeColor == null || timeColor.isBlank()) {
                timeColor = "E8C84A";
            }
            if (timeAlignment == null || timeAlignment.isBlank()) {
                timeAlignment = "left";
            }
            if (timePattern == null || timePattern.isBlank()) {
                timePattern = "yyyy-MM-dd HH:mm";
            }
        }
    }

    public static class SeparatorConfig {
        public boolean enabled = true;
        public float height = 1f;
        public String color = "FFFFFF33";
        public float topPadding = 8f;
        public float bottomPadding = 4f;

        public void sanitize() {
            if (height < 0f) {
                height = 1f;
            }
            if (color == null || color.isBlank()) {
                color = "FFFFFF33";
            }
        }
    }

    public static class CheckboxConfig {
        public float checkboxPositionX;
        public float checkboxPositionY;
        public float checkboxSize = 32f;
        public float checkboxTopPadding = 8f;
        public float checkboxBottomPadding = 12f;
        public float checkboxOffsetX;
        public float checkboxOffsetY;
        public String checkboxAlignment = "center";
        public String checkboxLabel = "Show all news";
        public String checkboxLabelFont = "BRIANNETOD";
        public float checkboxLabelFontSize = 0.99f;
        public String checkboxLabelColor = "F2E7C4";
        public String checkboxStyle = "default";
        public String checkboxHoverColor = "FFF4C0";

        public void sanitize() {
            if (checkboxSize <= 0f) {
                checkboxSize = 32f;
            }
            if (checkboxTopPadding < 0f) {
                checkboxTopPadding = 8f;
            }
            if (checkboxBottomPadding < 0f) {
                checkboxBottomPadding = 12f;
            }
            if (checkboxAlignment == null || checkboxAlignment.isBlank()) {
                checkboxAlignment = "center";
            }
            if (checkboxLabel == null || checkboxLabel.isBlank()) {
                checkboxLabel = "Show all news";
            }
            if (checkboxLabelFont == null || checkboxLabelFont.isBlank()) {
                checkboxLabelFont = "BRIANNETOD";
            }
            if (checkboxLabelFontSize <= 0f) {
                checkboxLabelFontSize = 0.99f;
            }
            if (checkboxLabelColor == null || checkboxLabelColor.isBlank()) {
                checkboxLabelColor = "F2E7C4";
            }
            if (checkboxStyle == null || checkboxStyle.isBlank()) {
                checkboxStyle = "default";
            }
            if (checkboxHoverColor == null || checkboxHoverColor.isBlank()) {
                checkboxHoverColor = "FFF4C0";
            }
        }
    }

    public static class EmptyStateConfig {
        public String unreadEmptyMessage = "No unread news.";
        public String allEmptyMessage = "No news to show.";
        public String emptyMessageFont = "AVENIRNEXTLTPRO-DEMICN";
        public String emptyMessageFontFallback = "AVENIRNEXTLTPRO_DEMICN";
        public float emptyMessageFontSize = 0.92f;
        public String emptyMessageColor = "E4DCC4";
        public String emptyMessageAlignment = "center";
        public float emptyMessageTopPadding = 24f;

        public void sanitize() {
            if (unreadEmptyMessage == null || unreadEmptyMessage.isBlank()) {
                unreadEmptyMessage = "No unread news.";
            }
            if (allEmptyMessage == null || allEmptyMessage.isBlank()) {
                allEmptyMessage = "No news to show.";
            }
            if (emptyMessageFont == null || emptyMessageFont.isBlank()) {
                emptyMessageFont = "AVENIRNEXTLTPRO-DEMICN";
            }
            if (emptyMessageFontFallback == null || emptyMessageFontFallback.isBlank()) {
                emptyMessageFontFallback = "AVENIRNEXTLTPRO_DEMICN";
            }
            if (emptyMessageFontSize <= 0f) {
                emptyMessageFontSize = 0.92f;
            }
            if (emptyMessageColor == null || emptyMessageColor.isBlank()) {
                emptyMessageColor = "E4DCC4";
            }
            if (emptyMessageAlignment == null || emptyMessageAlignment.isBlank()) {
                emptyMessageAlignment = "center";
            }
        }

        public String message(boolean showAll) {
            return showAll ? allEmptyMessage : unreadEmptyMessage;
        }
    }

    public static class SortingConfig {
        public boolean defaultShowUnreadOnly = true;
        public String defaultNewsOrder = "NEWEST_FIRST";

        public void sanitize() {
            if (defaultNewsOrder == null || defaultNewsOrder.isBlank()) {
                defaultNewsOrder = "NEWEST_FIRST";
            }
        }

        public NewsOrder order() {
            return NewsOrder.fromId(defaultNewsOrder);
        }

        public boolean showAllByDefault() {
            return !defaultShowUnreadOnly;
        }
    }

    public static Color color(String hex, Color fallback) {
        if (hex == null || hex.isBlank()) {
            return fallback.cpy();
        }
        String value = hex.trim();
        if (value.startsWith("#")) {
            value = value.substring(1);
        }
        try {
            return Color.valueOf(value);
        } catch (Exception exception) {
            return fallback.cpy();
        }
    }

    public static int align(String value, int fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return switch (value.trim().toLowerCase(Locale.ROOT)) {
            case "left", "left_top", "topleft" -> Align.left | Align.top;
            case "right", "right_top", "topright" -> Align.right | Align.top;
            case "center", "top" -> Align.top | Align.center;
            case "bottom" -> Align.bottom | Align.center;
            case "bottom_left", "bottomleft" -> Align.bottom | Align.left;
            case "bottom_right", "bottomright" -> Align.bottom | Align.right;
            default -> fallback;
        };
    }

    public static int textAlign(String value) {
        if (value == null || value.isBlank()) {
            return Align.left;
        }
        return switch (value.trim().toLowerCase(Locale.ROOT)) {
            case "center" -> Align.center;
            case "right" -> Align.right;
            default -> Align.left;
        };
    }

    public static class ListPaneConfig {
        public float widthRatio = 0.42f;
        public int previewMaxChars = 90;
        public String unreadMarker = "•";
        public String selectedTint = "FFF4C055";

        public void sanitize() {
            if (widthRatio <= 0f || widthRatio >= 1f) {
                widthRatio = 0.42f;
            }
            if (previewMaxChars < 24) {
                previewMaxChars = 90;
            }
            if (unreadMarker == null || unreadMarker.isBlank()) {
                unreadMarker = "•";
            }
            if (selectedTint == null || selectedTint.isBlank()) {
                selectedTint = "FFF4C055";
            }
        }
    }

    public static class DetailPaneConfig {
        public String headerTitle = "Neighborhood Newspaper";
        public String statusUnread = "Unread";
        public String statusRead = "Read";
        public String backLabel = "Back to list";

        public void sanitize() {
            if (headerTitle == null || headerTitle.isBlank()) {
                headerTitle = "Neighborhood Newspaper";
            }
            if (statusUnread == null || statusUnread.isBlank()) {
                statusUnread = "Unread";
            }
            if (statusRead == null || statusRead.isBlank()) {
                statusRead = "Read";
            }
            if (backLabel == null || backLabel.isBlank()) {
                backLabel = "Back to list";
            }
        }
    }
}

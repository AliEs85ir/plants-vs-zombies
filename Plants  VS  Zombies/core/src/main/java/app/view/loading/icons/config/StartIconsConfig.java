package app.view.loading.icons.config;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class StartIconsConfig {
    public LayoutConfig layout = new LayoutConfig();
    public HoverConfig hover = new HoverConfig();
    public PopupConfig popup = new PopupConfig();
    public ContentPaths content = new ContentPaths();
    public ArrayList<IconConfig> icons = new ArrayList<>();

    public StartIconsConfig sanitize() {
        if (layout == null) {
            layout = new LayoutConfig();
        }
        if (hover == null) {
            hover = new HoverConfig();
        }
        if (popup == null) {
            popup = new PopupConfig();
        }
        if (content == null) {
            content = new ContentPaths();
        }
        if (icons == null) {
            icons = new ArrayList<>();
        }
        layout.sanitize();
        hover.sanitize();
        popup.sanitize();
        content.sanitize();
        for (IconConfig icon : icons) {
            if (icon != null) {
                icon.sanitize();
            }
        }
        icons.removeIf(icon -> icon == null || !icon.visible || icon.id.isBlank());
        icons.sort(Comparator.comparingInt(icon -> icon.order));
        return this;
    }

    public List<IconConfig> visibleIcons() {
        List<IconConfig> visible = new ArrayList<>();
        for (IconConfig icon : icons) {
            if (icon != null && icon.visible) {
                visible.add(icon);
            }
        }
        return visible;
    }

    public static class LayoutConfig {
        public String anchor = "bottom";
        public String alignment = "center";
        public int columns = 8;
        public float iconWidth = 72f;
        public float iconHeight = 72f;
        public float scale = 1f;
        public float spacingX = 18f;
        public float spacingY = 10f;
        public float paddingLeft = 24f;
        public float paddingRight = 24f;
        public float paddingBottom = 16f;
        public float paddingTop = 0f;
        public float offsetX = 0f;
        public float offsetY = 8f;
        public float sectionGap = 0f;

        public void sanitize() {
            if (anchor == null || anchor.isBlank()) {
                anchor = "bottom";
            }
            if (alignment == null || alignment.isBlank()) {
                alignment = "center";
            }
            if (columns < 1) {
                columns = 8;
            }
            if (iconWidth <= 0f) {
                iconWidth = 72f;
            }
            if (iconHeight <= 0f) {
                iconHeight = 72f;
            }
            if (scale <= 0f) {
                scale = 1f;
            }
        }

        public float drawWidth() {
            return iconWidth * scale;
        }

        public float drawHeight() {
            return iconHeight * scale;
        }

        public boolean bottomAnchor() {
            return "bottom".equalsIgnoreCase(anchor);
        }

        public int align() {
            String value = alignment.toLowerCase(Locale.ROOT);
            if ("left".equals(value)) {
                return 0;
            }
            if ("right".equals(value)) {
                return 2;
            }
            return 1;
        }
    }

    public static class HoverConfig {
        public float scale = 1.08f;
        public float pressedScale = 0.96f;
        public float brightness = 1.12f;
        public float offsetY = 2f;
        public float duration = 0.08f;

        public void sanitize() {
            if (scale <= 0f) {
                scale = 1.08f;
            }
            if (pressedScale <= 0f) {
                pressedScale = 0.96f;
            }
            if (brightness <= 0f) {
                brightness = 1.12f;
            }
            if (duration < 0f) {
                duration = 0.08f;
            }
        }
    }

    public static class PopupConfig {
        public float width = 760f;
        public float height = 520f;
        public float headerHeight = 56f;
        public float contentPadding = 18f;
        public float headerPadding = 10f;
        public float closeButtonWidth = 110f;
        public float closeButtonHeight = 36f;
        public float overlayOpacity = 0.72f;
        public float fadeInDuration = 0.16f;
        public float fadeOutDuration = 0.12f;
        public float startScale = 0.95f;
        public float scaleDuration = 0.14f;
        public String panelDrawable = "image_ui_dialog_asset_inner_bkgd_10";
        public String headerDrawable = "image_ui_quests_panel_edge_to_edge_ten";
        public String titleStyle = "big_outline";
        public String bodyStyle = "default";
        public String closeStyle = "brown";
        public String primaryStyle = "green";
        public String secondaryStyle = "default";
        public String tabStyle = "purple";
        public float titleScale = 0.92f;
        public float bodyScale = 0.86f;
        public float scrollbarWidth = 8f;
        public float margin = 24f;
        public float bodyRowSpacing = 8f;
        public float contentHeaderGap = 8f;
        public float tutorialNavigationWidth = 188f;
        public float tutorialNavButtonHeight = 36f;
        public float tutorialNavSpacing = 4f;
        public float faqAnswerIndent = 12f;
        public float faqItemSpacing = 6f;
        public String defaultCloseLabel = "Close";

        public void sanitize() {
            if (width <= 0f) {
                width = 760f;
            }
            if (height <= 0f) {
                height = 520f;
            }
            if (headerHeight <= 0f) {
                headerHeight = 56f;
            }
            if (overlayOpacity < 0f) {
                overlayOpacity = 0f;
            }
            if (overlayOpacity > 1f) {
                overlayOpacity = 1f;
            }
            if (fadeInDuration < 0f) {
                fadeInDuration = 0.16f;
            }
            if (fadeOutDuration < 0f) {
                fadeOutDuration = 0.12f;
            }
            if (startScale <= 0f) {
                startScale = 0.95f;
            }
            if (scaleDuration < 0f) {
                scaleDuration = 0.14f;
            }
            if (panelDrawable == null || panelDrawable.isBlank()) {
                panelDrawable = "image_ui_dialog_asset_inner_bkgd_10";
            }
            if (headerDrawable == null || headerDrawable.isBlank()) {
                headerDrawable = "image_ui_quests_panel_edge_to_edge_ten";
            }
            if (titleStyle == null || titleStyle.isBlank()) {
                titleStyle = "big_outline";
            }
            if (bodyStyle == null || bodyStyle.isBlank()) {
                bodyStyle = "default";
            }
            if (closeStyle == null || closeStyle.isBlank()) {
                closeStyle = "brown";
            }
            if (primaryStyle == null || primaryStyle.isBlank()) {
                primaryStyle = "green";
            }
            if (secondaryStyle == null || secondaryStyle.isBlank()) {
                secondaryStyle = "default";
            }
            if (tabStyle == null || tabStyle.isBlank()) {
                tabStyle = "purple";
            }
            if (titleScale <= 0f) {
                titleScale = 0.92f;
            }
            if (bodyScale <= 0f) {
                bodyScale = 0.86f;
            }
            if (scrollbarWidth <= 0f) {
                scrollbarWidth = 8f;
            }
            if (margin < 0f) {
                margin = 24f;
            }
            if (bodyRowSpacing < 0f) {
                bodyRowSpacing = 8f;
            }
            if (contentHeaderGap < 0f) {
                contentHeaderGap = 8f;
            }
            if (tutorialNavigationWidth <= 0f) {
                tutorialNavigationWidth = 188f;
            }
            if (tutorialNavButtonHeight <= 0f) {
                tutorialNavButtonHeight = 36f;
            }
            if (tutorialNavSpacing < 0f) {
                tutorialNavSpacing = 4f;
            }
            if (faqAnswerIndent < 0f) {
                faqAnswerIndent = 12f;
            }
            if (faqItemSpacing < 0f) {
                faqItemSpacing = 6f;
            }
            if (defaultCloseLabel == null || defaultCloseLabel.isBlank()) {
                defaultCloseLabel = "Close";
            }
        }
    }

    public static class ContentPaths {
        public String calendarPath = "menu/content/calendar.json";
        public String donatePath = "menu/content/donate.json";
        public String socialPath = "menu/content/social_media.json";
        public String aboutPath = "menu/content/about.json";
        public String faqPath = "menu/content/faq.json";
        public String tutorialPath = "menu/content/tutorial.json";

        public void sanitize() {
            if (calendarPath == null || calendarPath.isBlank()) {
                calendarPath = "menu/content/calendar.json";
            }
            if (donatePath == null || donatePath.isBlank()) {
                donatePath = "menu/content/donate.json";
            }
            if (socialPath == null || socialPath.isBlank()) {
                socialPath = "menu/content/social_media.json";
            }
            if (aboutPath == null || aboutPath.isBlank()) {
                aboutPath = "menu/content/about.json";
            }
            if (faqPath == null || faqPath.isBlank()) {
                faqPath = "menu/content/faq.json";
            }
            if (tutorialPath == null || tutorialPath.isBlank()) {
                tutorialPath = "menu/content/tutorial.json";
            }
        }
    }

    public static class IconConfig {
        public String id = "";
        public String assetPath = "";
        public String action = "none";
        public String popup = "";
        public String url = "";
        public int order;
        public boolean enabled = true;
        public boolean visible = true;
        public float width;
        public float height;
        public float offsetX;
        public float offsetY;
        public float padding;
        public float hoverScale;
        public float hoverOffset;
        public boolean hasPosition;
        public float x;
        public float y;

        public void sanitize() {
            if (id == null) {
                id = "";
            }
            if (assetPath == null) {
                assetPath = "";
            }
            if (action == null || action.isBlank()) {
                action = "none";
            }
            if (popup == null) {
                popup = "";
            }
            if (url == null) {
                url = "";
            }
        }

        public boolean clickable() {
            return enabled && visible && !"none".equalsIgnoreCase(action);
        }
    }
}

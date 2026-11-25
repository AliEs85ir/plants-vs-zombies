package app.view.mainmenu.config;

import com.badlogic.gdx.math.Interpolation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class MainMenuConfig {
    public BackgroundConfig background = new BackgroundConfig();
    public String defaultTab = "game";
    public ContentAreaConfig content = new ContentAreaConfig();
    public NavigationConfig navigation = new NavigationConfig();
    public GameMenuConfig gameMenu = new GameMenuConfig();
    public TabTitleConfig tabTitle = new TabTitleConfig();

    public MainMenuConfig sanitize() {
        if (background == null) {
            background = new BackgroundConfig();
        }
        if (defaultTab == null || defaultTab.isBlank()) {
            defaultTab = "game";
        }
        if (content == null) {
            content = new ContentAreaConfig();
        }
        if (navigation == null) {
            navigation = new NavigationConfig();
        }
        if (gameMenu == null) {
            gameMenu = new GameMenuConfig();
        }
        if (tabTitle == null) {
            tabTitle = new TabTitleConfig();
        }
        background.sanitize();
        defaultTab = defaultTab.trim().toLowerCase(Locale.ROOT);
        content.sanitize();
        navigation.sanitize();
        gameMenu.sanitize();
        tabTitle.sanitize();
        return this;
    }

    public static class BackgroundConfig {
        public String path = "menu/bg/main.png";
        public float sourceWidth = 1920f;
        public float sourceHeight = 1080f;
        public String scaleMode = "cover";
        public float offsetX;
        public float offsetY;

        public void sanitize() {
            if (path == null || path.isBlank()) {
                path = "menu/bg/main.png";
            }
            if (sourceWidth <= 0f) {
                sourceWidth = 1920f;
            }
            if (sourceHeight <= 0f) {
                sourceHeight = 1080f;
            }
            if (scaleMode == null || scaleMode.isBlank()) {
                scaleMode = "cover";
            }
        }
    }

    public static class ContentAreaConfig {
        public float paddingLeft = 48f;
        public float paddingRight = 48f;
        public float paddingTop = 28f;
        public float paddingBottom = 118f;
        public float offsetX;
        public float offsetY;

        public void sanitize() {
            paddingLeft = Math.max(0f, paddingLeft);
            paddingRight = Math.max(0f, paddingRight);
            paddingTop = Math.max(0f, paddingTop);
            paddingBottom = Math.max(0f, paddingBottom);
        }
    }

    public static class NavigationConfig {
        public NavigationLayoutConfig layout = new NavigationLayoutConfig();
        public NavigationAppearanceConfig appearance = new NavigationAppearanceConfig();
        public UnreadWarningConfig unreadWarning = new UnreadWarningConfig();
        public ArrayList<NavIconConfig> icons = new ArrayList<>();

        public void sanitize() {
            if (layout == null) {
                layout = new NavigationLayoutConfig();
            }
            if (appearance == null) {
                appearance = new NavigationAppearanceConfig();
            }
            if (unreadWarning == null) {
                unreadWarning = new UnreadWarningConfig();
            }
            if (icons == null) {
                icons = new ArrayList<>();
            }
            layout.sanitize();
            appearance.sanitize();
            unreadWarning.sanitize();
            for (NavIconConfig icon : icons) {
                if (icon != null) {
                    icon.sanitize();
                }
            }
            icons.removeIf(icon -> icon == null || !icon.visible || icon.id.isBlank());
            if (icons.isEmpty()) {
                icons.add(icon("back", "menu/icon/back.png", "go_start_menu", "", 0));
                icons.add(icon("news", "menu/icon/news.png", "show_tab", "news", 1));
                icons.add(icon("game", "menu/icon/game.png", "show_tab", "game", 2));
                icons.add(icon("profile", "menu/icon/profile.png", "show_tab", "profile", 3));
                icons.add(icon("settings", "menu/icon/settings.png", "show_tab", "settings", 4));
            }
            icons.sort(Comparator.comparingInt(icon -> icon.order));
        }

        private static NavIconConfig icon(String id, String path, String action, String tab, int order) {
            NavIconConfig icon = new NavIconConfig();
            icon.id = id;
            icon.assetPath = path;
            icon.action = action;
            icon.tab = tab;
            icon.order = order;
            icon.sanitize();
            return icon;
        }

        public List<NavIconConfig> visibleIcons() {
            List<NavIconConfig> visible = new ArrayList<>();
            for (NavIconConfig icon : icons) {
                if (icon != null && icon.visible) {
                    visible.add(icon);
                }
            }
            return visible;
        }
    }

    public static class NavigationLayoutConfig {
        public float startX = 160f;
        public float endX = 1120f;
        public float bottomOffset = 18f;
        public float iconWidth = 72f;
        public float iconHeight = 72f;
        public float scale = 1f;
        public float iconSpacing;
        public float horizontalOffset;
        public float verticalOffset;
        public String alignment = "center";

        public void sanitize() {
            if (endX < startX) {
                float swap = startX;
                startX = endX;
                endX = swap;
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
            if (alignment == null || alignment.isBlank()) {
                alignment = "center";
            }
        }

        public float drawWidth() {
            return iconWidth * scale;
        }

        public float drawHeight() {
            return iconHeight * scale;
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

    public static class NavigationAppearanceConfig {
        public float normalDarkness = 0.35f;
        public float selectedDarkness;
        public float selectedScaleIncrease = 0.05f;
        public float selectedVerticalOffset = 10f;
        public float hoverScale = 1.04f;
        public float pressedScale = 0.96f;
        public float hoverDuration = 0.08f;
        public float selectedDuration = 0.12f;
        public String easing = "sineOut";

        public void sanitize() {
            normalDarkness = clamp01(normalDarkness, 0.35f);
            selectedDarkness = clamp01(selectedDarkness, 0f);
            if (selectedScaleIncrease < 0f) {
                selectedScaleIncrease = 0.05f;
            }
            if (hoverScale <= 0f) {
                hoverScale = 1.04f;
            }
            if (pressedScale <= 0f) {
                pressedScale = 0.96f;
            }
            if (hoverDuration < 0f) {
                hoverDuration = 0.08f;
            }
            if (selectedDuration < 0f) {
                selectedDuration = 0.12f;
            }
            if (easing == null || easing.isBlank()) {
                easing = "sineOut";
            }
        }

        public float selectedScale() {
            return 1f + selectedScaleIncrease;
        }

        public Interpolation interpolation() {
            return MainMenuEasings.parse(easing);
        }
    }

    public static class UnreadWarningConfig {
        public String assetPath = "menu/icon/warning.png";
        public boolean enabled = true;
        public float size = 26f;
        public float anchorOffsetX = 18f;
        public float anchorOffsetY = 18f;
        public float pulseMinScale = 0.94f;
        public float pulseMaxScale = 1.06f;
        public float pulseMinAlpha = 0.8f;
        public float pulseMaxAlpha = 1.0f;
        public float pulseDurationSec = 1.1f;

        public void sanitize() {
            if (assetPath == null || assetPath.isBlank()) {
                assetPath = "menu/icon/warning.png";
            }
            if (size <= 0f) {
                size = 26f;
            }
            if (pulseMinScale <= 0f) {
                pulseMinScale = 0.94f;
            }
            if (pulseMaxScale <= pulseMinScale) {
                pulseMaxScale = pulseMinScale + 0.08f;
            }
            if (pulseMinAlpha < 0f) {
                pulseMinAlpha = 0f;
            }
            if (pulseMaxAlpha > 1f) {
                pulseMaxAlpha = 1f;
            }
            if (pulseMaxAlpha < pulseMinAlpha) {
                pulseMaxAlpha = pulseMinAlpha;
            }
            if (pulseDurationSec <= 0f) {
                pulseDurationSec = 1.1f;
            }
        }
    }

    public static class NavIconConfig {
        public String id = "";
        public String assetPath = "";
        public String action = "none";
        public String tab = "";
        public int order;
        public boolean enabled = true;
        public boolean visible = true;
        public float width;
        public float height;
        public float offsetX;
        public float offsetY;

        public void sanitize() {
            if (id == null) {
                id = "";
            } else {
                id = id.trim().toLowerCase(Locale.ROOT);
            }
            if (assetPath == null) {
                assetPath = "";
            }
            if (action == null || action.isBlank()) {
                action = "none";
            } else {
                action = action.trim().toLowerCase(Locale.ROOT);
            }
            if (tab == null) {
                tab = "";
            } else {
                tab = tab.trim().toLowerCase(Locale.ROOT);
            }
        }

        public boolean clickable() {
            return enabled && visible && !"none".equalsIgnoreCase(action);
        }
    }

    public static class GameMenuConfig {
        public GameMenuLayoutConfig layout = new GameMenuLayoutConfig();
        public GameMenuAppearanceConfig appearance = new GameMenuAppearanceConfig();
        public ArrayList<GameCardConfig> cards = new ArrayList<>();

        public void sanitize() {
            if (layout == null) {
                layout = new GameMenuLayoutConfig();
            }
            if (appearance == null) {
                appearance = new GameMenuAppearanceConfig();
            }
            if (cards == null) {
                cards = new ArrayList<>();
            }
            layout.sanitize();
            appearance.sanitize();
            for (GameCardConfig card : cards) {
                if (card != null) {
                    card.sanitize();
                }
            }
            cards.removeIf(card -> card == null || !card.visible || card.id.isBlank());
            if (cards.isEmpty()) {
                cards.add(card("adventure", "menu/image/adventure.png", "open_adventure", 0));
                cards.add(card("mini_game", "menu/image/mini_game.png", "open_minigame", 1));
                cards.add(card("server/network", "menu/image/network.png", "open_network", 2));
            }
            cards.sort(Comparator.comparingInt(card -> card.order));
        }

        private static GameCardConfig card(String id, String path, String action, int order) {
            GameCardConfig card = new GameCardConfig();
            card.id = id;
            card.assetPath = path;
            card.action = action;
            card.order = order;
            card.sanitize();
            return card;
        }

        public List<GameCardConfig> visibleCards() {
            List<GameCardConfig> visible = new ArrayList<>();
            for (GameCardConfig card : cards) {
                if (card != null && card.visible) {
                    visible.add(card);
                }
            }
            return visible;
        }
    }

    public static class GameMenuLayoutConfig {
        public float firstCardX;
        public float firstCardY = 430f;
        public float cardWidth = 860f;
        public float cardHeight = 220f;
        public float scale = 1f;
        public float cardSpacing;
        public float cardOverlap = 78f;
        public float horizontalOffset;
        public float verticalOffset;
        public String alignment = "center";
        public boolean keepAspectRatio = true;

        public void sanitize() {
            if (cardWidth <= 0f) {
                cardWidth = 860f;
            }
            if (cardHeight <= 0f) {
                cardHeight = 220f;
            }
            if (scale <= 0f) {
                scale = 1f;
            }
            if (alignment == null || alignment.isBlank()) {
                alignment = "center";
            }
        }

        public float drawWidth() {
            return cardWidth * scale;
        }

        public float drawHeight() {
            return cardHeight * scale;
        }

        public float stepY() {
            return Math.max(1f, drawHeight() - cardOverlap + cardSpacing);
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

    public static class GameMenuAppearanceConfig {
        public float normalDarkness = 0.35f;
        public float hoverDarkness;
        public float hoverScale = 1.06f;
        public float hoverOffsetX;
        public float hoverOffsetY = 8f;
        public float hoverDuration = 0.12f;
        public String easing = "sineOut";

        public void sanitize() {
            normalDarkness = clamp01(normalDarkness, 0.35f);
            hoverDarkness = clamp01(hoverDarkness, 0f);
            if (hoverScale <= 0f) {
                hoverScale = 1.06f;
            }
            if (hoverDuration < 0f) {
                hoverDuration = 0.12f;
            }
            if (easing == null || easing.isBlank()) {
                easing = "sineOut";
            }
        }

        public Interpolation interpolation() {
            return MainMenuEasings.parse(easing);
        }
    }

    public static class GameCardConfig {
        public String id = "";
        public String assetPath = "";
        public String action = "none";
        public int order;
        public boolean enabled = true;
        public boolean visible = true;
        public float width;
        public float height;
        public float offsetX;
        public float offsetY;

        public void sanitize() {
            if (id == null) {
                id = "";
            } else {
                id = id.trim().toLowerCase(Locale.ROOT);
            }
            if (assetPath == null) {
                assetPath = "";
            }
            if (action == null || action.isBlank()) {
                action = "none";
            } else {
                action = action.trim().toLowerCase(Locale.ROOT);
            }
        }

        public boolean clickable() {
            return enabled && visible;
        }
    }

    public static class TabTitleConfig {
        public TabTitleLayoutConfig layout = new TabTitleLayoutConfig();
        public TabTitleTabConfig news = new TabTitleTabConfig();
        public TabTitleTabConfig game = new TabTitleTabConfig();
        public TabTitleTabConfig profile = new TabTitleTabConfig();
        public TabTitleTabConfig settings = new TabTitleTabConfig();

        public void sanitize() {
            if (layout == null) {
                layout = new TabTitleLayoutConfig();
            }
            if (news == null) {
                news = new TabTitleTabConfig();
            }
            if (game == null) {
                game = new TabTitleTabConfig();
            }
            if (profile == null) {
                profile = new TabTitleTabConfig();
            }
            if (settings == null) {
                settings = new TabTitleTabConfig();
            }
            layout.sanitize();
            news.sanitize("menu/image/news_title.png");
            game.sanitize("menu/image/game_title.png");
            profile.sanitize("menu/image/profile_title.png");
            settings.sanitize("menu/image/options_title.png");
        }

        public TabTitleTabConfig tab(String id) {
            if (id == null) {
                return game;
            }
            return switch (id.trim().toLowerCase(Locale.ROOT)) {
                case "news" -> news;
                case "profile" -> profile;
                case "settings" -> settings;
                default -> game;
            };
        }
    }

    public static class TabTitleLayoutConfig {
        public float titleWidth = 480f;
        public float titleHeight = 78f;
        public float titleScale = 1f;
        public float titleOffsetX;
        public float titleOffsetY;
        public float titleTopPadding = 10f;
        public float titleBottomPadding = 16f;
        public String titleAlignment = "top";
        public boolean keepAspectRatio = true;

        public void sanitize() {
            if (titleWidth <= 0f) {
                titleWidth = 480f;
            }
            if (titleHeight <= 0f) {
                titleHeight = 78f;
            }
            if (titleScale <= 0f) {
                titleScale = 1f;
            }
            if (titleAlignment == null || titleAlignment.isBlank()) {
                titleAlignment = "top";
            }
        }

        public float boxWidth() {
            return titleWidth * titleScale;
        }

        public float boxHeight() {
            return titleHeight * titleScale;
        }

        public float occupiedHeight() {
            return titleTopPadding + boxHeight() + titleBottomPadding;
        }
    }

    public static class TabTitleTabConfig {
        public String assetPath = "";
        public float titleWidth;
        public float titleHeight;
        public float titleScale;
        public float titleOffsetX;
        public float titleOffsetY;

        public void sanitize(String fallbackPath) {
            if (assetPath == null || assetPath.isBlank()) {
                assetPath = fallbackPath;
            }
        }
    }

    private static float clamp01(float value, float fallback) {
        if (Float.isNaN(value)) {
            return fallback;
        }
        if (value < 0f) {
            return 0f;
        }
        if (value > 1f) {
            return 1f;
        }
        return value;
    }
}

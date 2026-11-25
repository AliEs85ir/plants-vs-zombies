package app.view.quest;

import app.enums.quest.QuestName;
import app.view.adventure.AdventureMenuConfig;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Align;
import game.entity.quest.Quest;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class QuestMenuConfig {
    public OverlayConfig overlay = new OverlayConfig();
    public PanelConfig panel = new PanelConfig();
    public CloseButtonConfig closeButton = new CloseButtonConfig();
    public TabsConfig tabs = new TabsConfig();
    public TimerConfig timer = new TimerConfig();
    public ListConfig list = new ListConfig();
    public CardConfig card = new CardConfig();
    public ArrayList<String> progressStyles = defaultProgressStyles();
    public ArrayList<QuestPresentationConfig> quests = defaultQuests();

    public QuestMenuConfig sanitize() {
        if (overlay == null) {
            overlay = new OverlayConfig();
        }
        if (panel == null) {
            panel = new PanelConfig();
        }
        if (closeButton == null) {
            closeButton = new CloseButtonConfig();
        }
        if (tabs == null) {
            tabs = new TabsConfig();
        }
        if (timer == null) {
            timer = new TimerConfig();
        }
        if (list == null) {
            list = new ListConfig();
        }
        if (card == null) {
            card = new CardConfig();
        }
        if (progressStyles == null || progressStyles.isEmpty()) {
            progressStyles = defaultProgressStyles();
        }
        if (quests == null) {
            quests = defaultQuests();
        }
        overlay.sanitize();
        panel.sanitize();
        closeButton.sanitize();
        tabs.sanitize();
        timer.sanitize();
        list.sanitize();
        card.sanitize();
        progressStyles.removeIf(style -> style == null || style.isBlank());
        if (progressStyles.isEmpty()) {
            progressStyles = defaultProgressStyles();
        }
        quests.removeIf(item -> item == null);
        for (QuestPresentationConfig item : quests) {
            item.sanitize();
        }
        return this;
    }

    public QuestPresentationConfig presentationFor(Quest quest) {
        if (quest == null || quest.getName() == null) {
            return QuestPresentationConfig.fallback();
        }
        return presentationFor(quest.getName());
    }

    public QuestPresentationConfig presentationFor(QuestName name) {
        if (name == null) {
            return QuestPresentationConfig.fallback();
        }
        for (QuestPresentationConfig item : quests) {
            if (item.matches(name)) {
                return item;
            }
        }
        return QuestPresentationConfig.fallback();
    }

    public boolean showsQuest(Quest quest) {
        if (quest == null || quest.getName() == null) {
            return false;
        }
        if (list.showUnlistedDailyQuests) {
            return true;
        }
        for (QuestPresentationConfig item : quests) {
            if (item.matches(quest.getName())) {
                return true;
            }
        }
        return false;
    }

    public String resolvedProgressStyle(Quest quest, QuestPresentationConfig presentation) {
        if (presentation != null && presentation.progressStyle != null && !presentation.progressStyle.isBlank()) {
            return presentation.progressStyle;
        }
        if (progressStyles.isEmpty()) {
            return "xp_green";
        }
        int seed = quest == null || quest.getName() == null ? 0 : quest.getName().ordinal();
        return progressStyles.get(Math.floorMod(seed, progressStyles.size()));
    }

    public static Color color(String hex, Color fallback) {
        return AdventureMenuConfig.color(hex, fallback);
    }

    public static int textAlign(String value) {
        if (value == null || value.isBlank()) {
            return Align.left;
        }
        return switch (value.trim().toLowerCase(Locale.ROOT)) {
            case "right" -> Align.right;
            case "center" -> Align.center;
            default -> Align.left;
        };
    }

    static String text(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value;
    }

    static float positive(float value, float fallback) {
        if (Float.isNaN(value) || value <= 0f) {
            return fallback;
        }
        return value;
    }

    static float finite(float value, float fallback) {
        if (Float.isNaN(value) || Float.isInfinite(value)) {
            return fallback;
        }
        return value;
    }

    static float clamp01(float value, float fallback) {
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

    private static ArrayList<String> defaultProgressStyles() {
        ArrayList<String> styles = new ArrayList<>();
        styles.add("xp_green");
        styles.add("xp_fuschia");
        styles.add("default-horizontal");
        styles.add("xp_yellow");
        styles.add("xp_teal");
        return styles;
    }

    private static ArrayList<QuestPresentationConfig> defaultQuests() {
        ArrayList<QuestPresentationConfig> items = new ArrayList<>();
        items.add(QuestPresentationConfig.sunCollector());
        items.add(QuestPresentationConfig.listed(QuestName.WIN_STREAK));
        items.add(QuestPresentationConfig.listed(QuestName.PLANT_EXPERT));
        items.add(QuestPresentationConfig.listed(QuestName.CACTUS_ONLY));
        items.add(QuestPresentationConfig.listed(QuestName.DEMOLITION_EXPERT));
        items.add(QuestPresentationConfig.listed(QuestName.SYMMETRY));
        items.add(QuestPresentationConfig.listed(QuestName.FAMILY_HUNTER));
        items.add(QuestPresentationConfig.listed(QuestName.LIMITED_BLOOM));
        items.add(QuestPresentationConfig.listed(QuestName.OCD_MASTER));
        items.add(QuestPresentationConfig.listed(QuestName.CLOUDY_DAY));
        items.add(QuestPresentationConfig.listed(QuestName.ONE_COLUMN_LESS));
        items.add(QuestPresentationConfig.listed(QuestName.DEFENSELESS_ROW));
        items.add(QuestPresentationConfig.listed(QuestName.DEFENSELESS_CROSS));
        items.add(QuestPresentationConfig.listed(QuestName.ECONOMICAL_GARDENER));
        items.add(QuestPresentationConfig.listed(QuestName.DEFENSE_MASTER));
        items.add(QuestPresentationConfig.listed(QuestName.QUICK_ACTION));
        items.add(QuestPresentationConfig.listed(QuestName.DAY_OR_NIGHT));
        items.add(QuestPresentationConfig.listed(QuestName.LAWN_MOWER_TIME));
        return items;
    }

    public static class OverlayConfig {
        public String color = "0000008C";
        public boolean closeOnOutsideClick;

        public void sanitize() {
            color = text(color, "0000008C");
        }
    }

    public static class PanelConfig {
        public String backgroundDrawable = "image_ui_quests_panel_edge_to_edge_ten";
        public String backgroundFallback = "image_ui_quests_panel_edge_to_edge_ten";
        public float widthRatio = 0.72f;
        public float heightRatio = 0.74f;
        public float minWidth = 720f;
        public float minHeight = 420f;
        public float offsetX;
        public float offsetY = 12f;
        public float padTop = 18f;
        public float padLeft = 22f;
        public float padBottom = 18f;
        public float padRight = 18f;

        public void sanitize() {
            backgroundDrawable = text(backgroundDrawable, "image_ui_quests_panel_edge_to_edge_ten");
            backgroundFallback = text(backgroundFallback, "image_ui_quests_panel_edge_to_edge_ten");
            widthRatio = clamp01(widthRatio, 0.72f);
            if (widthRatio <= 0.2f) {
                widthRatio = 0.72f;
            }
            heightRatio = clamp01(heightRatio, 0.74f);
            if (heightRatio <= 0.2f) {
                heightRatio = 0.74f;
            }
            minWidth = positive(minWidth, 720f);
            minHeight = positive(minHeight, 420f);
            offsetX = finite(offsetX, 0f);
            offsetY = finite(offsetY, 12f);
            padTop = finite(padTop, 18f);
            padLeft = finite(padLeft, 22f);
            padBottom = finite(padBottom, 18f);
            padRight = finite(padRight, 18f);
        }
    }

    public static class CloseButtonConfig {
        public String skinStyle = "generic_close";
        public float width = 44f;
        public float height = 44f;
        public float offsetX = -6f;
        public float offsetY = -6f;

        public void sanitize() {
            skinStyle = text(skinStyle, "generic_close");
            width = positive(width, 44f);
            height = positive(height, 44f);
            offsetX = finite(offsetX, -6f);
            offsetY = finite(offsetY, -6f);
        }
    }

    public static class TabsConfig {
        public float height = 40f;
        public float spacing = 8f;
        public float offsetX;
        public float offsetY;
        public float padRightForClose = 52f;
        public TabConfig daily = TabConfig.daily();
        public TabConfig world = TabConfig.world();
        public TabConfig events = TabConfig.events();

        public void sanitize() {
            height = positive(height, 40f);
            spacing = finite(spacing, 8f);
            offsetX = finite(offsetX, 0f);
            offsetY = finite(offsetY, 0f);
            padRightForClose = finite(padRightForClose, 52f);
            if (daily == null) {
                daily = TabConfig.daily();
            }
            if (world == null) {
                world = TabConfig.world();
            }
            if (events == null) {
                events = TabConfig.events();
            }
            daily.sanitize("DAILY", "DAILY", "green", "green");
            world.sanitize("WORLD", "WORLD", "brown", "green");
            events.sanitize("EVENTS", "EVENTS", "blue", "green");
        }

        public List<TabConfig> ordered() {
            return List.of(daily, world, events);
        }
    }

    public static class TabConfig {
        public String id = "DAILY";
        public String text = "DAILY";
        public String style = "green";
        public String selectedStyle = "green";

        public static TabConfig daily() {
            TabConfig tab = new TabConfig();
            tab.id = "DAILY";
            tab.text = "DAILY";
            tab.style = "green";
            tab.selectedStyle = "green";
            return tab;
        }

        public static TabConfig world() {
            TabConfig tab = new TabConfig();
            tab.id = "WORLD";
            tab.text = "WORLD";
            tab.style = "brown";
            tab.selectedStyle = "green";
            return tab;
        }

        public static TabConfig events() {
            TabConfig tab = new TabConfig();
            tab.id = "EVENTS";
            tab.text = "EVENTS";
            tab.style = "blue";
            tab.selectedStyle = "green";
            return tab;
        }

        public void sanitize(String fallbackId, String fallbackText, String fallbackStyle, String fallbackSelected) {
            id = text(id, fallbackId).trim().toUpperCase(Locale.ROOT);
            text = text(text, fallbackText);
            style = text(style, fallbackStyle);
            selectedStyle = text(selectedStyle, fallbackSelected);
        }
    }

    public static class TimerConfig {
        public String text = "Daily Activities refresh in {time}";
        public String font = "BRIANNETOD";
        public String fontFallback = "BRIANNETOD";
        public float fontSize = 0.82f;
        public String color = "F8D84A";
        public String alignment = "left";
        public float offsetX = 4f;
        public float offsetY;
        public float height = 28f;
        public float topPadding = 8f;

        public void sanitize() {
            text = QuestMenuConfig.text(text, "Daily Activities refresh in {time}");
            font = QuestMenuConfig.text(font, "BRIANNETOD");
            fontFallback = QuestMenuConfig.text(fontFallback, "BRIANNETOD");
            fontSize = positive(fontSize, 0.82f);
            color = text(color, "F8D84A");
            alignment = text(alignment, "left");
            offsetX = finite(offsetX, 4f);
            offsetY = finite(offsetY, 0f);
            height = positive(height, 28f);
            topPadding = finite(topPadding, 8f);
        }
    }

    public static class ListConfig {
        public float spacing = 10f;
        public float padTop = 8f;
        public float padBottom = 8f;
        public String emptyDailyMessage = "No daily quests right now.";
        public String emptyWorldMessage = "";
        public String emptyEventsMessage = "No event quests right now.";
        public String emptyFont = "BRIANNETOD";
        public String emptyFontFallback = "BRIANNETOD";
        public float emptyFontSize = 0.86f;
        public String emptyColor = "E4DCC4";
        public boolean showUnlistedDailyQuests;

        public void sanitize() {
            spacing = finite(spacing, 10f);
            padTop = finite(padTop, 8f);
            padBottom = finite(padBottom, 8f);
            if (emptyDailyMessage == null) {
                emptyDailyMessage = "No daily quests right now.";
            }
            if (emptyWorldMessage == null) {
                emptyWorldMessage = "";
            }
            if (emptyEventsMessage == null) {
                emptyEventsMessage = "No event quests right now.";
            }
            emptyFont = text(emptyFont, "BRIANNETOD");
            emptyFontFallback = text(emptyFontFallback, "BRIANNETOD");
            emptyFontSize = positive(emptyFontSize, 0.86f);
            emptyColor = text(emptyColor, "E4DCC4");
        }
    }

    public static class CardConfig {
        public String backgroundDrawable = "image_ui_powerups_powerup_cost_10";
        public String backgroundFallback = "image_ui_powerups_powerup_cost_10";
        public float height = 110f;
        public float padLeft = 10f;
        public float padRight = 10f;
        public float padTop = 8f;
        public float padBottom = 8f;
        public float iconSize = 64f;
        public float iconToTextGap = 12f;
        public String titleFont = "BRIANNETOD";
        public String titleFontFallback = "BRIANNETOD";
        public float titleFontSize = 0.86f;
        public String titleColor = "2A1C10";
        public String titleAlignment = "left";
        public float progressHeight = 20f;
        public float progressToRewardGap = 10f;
        public float rewardSize = 42f;
        public float claimWidth = 118f;
        public float claimHeight = 42f;
        public String claimReadyStyle = "green";
        public String claimLockedStyle = "brown";
        public String claimText = "CLAIM";
        public float claimLabelScale = 0.92f;
        public String progressLabelFont = "BRIANNETOD";
        public String progressLabelFontFallback = "BRIANNETOD";
        public float progressLabelFontSize = 0.72f;
        public String progressLabelColor = "FFFFFF";

        public void sanitize() {
            backgroundDrawable = text(backgroundDrawable, "image_ui_powerups_powerup_cost_10");
            backgroundFallback = text(backgroundFallback, "image_ui_powerups_powerup_cost_10");
            height = positive(height, 110f);
            padLeft = finite(padLeft, 10f);
            padRight = finite(padRight, 10f);
            padTop = finite(padTop, 8f);
            padBottom = finite(padBottom, 8f);
            iconSize = positive(iconSize, 64f);
            iconToTextGap = finite(iconToTextGap, 12f);
            titleFont = text(titleFont, "BRIANNETOD");
            titleFontFallback = text(titleFontFallback, "BRIANNETOD");
            titleFontSize = positive(titleFontSize, 0.86f);
            titleColor = text(titleColor, "2A1C10");
            titleAlignment = text(titleAlignment, "left");
            progressHeight = positive(progressHeight, 20f);
            progressToRewardGap = finite(progressToRewardGap, 10f);
            rewardSize = positive(rewardSize, 42f);
            claimWidth = positive(claimWidth, 118f);
            claimHeight = positive(claimHeight, 42f);
            claimReadyStyle = text(claimReadyStyle, "green");
            claimLockedStyle = text(claimLockedStyle, "brown");
            claimText = text(claimText, "CLAIM");
            claimLabelScale = positive(claimLabelScale, 0.92f);
            progressLabelFont = text(progressLabelFont, "BRIANNETOD");
            progressLabelFontFallback = text(progressLabelFontFallback, "BRIANNETOD");
            progressLabelFontSize = positive(progressLabelFontSize, 0.72f);
            progressLabelColor = text(progressLabelColor, "FFFFFF");
        }
    }

    public static class QuestPresentationConfig {
        public String id = "";
        public String title = "";
        public String leftImagePath = "";
        public String rightImagePath = "";
        public String progressStyle = "";
        public String backgroundDrawable = "";

        public static QuestPresentationConfig sunCollector() {
            QuestPresentationConfig config = new QuestPresentationConfig();
            config.id = QuestName.DAILY_SUN_COLLECTOR.name();
            config.title = "Collect sun throughout the day!";
            return config;
        }

        public static QuestPresentationConfig listed(QuestName name) {
            QuestPresentationConfig config = new QuestPresentationConfig();
            config.id = name == null ? "" : name.name();
            config.title = "";
            return config;
        }

        public static QuestPresentationConfig fallback() {
            return new QuestPresentationConfig();
        }

        public void sanitize() {
            if (id == null) {
                id = "";
            }
            if (title == null) {
                title = "";
            }
            if (leftImagePath == null) {
                leftImagePath = "";
            }
            if (rightImagePath == null) {
                rightImagePath = "";
            }
            if (progressStyle == null) {
                progressStyle = "";
            }
            if (backgroundDrawable == null) {
                backgroundDrawable = "";
            }
        }

        public boolean matches(QuestName name) {
            return name != null && name.name().equalsIgnoreCase(id);
        }

        public String resolvedTitle(Quest quest) {
            if (title != null && !title.isBlank()) {
                return title;
            }
            if (quest == null) {
                return "";
            }
            if (quest.getTitle() != null && !quest.getTitle().isBlank()
                && (quest.getDescription() == null || quest.getDescription().isBlank())) {
                return quest.getTitle();
            }
            if (quest.getDescription() != null && !quest.getDescription().isBlank()) {
                return quest.getDescription();
            }
            return quest.getTitle() == null ? "" : quest.getTitle();
        }

        public String resolvedBackground(CardConfig card) {
            if (backgroundDrawable != null && !backgroundDrawable.isBlank()) {
                return backgroundDrawable;
            }
            return card.backgroundDrawable;
        }
    }
}

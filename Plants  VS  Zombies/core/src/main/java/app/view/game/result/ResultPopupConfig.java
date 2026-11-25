package app.view.game.result;

import app.view.adventure.AdventureMenuConfig;
import app.view.game.pause.PauseLayout;
import app.view.ui.PanelStyle;
import app.view.ui.UiTheme;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Align;

public final class ResultPopupConfig {
    public Overlay overlay = new Overlay();
    public Spacing spacing = new Spacing();
    public Size size = new Size();
    public Animation animation = new Animation();
    public Backgrounds backgrounds = new Backgrounds();
    public String headerIcon = "IMAGE_UI_PAUSEMENU_STAR_GOAL_BORDER";
    public String messageColor = "000000";
    public Stats stats = new Stats();
    public Dropdown dropdown = new Dropdown();
    public Cards cards = new Cards();
    public Variant win = new Variant();
    public Variant lose = new Variant();

    public ResultPopupConfig sanitize() {
        if (overlay == null) overlay = new Overlay();
        if (spacing == null) spacing = new Spacing();
        if (size == null) size = new Size();
        if (animation == null) animation = new Animation();
        if (backgrounds == null) backgrounds = new Backgrounds();
        if (stats == null) stats = new Stats();
        if (dropdown == null) dropdown = new Dropdown();
        if (cards == null) cards = new Cards();
        if (win == null) win = new Variant();
        if (lose == null) lose = new Variant();
        overlay.sanitize();
        spacing.sanitize();
        size.sanitize();
        animation.sanitize();
        backgrounds.sanitize();
        stats.sanitize();
        dropdown.sanitize();
        cards.sanitize();
        win.sanitizeWin();
        lose.sanitizeLose();
        headerIcon = text(headerIcon, "IMAGE_UI_PAUSEMENU_STAR_GOAL_BORDER");
        messageColor = text(messageColor, "000000");
        return this;
    }

    public ResultLayout toLayout(boolean isWin) {
        sanitize();
        ResultLayout layout = new ResultLayout();
        Variant variant = isWin ? win : lose;
        layout.panelWidth = variant.panelWidth;
        layout.panelHeight = variant.panelHeight;
        layout.cardScale = cards.scale;
        layout.plantColumns = cards.plantColumns;
        layout.zombieColumns = cards.zombieColumns;
        layout.dropdownBodyPad = dropdown.bodyPad;
        layout.dropdownGap = dropdown.gap;
        layout.dropdownHeaderHeight = dropdown.headerHeight;
        layout.dropdownContentMaxHeight = dropdown.contentMaxHeight;
        layout.cardPad = cards.pad;
        layout.rewardCardScale = cards.rewardCardScale;
        layout.captionPadTop = cards.captionPadTop;
        layout.questRowGap = cards.questRowGap;
        layout.rewardChipPadX = cards.rewardChipPadX;
        layout.rewardChipPadY = cards.rewardChipPadY;
        layout.rewardCardGap = cards.rewardCardGap;
        layout.dropdownCountFormat = dropdown.countFormat;
        layout.dropdownExpandedMark = dropdown.expandedMark;
        layout.dropdownCollapsedMark = dropdown.collapsedMark;
        layout.texts = variant;
        applyPause(layout.pause, variant);
        applyStats(layout.stats);
        return layout;
    }

    private void applyPause(PauseLayout pause, Variant variant) {
        pause.overlay.opacity = overlay.opacity;
        pause.overlay.color.set(AdventureMenuConfig.color(overlay.color, Color.BLACK));
        pause.spacing.contentPadding = spacing.contentPadding;
        pause.spacing.innerPadding = spacing.innerPadding;
        pause.spacing.tightPadding = spacing.tightPadding;
        pause.spacing.sectionSpacing = spacing.sectionSpacing;
        pause.spacing.headerSpacing = spacing.headerSpacing;
        pause.spacing.bottomPadding = spacing.bottomPadding;
        pause.spacing.separatorHeight = spacing.separatorHeight;
        pause.spacing.separatorGap = spacing.separatorGap;
        pause.header.paddingBottom = spacing.headerPaddingBottom;
        pause.header.separatorGap = spacing.separatorGap;
        pause.size.scale = size.scale;
        pause.size.headerHeight = size.headerHeight;
        pause.size.footerHeight = size.footerHeight;
        pause.header.sectionIconSize = size.sectionIconSize;
        pause.header.sectionIconGap = size.sectionIconGap;
        pause.header.sectionHeaderHeight = size.sectionHeaderHeight;
        pause.header.title = variant.title;
        pause.animation.fadeIn = animation.fadeIn;
        pause.animation.fadeOut = animation.fadeOut;
        pause.animation.startScale = animation.startScale;
        pause.animation.scaleDuration = animation.scaleDuration;
        pause.icons.header = headerIcon;
        pause.text.channel.color = AdventureMenuConfig.color(messageColor, Color.BLACK);
        pause.buttons.actions.width = variant.buttonWidth;
        pause.buttons.actions.height = variant.buttonHeight;
        assign(pause.backgrounds.popupRoot, backgrounds.popupRoot, backgrounds.popupRootFallback);
        assign(pause.backgrounds.header, backgrounds.header, backgrounds.headerFallback);
        assign(pause.backgrounds.section, backgrounds.section, backgrounds.sectionFallback);
        assign(pause.backgrounds.statField, backgrounds.statField, backgrounds.statFieldFallback);
        assign(pause.backgrounds.separator, backgrounds.separator, backgrounds.separatorFallback);
    }

    private void applyStats(ResultLayout.Stats target) {
        target.iconBox = stats.iconBox;
        target.chipHeight = stats.chipHeight;
        target.chipSpacing = stats.chipSpacing;
        target.chipPadX = stats.chipPadX;
        target.chipPadY = stats.chipPadY;
        target.iconTextGap = stats.iconTextGap;
        target.labelValueGap = stats.labelValueGap;
        target.labelScale = stats.labelScale;
        target.valueScale = stats.valueScale;
        target.labelAlign = align(stats.labelAlign, Align.left);
        target.valueAlign = align(stats.valueAlign, Align.left);
        target.labelColor = AdventureMenuConfig.color(stats.labelColor, Color.WHITE);
        target.valueColor = AdventureMenuConfig.color(stats.valueColor, UiTheme.TEXT_STAT);
        target.zombiesIcon = stats.zombiesIcon;
        target.plantsIcon = stats.plantsIcon;
        target.sunsIcon = stats.sunsIcon;
        target.timeIcon = stats.timeIcon;
        target.zombiesLabel = stats.zombiesLabel;
        target.plantsLabel = stats.plantsLabel;
        target.sunsLabel = stats.sunsLabel;
        target.timeLabel = stats.timeLabel;
    }

    private static void assign(PanelStyle style, String drawable, String fallback) {
        if (style == null) {
            return;
        }
        if (drawable != null && !drawable.isBlank()) {
            style.drawable = drawable;
        }
        if (fallback != null && !fallback.isBlank()) {
            style.fallback = fallback;
        }
    }

    private static int align(String value, int fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return switch (value.trim().toLowerCase()) {
            case "center" -> Align.center;
            case "right" -> Align.right;
            case "top" -> Align.top;
            case "bottom" -> Align.bottom;
            default -> Align.left;
        };
    }

    private static String text(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private static float atLeast(float value, float fallback) {
        return value > 0f ? value : fallback;
    }

    public static final class Overlay {
        public float opacity = 0.78f;
        public String color = "000000";

        void sanitize() {
            if (opacity < 0f) opacity = 0.78f;
            color = text(color, "000000");
        }
    }

    public static final class Spacing {
        public float contentPadding = 16f;
        public float innerPadding = 8f;
        public float tightPadding = 4f;
        public float sectionSpacing = 8f;
        public float headerSpacing = 8f;
        public float bottomPadding = 8f;
        public float separatorHeight = 3f;
        public float separatorGap = 4f;
        public float headerPaddingBottom = 6f;

        void sanitize() {
            contentPadding = Math.max(0f, contentPadding);
            innerPadding = Math.max(0f, innerPadding);
            tightPadding = Math.max(0f, tightPadding);
            sectionSpacing = Math.max(0f, sectionSpacing);
            headerSpacing = Math.max(0f, headerSpacing);
            bottomPadding = Math.max(0f, bottomPadding);
            separatorHeight = Math.max(0f, separatorHeight);
            separatorGap = Math.max(0f, separatorGap);
            headerPaddingBottom = Math.max(0f, headerPaddingBottom);
        }
    }

    public static final class Size {
        public float scale = 1f;
        public float headerHeight = 48f;
        public float footerHeight = 42f;
        public float sectionIconSize = 18f;
        public float sectionIconGap = 6f;
        public float sectionHeaderHeight = 22f;

        void sanitize() {
            scale = atLeast(scale, 1f);
            headerHeight = atLeast(headerHeight, 48f);
            footerHeight = atLeast(footerHeight, 42f);
            sectionIconSize = atLeast(sectionIconSize, 18f);
            sectionIconGap = Math.max(0f, sectionIconGap);
            sectionHeaderHeight = atLeast(sectionHeaderHeight, 22f);
        }
    }

    public static final class Animation {
        public float fadeIn = 0.18f;
        public float fadeOut = 0.12f;
        public float startScale = 0.96f;
        public float scaleDuration = 0.14f;

        void sanitize() {
            fadeIn = Math.max(0f, fadeIn);
            fadeOut = Math.max(0f, fadeOut);
            startScale = atLeast(startScale, 0.96f);
            scaleDuration = Math.max(0f, scaleDuration);
        }
    }

    public static final class Backgrounds {
        public String popupRoot = UiTheme.BG_DIALOG;
        public String popupRootFallback = UiTheme.BG_TAB;
        public String header = UiTheme.BG_RIBBON;
        public String headerFallback = UiTheme.BG_TAB;
        public String section = UiTheme.BG_GUARANTEE;
        public String sectionFallback = UiTheme.BG_TAB;
        public String statField = UiTheme.BG_REWARD;
        public String statFieldFallback = UiTheme.BG_TAB;
        public String separator = UiTheme.BG_COST;
        public String separatorFallback = UiTheme.BG_TAB;

        void sanitize() {
            popupRoot = text(popupRoot, UiTheme.BG_DIALOG);
            popupRootFallback = text(popupRootFallback, UiTheme.BG_TAB);
            header = text(header, UiTheme.BG_RIBBON);
            headerFallback = text(headerFallback, UiTheme.BG_TAB);
            section = text(section, UiTheme.BG_GUARANTEE);
            sectionFallback = text(sectionFallback, UiTheme.BG_TAB);
            statField = text(statField, UiTheme.BG_REWARD);
            statFieldFallback = text(statFieldFallback, UiTheme.BG_TAB);
            separator = text(separator, UiTheme.BG_COST);
            separatorFallback = text(separatorFallback, UiTheme.BG_TAB);
        }
    }

    public static final class Stats {
        public float iconBox = 28f;
        public float chipHeight = 54f;
        public float chipSpacing = 10f;
        public float chipPadX = 10f;
        public float chipPadY = 6f;
        public float iconTextGap = 8f;
        public float labelValueGap = 2f;
        public float labelScale = 0.70f;
        public float valueScale = 0.88f;
        public String labelAlign = "left";
        public String valueAlign = "left";
        public String labelColor = "FFFFFF";
        public String valueColor = "FCE38F";
        public String zombiesIcon;
        public String plantsIcon;
        public String sunsIcon;
        public String timeIcon;
        public String zombiesLabel = "Zombies Killed";
        public String plantsLabel = "Plants Planted";
        public String sunsLabel = "Suns Collected";
        public String timeLabel = "Play Time";

        void sanitize() {
            iconBox = atLeast(iconBox, 28f);
            chipHeight = atLeast(chipHeight, 54f);
            chipSpacing = Math.max(0f, chipSpacing);
            chipPadX = Math.max(0f, chipPadX);
            chipPadY = Math.max(0f, chipPadY);
            iconTextGap = Math.max(0f, iconTextGap);
            labelValueGap = Math.max(0f, labelValueGap);
            labelScale = atLeast(labelScale, 0.70f);
            valueScale = atLeast(valueScale, 0.88f);
            labelAlign = text(labelAlign, "left");
            valueAlign = text(valueAlign, "left");
            labelColor = text(labelColor, "FFFFFF");
            valueColor = text(valueColor, "FCE38F");
            zombiesIcon = text(zombiesIcon, "IMAGE_NOTES_ZOMBIENOTE_DARK");
            plantsIcon = text(plantsIcon, "IMAGE_UI_HUD_INGAME_NOTIFICATION_ICON");
            sunsIcon = text(sunsIcon, "IMAGE_UI_SEASONS_UNCOMPRESSED_PVZ2_SEASONS_UIASSET_ICON_SUN");
            timeIcon = text(timeIcon, "IMAGE_UI_PERKS_RIFT_ICON_TIMEBONUS");
            zombiesLabel = text(zombiesLabel, "Zombies Killed");
            plantsLabel = text(plantsLabel, "Plants Planted");
            sunsLabel = text(sunsLabel, "Suns Collected");
            timeLabel = text(timeLabel, "Play Time");
        }
    }

    public static final class Dropdown {
        public float bodyPad = 8f;
        public float gap = 8f;
        public float headerHeight = 28f;
        public float contentMaxHeight = 200f;
        public String countFormat = "{title}  ({count})";
        public String expandedMark = "  ▴";
        public String collapsedMark = "  ▾";

        void sanitize() {
            bodyPad = Math.max(0f, bodyPad);
            gap = Math.max(0f, gap);
            headerHeight = atLeast(headerHeight, 28f);
            contentMaxHeight = atLeast(contentMaxHeight, 200f);
            countFormat = text(countFormat, "{title}  ({count})");
            expandedMark = text(expandedMark, "  ▴");
            collapsedMark = text(collapsedMark, "  ▾");
        }
    }

    public static final class Cards {
        public float scale = 0.50f;
        public int plantColumns = 6;
        public int zombieColumns = 5;
        public float pad = 4f;
        public float rewardCardScale = 0.425f;
        public float captionPadTop = 2f;
        public float questRowGap = 6f;
        public float rewardChipPadX = 10f;
        public float rewardChipPadY = 6f;
        public float rewardCardGap = 8f;

        void sanitize() {
            scale = atLeast(scale, 0.50f);
            plantColumns = Math.max(1, plantColumns);
            zombieColumns = Math.max(1, zombieColumns);
            pad = Math.max(0f, pad);
            rewardCardScale = atLeast(rewardCardScale, 0.425f);
            captionPadTop = Math.max(0f, captionPadTop);
            questRowGap = Math.max(0f, questRowGap);
            rewardChipPadX = Math.max(0f, rewardChipPadX);
            rewardChipPadY = Math.max(0f, rewardChipPadY);
            rewardCardGap = Math.max(0f, rewardCardGap);
        }
    }

    public static final class Variant {
        public float panelWidth = 780f;
        public float panelHeight = 0f;
        public float buttonWidth = 168f;
        public float buttonHeight = 42f;
        public String leftButtonStyle = UiTheme.BTN_BROWN;
        public String middleButtonStyle = UiTheme.BTN_PURPLE;
        public String rightButtonStyle = UiTheme.BTN_GREEN;
        public String title = "";
        public String message = "";
        public String messageFirstClear = "";
        public String messageBoughtWin = "";
        public String replay = "Replay";
        public String stageSelect = "Stage Select";
        public String nextStage = "Next Stage";
        public String chapterComplete = "Chapter Complete";
        public String retry = "Retry";
        public String dropdownPlants = "Newly Unlocked Plants";
        public String dropdownZombies = "Newly Unlocked Zombies";
        public String dropdownQuests = "Quest Changes";
        public String dropdownRewards = "Rewards";
        public String emptyPlants = "No new plants unlocked.";
        public String emptyZombies = "No new zombies discovered.";
        public String emptyQuests = "No quests updated.";
        public String emptyRewards = "No first-clear rewards.";

        void sanitizeWin() {
            sanitizeShared();
            panelWidth = atLeast(panelWidth, 980f);
            buttonWidth = atLeast(buttonWidth, 176f);
            title = text(title, "YOU WON");
            message = text(message, "Stage cleared. Replay grants no first-clear rewards.");
            messageFirstClear = text(messageFirstClear, "First clear! Review new unlocks and rewards below.");
            messageBoughtWin = text(messageBoughtWin, "Victory purchased. Progression was applied once.");
        }

        void sanitizeLose() {
            sanitizeShared();
            panelWidth = atLeast(panelWidth, 780f);
            buttonWidth = atLeast(buttonWidth, 168f);
            title = text(title, "YOU LOST");
            message = text(message, "The stage is lost. Return to stage select, buy the win, or retry this run.");
        }

        private void sanitizeShared() {
            panelHeight = Math.max(0f, panelHeight);
            buttonHeight = atLeast(buttonHeight, 42f);
            leftButtonStyle = text(leftButtonStyle, UiTheme.BTN_BROWN);
            middleButtonStyle = text(middleButtonStyle, UiTheme.BTN_PURPLE);
            rightButtonStyle = text(rightButtonStyle, UiTheme.BTN_GREEN);
            replay = text(replay, "Replay");
            stageSelect = text(stageSelect, "Stage Select");
            nextStage = text(nextStage, "Next Stage");
            chapterComplete = text(chapterComplete, "Chapter Complete");
            retry = text(retry, "Retry");
            dropdownPlants = text(dropdownPlants, "Newly Unlocked Plants");
            dropdownZombies = text(dropdownZombies, "Newly Unlocked Zombies");
            dropdownQuests = text(dropdownQuests, "Quest Changes");
            dropdownRewards = text(dropdownRewards, "Rewards");
            emptyPlants = text(emptyPlants, "No new plants unlocked.");
            emptyZombies = text(emptyZombies, "No new zombies discovered.");
            emptyQuests = text(emptyQuests, "No quests updated.");
            emptyRewards = text(emptyRewards, "No first-clear rewards.");
        }
    }
}

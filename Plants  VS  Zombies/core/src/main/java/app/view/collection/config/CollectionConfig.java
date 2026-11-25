package app.view.collection.config;

import app.enums.plant.PlantCategory;
import app.view.ui.UiTheme;
import com.badlogic.gdx.graphics.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollectionConfig {
    public String backgroundPath = "menu/bg/collection.png";
    public String titlePath = "menu/image/collection_title.png";
    public String lockIconId = "IMAGE_ZEN_GARDEN_LOCKED_POT_ICON";
    public String defaultDetailsBackground = "IMAGE_UI_CARDS_BACKGROUNDS_CARD_PLANT_BG_MODERN";
    public String boostDetailsBackground = "IMAGE_UI_CARDS_BACKGROUNDS_CARD_PLANT_BG_BOOST";
    public AnchoredConfig title = titleDefaults();
    public PanelConfig panel = new PanelConfig();
    public TopBarConfig topBar = new TopBarConfig();
    public TabsConfig tabs = new TabsConfig();
    public GridConfig plantGrid = plantGridDefaults();
    public GridConfig zombieGrid = zombieGridDefaults();
    public DetailsConfig details = new DetailsConfig();
    public CardChromeConfig card = new CardChromeConfig();
    public CardHoverConfig cardHover = new CardHoverConfig();
    public ScrollPaneConfig scrollPane = new ScrollPaneConfig();
    public ProgressConfig progress = new ProgressConfig();
    public LabelsConfig labels = new LabelsConfig();
    public ColorsConfig colors = new ColorsConfig();
    public OverlayConfig overlay = new OverlayConfig();
    public InteractionConfig interaction = new InteractionConfig();
    public FilterPanelConfig filterPanel = new FilterPanelConfig();
    public Map<String, String> categoryIcons = new HashMap<>();
    public Map<String, String> packetToDetailsBackground = new HashMap<>();
    public List<StatConfig> stats = new ArrayList<>();
    public List<StatConfig> zombieStats = new ArrayList<>();

    public CollectionConfig sanitize() {
        backgroundPath = text(backgroundPath, "menu/bg/collection.png");
        titlePath = text(titlePath, "menu/image/collection_title.png");
        lockIconId = text(lockIconId, "IMAGE_ZEN_GARDEN_LOCKED_POT_ICON");
        defaultDetailsBackground = text(defaultDetailsBackground, "IMAGE_UI_CARDS_BACKGROUNDS_CARD_PLANT_BG_MODERN");
        boostDetailsBackground = text(boostDetailsBackground, "IMAGE_UI_CARDS_BACKGROUNDS_CARD_PLANT_BG_BOOST");
        if (title == null) title = titleDefaults();
        if (panel == null) panel = new PanelConfig();
        if (topBar == null) topBar = new TopBarConfig();
        if (tabs == null) tabs = new TabsConfig();
        if (plantGrid == null) plantGrid = plantGridDefaults();
        if (zombieGrid == null) zombieGrid = zombieGridDefaults();
        if (details == null) details = new DetailsConfig();
        if (card == null) card = new CardChromeConfig();
        if (cardHover == null) cardHover = new CardHoverConfig();
        if (scrollPane == null) scrollPane = new ScrollPaneConfig();
        if (progress == null) progress = new ProgressConfig();
        if (labels == null) labels = new LabelsConfig();
        if (colors == null) colors = new ColorsConfig();
        if (overlay == null) overlay = new OverlayConfig();
        if (interaction == null) interaction = new InteractionConfig();
        if (filterPanel == null) filterPanel = new FilterPanelConfig();
        if (categoryIcons == null) categoryIcons = new HashMap<>();
        if (packetToDetailsBackground == null) packetToDetailsBackground = new HashMap<>();
        if (stats == null) stats = new ArrayList<>();
        if (zombieStats == null) zombieStats = new ArrayList<>();
        title.sanitize(220f, 88f, 0.5f, 1f, 0.5f, 1f);
        panel.sanitize();
        topBar.sanitize();
        tabs.sanitize();
        plantGrid.sanitize();
        zombieGrid.sanitize();
        details.sanitize();
        card.sanitize();
        cardHover.sanitize();
        scrollPane.sanitize();
        progress.sanitize();
        labels.sanitize();
        colors.sanitize();
        overlay.sanitize();
        interaction.sanitize();
        filterPanel.sanitize();
        putDefaultCategoryIcon("SUN_PRODUCER", "IMAGE_UI_PACKETS_MINTFAM_SUN");
        putDefaultCategoryIcon("SHOOTER", "IMAGE_UI_PACKETS_MINTFAM_PEASHOOTER");
        putDefaultCategoryIcon("HOMING", "IMAGE_UI_PACKETS_MINTFAM_TRAP");
        putDefaultCategoryIcon("STRIKE_THROUGH", "IMAGE_UI_PACKETS_MINTFAM_SHADOW");
        putDefaultCategoryIcon("LOBBER", "IMAGE_UI_PACKETS_MINTFAM_LOBBER");
        putDefaultCategoryIcon("EXPLOSIVE", "IMAGE_UI_PACKETS_MINTFAM_EXPLOSIVE");
        putDefaultCategoryIcon("MELEE", "IMAGE_UI_PACKETS_MINTFAM_MELEE");
        putDefaultCategoryIcon("WALL_NUT", "IMAGE_UI_PACKETS_MINTFAM_DEFENSE");
        putDefaultCategoryIcon("MODIFIER", "IMAGE_UI_PACKETS_MINTFAM_POISON");
        putDefaultPacketMap();
        ensureDefaultStats();
        ensureDefaultZombieStats();
        return this;
    }

    public String categoryIcon(PlantCategory category) {
        if (category == null) {
            return "";
        }
        String icon = categoryIcons.get(category.name());
        return icon == null ? "" : icon;
    }

    public String resolveDetailsBackground(String packetBgKey, boolean boosted) {
        if (boosted) {
            return boostDetailsBackground;
        }
        if (packetBgKey != null && !packetBgKey.isBlank()) {
            String mapped = packetToDetailsBackground.get(packetBgKey);
            if (mapped != null && !mapped.isBlank()) {
                return mapped;
            }
            String derived = deriveDetailsBackground(packetBgKey);
            if (derived != null) {
                return derived;
            }
        }
        return defaultDetailsBackground;
    }

    public float cardWidth(GridConfig grid, float availableWidth) {
        int columns = Math.max(1, grid.columns);
        float inner = Math.max(1f, availableWidth - grid.horizontalPad() * 2f);
        float width = (inner - grid.gapX * (columns - 1)) / columns;
        if (!grid.autoFit && grid.cardWidth > 0f) {
            width = grid.cardWidth;
        }
        width = Math.max(grid.minCardWidth, width);
        if (!grid.autoFit) {
            width = Math.min(grid.maxCardWidth, width);
        }
        return Math.max(1f, width);
    }

    private String deriveDetailsBackground(String packetBgKey) {
        String suffix = packetBgKey;
        if (suffix.startsWith("IMAGE_UI_PACKETS_")) {
            suffix = suffix.substring("IMAGE_UI_PACKETS_".length());
        }
        if ("MODERNDAY".equals(suffix) || "READY_PREMIUM".equals(suffix) || "READY".equals(suffix)) {
            suffix = "MODERN";
        }
        String candidate = "IMAGE_UI_CARDS_BACKGROUNDS_CARD_PLANT_BG_" + suffix;
        if (CollectionAnchors.hasRegion(candidate)) {
            return candidate;
        }
        return null;
    }

    private void putDefaultCategoryIcon(String key, String iconId) {
        if (!categoryIcons.containsKey(key) || categoryIcons.get(key) == null || categoryIcons.get(key).isBlank()) {
            categoryIcons.put(key, iconId);
        }
    }

    private void putDefaultPacketMap() {
        putPacket("IMAGE_UI_PACKETS_EGYPT", "IMAGE_UI_CARDS_BACKGROUNDS_CARD_PLANT_BG_EGYPT");
        putPacket("IMAGE_UI_PACKETS_DARK", "IMAGE_UI_CARDS_BACKGROUNDS_CARD_PLANT_BG_DARK");
        putPacket("IMAGE_UI_PACKETS_DINO", "IMAGE_UI_CARDS_BACKGROUNDS_CARD_PLANT_BG_DINO");
        putPacket("IMAGE_UI_PACKETS_LOSTCITY", "IMAGE_UI_CARDS_BACKGROUNDS_CARD_PLANT_BG_LOSTCITY");
        putPacket("IMAGE_UI_PACKETS_PIRATE", "IMAGE_UI_CARDS_BACKGROUNDS_CARD_PLANT_BG_PIRATE");
        putPacket("IMAGE_UI_PACKETS_ICEAGE", "IMAGE_UI_CARDS_BACKGROUNDS_CARD_PLANT_BG_ICEAGE");
        putPacket("IMAGE_UI_PACKETS_COWBOY", "IMAGE_UI_CARDS_BACKGROUNDS_CARD_PLANT_BG_COWBOY");
        putPacket("IMAGE_UI_PACKETS_MODERNDAY", "IMAGE_UI_CARDS_BACKGROUNDS_CARD_PLANT_BG_MODERN");
        putPacket("IMAGE_UI_PACKETS_FUTURE", "IMAGE_UI_CARDS_BACKGROUNDS_CARD_PLANT_BG_FUTURE");
        putPacket("IMAGE_UI_PACKETS_BEACH", "IMAGE_UI_CARDS_BACKGROUNDS_CARD_PLANT_BG_BEACH");
        putPacket("IMAGE_UI_PACKETS_EIGHTIES", "IMAGE_UI_CARDS_BACKGROUNDS_CARD_PLANT_BG_EIGHTIES");
        putPacket("IMAGE_UI_PACKETS_CARNIVAL", "IMAGE_UI_CARDS_BACKGROUNDS_CARD_PLANT_BG_CARNIVAL");
        putPacket("IMAGE_UI_PACKETS_WARP", "IMAGE_UI_CARDS_BACKGROUNDS_CARD_PLANT_BG_WARP");
        putPacket("IMAGE_UI_PACKETS_ZCORP", "IMAGE_UI_CARDS_BACKGROUNDS_CARD_PLANT_BG_ZCORP");
        putPacket("IMAGE_UI_PACKETS_ROMAN", "IMAGE_UI_CARDS_BACKGROUNDS_CARD_PLANT_BG_ROMAN");
        putPacket("IMAGE_UI_PACKETS_BOOST", "IMAGE_UI_CARDS_BACKGROUNDS_CARD_PLANT_BG_BOOST");
        putPacket("IMAGE_UI_PACKETS_READY_PREMIUM", "IMAGE_UI_CARDS_BACKGROUNDS_CARD_PLANT_BG_MODERN");
    }

    private void putPacket(String packet, String details) {
        if (!packetToDetailsBackground.containsKey(packet)
            || packetToDetailsBackground.get(packet) == null
            || packetToDetailsBackground.get(packet).isBlank()) {
            packetToDetailsBackground.put(packet, details);
        }
    }

    private void ensureDefaultStats() {
        if (!stats.isEmpty()) {
            stats.forEach(stat -> {
                if (stat != null) stat.sanitize();
            });
            return;
        }
        stats.add(stat("sunCost", "Cost", "IMAGE_UI_ALMANAC_PLANTS_POWERAREA_ICON"));
        stats.add(stat("maxHealth", "Toughness", "IMAGE_UI_ALMANAC_PLANTS_TOUGHNESS_ICON"));
        stats.add(stat("attackDamage", "Damage", "IMAGE_UI_ALMANAC_PLANTS_DAMAGE_ICON"));
        stats.add(stat("cooldownTicks", "Recharge", "IMAGE_UI_ALMANAC_PLANTS_RECHARGE_ICON"));
        stats.add(stat("category", "Category", "IMAGE_UI_ALMANAC_ALMANAC_PIERCE"));
        stats.add(stat("tags", "Tags", "IMAGE_UI_ALMANAC_PLANTS_VARIABLE_ICON"));
    }

    private void ensureDefaultZombieStats() {
        if (!zombieStats.isEmpty()) {
            zombieStats.forEach(stat -> {
                if (stat != null) stat.sanitize();
            });
            return;
        }
        zombieStats.add(stat("hp", "Toughness", "IMAGE_UI_ALMANAC_PLANTS_TOUGHNESS_ICON"));
        zombieStats.add(stat("speed", "Speed", "IMAGE_UI_ALMANAC_ALMANAC_BOOST"));
    }

    private static StatConfig stat(String id, String title, String icon) {
        StatConfig config = new StatConfig();
        config.id = id;
        config.title = title;
        config.icon = icon;
        config.sanitize();
        return config;
    }

    private static AnchoredConfig titleDefaults() {
        AnchoredConfig config = new AnchoredConfig();
        config.anchorX = 0.5f;
        config.anchorY = 1f;
        config.originX = 0.5f;
        config.originY = 1f;
        config.offsetY = -8f;
        config.width = 220f;
        config.height = 88f;
        return config;
    }

    private static GridConfig plantGridDefaults() {
        GridConfig grid = new GridConfig();
        grid.showCardProgress = true;
        return grid;
    }

    private static GridConfig zombieGridDefaults() {
        GridConfig grid = new GridConfig();
        grid.showCardProgress = false;
        return grid;
    }

    public static class AnchoredConfig {
        public float anchorX = 0.5f;
        public float anchorY = 0.5f;
        public float originX = 0.5f;
        public float originY = 0.5f;
        public float offsetX = 0f;
        public float offsetY = 0f;
        public float width = 100f;
        public float height = 100f;
        public float scale = 1f;
        public float alpha = 1f;
        public float rotation = 0f;
        public boolean keepAspect = true;
        public String path = "";

        public void sanitize(float defaultWidth, float defaultHeight, float ax, float ay, float ox, float oy) {
            width = positive(width, defaultWidth);
            height = positive(height, defaultHeight);
            scale = positive(scale, 1f);
            alpha = clamp(alpha, 0f, 1f, 1f);
            anchorX = clamp(anchorX, 0f, 1f, ax);
            anchorY = clamp(anchorY, 0f, 1f, ay);
            originX = clamp(originX, 0f, 1f, ox);
            originY = clamp(originY, 0f, 1f, oy);
        }
    }

    public static class PanelConfig {
        public String drawable = UiTheme.BG_PANEL;
        public String fallback = "image_ui_generic_guaranteed_bg_10";
        public float alpha = 0.9f;
        public float widthRatio = 1f;
        public float heightRatio = 0.80f;
        public float anchorX = 0.5f;
        public float anchorY = 0f;
        public float offsetX = 0f;
        public float offsetY = 0f;
        public float sidePad = 20f;
        public float padLeft = 20f;
        public float padRight = 20f;
        public float padTop = 16f;
        public float padBottom = 12f;
        public float marginTop = 0f;

        public void sanitize() {
            drawable = text(drawable, UiTheme.BG_PANEL);
            fallback = text(fallback, "image_ui_generic_guaranteed_bg_10");
            alpha = clamp(alpha, 0f, 1f, 0.9f);
            widthRatio = clamp(widthRatio, 0.2f, 1f, 1f);
            heightRatio = clamp(heightRatio, 0.2f, 1f, 0.80f);
            anchorX = clamp(anchorX, 0f, 1f, 0.5f);
            anchorY = clamp(anchorY, 0f, 1f, 0f);
            sidePad = Math.max(0f, sidePad);
            padLeft = sidePad;
            padRight = sidePad;
            padTop = Math.max(0f, padTop);
            padBottom = Math.max(0f, padBottom);
            marginTop = Math.max(0f, marginTop);
        }
    }

    public static class TopBarConfig {
        public boolean visible = true;
        public float padTop = 10f;
        public float padLeft = 12f;
        public float padRight = 12f;
        public float buttonSize = 72f;
        public float iconSize = 36f;
        public float valueWidth = 72f;
        public float gap = 4f;
        public String coinIconPath = "menu/icon/coin2.png";
        public String gemIconPath = "menu/icon/gem.png";
        public String backNormalId = "IMAGE_UI_ALMANAC_BUTTONS_HUD_BACK_NORMAL";
        public String backHoverId = "IMAGE_UI_ALMANAC_BUTTONS_HUD_BACK_SELECTED";
        public String shopNormalId = "IMAGE_UI_HUD_WORLDMAP_BUTTONS_HUD_STORE_NORMAL";
        public String shopHoverId = "IMAGE_UI_HUD_WORLDMAP_BUTTONS_HUD_STORE_SELECTED";

        public void sanitize() {
            padTop = Math.max(0f, padTop);
            padLeft = Math.max(0f, padLeft);
            padRight = Math.max(0f, padRight);
            buttonSize = positive(buttonSize, 72f);
            iconSize = positive(iconSize, 36f);
            valueWidth = positive(valueWidth, 72f);
            gap = Math.max(0f, gap);
            coinIconPath = text(coinIconPath, "menu/icon/coin2.png");
            gemIconPath = text(gemIconPath, "menu/icon/gem.png");
            backNormalId = text(backNormalId, "IMAGE_UI_ALMANAC_BUTTONS_HUD_BACK_NORMAL");
            backHoverId = text(backHoverId, "IMAGE_UI_ALMANAC_BUTTONS_HUD_BACK_SELECTED");
            shopNormalId = text(shopNormalId, "IMAGE_UI_HUD_WORLDMAP_BUTTONS_HUD_STORE_NORMAL");
            shopHoverId = text(shopHoverId, "IMAGE_UI_HUD_WORLDMAP_BUTTONS_HUD_STORE_SELECTED");
        }
    }

    public static class TabsConfig {
        public float selectedAlpha = 1f;
        public float unselectedAlpha = 0.55f;
        public float selectedScale = 1f;
        public float unselectedScale = 0.92f;
        public float hoverLift = 8f;
        public float hoverAlpha = 0.82f;
        public float hoverSpeed = 10f;
        public TabBrowseByTab browse = new TabBrowseByTab();
        public AnchoredConfig plants = tabDefaults(0.32f, "menu/image/plants_element.png");
        public AnchoredConfig zombies = tabDefaults(0.68f, "menu/image/zombie_element.png");

        public TabBrowseLook browseLook(boolean zombiesTabOpen, boolean zombieElement) {
            TabBrowsePair pair = zombiesTabOpen
                ? (browse == null ? null : browse.zombies)
                : (browse == null ? null : browse.plants);
            if (pair == null) {
                pair = new TabBrowsePair();
            }
            TabBrowseLook look = zombieElement ? pair.zombies : pair.plants;
            return look == null ? new TabBrowseLook() : look;
        }

        public void sanitize() {
            selectedAlpha = clamp(selectedAlpha, 0f, 1f, 1f);
            unselectedAlpha = clamp(unselectedAlpha, 0f, 1f, 0.55f);
            selectedScale = positive(selectedScale, 1f);
            unselectedScale = positive(unselectedScale, 0.92f);
            hoverLift = Math.max(0f, hoverLift);
            hoverAlpha = clamp(hoverAlpha, 0f, 1f, 0.82f);
            hoverSpeed = positive(hoverSpeed, 10f);
            if (browse == null) browse = new TabBrowseByTab();
            browse.sanitize();
            if (plants == null) plants = tabDefaults(0.32f, "menu/image/plants_element.png");
            if (zombies == null) zombies = tabDefaults(0.68f, "menu/image/zombie_element.png");
            plants.path = text(plants.path, "menu/image/plants_element.png");
            zombies.path = text(zombies.path, "menu/image/zombie_element.png");
            plants.sanitize(280f, 420f, 0.32f, 0.46f, 0.5f, 0.5f);
            zombies.sanitize(280f, 420f, 0.68f, 0.46f, 0.5f, 0.5f);
        }

        private static AnchoredConfig tabDefaults(float anchorX, String path) {
            AnchoredConfig config = new AnchoredConfig();
            config.path = path;
            config.anchorX = anchorX;
            config.anchorY = 0.46f;
            config.originX = 0.5f;
            config.originY = 0.5f;
            config.width = 280f;
            config.height = 420f;
            return config;
        }
    }

    public static class TabBrowseByTab {
        public TabBrowsePair plants = TabBrowsePair.of(0.52f, 0.28f, 0.34f, 0.52f);
        public TabBrowsePair zombies = TabBrowsePair.of(0.34f, 0.52f, 0.52f, 0.28f);

        public void sanitize() {
            if (plants == null) plants = TabBrowsePair.of(0.52f, 0.28f, 0.34f, 0.52f);
            if (zombies == null) zombies = TabBrowsePair.of(0.34f, 0.52f, 0.52f, 0.28f);
            plants.sanitize();
            zombies.sanitize();
        }
    }

    public static class TabBrowsePair {
        public TabBrowseLook plants = TabBrowseLook.of(0.52f, 0.28f);
        public TabBrowseLook zombies = TabBrowseLook.of(0.34f, 0.52f);

        public static TabBrowsePair of(
            float plantsAlpha, float plantsDim, float zombiesAlpha, float zombiesDim
        ) {
            TabBrowsePair pair = new TabBrowsePair();
            pair.plants = TabBrowseLook.of(plantsAlpha, plantsDim);
            pair.zombies = TabBrowseLook.of(zombiesAlpha, zombiesDim);
            return pair;
        }

        public void sanitize() {
            if (plants == null) plants = TabBrowseLook.of(0.52f, 0.28f);
            if (zombies == null) zombies = TabBrowseLook.of(0.34f, 0.52f);
            plants.sanitize();
            zombies.sanitize();
        }
    }

    public static class TabBrowseLook {
        public float alpha = 0.48f;
        public float dim = 0.35f;

        public static TabBrowseLook of(float alpha, float dim) {
            TabBrowseLook look = new TabBrowseLook();
            look.alpha = alpha;
            look.dim = dim;
            return look;
        }

        public void sanitize() {
            alpha = clamp(alpha, 0f, 1f, 0.48f);
            dim = clamp(dim, 0f, 1f, 0.35f);
        }
    }

    public static class GridConfig {
        public int columns = 7;
        public boolean autoFit = true;
        public float cardWidth = 0f;
        public float minCardWidth = 48f;
        public float maxCardWidth = 400f;
        public float gapX = 10f;
        public float gapY = 12f;
        public float sidePad = 0f;
        public float padLeft = 0f;
        public float padRight = 0f;
        public float padTop = 6f;
        public float padBottom = 8f;
        public boolean showCardProgress = true;
        public float progressPadX = 0f;
        public float progressPadY = 3f;

        public float horizontalPad() {
            return Math.max(0f, sidePad);
        }

        public void sanitize() {
            columns = Math.max(1, columns);
            minCardWidth = positive(minCardWidth, 48f);
            maxCardWidth = positive(maxCardWidth, 400f);
            if (maxCardWidth < minCardWidth) {
                maxCardWidth = minCardWidth;
            }
            gapX = Math.max(0f, gapX);
            gapY = Math.max(0f, gapY);
            sidePad = Math.max(0f, sidePad);
            padLeft = sidePad;
            padRight = sidePad;
            padTop = Math.max(0f, padTop);
            padBottom = Math.max(0f, padBottom);
            progressPadX = finite(progressPadX, 0f);
            progressPadY = finite(progressPadY, 3f);
        }
    }

    public static class DetailsConfig {
        public float previewWidth = 220f;
        public float previewHeight = 220f;
        public float previewPad = 10f;
        public float columnGap = 16f;
        public int statColumns = 2;
        public float statRowGap = 8f;
        public float statColGap = 14f;
        public float statIconSize = 41f;
        public float statIconPad = 8f;
        public float statTitleScale = 1.16f;
        public float titleScale = 1.55f;
        public float bodyScale = 1.16f;
        public float valueScale = 1.22f;
        public float plantFoodScale = 1.16f;
        public int plantDescriptionLines = 2;
        public int zombieDescriptionLines = 7;
        public float descriptionLineHeight = 24f;
        public float arrowSize = 58f;
        public float progressBarHeight = 22f;
        public float progressPadX = 0f;
        public float progressPadY = 3f;
        public String arrowPreviousId = "IMAGE_UI_ALMANAC_STATS_SCREEN_NAV_ARROW_PREVIOUS";
        public String arrowPreviousDownId = "IMAGE_UI_ALMANAC_STATS_SCREEN_NAV_ARROW_PREVIOUS_DOWN";
        public String arrowNextId = "IMAGE_UI_ALMANAC_STATS_SCREEN_NAV_ARROW_NEXT";
        public String arrowNextDownId = "IMAGE_UI_ALMANAC_STATS_SCREEN_NAV_ARROW_NEXT_DOWN";
        public String upgradeStyle = UiTheme.BTN_GREEN;
        public float upgradeWidth = 160f;
        public float upgradeHeight = 44f;
        public String buyStyle = UiTheme.BTN_GREEN;
        public float buyWidth = 168f;
        public float buyHeight = 44f;
        public String buyGemIconPath = "menu/icon/gem3.png";
        public float buyGemIconSize = 28f;
        public float buyGemGap = 8f;
        public String previewSeparator = " > ";
        public float previewValueGap = 4f;
        public float previewSeparatorScale = 1.22f;
        public float lockedPreviewAlpha = 0.42f;
        public float padTop = 8f;
        public float padBottom = 8f;
        public float padLeft = 0f;
        public float padRight = 0f;
        public float bottomGap = 12f;
        public float bottomColumnGap = 16f;
        public float titlePad = 6f;
        public float arrowPad = 8f;
        public float statsPadTop = 8f;
        public float descriptionPadTop = 8f;
        public float infoWidth = 360f;

        public void sanitize() {
            previewWidth = positive(previewWidth, 220f);
            previewHeight = positive(previewHeight, 220f);
            previewPad = Math.max(0f, previewPad);
            columnGap = finite(columnGap, 16f);
            statColumns = Math.max(1, statColumns);
            statRowGap = finite(statRowGap, 8f);
            statColGap = finite(statColGap, 14f);
            statIconSize = positive(statIconSize, 41f);
            statIconPad = finite(statIconPad, 8f);
            statTitleScale = positive(statTitleScale, 1.16f);
            titleScale = positive(titleScale, 1.55f);
            bodyScale = positive(bodyScale, 1.16f);
            valueScale = positive(valueScale, 1.22f);
            plantFoodScale = positive(plantFoodScale, 1.16f);
            plantDescriptionLines = Math.max(1, plantDescriptionLines);
            zombieDescriptionLines = Math.max(1, zombieDescriptionLines);
            descriptionLineHeight = positive(descriptionLineHeight, 24f);
            arrowSize = positive(arrowSize, 58f);
            progressBarHeight = positive(progressBarHeight, 22f);
            progressPadX = finite(progressPadX, 0f);
            progressPadY = finite(progressPadY, 3f);
            arrowPreviousId = text(arrowPreviousId, "IMAGE_UI_ALMANAC_STATS_SCREEN_NAV_ARROW_PREVIOUS");
            arrowPreviousDownId = text(arrowPreviousDownId, "IMAGE_UI_ALMANAC_STATS_SCREEN_NAV_ARROW_PREVIOUS_DOWN");
            arrowNextId = text(arrowNextId, "IMAGE_UI_ALMANAC_STATS_SCREEN_NAV_ARROW_NEXT");
            arrowNextDownId = text(arrowNextDownId, "IMAGE_UI_ALMANAC_STATS_SCREEN_NAV_ARROW_NEXT_DOWN");
            upgradeStyle = text(upgradeStyle, UiTheme.BTN_GREEN);
            upgradeWidth = positive(upgradeWidth, 160f);
            upgradeHeight = positive(upgradeHeight, 44f);
            buyStyle = text(buyStyle, UiTheme.BTN_GREEN);
            buyWidth = positive(buyWidth, 168f);
            buyHeight = positive(buyHeight, 44f);
            buyGemIconPath = text(buyGemIconPath, "menu/icon/gem3.png");
            buyGemIconSize = positive(buyGemIconSize, 28f);
            buyGemGap = Math.max(0f, buyGemGap);
            previewSeparator = text(previewSeparator, " > ");
            previewValueGap = finite(previewValueGap, 4f);
            previewSeparatorScale = positive(previewSeparatorScale, 1.22f);
            lockedPreviewAlpha = clamp(lockedPreviewAlpha, 0f, 1f, 0.42f);
            padTop = finite(padTop, 8f);
            padBottom = finite(padBottom, 8f);
            padLeft = finite(padLeft, 0f);
            padRight = finite(padRight, 0f);
            bottomGap = finite(bottomGap, 12f);
            bottomColumnGap = finite(bottomColumnGap, 16f);
            titlePad = finite(titlePad, 6f);
            arrowPad = Math.max(0f, arrowPad);
            statsPadTop = finite(statsPadTop, 8f);
            descriptionPadTop = finite(descriptionPadTop, 8f);
            infoWidth = positive(infoWidth, 360f);
        }
    }

    public static class CardChromeConfig {
        public String font = "FBUSV8C5EI_1";
        public String levelFormat = "Lv.%d";
        public float levelPadX = 7f;
        public float levelPadY = 6f;
        public float levelFontScale = 0.72f;
        public float categoryPadX = 5f;
        public float categoryPadY = 6f;
        public float categoryOffsetX = 0f;
        public float categoryOffsetY = 0f;
        public float categorySizeRatio = 0.28f;
        public float lockSizeRatio = 0.46f;
        public float lockedTint = 0.58f;
        public float lockedOverlayAlpha = 0.55f;

        public void sanitize() {
            font = text(font, "FBUSV8C5EI_1");
            levelFormat = text(levelFormat, "Lv.%d");
            levelPadX = Math.max(0f, levelPadX);
            levelPadY = Math.max(0f, levelPadY);
            levelFontScale = positive(levelFontScale, 0.72f);
            if (Float.isNaN(categoryPadX)) categoryPadX = 5f;
            if (Float.isNaN(categoryPadY)) categoryPadY = 6f;
            if (Float.isNaN(categoryOffsetX)) categoryOffsetX = 0f;
            if (Float.isNaN(categoryOffsetY)) categoryOffsetY = 0f;
            categorySizeRatio = clamp(categorySizeRatio, 0.1f, 0.6f, 0.28f);
            lockSizeRatio = clamp(lockSizeRatio, 0.2f, 0.8f, 0.46f);
            lockedTint = clamp(lockedTint, 0.2f, 1f, 0.58f);
            lockedOverlayAlpha = clamp(lockedOverlayAlpha, 0f, 1f, 0.55f);
        }
    }

    public static class CardHoverConfig {
        public float hoverLift = 5f;
        public float hoverScale = 1.06f;
        public float hoverSpeed = 10f;

        public void sanitize() {
            hoverLift = Math.max(0f, hoverLift);
            hoverScale = positive(hoverScale, 1.06f);
            hoverSpeed = positive(hoverSpeed, 10f);
        }
    }

    public static class ScrollPaneConfig {
        public float scrollBarWidth = 7f;
        public boolean fadeScrollBars = false;
        public float scrollSpeed = 48f;

        public void sanitize() {
            scrollBarWidth = positive(scrollBarWidth, 7f);
            scrollSpeed = positive(scrollSpeed, 48f);
        }
    }

    public static class ProgressConfig {
        public float barHeight = 14f;
        public float iconSize = 18f;
        public float iconGap = 2f;
        public float textScale = 0.62f;
        public String yellowStyle = "xp_yellow";
        public String greenStyle = "xp_green";
        public String yellowIcon = "IMAGE_UI_GENERIC_XP_PROGRESS_ICON_YELLOW_LARGE";
        public String greenIcon = "IMAGE_UI_GENERIC_XP_PROGRESS_ICON_GREEN_LARGE";
        public float gap = 3f;
        public float iconHeight = 16f;
        public float seedScale = 0.72f;
        public float coinScale = 0.68f;
        public String countFormat = "%d/%d";
        public String maxText = "MAX";

        public void sanitize() {
            barHeight = positive(barHeight, 14f);
            iconSize = positive(iconSize, 18f);
            iconGap = Math.max(0f, iconGap);
            textScale = positive(textScale, 0.62f);
            yellowStyle = text(yellowStyle, "xp_yellow");
            greenStyle = text(greenStyle, "xp_green");
            yellowIcon = text(yellowIcon, "IMAGE_UI_GENERIC_XP_PROGRESS_ICON_YELLOW_LARGE");
            greenIcon = text(greenIcon, "IMAGE_UI_GENERIC_XP_PROGRESS_ICON_GREEN_LARGE");
            gap = Math.max(0f, gap);
            iconHeight = positive(iconHeight, 16f);
            seedScale = positive(seedScale, 0.72f);
            coinScale = positive(coinScale, 0.68f);
            countFormat = text(countFormat, "%d/%d");
            maxText = text(maxText, "MAX");
        }
    }

    public static class LabelsConfig {
        public String upgrade = "Upgrade";
        public String buy = "Buy";
        public String buyFormat = "Buy %d";
        public String maxLevel = "Max Level";
        public String locked = "Locked";
        public String lockedPlantBody = "Unlock this plant to view its stats.";
        public String lockedZombieBody = "Encounter this zombie to view its stats.";
        public String plantFoodPrefix = "Plant Food: ";
        public String noPlantFood = "This plant has no Plant Food ability.";
        public String levelFormat = "LEVEL %d";
        public String levelNextFormat = "LEVEL %d → %d";
        public String filterAllCategories = "All categories";
        public String filterUnlockAll = "All";
        public String filterUnlockUnlocked = "Unlocked";
        public String filterUnlockLocked = "Locked";
        public String filterDiscoverAll = "All";
        public String filterDiscoverDiscovered = "Discovered";
        public String filterDiscoverUndiscovered = "Undiscovered";
        public String filterUpgradeableOnly = "Upgradeable only";
        public String filterApply = "Apply";
        public String filterEmpty = "No items match these filters.";

        public void sanitize() {
            upgrade = text(upgrade, "Upgrade");
            buy = text(buy, "Buy");
            buyFormat = text(buyFormat, "Buy %d");
            maxLevel = text(maxLevel, "Max Level");
            locked = text(locked, "Locked");
            lockedPlantBody = text(lockedPlantBody, "Unlock this plant to view its stats.");
            lockedZombieBody = text(lockedZombieBody, "Encounter this zombie to view its stats.");
            plantFoodPrefix = text(plantFoodPrefix, "Plant Food: ");
            noPlantFood = text(noPlantFood, "This plant has no Plant Food ability.");
            levelFormat = text(levelFormat, "LEVEL %d");
            levelNextFormat = text(levelNextFormat, "LEVEL %d → %d");
            filterAllCategories = text(filterAllCategories, "All categories");
            filterUnlockAll = text(filterUnlockAll, "All");
            filterUnlockUnlocked = text(filterUnlockUnlocked, "Unlocked");
            filterUnlockLocked = text(filterUnlockLocked, "Locked");
            filterDiscoverAll = text(filterDiscoverAll, "All");
            filterDiscoverDiscovered = text(filterDiscoverDiscovered, "Discovered");
            filterDiscoverUndiscovered = text(filterDiscoverUndiscovered, "Undiscovered");
            filterUpgradeableOnly = text(filterUpgradeableOnly, "Upgradeable only");
            filterApply = text(filterApply, "Apply");
            filterEmpty = text(filterEmpty, "No items match these filters.");
        }
    }

    public static class ColorsConfig {
        public String currentValue = "FFFFFF";
        public String nextValue = "D4F5C0";
        public String separator = "F0E6D0";
        public String title = "FFF4D6";
        public String body = "FFFFFF";
        public String muted = "F2EBDD";
        public String statTitle = "E8D9B0";
        public String lockedOverlay = "141414AD";

        public void sanitize() {
            currentValue = text(currentValue, "FFFFFF");
            nextValue = text(nextValue, "D4F5C0");
            separator = text(separator, "F0E6D0");
            title = text(title, "FFF4D6");
            body = text(body, "FFFFFF");
            muted = text(muted, "F2EBDD");
            statTitle = text(statTitle, "E8D9B0");
            lockedOverlay = text(lockedOverlay, "141414AD");
        }

        public Color resolve(String token, Color fallback) {
            return CollectionConfig.resolveColor(token, fallback);
        }
    }

    public static class OverlayConfig {
        public String color = "#000000";
        public float alpha = 0.82f;

        public void sanitize() {
            color = text(color, "#000000");
            alpha = clamp(alpha, 0f, 1f, 0.82f);
        }

        public Color tint() {
            Color resolved = CollectionConfig.resolveColor(color, Color.BLACK);
            resolved.a = alpha;
            return resolved;
        }
    }


    public static class FilterPanelConfig {
        public float width = 860f;
        public float height = 72f;
        public float bottomOffset = 18f;
        public float xOffset = 0f;
        public String background = UiTheme.BG_TAB;
        public float alpha = 0.92f;
        public float padTop = 8f;
        public float padBottom = 8f;
        public float padLeft = 14f;
        public float padRight = 14f;
        public String iconPath = "menu/icon/filter.png";
        public float iconSize = 35f;
        public float iconPadding = 6f;
        public float iconOffsetX = 0f;
        public float iconOffsetY = 0f;
        public float dropdownWidth = 170f;
        public float dropdownHeight = 36f;
        public float controlSpacing = 12f;
        public float popupOffset = 4f;
        public String popupDirection = "up";
        public float applyButtonWidth = 110f;
        public float applyButtonHeight = 40f;
        public float applyButtonSpacing = 14f;
        public float checkboxSize = 28f;
        public float checkboxLabelSpacing = 8f;
        /** Extra pad before left-aligned filter controls (icon + dropdowns). */
        public float contentPadLeft = 4f;
        /** Extra pad after right-aligned Apply button. */
        public float applyPadRight = 4f;

        public void sanitize() {
            width = positive(width, 860f);
            height = positive(height, 72f);
            bottomOffset = Math.max(0f, bottomOffset);
            if (Float.isNaN(xOffset) || Float.isInfinite(xOffset)) xOffset = 0f;
            background = text(background, UiTheme.BG_TAB);
            alpha = clamp(alpha, 0f, 1f, 0.92f);
            padTop = Math.max(0f, padTop);
            padBottom = Math.max(0f, padBottom);
            padLeft = Math.max(0f, padLeft);
            padRight = Math.max(0f, padRight);
            iconPath = text(iconPath, "menu/icon/filter.png");
            iconSize = positive(iconSize, 35f);
            iconPadding = Math.max(0f, iconPadding);
            if (Float.isNaN(iconOffsetX) || Float.isInfinite(iconOffsetX)) iconOffsetX = 0f;
            if (Float.isNaN(iconOffsetY) || Float.isInfinite(iconOffsetY)) iconOffsetY = 0f;
            dropdownWidth = positive(dropdownWidth, 170f);
            dropdownHeight = positive(dropdownHeight, 36f);
            controlSpacing = Math.max(0f, controlSpacing);
            popupOffset = Math.max(0f, popupOffset);
            popupDirection = text(popupDirection, "up");
            applyButtonWidth = positive(applyButtonWidth, 110f);
            applyButtonHeight = positive(applyButtonHeight, 40f);
            applyButtonSpacing = Math.max(0f, applyButtonSpacing);
            checkboxSize = positive(checkboxSize, 28f);
            checkboxLabelSpacing = Math.max(0f, checkboxLabelSpacing);
            contentPadLeft = Math.max(0f, contentPadLeft);
            applyPadRight = Math.max(0f, applyPadRight);
        }
    }

    public static class InteractionConfig {
        public float fadeDuration = 0.18f;

        public void sanitize() {
            fadeDuration = Math.max(0f, fadeDuration);
        }
    }

    public static class StatConfig {
        public String id = "";
        public String title = "";
        public String icon = "";

        public void sanitize() {
            id = text(id, "");
            title = text(title, id);
            icon = icon == null ? "" : icon.trim();
        }
    }

    public static Color resolveColor(String token, Color fallback) {
        Color fallbackColor = fallback == null ? Color.WHITE.cpy() : fallback.cpy();
        if (token == null || token.isBlank()) {
            return fallbackColor;
        }
        String key = token.trim();
        Color hex = parseHexColor(key);
        if (hex != null) {
            return hex;
        }
        Color themed = switch (key.toUpperCase().replace('-', '_')) {
            case "TEXT_PRIMARY" -> UiTheme.TEXT_PRIMARY;
            case "TEXT_SECONDARY" -> UiTheme.TEXT_SECONDARY;
            case "TEXT_MUTED" -> UiTheme.TEXT_MUTED;
            case "TEXT_STAT" -> UiTheme.TEXT_STAT;
            case "TEXT_SUCCESS" -> UiTheme.TEXT_SUCCESS;
            case "TEXT_ERROR" -> UiTheme.TEXT_ERROR;
            case "ACCENT" -> UiTheme.ACCENT;
            case "CARD_DISABLED" -> UiTheme.CARD_DISABLED;
            default -> null;
        };
        return themed == null ? fallbackColor : themed.cpy();
    }

    private static Color parseHexColor(String raw) {
        String hex = raw.startsWith("#") ? raw.substring(1) : raw;
        if (hex.startsWith("0x") || hex.startsWith("0X")) {
            hex = hex.substring(2);
        }
        hex = hex.replace("_", "").trim();
        if (hex.length() == 3) {
            hex = "" + hex.charAt(0) + hex.charAt(0)
                + hex.charAt(1) + hex.charAt(1)
                + hex.charAt(2) + hex.charAt(2);
        }
        if ((hex.length() != 6 && hex.length() != 8) || !hex.chars().allMatch(CollectionConfig::isHexChar)) {
            return null;
        }
        try {
            return Color.valueOf("#" + hex);
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    private static boolean isHexChar(int ch) {
        return (ch >= '0' && ch <= '9')
            || (ch >= 'a' && ch <= 'f')
            || (ch >= 'A' && ch <= 'F');
    }

    protected static String text(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value.trim();
    }

    protected static float finite(float value, float fallback) {
        if (Float.isNaN(value) || Float.isInfinite(value)) {
            return fallback;
        }
        return value;
    }

    protected static float positive(float value, float fallback) {
        if (Float.isNaN(value) || value <= 0f) {
            return fallback;
        }
        return value;
    }

    protected static float clamp(float value, float min, float max, float fallback) {
        if (Float.isNaN(value)) {
            return fallback;
        }
        return Math.max(min, Math.min(max, value));
    }
}

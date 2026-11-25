package app.view.network;

import app.view.mainmenu.config.MainMenuEasings;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class NetworkMenuConfig {
    public LayoutConfig layout = new LayoutConfig();
    public BackgroundConfig background = new BackgroundConfig();
    public BackButtonConfig backButton = new BackButtonConfig();
    public MinigamesConfig minigames = new MinigamesConfig();
    public RankingConfig ranking = new RankingConfig();

    public NetworkMenuConfig sanitize() {
        if (layout == null) {
            layout = new LayoutConfig();
        }
        if (background == null) {
            background = new BackgroundConfig();
        }
        if (backButton == null) {
            backButton = new BackButtonConfig();
        }
        if (minigames == null) {
            minigames = new MinigamesConfig();
        }
        if (ranking == null) {
            ranking = new RankingConfig();
        }
        layout.sanitize();
        background.sanitize();
        backButton.sanitize();
        minigames.sanitize();
        ranking.sanitize();
        return this;
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

    public static String text(String value, String fallback) {
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

    static float nonNegative(float value, float fallback) {
        if (Float.isNaN(value) || value < 0f) {
            return fallback;
        }
        return value;
    }

    static float clamp01(float value, float fallback) {
        if (Float.isNaN(value)) {
            return fallback;
        }
        return Math.max(0f, Math.min(1f, value));
    }

    static String scaling(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value.trim().toLowerCase(Locale.ROOT);
    }

    public static class LayoutConfig {
        public float designWidth = 1280f;
        public float designHeight = 720f;

        public void sanitize() {
            designWidth = positive(designWidth, 1280f);
            designHeight = positive(designHeight, 720f);
        }
    }

    public static class BackgroundConfig {
        public String path = "menu/bg/network.png";
        public float sourceWidth = 1536f;
        public float sourceHeight = 1024f;
        public String scaleMode = "cover";
        public float offsetX;
        public float offsetY;

        public void sanitize() {
            path = text(path, "menu/bg/network.png");
            if (sourceWidth <= 0f) {
                sourceWidth = 1536f;
            }
            if (sourceHeight <= 0f) {
                sourceHeight = 1024f;
            }
            if (scaleMode == null || scaleMode.isBlank()) {
                scaleMode = "cover";
            }
        }
    }

    public static class BackButtonConfig {
        public String assetPath = "menu/icon/back.png";
        public float anchorX;
        public float anchorY = 1f;
        public float x = 28f;
        public float y = -28f;
        public float offsetX;
        public float offsetY;
        public float width = 72f;
        public float height = 72f;
        public float scale = 1f;
        public float hoverScale = 1.06f;
        public float pressedScale = 0.96f;
        public float hoverDuration = 0.1f;
        public String easing = "sineOut";
        public boolean visible = true;

        public void sanitize() {
            assetPath = text(assetPath, "menu/icon/back.png");
            width = positive(width, 72f);
            height = positive(height, 72f);
            scale = positive(scale, 1f);
            hoverScale = positive(hoverScale, 1.06f);
            pressedScale = positive(pressedScale, 0.96f);
            hoverDuration = nonNegative(hoverDuration, 0.1f);
            if (easing == null || easing.isBlank()) {
                easing = "sineOut";
            }
            if (Float.isNaN(anchorX)) {
                anchorX = 0f;
            }
            if (Float.isNaN(anchorY)) {
                anchorY = 1f;
            }
        }

        public Interpolation interpolation() {
            return MainMenuEasings.parse(easing);
        }

        public float drawWidth() {
            return width * scale;
        }

        public float drawHeight() {
            return height * scale;
        }

        public float resolvedX(float areaWidth) {
            return areaWidth * anchorX + x + offsetX;
        }

        public float resolvedY(float areaHeight, float drawHeight) {
            float origin = areaHeight * anchorY + y + offsetY;
            if (anchorY >= 0.5f) {
                return origin - drawHeight;
            }
            return origin;
        }
    }

    public static class MinigamesConfig {
        public String frameAssetPath = "menu/image/network/shelf_slot.png";
        public float x = 32f;
        public float width = 324f;
        public float slotHeight = 216f;
        public float cardHeight = 186f;
        public float playHeight = 48f;
        public float playInset = 4f;
        public float playGap = 6f;
        public float cardX = 0.045f;
        public float cardY = 0.265f;
        public float cardW = 0.91f;
        public float cardH = 0.55f;
        public float playX = 0.07f;
        public float playY = 0.07f;
        public float playW = 0.86f;
        public float playH = 0.12f;
        public float playRegionX;
        public float playRegionY;
        public float playRegionWidth = 1f;
        public float playRegionHeight = 1f;
        public String cardScaling = "fill";
        public String playScaling = "fit";
        public float top = 696f;
        public float spacing = 8f;
        public boolean keepAspectRatio;
        public float hoverScale = 1.06f;
        public float normalDarkness = 0.12f;
        public float hoverDarkness;
        public float hoverDuration = 0.12f;
        public float playFadeDuration = 0.14f;
        public String easing = "sineOut";
        public ArrayList<GameConfig> games = new ArrayList<>();

        public void sanitize() {
            if (games == null) {
                games = new ArrayList<>();
            }
            frameAssetPath = text(frameAssetPath, "menu/image/network/shelf_slot.png");
            x = nonNegative(x, 32f);
            width = positive(width, 324f);
            cardHeight = positive(cardHeight, 186f);
            playHeight = positive(playHeight, 48f);
            playInset = nonNegative(playInset, 4f);
            playGap = nonNegative(playGap, 6f);
            slotHeight = positive(slotHeight, cardHeight + playGap + playHeight);
            cardX = clamp01(cardX, 0.045f);
            cardY = clamp01(cardY, 0.265f);
            cardW = positive(cardW, 0.91f);
            cardH = positive(cardH, 0.55f);
            playX = clamp01(playX, 0.07f);
            playY = clamp01(playY, 0.07f);
            playW = positive(playW, 0.86f);
            playH = positive(playH, 0.12f);
            if (cardX + cardW > 1f) {
                cardW = 1f - cardX;
            }
            if (cardY + cardH > 1f) {
                cardH = 1f - cardY;
            }
            if (playX + playW > 1f) {
                playW = 1f - playX;
            }
            if (playY + playH > 1f) {
                playH = 1f - playY;
            }
            playRegionX = clamp01(playRegionX, 0f);
            playRegionY = clamp01(playRegionY, 0f);
            playRegionWidth = positive(playRegionWidth, 1f);
            playRegionHeight = positive(playRegionHeight, 1f);
            if (playRegionX + playRegionWidth > 1f) {
                playRegionWidth = 1f - playRegionX;
            }
            if (playRegionY + playRegionHeight > 1f) {
                playRegionHeight = 1f - playRegionY;
            }
            cardScaling = scaling(cardScaling, keepAspectRatio ? "fit" : "fill");
            playScaling = scaling(playScaling, "fit");
            top = positive(top, 696f);
            spacing = nonNegative(spacing, 8f);
            hoverScale = positive(hoverScale, 1.06f);
            normalDarkness = nonNegative(normalDarkness, 0.12f);
            hoverDarkness = nonNegative(hoverDarkness, 0f);
            hoverDuration = nonNegative(hoverDuration, 0.12f);
            playFadeDuration = nonNegative(playFadeDuration, 0.14f);
            if (easing == null || easing.isBlank()) {
                easing = "sineOut";
            }
            for (GameConfig game : games) {
                if (game != null) {
                    game.sanitize();
                }
            }
            games.removeIf(game -> game == null || !game.visible || game.id.isBlank());
            if (games.isEmpty()) {
                games.add(game("izombie_network",
                    "menu/image/network/izombie_network.png",
                    "menu/image/network/play.png",
                    "open_izombie",
                    "I_ZOMBIE",
                    0));
                games.add(game("izombie_couch",
                    "menu/image/network/izombie_couch.png",
                    "menu/image/network/play.png",
                    "open_izombie_couch",
                    "I_ZOMBIE_COUCH",
                    1));
                games.add(game("scoring_game",
                    "menu/image/network/scoring_game.png",
                    "menu/image/network/play.png",
                    "open_scoring_game",
                    "SCORING_GAME",
                    2));
            }
            games.sort(Comparator.comparingInt(game -> game.order));
        }

        public Interpolation interpolation() {
            return MainMenuEasings.parse(easing);
        }

        public List<GameConfig> visibleGames() {
            List<GameConfig> visible = new ArrayList<>();
            for (GameConfig game : games) {
                if (game != null && game.visible) {
                    visible.add(game);
                }
            }
            return visible;
        }

        private static GameConfig game(
            String id,
            String assetPath,
            String playAssetPath,
            String action,
            String chapter,
            int order
        ) {
            GameConfig config = new GameConfig();
            config.id = id;
            config.assetPath = assetPath;
            config.playAssetPath = playAssetPath;
            config.action = action;
            config.chapter = chapter;
            config.order = order;
            config.sanitize();
            return config;
        }
    }

    public static class GameConfig {
        public String id = "";
        public String assetPath = "";
        public String playAssetPath = "";
        public String action = "none";
        public String chapter = "";
        public int order;
        public boolean enabled = true;
        public boolean visible = true;

        public void sanitize() {
            if (id == null) {
                id = "";
            } else {
                id = id.trim().toLowerCase(Locale.ROOT);
            }
            if (assetPath == null) {
                assetPath = "";
            }
            if (playAssetPath == null || playAssetPath.isBlank()) {
                playAssetPath = "menu/image/network/play.png";
            }
            if (action == null || action.isBlank()) {
                action = "none";
            } else {
                action = action.trim().toLowerCase(Locale.ROOT);
            }
            if (chapter == null || chapter.isBlank()) {
                chapter = "";
            } else {
                chapter = chapter.trim().toUpperCase(Locale.ROOT);
            }
        }

        public String chapterName() {
            return chapter;
        }

        public boolean clickable() {
            return enabled && visible;
        }
    }

    public static class RankingConfig {
        public String backgroundPath = "menu/image/network/ranking_panel.png";
        public float x = 848f;
        public float y = 56f;
        public float width = 400f;
        public float height = 600f;
        public float contentPadLeft = 70f;
        public float contentPadRight = 70f;
        public float contentPadTop = 168f;
        public float contentPadBottom = 84f;
        public float rowHeight = 52f;
        public float rowSpacing = 5f;
        public float slotPadLeft = 8f;
        public float slotPadRight = 8f;
        public float slotPadTop = 4f;
        public float slotPadBottom = 4f;
        public float rankWidth = 36f;
        public float avatarSize = 38f;
        public float avatarGap = 8f;
        public String slotBackground = "image_ui_if_bundle_reward_multiplier_bg_10";
        public String rankFont = "FBUSV8C5EI_2";
        public String rankFontFallback = "FBUSV8C5EI_2";
        public float rankFontSize = 0.72f;
        public String nameFont = "BRIANNETOD";
        public String nameFontFallback = "BRIANNETOD";
        public float nameFontSize = 0.62f;
        public String scoreFont = "BRIANNETOD";
        public String scoreFontFallback = "BRIANNETOD";
        public float scoreFontSize = 0.52f;
        public String font = "BRIANNETOD";
        public String fontFallback = "BRIANNETOD";
        public float fontSize = 0.78f;
        public String nameColor = "3A2410FF";
        public String scoreColor = "6B3A12FF";
        public String rankColor = "5A2E0AFF";
        public String youColor = "8A4A12FF";
        public String emptyText = "No players ranked yet.";
        public String scoreFormat = "{score} MP";
        public String rowFormat = "{rank}.  {name}     {score} MP";
        public boolean includeLoggedInUser = true;
        public SortSelectConfig sort = new SortSelectConfig();
        public ArrayList<RankEntryConfig> entries = new ArrayList<>();

        public void sanitize() {
            if (entries == null) {
                entries = new ArrayList<>();
            }
            if (sort == null) {
                sort = new SortSelectConfig();
            }
            backgroundPath = text(backgroundPath, "menu/image/network/ranking_panel.png");
            x = nonNegative(x, 848f);
            y = nonNegative(y, 56f);
            width = positive(width, 400f);
            height = positive(height, 600f);
            contentPadLeft = nonNegative(contentPadLeft, 70f);
            contentPadRight = nonNegative(contentPadRight, 70f);
            contentPadTop = nonNegative(contentPadTop, 168f);
            contentPadBottom = nonNegative(contentPadBottom, 84f);
            rowHeight = positive(rowHeight, 52f);
            rowSpacing = nonNegative(rowSpacing, 5f);
            slotPadLeft = nonNegative(slotPadLeft, 8f);
            slotPadRight = nonNegative(slotPadRight, 8f);
            slotPadTop = nonNegative(slotPadTop, 4f);
            slotPadBottom = nonNegative(slotPadBottom, 4f);
            rankWidth = positive(rankWidth, 36f);
            avatarSize = positive(avatarSize, 38f);
            avatarGap = nonNegative(avatarGap, 8f);
            slotBackground = text(slotBackground, "image_ui_if_bundle_reward_multiplier_bg_10");
            rankFont = text(rankFont, "FBUSV8C5EI_2");
            rankFontFallback = text(rankFontFallback, "FBUSV8C5EI_2");
            rankFontSize = positive(rankFontSize, 0.72f);
            nameFont = text(nameFont, "BRIANNETOD");
            nameFontFallback = text(nameFontFallback, "BRIANNETOD");
            nameFontSize = positive(nameFontSize, 0.62f);
            scoreFont = text(scoreFont, "BRIANNETOD");
            scoreFontFallback = text(scoreFontFallback, "BRIANNETOD");
            scoreFontSize = positive(scoreFontSize, 0.52f);
            font = text(font, "BRIANNETOD");
            fontFallback = text(fontFallback, "BRIANNETOD");
            fontSize = positive(fontSize, 0.78f);
            nameColor = text(nameColor, "3A2410FF");
            scoreColor = text(scoreColor, "6B3A12FF");
            rankColor = text(rankColor, "5A2E0AFF");
            youColor = text(youColor, "8A4A12FF");
            emptyText = text(emptyText, "No players ranked yet.");
            scoreFormat = text(scoreFormat, "{score} MP");
            rowFormat = text(rowFormat, "{rank}.  {name}     {score} MP");
            sort.sanitize();
            for (RankEntryConfig entry : entries) {
                if (entry != null) {
                    entry.sanitize();
                }
            }
            entries.removeIf(entry -> entry == null || entry.name.isBlank());
        }

        public String formatRow(int rank, String name, int score) {
            String safeName = name == null || name.isBlank() ? "Player" : name;
            return rowFormat
                .replace("{rank}", String.valueOf(rank))
                .replace("{name}", safeName)
                .replace("{score}", String.valueOf(Math.max(0, score)));
        }

        public String formatScore(int score) {
            return formatScore(score, NetworkRankSort.MIOPOINT);
        }

        public String formatScore(int score, NetworkRankSort sortType) {
            NetworkRankSort resolved = sortType == null ? NetworkRankSort.MIOPOINT : sortType;
            return scoreFormatFor(resolved).replace("{score}", String.valueOf(Math.max(0, score)));
        }

        public String labelFor(NetworkRankSort sortType) {
            NetworkRankSort resolved = sortType == null ? NetworkRankSort.MIOPOINT : sortType;
            return switch (resolved) {
                case MIOPOINT -> text(sort.miopointLabel, resolved.defaultLabel());
                case DIAMOND -> text(sort.diamondLabel, resolved.defaultLabel());
                case COIN -> text(sort.coinLabel, resolved.defaultLabel());
                case GAMES_PLAYED -> text(sort.gamesPlayedLabel, resolved.defaultLabel());
            };
        }

        public String scoreFormatFor(NetworkRankSort sortType) {
            NetworkRankSort resolved = sortType == null ? NetworkRankSort.MIOPOINT : sortType;
            return switch (resolved) {
                case MIOPOINT -> text(sort.miopointScoreFormat, resolved.defaultScoreFormat());
                case DIAMOND -> text(sort.diamondScoreFormat, resolved.defaultScoreFormat());
                case COIN -> text(sort.coinScoreFormat, resolved.defaultScoreFormat());
                case GAMES_PLAYED -> text(sort.gamesPlayedScoreFormat, resolved.defaultScoreFormat());
            };
        }
    }

    public static class SortSelectConfig {
        public float width = 260f;
        public float height = 58f;
        public float gap = 8f;
        public float optionHeight = 48f;
        public float optionSpacing = 6f;
        public float optionPadLeft = 8f;
        public float optionPadRight = 8f;
        public float optionPadTop = 4f;
        public float optionPadBottom = 4f;
        public String optionSlotBackground = "image_ui_if_bundle_reward1_bg_10";
        public String font = "FBUSV8C5EI_1";
        public String fontFallback = "FBUSV8C5EI_1";
        public float fontSize = 0.92f;
        public String fontColor = "F4E7B0FF";
        public String optionFont = "FBUSV8C5EI_1_outline";
        public String optionFontFallback = "FBUSV8C5EI_1";
        public float optionFontSize = 0.86f;
        public String optionFontColor = "F4E7B0FF";
        public String selectedColor = "FFE08AFF";
        public String defaultSort = "miopoint";
        public String miopointLabel = "miopoint";
        public String diamondLabel = "diamond";
        public String coinLabel = "coin";
        public String gamesPlayedLabel = "games played";
        public String miopointScoreFormat = "{score} MP";
        public String diamondScoreFormat = "{score}";
        public String coinScoreFormat = "{score}";
        public String gamesPlayedScoreFormat = "{score}";

        public void sanitize() {
            width = positive(width, 260f);
            height = positive(height, 58f);
            gap = nonNegative(gap, 8f);
            optionHeight = positive(optionHeight, 48f);
            optionSpacing = nonNegative(optionSpacing, 6f);
            optionPadLeft = nonNegative(optionPadLeft, 8f);
            optionPadRight = nonNegative(optionPadRight, 8f);
            optionPadTop = nonNegative(optionPadTop, 4f);
            optionPadBottom = nonNegative(optionPadBottom, 4f);
            optionSlotBackground = text(optionSlotBackground, "image_ui_if_bundle_reward1_bg_10");
            font = text(font, "FBUSV8C5EI_1");
            fontFallback = text(fontFallback, "FBUSV8C5EI_1");
            fontSize = positive(fontSize, 0.92f);
            fontColor = text(fontColor, "F4E7B0FF");
            optionFont = text(optionFont, "FBUSV8C5EI_1_outline");
            optionFontFallback = text(optionFontFallback, "FBUSV8C5EI_1");
            optionFontSize = positive(optionFontSize, 0.86f);
            optionFontColor = text(optionFontColor, "F4E7B0FF");
            selectedColor = text(selectedColor, "FFE08AFF");
            defaultSort = text(defaultSort, "miopoint");
            miopointLabel = text(miopointLabel, "miopoint");
            diamondLabel = text(diamondLabel, "diamond");
            coinLabel = text(coinLabel, "coin");
            gamesPlayedLabel = text(gamesPlayedLabel, "games played");
            miopointScoreFormat = text(miopointScoreFormat, "{score} MP");
            diamondScoreFormat = text(diamondScoreFormat, "{score}");
            coinScoreFormat = text(coinScoreFormat, "{score}");
            gamesPlayedScoreFormat = text(gamesPlayedScoreFormat, "{score}");
        }

        public NetworkRankSort initialSort() {
            return NetworkRankSort.parse(defaultSort);
        }
    }

    public static class RankEntryConfig {
        public String name = "";
        public int score;
        public boolean you;
        public String pictureId = "";

        public void sanitize() {
            if (name == null) {
                name = "";
            } else {
                name = name.trim();
            }
            if (score < 0) {
                score = 0;
            }
            if (pictureId == null) {
                pictureId = "";
            } else {
                pictureId = pictureId.trim();
            }
        }
    }
}


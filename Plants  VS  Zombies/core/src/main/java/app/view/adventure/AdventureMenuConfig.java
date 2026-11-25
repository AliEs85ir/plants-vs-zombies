package app.view.adventure;

import app.enums.SeasonType;
import app.view.mainmenu.config.MainMenuEasings;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class AdventureMenuConfig {
    public BackgroundConfig background = new BackgroundConfig();
    public CarouselConfig carousel = new CarouselConfig();
    public GeometryConfig geometry = new GeometryConfig();
    public ChapterStyleConfig chapter = new ChapterStyleConfig();
    public RotationConfig rotation = new RotationConfig();
    public DepthConfig depth = new DepthConfig();
    public ArrowsConfig arrows = new ArrowsConfig();
    public TitleConfig title = new TitleConfig();
    public ProgressConfig progress = new ProgressConfig();
    public LevelIconConfig levelIcon = new LevelIconConfig();
    public EnterButtonConfig enterButton = new EnterButtonConfig();
    public HoverOverlayConfig hoverOverlay = new HoverOverlayConfig();
    public BackButtonConfig backButton = new BackButtonConfig();
    public String initialFrontId = "egypt";
    public ArrayList<ChapterConfig> chapters = new ArrayList<>();

    public AdventureMenuConfig sanitize() {
        if (background == null) {
            background = new BackgroundConfig();
        }
        if (carousel == null) {
            carousel = new CarouselConfig();
        }
        if (geometry == null) {
            geometry = new GeometryConfig();
        }
        if (chapter == null) {
            chapter = new ChapterStyleConfig();
        }
        if (rotation == null) {
            rotation = new RotationConfig();
        }
        if (depth == null) {
            depth = new DepthConfig();
        }
        if (arrows == null) {
            arrows = new ArrowsConfig();
        }
        if (title == null) {
            title = new TitleConfig();
        }
        if (progress == null) {
            progress = new ProgressConfig();
        }
        if (levelIcon == null) {
            levelIcon = new LevelIconConfig();
        }
        if (enterButton == null) {
            enterButton = new EnterButtonConfig();
        }
        if (hoverOverlay == null) {
            hoverOverlay = new HoverOverlayConfig();
        }
        if (backButton == null) {
            backButton = new BackButtonConfig();
        }
        if (chapters == null) {
            chapters = new ArrayList<>();
        }
        background.sanitize();
        carousel.sanitize();
        geometry.sanitize();
        chapter.sanitize();
        rotation.sanitize();
        depth.sanitize();
        arrows.sanitize();
        title.sanitize();
        progress.sanitize();
        levelIcon.sanitize();
        enterButton.sanitize();
        hoverOverlay.sanitize();
        backButton.sanitize();
        for (ChapterConfig item : chapters) {
            if (item != null) {
                item.sanitize();
            }
        }
        chapters.removeIf(item -> item == null || !item.visible || item.id.isBlank());
        if (chapters.isEmpty()) {
            chapters.add(chapter("egypt", "ANCIENT_EGYPT", "IMAGE_UI_UNIVERSE_WORLDS_EGYPT",
                "menu/image/egypt_universe_title.png", "menu/image/egypt_universe_level.png",
                "menu/image/egypt_universe_btn.png", "menu/image/btn3.png", 0));
            chapters.add(chapter("iceage", "FROSTBITE_CAVES", "IMAGE_UI_UNIVERSE_WORLDS_ICEAGE",
                "menu/image/iceage_universe_title.png", "menu/image/iceage_universe_level.png",
                "menu/image/iceage_universe_btn.png", "menu/image/btn5.png", 1));
            chapters.add(chapter("dark", "DARK_AGES", "IMAGE_UI_UNIVERSE_WORLDS_DARK",
                "menu/image/dark_universe_title.png", "menu/image/dark_universe_level.png",
                "menu/image/dark_universe_btn.png", "menu/image/btn6.png", 2));
            chapters.add(chapter("beach", "BIG_WAVE_BEACH", "IMAGE_UI_UNIVERSE_WORLDS_BEACH",
                "menu/image/beach_universe_title.png", "menu/image/beach_universe_level.png",
                "menu/image/beach_universe_btn.png", "menu/image/btn2.png", 3));
        }
        chapters.sort(Comparator.comparingInt(item -> item.order));
        if (initialFrontId == null || initialFrontId.isBlank()) {
            initialFrontId = chapters.get(0).id;
        } else {
            initialFrontId = initialFrontId.trim().toLowerCase(Locale.ROOT);
        }
        return this;
    }

    public List<ChapterConfig> visibleChapters() {
        List<ChapterConfig> visible = new ArrayList<>();
        for (ChapterConfig item : chapters) {
            if (item != null && item.visible) {
                visible.add(item);
            }
        }
        return visible;
    }

    public int indexOf(String id) {
        if (id == null || id.isBlank()) {
            return 0;
        }
        String key = id.trim().toLowerCase(Locale.ROOT);
        List<ChapterConfig> visible = visibleChapters();
        for (int i = 0; i < visible.size(); i++) {
            if (key.equals(visible.get(i).id)) {
                return i;
            }
        }
        return 0;
    }

    private static ChapterConfig chapter(
        String id,
        String season,
        String worldAsset,
        String titleAsset,
        String levelIcon,
        String enterButtonAsset,
        String levelProgressBackground,
        int order
    ) {
        ChapterConfig item = new ChapterConfig();
        item.id = id;
        item.season = season;
        item.worldAsset = worldAsset;
        item.titleAsset = titleAsset;
        item.levelIcon = levelIcon;
        item.enterButtonAsset = enterButtonAsset;
        item.levelProgressBackground = levelProgressBackground;
        item.order = order;
        item.sanitize();
        return item;
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
        if (value < 0f) {
            return 0f;
        }
        if (value > 1f) {
            return 1f;
        }
        return value;
    }

    public static class BackgroundConfig {
        public String path = "menu/bg/adventure.png";
        public float sourceWidth = 1920f;
        public float sourceHeight = 1080f;
        public String scaleMode = "cover";
        public float offsetX;
        public float offsetY;

        public void sanitize() {
            path = text(path, "menu/bg/adventure.png");
            sourceWidth = positive(sourceWidth, 1920f);
            sourceHeight = positive(sourceHeight, 1080f);
            if (scaleMode == null || scaleMode.isBlank()) {
                scaleMode = "cover";
            }
        }
    }

    public static class CarouselConfig {
        public float anchorX = 0.5f;
        public float anchorY = 0.52f;
        public float x;
        public float y;
        public float offsetX;
        public float offsetY;
        public float scale = 1f;
        public float centerX;
        public float centerY;
        public float frontOffsetX;
        public float frontOffsetY;

        public void sanitize() {
            if (Float.isNaN(anchorX)) {
                anchorX = 0.5f;
            }
            if (Float.isNaN(anchorY)) {
                anchorY = 0.52f;
            }
            scale = positive(scale, 1f);
        }

        public float resolvedCenterX(float areaWidth) {
            return areaWidth * anchorX + x + offsetX + centerX;
        }

        public float resolvedCenterY(float areaHeight) {
            return areaHeight * anchorY + y + offsetY + centerY;
        }
    }

    public static class GeometryConfig {
        public String path = "ellipse";
        public float radius = 180f;
        public float radiusX = 210f;
        public float radiusY = 42f;
        public float depth = 1f;
        public float perspective = 560f;

        public void sanitize() {
            if (path == null || path.isBlank()) {
                path = "ellipse";
            } else {
                path = path.trim().toLowerCase(Locale.ROOT);
            }
            radius = positive(radius, 180f);
            if (radiusX <= 0f || Float.isNaN(radiusX)) {
                radiusX = radius;
            }
            if (radiusY < 0f || Float.isNaN(radiusY)) {
                radiusY = 0f;
            }
            if ("circle".equals(path)) {
                radiusX = radius;
                radiusY = radius;
            }
            depth = positive(depth, 1f);
            if (Float.isNaN(perspective) || perspective < 0f) {
                perspective = 560f;
            }
        }
    }

    public static class ChapterStyleConfig {
        public float width = 128f;
        public float height = 128f;
        public float scale = 1f;
        public float minScale = 0.72f;
        public float maxScale = 1f;
        public float frontScale = 1f;
        public float hoverScale = 1.08f;
        public float hoverDarkness = 0.05f;
        public float hoverBobAmplitude = 8f;
        public float hoverBobSpeed = 5.5f;
        public float spacing;
        public int maxVisible;
        public float hoverDuration = 0.12f;
        public float inputPad;

        public void sanitize() {
            width = positive(width, 128f);
            height = positive(height, 128f);
            scale = positive(scale, 1f);
            minScale = positive(minScale, 0.72f);
            maxScale = positive(maxScale, 1f);
            if (maxScale < minScale) {
                float swap = maxScale;
                maxScale = minScale;
                minScale = swap;
            }
            frontScale = positive(frontScale, 1f);
            hoverScale = positive(hoverScale, 1.08f);
            hoverDarkness = clamp01(hoverDarkness, 0.05f);
            hoverBobAmplitude = nonNegative(hoverBobAmplitude, 8f);
            hoverBobSpeed = positive(hoverBobSpeed, 5.5f);
            spacing = nonNegative(spacing, 0f);
            if (maxVisible < 0) {
                maxVisible = 0;
            }
            hoverDuration = nonNegative(hoverDuration, 0.12f);
            inputPad = nonNegative(inputPad, 0f);
        }

        public float drawWidth(float globalScale) {
            return width * scale * globalScale;
        }

        public float drawHeight(float globalScale) {
            return height * scale * globalScale;
        }
    }

    public static class RotationConfig {
        public float speed = 2f;
        public float duration = 0.55f;
        public float step;
        public String easing = "sineInOut";
        public String direction = "nextOnRight";
        public float angularSpacing;
        public float frontAngle;
        public boolean queueInput = true;

        public void sanitize() {
            speed = positive(speed, 2f);
            duration = nonNegative(duration, 0.55f);
            step = nonNegative(step, 0f);
            if (easing == null || easing.isBlank()) {
                easing = "sineInOut";
            }
            if (direction == null || direction.isBlank()) {
                direction = "nextOnRight";
            } else {
                direction = direction.trim();
            }
            angularSpacing = nonNegative(angularSpacing, 0f);
            if (Float.isNaN(frontAngle)) {
                frontAngle = 0f;
            }
        }

        public boolean nextOnRight() {
            String value = direction.toLowerCase(Locale.ROOT).replace("_", "").replace("-", "");
            return !"nextonleft".equals(value) && !"left".equals(value);
        }

        public Interpolation interpolation() {
            return MainMenuEasings.parse(easing);
        }

        public float stepRadians(int count) {
            if (count <= 0) {
                return 0f;
            }
            if (step > 0f) {
                return (float) Math.toRadians(step);
            }
            if (angularSpacing > 0f) {
                return (float) Math.toRadians(angularSpacing);
            }
            return (float) (Math.PI * 2.0 / count);
        }

        public float durationSeconds(float stepRadians) {
            if (duration > 0f) {
                return duration;
            }
            if (speed <= 0f || stepRadians <= 0f) {
                return 0f;
            }
            return stepRadians / speed;
        }
    }

    public static class DepthConfig {
        public float backScale = 0.72f;
        public float frontScale = 1f;
        public float verticalShift = 16f;
        public boolean fadeBack = true;
        public float backAlpha = 0.72f;
        public float frontAlpha = 1f;
        public boolean shadeEnabled = true;
        public float shadeBack = 0.20f;
        public float shadeFront = 0f;

        public void sanitize() {
            backScale = positive(backScale, 0.72f);
            frontScale = positive(frontScale, 1f);
            if (Float.isNaN(verticalShift)) {
                verticalShift = 16f;
            }
            backAlpha = clamp01(backAlpha, 0.72f);
            frontAlpha = clamp01(frontAlpha, 1f);
            shadeBack = clamp01(shadeBack, 0.20f);
            shadeFront = clamp01(shadeFront, 0f);
        }
    }

    public static class ArrowsConfig {
        public float width = 72f;
        public float height = 72f;
        public float scale = 1f;
        public float distance = 280f;
        public float horizontalOffset;
        public float verticalOffset;
        public float hoverScale = 1.08f;
        public float pressedScale = 0.96f;
        public float hoverDuration = 0.1f;
        public String easing = "sineOut";
        public float alpha = 1f;
        public float rotation;
        public float inputWidth;
        public float inputHeight;
        public ArrowSideConfig left = new ArrowSideConfig();
        public ArrowSideConfig right = new ArrowSideConfig();

        public void sanitize() {
            if (left == null) {
                left = new ArrowSideConfig();
            }
            if (right == null) {
                right = new ArrowSideConfig();
            }
            width = positive(width, 72f);
            height = positive(height, 72f);
            scale = positive(scale, 1f);
            distance = nonNegative(distance, 280f);
            hoverScale = positive(hoverScale, 1.08f);
            pressedScale = positive(pressedScale, 0.96f);
            hoverDuration = nonNegative(hoverDuration, 0.1f);
            if (easing == null || easing.isBlank()) {
                easing = "sineOut";
            }
            alpha = clamp01(alpha, 1f);
            if (Float.isNaN(rotation)) {
                rotation = 0f;
            }
            inputWidth = nonNegative(inputWidth, 0f);
            inputHeight = nonNegative(inputHeight, 0f);
            left.sanitize("menu/icon/arrow_left.png");
            right.sanitize("menu/icon/arrow_right.png");
        }

        public Interpolation interpolation() {
            return MainMenuEasings.parse(easing);
        }

        public float drawWidth(ArrowSideConfig side, float globalScale) {
            float local = side.width > 0f ? side.width : width;
            float localScale = side.scale > 0f ? side.scale : scale;
            return local * localScale * globalScale;
        }

        public float drawHeight(ArrowSideConfig side, float globalScale) {
            float local = side.height > 0f ? side.height : height;
            float localScale = side.scale > 0f ? side.scale : scale;
            return local * localScale * globalScale;
        }

        public float hitWidth(ArrowSideConfig side, float globalScale) {
            float visual = drawWidth(side, globalScale);
            return inputWidth > 0f ? Math.max(visual, inputWidth * globalScale) : visual;
        }

        public float hitHeight(ArrowSideConfig side, float globalScale) {
            float visual = drawHeight(side, globalScale);
            return inputHeight > 0f ? Math.max(visual, inputHeight * globalScale) : visual;
        }

        public float resolvedAlpha(ArrowSideConfig side) {
            return side.alpha > 0f ? clamp01(side.alpha, alpha) : alpha;
        }

        public float resolvedRotation(ArrowSideConfig side) {
            return side.rotation != 0f ? side.rotation : rotation;
        }
    }

    public static class ArrowSideConfig {
        public String assetPath = "";
        public float offsetX;
        public float offsetY;
        public float width;
        public float height;
        public float scale;
        public float alpha;
        public float rotation;

        public void sanitize(String fallbackPath) {
            if (assetPath == null || assetPath.isBlank()) {
                assetPath = fallbackPath;
            }
            width = nonNegative(width, 0f);
            height = nonNegative(height, 0f);
            scale = nonNegative(scale, 0f);
            alpha = nonNegative(alpha, 0f);
            if (Float.isNaN(rotation)) {
                rotation = 0f;
            }
        }
    }

    public static class TitleConfig {
        public float offsetX;
        public float offsetY = 118f;
        public float width = 420f;
        public float height = 86f;
        public float scale = 1f;
        public float alpha = 1f;
        public boolean visibleOnHover = true;

        public void sanitize() {
            width = positive(width, 420f);
            height = positive(height, 86f);
            scale = positive(scale, 1f);
            alpha = clamp01(alpha, 1f);
        }

        public float drawWidth(float globalScale) {
            return width * scale * globalScale;
        }

        public float drawHeight(float globalScale) {
            return height * scale * globalScale;
        }
    }

    public static class ProgressConfig {
        public float offsetX = 18f;
        public float offsetY = -118f;
        public float width = 168f;
        public float height = 48f;
        public float scale = 1f;
        public String backgroundPath = "menu/image/bg_levels.png";
        public String format = "{played} / {total}";
        public String font = "BRIANNETOD";
        public String fontFallback = "BRIANNETOD";
        public float fontSize = 1.05f;
        public String color = "F4E7B0";
        public float textOffsetX = 10f;
        public float textOffsetY;
        public boolean visibleOnHover = true;

        public void sanitize() {
            width = positive(width, 168f);
            height = positive(height, 48f);
            scale = positive(scale, 1f);
            backgroundPath = text(backgroundPath, "menu/image/bg_levels.png");
            format = text(format, "{played} / {total}");
            font = text(font, "BRIANNETOD");
            fontFallback = text(fontFallback, "BRIANNETOD");
            fontSize = positive(fontSize, 1.05f);
            color = text(color, "F4E7B0");
        }

        public float drawWidth(float globalScale) {
            return width * scale * globalScale;
        }

        public float drawHeight(float globalScale) {
            return height * scale * globalScale;
        }

        public String formatted(int played, int total) {
            return format
                .replace("{played}", String.valueOf(Math.max(0, played)))
                .replace("{total}", String.valueOf(Math.max(0, total)));
        }
    }

    public static class LevelIconConfig {
        public float offsetX = -36f;
        public float offsetY;
        public float width = 56f;
        public float height = 56f;
        public float scale = 1f;
        public float alpha = 1f;

        public void sanitize() {
            width = positive(width, 56f);
            height = positive(height, 56f);
            scale = positive(scale, 1f);
            alpha = clamp01(alpha, 1f);
        }

        public float drawWidth(float globalScale) {
            return width * scale * globalScale;
        }

        public float drawHeight(float globalScale) {
            return height * scale * globalScale;
        }
    }

    public static class EnterButtonConfig {
        public boolean visible = true;
        public boolean visibleOnHover = true;
        public float offsetX;
        public float offsetY = -178f;
        public float width = 220f;
        public float height = 72f;
        public float scale = 1f;
        public float hoverScale = 1.06f;
        public float pressedScale = 0.96f;
        public float hoverDuration = 0.1f;
        public String easing = "sineOut";

        public void sanitize() {
            width = positive(width, 220f);
            height = positive(height, 72f);
            scale = positive(scale, 1f);
            hoverScale = positive(hoverScale, 1.06f);
            pressedScale = positive(pressedScale, 0.96f);
            hoverDuration = nonNegative(hoverDuration, 0.1f);
            if (easing == null || easing.isBlank()) {
                easing = "sineOut";
            }
            if (Float.isNaN(offsetX)) {
                offsetX = 0f;
            }
            if (Float.isNaN(offsetY)) {
                offsetY = -178f;
            }
        }

        public Interpolation interpolation() {
            return MainMenuEasings.parse(easing);
        }

        public float drawWidth(float globalScale) {
            return width * scale * globalScale;
        }

        public float drawHeight(float globalScale) {
            return height * scale * globalScale;
        }
    }

    public static class HoverOverlayConfig {
        /** Uniform scale for the whole hover stack (title + progress + play). */
        public float scale = 1f;
        public boolean useStackedGaps = true;
        public float titleToProgressGap = 8f;
        public float progressToEnterGap = 8f;

        public void sanitize() {
            scale = positive(scale, 1f);
            if (Float.isNaN(titleToProgressGap)) {
                titleToProgressGap = 8f;
            }
            if (Float.isNaN(progressToEnterGap)) {
                progressToEnterGap = 8f;
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

    public static class ChapterConfig {
        public String id = "";
        public String season = "";
        public String worldAsset = "";
        public String titleAsset = "";
        public String levelIcon = "";
        public String enterButtonAsset = "";
        public String levelProgressBackground = "";
        public String action = "open_level_select";
        public int order;
        public boolean unlocked = true;
        public boolean selectable = true;
        public boolean visible = true;
        public int totalLevels;
        public int playedLevels;
        public String description = "";
        public String specialIcon = "";
        public String lockedIcon = "";
        public String badge = "";
        public String subtitle = "";
        public String extraProgress = "";
        public String reward = "";
        public String difficulty = "";
        public String customAnimation = "";

        public void sanitize() {
            if (id == null) {
                id = "";
            } else {
                id = id.trim().toLowerCase(Locale.ROOT);
            }
            if (season == null) {
                season = "";
            } else {
                season = season.trim().toUpperCase(Locale.ROOT);
            }
            if (worldAsset == null) {
                worldAsset = "";
            }
            if (titleAsset == null) {
                titleAsset = "";
            }
            if (levelIcon == null) {
                levelIcon = "";
            }
            if (enterButtonAsset == null) {
                enterButtonAsset = "";
            }
            if (levelProgressBackground == null) {
                levelProgressBackground = "";
            }
            if (action == null || action.isBlank()) {
                action = "open_level_select";
            } else {
                action = action.trim().toLowerCase(Locale.ROOT);
            }
            if (description == null) {
                description = "";
            }
            if (specialIcon == null) {
                specialIcon = "";
            }
            if (lockedIcon == null) {
                lockedIcon = "";
            }
            if (badge == null) {
                badge = "";
            }
            if (subtitle == null) {
                subtitle = "";
            }
            if (extraProgress == null) {
                extraProgress = "";
            }
            if (reward == null) {
                reward = "";
            }
            if (difficulty == null) {
                difficulty = "";
            }
            if (customAnimation == null) {
                customAnimation = "";
            }
            if (totalLevels < 0) {
                totalLevels = 0;
            }
            if (playedLevels < 0) {
                playedLevels = 0;
            }
        }

        public SeasonType seasonType() {
            if (season == null || season.isBlank()) {
                return null;
            }
            try {
                return SeasonType.valueOf(season);
            } catch (IllegalArgumentException exception) {
                return null;
            }
        }

        public boolean interactive() {
            return visible && unlocked && selectable && !"none".equalsIgnoreCase(action);
        }
    }
}

package app.view.levelselect;

import app.enums.SeasonType;
import app.view.mainmenu.config.MainMenuEasings;
import com.badlogic.gdx.math.Interpolation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class LevelSelectMenuConfig {
    public LayoutConfig layout = new LayoutConfig();
    public CentralFloatConfig centralFloat = new CentralFloatConfig();
    public LevelFloatConfig levelFloat = new LevelFloatConfig();
    public HoverConfig hover = new HoverConfig();
    public BackButtonConfig backButton = new BackButtonConfig();
    public ArrayList<ChapterConfig> chapters = new ArrayList<>();

    public LevelSelectMenuConfig sanitize() {
        if (layout == null) {
            layout = new LayoutConfig();
        }
        if (centralFloat == null) {
            centralFloat = new CentralFloatConfig();
        }
        if (levelFloat == null) {
            levelFloat = new LevelFloatConfig();
        }
        if (hover == null) {
            hover = new HoverConfig();
        }
        if (backButton == null) {
            backButton = new BackButtonConfig();
        }
        if (chapters == null) {
            chapters = new ArrayList<>();
        }
        layout.sanitize();
        centralFloat.sanitize();
        levelFloat.sanitize();
        hover.sanitize();
        backButton.sanitize();
        for (ChapterConfig chapter : chapters) {
            if (chapter != null) {
                chapter.sanitize();
            }
        }
        chapters.removeIf(chapter -> chapter == null || !chapter.visible || chapter.id.isBlank());
        if (chapters.isEmpty()) {
            chapters.add(egypt());
            chapters.add(dark());
            chapters.add(beach());
            chapters.add(ice());
        }
        chapters.sort(Comparator.comparingInt(chapter -> chapter.order));
        return this;
    }

    public List<ChapterConfig> visibleChapters() {
        List<ChapterConfig> visible = new ArrayList<>();
        for (ChapterConfig chapter : chapters) {
            if (chapter != null && chapter.visible) {
                visible.add(chapter);
            }
        }
        return visible;
    }

    public ChapterConfig chapterForSeason(SeasonType season) {
        if (season == null) {
            return visibleChapters().isEmpty() ? null : visibleChapters().get(0);
        }
        for (ChapterConfig chapter : visibleChapters()) {
            if (season == chapter.seasonType()) {
                return chapter;
            }
        }
        String key = season.name().toLowerCase(Locale.ROOT);
        for (ChapterConfig chapter : visibleChapters()) {
            if (key.contains(chapter.id) || chapter.id.contains(key)) {
                return chapter;
            }
        }
        return visibleChapters().isEmpty() ? null : visibleChapters().get(0);
    }

    public ChapterConfig chapterById(String id) {
        if (id == null || id.isBlank()) {
            return null;
        }
        String key = id.trim().toLowerCase(Locale.ROOT);
        for (ChapterConfig chapter : visibleChapters()) {
            if (key.equals(chapter.id)) {
                return chapter;
            }
        }
        return null;
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

    static String text(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value;
    }

    static String normalizePath(String value) {
        return value == null ? "" : value.trim().replace('\\', '/');
    }

    private static ChapterConfig egypt() {
        return chapter(
            "egypt",
            "ANCIENT_EGYPT",
            "egypt",
            "768/INITIAL/WORLDMAP/ZOMBOSS_NODE_EGYPT/ZOMBOSS_NODE_EGYPT.PAM",
            "active",
            "768/INITIAL/ZOMBIE/ZOMBIE_EGYPT_RA/ZOMBIE_EGYPT_RA.PAM",
            "idle",
            "768/INITIAL/WORLDMAP/DANGER_NODE_EGYPT/DANGER_NODE_EGYPT.PAM",
            0
        );
    }

    private static ChapterConfig dark() {
        return chapter(
            "dark",
            "DARK_AGES",
            "dark",
            "768/FULL/WORLDMAP/ZOMBOSS_NODE_DARK/ZOMBOSS_NODE_DARK.PAM",
            "active",
            "768/FULL/ZOMBIE/ZOMBIE_DARK_WIZARD_VETERAN/ZOMBIE_DARK_WIZARD_VETERAN.PAM",
            "idle",
            "768/FULL/WORLDMAP/DANGER_NODE_DARK/DANGER_NODE_DARK.PAM",
            1
        );
    }

    private static ChapterConfig beach() {
        return chapter(
            "beach",
            "BIG_WAVE_BEACH",
            "beach",
            "768/FULL/WORLDMAP/ZOMBOSS_NODE_BEACH/ZOMBOSS_NODE_BEACH.PAM",
            "active",
            "768/FULL/ZOMBIE/ZOMBIE_BEACH_SURFER/ZOMBIE_BEACH_SURFER.PAM",
            "idle",
            "768/FULL/WORLDMAP/DANGER_NODE_BEACH/DANGER_NODE_BEACH.PAM",
            2
        );
    }

    private static ChapterConfig ice() {
        return chapter(
            "ice",
            "FROSTBITE_CAVES",
            "ice",
            "768/FULL/WORLDMAP/ZOMBOSS_NODE_ICEAGE/ZOMBOSS_NODE_ICEAGE.PAM",
            "active",
            "768/FULL/ZOMBIE/ZOMBIE_ICEAGE_GARGANTUAR/ZOMBIE_ICEAGE_GARGANTUAR.PAM",
            "idle",
            "768/FULL/WORLDMAP/DANGER_NODE_ICEAGE/DANGER_NODE_ICEAGE.PAM",
            3
        );
    }

    private static ChapterConfig chapter(
        String id,
        String season,
        String background,
        String mainPam,
        String mainClip,
        String subPam,
        String subClip,
        String levelPam,
        int order
    ) {
        ChapterConfig item = new ChapterConfig();
        item.id = id;
        item.season = season;
        item.background = background;
        item.order = order;
        item.main.pamPath = mainPam;
        item.main.clip = mainClip;
        item.main.layer = 0;
        item.subordinate.pamPath = subPam;
        item.subordinate.clip = subClip;
        item.subordinate.layer = 1;
        item.levelReference.visible = false;
        item.level.pamPath = levelPam;
        item.sanitize();
        return item;
    }

    public static class LayoutConfig {
        public float centralAnchorX = 0.5f;
        public float centralAnchorY = 0.5f;
        public float centralGroupX;
        public float centralGroupY;
        public float centralGroupScale = 1f;
        public float centralHitWidth = 280f;
        public float centralHitHeight = 340f;
        public float levelsOffsetX;
        public float levelsOffsetY;
        public float levelLineDistance = 210f;
        public float levelSpacingX = 148f;
        public float levelSpacingY;
        public float leftOffsetX;
        public float leftOffsetY;
        public float rightOffsetX;
        public float rightOffsetY;
        public float levelScale = 0.22f;
        public float frontScale = 1f;
        public float backScale = 0.78f;
        public float levelHitPad = 12f;
        public float frontDepth = 1f;
        public float backDepth;
        public float centralGroupDepth = 0.5f;
        public float frontAlpha = 1f;
        public float backAlpha = 0.72f;
        public float frontDarkness;
        public float backDarkness = 0.28f;
        public float depthAlpha = 0.72f;
        public float depthDarkness = 0.28f;
        public boolean applyDepthFade = true;
        public boolean applyDepthDarkness = true;

        public void sanitize() {
            if (Float.isNaN(centralAnchorX)) {
                centralAnchorX = 0.5f;
            }
            if (Float.isNaN(centralAnchorY)) {
                centralAnchorY = 0.5f;
            }
            centralGroupX = finite(centralGroupX, 0f);
            centralGroupY = finite(centralGroupY, 0f);
            centralGroupScale = positive(centralGroupScale, 1f);
            centralHitWidth = positive(centralHitWidth, 280f);
            centralHitHeight = positive(centralHitHeight, 340f);
            levelsOffsetX = finite(levelsOffsetX, 0f);
            levelsOffsetY = finite(levelsOffsetY, 0f);
            levelLineDistance = nonNegative(levelLineDistance, 210f);
            levelSpacingX = nonNegative(levelSpacingX, 148f);
            levelSpacingY = finite(levelSpacingY, 0f);
            leftOffsetX = finite(leftOffsetX, 0f);
            leftOffsetY = finite(leftOffsetY, 0f);
            rightOffsetX = finite(rightOffsetX, 0f);
            rightOffsetY = finite(rightOffsetY, 0f);
            levelScale = positive(levelScale, 0.22f);
            frontScale = positive(frontScale, 1f);
            backScale = positive(backScale, 0.78f);
            levelHitPad = nonNegative(levelHitPad, 12f);
            frontDepth = finite(frontDepth, 1f);
            backDepth = finite(backDepth, 0f);
            centralGroupDepth = finite(centralGroupDepth, 0.5f);
            frontAlpha = clamp01(frontAlpha, 1f);
            depthAlpha = clamp01(depthAlpha, 0.72f);
            if (Float.isNaN(backAlpha) || backAlpha < 0f) {
                backAlpha = depthAlpha;
            }
            backAlpha = clamp01(backAlpha, 0.72f);
            frontDarkness = clamp01(frontDarkness, 0f);
            depthDarkness = clamp01(depthDarkness, 0.28f);
            backDarkness = clamp01(backDarkness, 0.28f);
        }

        public float resolvedCentralX(float areaWidth) {
            return areaWidth * centralAnchorX + centralGroupX;
        }

        public float resolvedCentralY(float areaHeight) {
            return areaHeight * centralAnchorY + centralGroupY;
        }

        public float resolvedAxisX(float areaWidth) {
            return resolvedCentralX(areaWidth) + levelsOffsetX;
        }

        public float resolvedAxisY(float areaHeight) {
            return resolvedCentralY(areaHeight) + levelsOffsetY;
        }

        public float nodeScale(boolean front) {
            return levelScale * (front ? frontScale : backScale);
        }
    }

    public static class CentralFloatConfig {
        public boolean enabled = true;
        public float amplitudeX;
        public float amplitudeY = 8f;
        public float speedX = 0.7f;
        public float speedY = 1.15f;
        public float phaseX;
        public float phaseY = 0.35f;

        public void sanitize() {
            amplitudeX = nonNegative(amplitudeX, 0f);
            amplitudeY = nonNegative(amplitudeY, 8f);
            speedX = nonNegative(speedX, 0.7f);
            speedY = nonNegative(speedY, 1.15f);
            phaseX = finite(phaseX, 0f);
            phaseY = finite(phaseY, 0.35f);
        }
    }

    public static class LevelFloatConfig {
        public boolean enabled = true;
        public float baseAmplitude = 6f;
        public float amplitudeVariation = 3.5f;
        public float baseSpeed = 1.35f;
        public float speedVariation = 0.55f;
        public float phaseVariation = 1.1f;

        public void sanitize() {
            baseAmplitude = nonNegative(baseAmplitude, 6f);
            amplitudeVariation = nonNegative(amplitudeVariation, 3.5f);
            baseSpeed = nonNegative(baseSpeed, 1.35f);
            speedVariation = nonNegative(speedVariation, 0.55f);
            phaseVariation = finite(phaseVariation, 1.1f);
        }
    }

    public static class HoverConfig {
        public boolean levelEnabled = true;
        public float levelScale = 1.1f;
        public float levelOffsetY = 10f;
        public float levelAlpha = 1f;
        public float levelDarkness = 0.08f;
        public float levelDuration = 0.12f;
        public boolean centerEnabled = true;
        public float centerScale = 1.07f;
        public float centerOffsetY = 8f;
        public float centerAlpha = 1f;
        public float centerDarkness = 0.1f;
        public float centerDuration = 0.12f;
        public float centerBobAmplitude = 6f;
        public float centerBobSpeed = 4.8f;

        public void sanitize() {
            levelScale = positive(levelScale, 1.1f);
            levelOffsetY = finite(levelOffsetY, 10f);
            levelAlpha = clamp01(levelAlpha, 1f);
            levelDarkness = clamp01(levelDarkness, 0.08f);
            levelDuration = nonNegative(levelDuration, 0.12f);
            centerScale = positive(centerScale, 1.07f);
            centerOffsetY = finite(centerOffsetY, 8f);
            centerAlpha = clamp01(centerAlpha, 1f);
            centerDarkness = clamp01(centerDarkness, 0.1f);
            centerDuration = nonNegative(centerDuration, 0.12f);
            centerBobAmplitude = nonNegative(centerBobAmplitude, 6f);
            centerBobSpeed = positive(centerBobSpeed, 4.8f);
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

    public static class AnimationConfig {
        public String pamPath = "";
        public String clip = "";
        public float offsetX;
        public float offsetY;
        public float scale = 1f;
        public float rotation;
        public float alpha = 1f;
        public int layer;
        public boolean flipX;
        public boolean flipY;
        public boolean loop = true;
        public boolean visible = true;

        public void sanitize() {
            pamPath = normalizePath(pamPath);
            clip = clip == null ? "" : clip.trim();
            offsetX = finite(offsetX, 0f);
            offsetY = finite(offsetY, 0f);
            scale = positive(scale, 1f);
            rotation = finite(rotation, 0f);
            alpha = clamp01(alpha, 1f);
        }

        public boolean present() {
            return visible && pamPath != null && !pamPath.isBlank();
        }
    }

    public static class LevelAnimationConfig {
        public String pamPath = "";
        public String lockedIdleClip = "locked_idle";
        public String unlockAnimationClip = "unlocked_animation";
        public String unlockedIdleClip = "unlocked_idle";
        public float offsetX;
        public float offsetY;
        public float scale = 1f;
        public float rotation;
        public float alpha = 1f;
        public boolean flipX;
        public boolean flipY;
        public boolean flipTowardCenter;
        public boolean flipLeft;
        public boolean flipRight;

        public void sanitize() {
            pamPath = normalizePath(pamPath);
            lockedIdleClip = text(lockedIdleClip, "locked_idle");
            unlockAnimationClip = text(unlockAnimationClip, "unlocked_animation");
            unlockedIdleClip = text(unlockedIdleClip, "unlocked_idle");
            offsetX = finite(offsetX, 0f);
            offsetY = finite(offsetY, 0f);
            scale = positive(scale, 1f);
            rotation = finite(rotation, 0f);
            alpha = clamp01(alpha, 1f);
        }
    }

    public static class BackgroundConfig {
        public String path = "";
        public float sourceWidth = 1920f;
        public float sourceHeight = 1080f;
        public float offsetX;
        public float offsetY;

        public void sanitize() {
            path = normalizePath(path);
            sourceWidth = positive(sourceWidth, 1920f);
            sourceHeight = positive(sourceHeight, 1080f);
            offsetX = finite(offsetX, 0f);
            offsetY = finite(offsetY, 0f);
        }
    }

    public static class ChapterConfig {
        public String id = "";
        public String season = "";
        public String background = "";
        public BackgroundConfig backgroundLayout = new BackgroundConfig();
        public int order;
        public boolean visible = true;
        public int levelCount;
        public AnimationConfig main = new AnimationConfig();
        public AnimationConfig subordinate = new AnimationConfig();
        public AnimationConfig levelReference = new AnimationConfig();
        public LevelAnimationConfig level = new LevelAnimationConfig();

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
            background = normalizePath(background);
            if (backgroundLayout == null) {
                backgroundLayout = new BackgroundConfig();
            }
            backgroundLayout.sanitize();
            if (backgroundLayout.path.isBlank()) {
                backgroundLayout.path = resolveBackgroundPath(background, id);
            }
            if (levelCount < 0) {
                levelCount = 0;
            }
            if (main == null) {
                main = new AnimationConfig();
            }
            if (subordinate == null) {
                subordinate = new AnimationConfig();
            }
            if (levelReference == null) {
                levelReference = new AnimationConfig();
            }
            if (level == null) {
                level = new LevelAnimationConfig();
            }
            main.sanitize();
            subordinate.sanitize();
            levelReference.sanitize();
            level.sanitize();
            if (main.layer == subordinate.layer) {
                main.layer = 0;
                subordinate.layer = 1;
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

        public String resolvedBackgroundPath() {
            if (backgroundLayout != null && backgroundLayout.path != null && !backgroundLayout.path.isBlank()) {
                return backgroundLayout.path;
            }
            return resolveBackgroundPath(background, id);
        }
    }

    static String resolveBackgroundPath(String background, String id) {
        String value = normalizePath(background);
        if (value.isBlank()) {
            value = normalizePath(id);
        }
        if (value.isBlank()) {
            return "";
        }
        if (value.contains("/") || value.contains(".")) {
            return value;
        }
        return "menu/bg/" + value + ".png";
    }
}

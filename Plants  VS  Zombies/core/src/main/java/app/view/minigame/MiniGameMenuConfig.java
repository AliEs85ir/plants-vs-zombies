package app.view.minigame;

import app.view.mainmenu.config.MainMenuEasings;
import com.badlogic.gdx.math.Interpolation;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MiniGameMenuConfig {
    public String mapPath = "Maps/mini_game.tmx";
    public String bgLayer = "bg";
    public String mapLayer = "map";
    public String miniGamesLayer = "miniGames";
    public float mapPixelWidth;
    public float mapPixelHeight;
    public BackButtonConfig backButton = new BackButtonConfig();
    public ArrayList<HoverAnimationConfig> defaultHoverAnimations = new ArrayList<>();
    public ArrayList<GameConfig> games = new ArrayList<>();

    public MiniGameMenuConfig sanitize() {
        mapPath = text(mapPath, "Maps/mini_game.tmx");
        bgLayer = text(bgLayer, "bg");
        mapLayer = text(mapLayer, "map");
        miniGamesLayer = text(miniGamesLayer, "miniGames");
        if (mapPixelWidth < 0f || Float.isNaN(mapPixelWidth)) {
            mapPixelWidth = 0f;
        }
        if (mapPixelHeight < 0f || Float.isNaN(mapPixelHeight)) {
            mapPixelHeight = 0f;
        }
        if (backButton == null) {
            backButton = new BackButtonConfig();
        }
        if (defaultHoverAnimations == null) {
            defaultHoverAnimations = new ArrayList<>();
        }
        if (games == null) {
            games = new ArrayList<>();
        }
        backButton.sanitize();
        defaultHoverAnimations.removeIf(item -> item == null);
        for (HoverAnimationConfig animation : defaultHoverAnimations) {
            animation.sanitize();
        }
        defaultHoverAnimations.removeIf(animation -> !animation.present());
        if (defaultHoverAnimations.isEmpty()) {
            defaultHoverAnimations.addAll(builtInHoverAnimations());
        }
        for (GameConfig game : games) {
            if (game != null) {
                game.sanitize();
            }
        }
        games.removeIf(game -> game == null || game.id.isBlank());
        if (games.isEmpty()) {
            games.add(game("Walnut Bowling", "start_chapter", "WALLNUT_BOWLING", 0));
            games.add(game("Vase Breaker", "start_chapter", "VASE_BREAKER", 1));
            games.add(game("Plant zombies", "start_chapter", "PLANT_ZOMBIES", 2));
            games.add(game("Beghouled", "start_chapter", "BEGHOULED", 3));
        }
        return this;
    }

    public GameConfig gameFor(String tiledName) {
        if (tiledName == null || tiledName.isBlank()) {
            return null;
        }
        String name = tiledName.trim();
        for (GameConfig game : games) {
            if (game != null && game.matches(name)) {
                return game;
            }
        }
        return null;
    }

    public List<HoverAnimationConfig> defaultHoverAnimations() {
        return copyAnimations(defaultHoverAnimations);
    }

    static List<HoverAnimationConfig> resolveHoverAnimations(GameConfig game, MiniGameMenuConfig config) {
        MiniGameMenuConfig safe = config == null ? new MiniGameMenuConfig().sanitize() : config;
        if (game == null || game.hoverAnimations == null) {
            return safe.defaultHoverAnimations();
        }
        if (game.hoverAnimations.isEmpty()) {
            return List.of();
        }
        return copyAnimations(game.hoverAnimations);
    }

    static List<HoverAnimationConfig> copyAnimations(List<HoverAnimationConfig> source) {
        ArrayList<HoverAnimationConfig> copy = new ArrayList<>();
        if (source == null) {
            return copy;
        }
        for (HoverAnimationConfig animation : source) {
            if (animation != null && animation.present()) {
                copy.add(animation.copy());
            }
        }
        return copy;
    }

    private static GameConfig game(String id, String action, String chapter, int order) {
        GameConfig config = new GameConfig();
        config.id = id;
        config.action = action;
        config.chapter = chapter;
        config.order = order;
        config.sanitize();
        return config;
    }

    static List<HoverAnimationConfig> builtInHoverAnimations() {
        ArrayList<HoverAnimationConfig> animations = new ArrayList<>();
        animations.add(electricity());
        animations.add(cloud());
        return animations;
    }

    static HoverAnimationConfig electricity() {
        HoverAnimationConfig animation = new HoverAnimationConfig();
        animation.id = "electricity";
        animation.pamPath = "768/INITIAL/EFFECTS/LEVEL_WINDOW_ELECTRICITY/LEVEL_WINDOW_ELECTRICITY.PAM";
        animation.clip = "animation";
        animation.loop = true;
        animation.sanitize();
        return animation;
    }

    static HoverAnimationConfig cloud() {
        HoverAnimationConfig animation = new HoverAnimationConfig();
        animation.id = "cloud";
        animation.pamPath = "768/INITIAL/EFFECTS/ELECTRICBLUEBERRY_CLOUD_PROJECTILE/ELECTRICBLUEBERRY_CLOUD_PROJECTILE.PAM";
        animation.startClip = "start";
        animation.loopClip = "attack";
        animation.endClip = "death";
        animation.loop = true;
        animation.sanitize();
        return animation;
    }

    static String text(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value.trim();
    }

    static float positive(float value, float fallback) {
        if (Float.isNaN(value) || value <= 0f) {
            return fallback;
        }
        return value;
    }

    static float finite(float value, float fallback) {
        if (Float.isNaN(value)) {
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

    static float nonNegative(float value, float fallback) {
        if (Float.isNaN(value) || value < 0f) {
            return fallback;
        }
        return value;
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

    public static class GameConfig {
        public String id = "";
        public String action = "none";
        public String chapter = "";
        public int order;
        public boolean enabled = true;
        public boolean visible = true;
        public ArrayList<String> aliases = new ArrayList<>();
        public ArrayList<HoverAnimationConfig> hoverAnimations;

        public void sanitize() {
            id = id == null ? "" : id.trim();
            if (action == null || action.isBlank()) {
                action = "none";
            } else {
                action = action.trim().toLowerCase(Locale.ROOT);
            }
            if (chapter == null) {
                chapter = "";
            } else {
                chapter = chapter.trim();
            }
            if (aliases == null) {
                aliases = new ArrayList<>();
            }
            aliases.removeIf(alias -> alias == null || alias.isBlank());
            if (hoverAnimations != null) {
                hoverAnimations.removeIf(item -> item == null);
                for (HoverAnimationConfig animation : hoverAnimations) {
                    animation.sanitize();
                }
                hoverAnimations.removeIf(animation -> !animation.present());
            }
        }

        public boolean matches(String tiledName) {
            if (tiledName == null || tiledName.isBlank() || id.isBlank()) {
                return false;
            }
            if (id.equalsIgnoreCase(tiledName)) {
                return true;
            }
            for (String alias : aliases) {
                if (alias != null && alias.equalsIgnoreCase(tiledName)) {
                    return true;
                }
            }
            return false;
        }

        public boolean clickable() {
            return enabled && visible && !"none".equalsIgnoreCase(action);
        }
    }

    public static class HoverAnimationConfig {
        public String id = "";
        public String pamPath = "";
        public String clip = "";
        public String startClip = "";
        public String loopClip = "";
        public String endClip = "";
        public boolean loop = true;
        public float offsetX;
        public float offsetY;
        public float scale = 1f;
        public float scaleX;
        public float scaleY;
        public float rotation;
        public float alpha = 1f;
        public float originX = 0.5f;
        public float originY = 0.5f;
        public float originOffsetX;
        public float originOffsetY;
        public boolean flipX;
        public boolean flipY;
        public boolean scaleWithMap = true;
        public boolean visible = true;

        public void sanitize() {
            id = id == null ? "" : id.trim();
            pamPath = pamPath == null ? "" : pamPath.trim().replace('\\', '/');
            clip = clip == null ? "" : clip.trim();
            startClip = startClip == null ? "" : startClip.trim();
            loopClip = loopClip == null ? "" : loopClip.trim();
            endClip = endClip == null ? "" : endClip.trim();
            offsetX = finite(offsetX, 0f);
            offsetY = finite(offsetY, 0f);
            scale = positive(scale, 1f);
            scaleX = scaleX <= 0f || Float.isNaN(scaleX) ? 1f : scaleX;
            scaleY = scaleY <= 0f || Float.isNaN(scaleY) ? 1f : scaleY;
            rotation = finite(rotation, 0f);
            alpha = clamp01(alpha, 1f);
            originX = clamp01(originX, 0.5f);
            originY = clamp01(originY, 0.5f);
            originOffsetX = finite(originOffsetX, 0f);
            originOffsetY = finite(originOffsetY, 0f);
        }

        public boolean present() {
            return visible && pamPath != null && !pamPath.isBlank();
        }

        public String resolvedLoopClip() {
            if (!loopClip.isBlank()) {
                return loopClip;
            }
            return clip;
        }

        public float resolvedScaleX() {
            return scale * (scaleX <= 0f || Float.isNaN(scaleX) ? 1f : scaleX);
        }

        public float resolvedScaleY() {
            return scale * (scaleY <= 0f || Float.isNaN(scaleY) ? 1f : scaleY);
        }

        public HoverAnimationConfig copy() {
            HoverAnimationConfig copy = new HoverAnimationConfig();
            copy.id = id;
            copy.pamPath = pamPath;
            copy.clip = clip;
            copy.startClip = startClip;
            copy.loopClip = loopClip;
            copy.endClip = endClip;
            copy.loop = loop;
            copy.offsetX = offsetX;
            copy.offsetY = offsetY;
            copy.scale = scale;
            copy.scaleX = scaleX;
            copy.scaleY = scaleY;
            copy.rotation = rotation;
            copy.alpha = alpha;
            copy.originX = originX;
            copy.originY = originY;
            copy.originOffsetX = originOffsetX;
            copy.originOffsetY = originOffsetY;
            copy.flipX = flipX;
            copy.flipY = flipY;
            copy.scaleWithMap = scaleWithMap;
            copy.visible = visible;
            return copy;
        }
    }
}

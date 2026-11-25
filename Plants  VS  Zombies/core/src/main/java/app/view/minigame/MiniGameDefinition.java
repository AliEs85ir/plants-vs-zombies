package app.view.minigame;

import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MiniGameDefinition {
    private final String id;
    private final Rectangle bounds;
    private final String action;
    private final String chapter;
    private final boolean enabled;
    private final List<MiniGameMenuConfig.HoverAnimationConfig> hoverAnimations;

    public MiniGameDefinition(
        String id,
        Rectangle bounds,
        String action,
        String chapter,
        boolean enabled,
        List<MiniGameMenuConfig.HoverAnimationConfig> hoverAnimations
    ) {
        this.id = id == null ? "" : id.trim();
        this.bounds = bounds == null ? new Rectangle() : new Rectangle(bounds);
        this.action = action == null || action.isBlank() ? "none" : action.trim().toLowerCase();
        this.chapter = chapter == null ? "" : chapter.trim();
        this.enabled = enabled;
        this.hoverAnimations = Collections.unmodifiableList(
            hoverAnimations == null ? List.of() : new ArrayList<>(hoverAnimations)
        );
    }

    public static List<MiniGameDefinition> bind(
        List<MiniGameTiledMap.ObjectRef> objects,
        MiniGameMenuConfig config
    ) {
        MiniGameMenuConfig safe = config == null ? new MiniGameMenuConfig().sanitize() : config;
        ArrayList<MiniGameDefinition> definitions = new ArrayList<>();
        if (objects == null) {
            return definitions;
        }
        for (MiniGameTiledMap.ObjectRef object : objects) {
            if (object == null || object.name.isBlank() || object.bounds == null) {
                continue;
            }
            MiniGameMenuConfig.GameConfig game = safe.gameFor(object.name);
            List<MiniGameMenuConfig.HoverAnimationConfig> animations =
                MiniGameMenuConfig.resolveHoverAnimations(game, safe);
            String action = game == null ? "none" : game.action;
            String chapter = game == null ? "" : game.chapter;
            boolean enabled = game == null || game.enabled && game.visible;
            definitions.add(new MiniGameDefinition(
                object.name,
                object.bounds,
                action,
                chapter,
                enabled,
                animations
            ));
        }
        return definitions;
    }

    public String id() {
        return id;
    }

    public Rectangle bounds() {
        return new Rectangle(bounds);
    }

    public String action() {
        return action;
    }

    public String chapter() {
        return chapter;
    }

    public boolean enabled() {
        return enabled;
    }

    public boolean clickable() {
        return enabled && !"none".equals(action);
    }

    public List<MiniGameMenuConfig.HoverAnimationConfig> hoverAnimations() {
        return hoverAnimations;
    }

    public float centerX() {
        return bounds.x + bounds.width * 0.5f;
    }

    public float centerY() {
        return bounds.y + bounds.height * 0.5f;
    }

    public boolean contains(float mapX, float mapY) {
        return bounds.contains(mapX, mapY);
    }
}

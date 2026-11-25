package app.view.ui;

import com.badlogic.gdx.graphics.Color;

public enum UiColor {
    WHITE(1.00f, 1.00f, 1.00f),
    CREAM(0.98f, 0.93f, 0.78f),
    GOLD(0.93f, 0.75f, 0.22f),
    YELLOW(0.98f, 0.86f, 0.28f),
    ORANGE(0.92f, 0.48f, 0.14f),
    DARK_RED(0.58f, 0.07f, 0.08f),
    CRIMSON(0.76f, 0.11f, 0.13f),
    RED(0.86f, 0.16f, 0.14f),
    BROWN(0.45f, 0.26f, 0.14f),
    GREEN(0.28f, 0.62f, 0.22f),
    DARK_GREEN(0.14f, 0.38f, 0.16f),
    LIME(0.55f, 0.82f, 0.22f),
    TEAL(0.16f, 0.55f, 0.52f),
    CYAN(0.22f, 0.78f, 0.82f),
    BLUE(0.22f, 0.42f, 0.82f),
    NAVY(0.10f, 0.18f, 0.42f),
    PURPLE(0.48f, 0.22f, 0.62f),
    MAGENTA(0.78f, 0.22f, 0.58f),
    PINK(0.92f, 0.48f, 0.68f),
    GRAY(0.45f, 0.45f, 0.45f),
    BLACK(0.08f, 0.08f, 0.08f);

    private final Color value;

    UiColor(float r, float g, float b) {
        this.value = new Color(r, g, b, 1f);
    }

    public Color gdx() {
        return value.cpy();
    }

    public Color gdx(float alpha) {
        return new Color(value.r, value.g, value.b, alpha);
    }

    public static UiColor parse(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        String normalized = name.trim().replace('-', '_').replace(' ', '_');
        for (UiColor color : values()) {
            if (color.name().equalsIgnoreCase(normalized)) {
                return color;
            }
        }
        return null;
    }
}

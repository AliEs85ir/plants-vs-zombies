package app.enums;

import java.util.Locale;

public enum NewsCategory {
    WELCOME,
    PROFILE,
    PLANT,
    ZOMBIE,
    GREENHOUSE,
    QUEST,
    SHOP,
    LEVEL,
    SYSTEM;

    public static NewsCategory from(String raw) {
        if (raw == null || raw.isBlank()) {
            return SYSTEM;
        }
        try {
            return NewsCategory.valueOf(raw.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            return SYSTEM;
        }
    }
}

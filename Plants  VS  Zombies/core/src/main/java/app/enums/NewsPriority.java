package app.enums;

import java.util.Locale;

public enum NewsPriority {
    NORMAL,
    IMPORTANT,
    SPECIAL;

    public static NewsPriority from(String raw) {
        if (raw == null || raw.isBlank()) {
            return NORMAL;
        }
        try {
            return NewsPriority.valueOf(raw.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            return NORMAL;
        }
    }
}

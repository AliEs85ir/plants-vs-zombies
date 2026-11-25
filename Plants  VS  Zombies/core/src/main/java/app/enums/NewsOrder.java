package app.enums;

import java.util.Locale;

public enum NewsOrder {
    NEWEST_FIRST,
    OLDEST_FIRST;

    public static NewsOrder fromId(String value) {
        if (value == null || value.isBlank()) {
            return NEWEST_FIRST;
        }
        String key = value.trim().toUpperCase(Locale.ROOT).replace('-', '_').replace(' ', '_');
        return switch (key) {
            case "OLDEST_FIRST", "OLDEST", "ASC", "CHRONOLOGICAL" -> OLDEST_FIRST;
            default -> NEWEST_FIRST;
        };
    }
}

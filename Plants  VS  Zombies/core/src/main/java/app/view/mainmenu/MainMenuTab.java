package app.view.mainmenu;

import java.util.Locale;

public enum MainMenuTab {
    NEWS,
    GAME,
    PROFILE,
    SETTINGS;

    public static MainMenuTab fromId(String id) {
        if (id == null || id.isBlank()) {
            return GAME;
        }
        return switch (id.trim().toLowerCase(Locale.ROOT)) {
            case "news" -> NEWS;
            case "profile" -> PROFILE;
            case "settings" -> SETTINGS;
            default -> GAME;
        };
    }

    public String id() {
        return name().toLowerCase(Locale.ROOT);
    }
}

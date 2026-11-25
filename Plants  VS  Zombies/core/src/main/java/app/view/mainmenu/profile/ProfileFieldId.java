package app.view.mainmenu.profile;

import java.util.Locale;

public enum ProfileFieldId {
    USERNAME,
    NICKNAME,
    EMAIL,
    GAMES_PLAYED,
    COINS,
    GEMS,
    CHAPTER_PASSED;

    public static ProfileFieldId fromId(String id) {
        if (id == null || id.isBlank()) {
            return null;
        }
        String key = id.trim().toLowerCase(Locale.ROOT).replace('-', '_').replace(' ', '_');
        return switch (key) {
            case "username" -> USERNAME;
            case "nickname" -> NICKNAME;
            case "email" -> EMAIL;
            case "gamesplayed", "games_played" -> GAMES_PLAYED;
            case "coins" -> COINS;
            case "gems" -> GEMS;
            case "chapterpassed", "chapter_passed", "chapters_passed" -> CHAPTER_PASSED;
            default -> null;
        };
    }
}

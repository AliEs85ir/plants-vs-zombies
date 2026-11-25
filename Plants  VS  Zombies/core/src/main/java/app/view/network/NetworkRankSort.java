package app.view.network;

import java.util.Locale;

public enum NetworkRankSort {
    MIOPOINT("miopoint", "miopoint", "{score} MP"),
    DIAMOND("diamond", "diamond", "{score}"),
    COIN("coin", "coin", "{score}"),
    GAMES_PLAYED("games_played", "games played", "{score}");

    private final String id;
    private final String defaultLabel;
    private final String defaultScoreFormat;

    NetworkRankSort(String id, String defaultLabel, String defaultScoreFormat) {
        this.id = id;
        this.defaultLabel = defaultLabel;
        this.defaultScoreFormat = defaultScoreFormat;
    }

    public String id() {
        return id;
    }

    public String defaultLabel() {
        return defaultLabel;
    }

    public String defaultScoreFormat() {
        return defaultScoreFormat;
    }

    public static NetworkRankSort parse(String value) {
        if (value == null || value.isBlank()) {
            return MIOPOINT;
        }
        String normalized = value.trim().toLowerCase(Locale.ROOT).replace(' ', '_');
        for (NetworkRankSort sort : values()) {
            if (sort.id.equals(normalized) || sort.defaultLabel.replace(' ', '_').equals(normalized)) {
                return sort;
            }
        }
        if ("gems".equals(normalized) || "gem".equals(normalized) || "diamonds".equals(normalized)) {
            return DIAMOND;
        }
        if ("coins".equals(normalized)) {
            return COIN;
        }
        if ("games".equals(normalized) || "gamesplayed".equals(normalized)) {
            return GAMES_PLAYED;
        }
        return MIOPOINT;
    }
}

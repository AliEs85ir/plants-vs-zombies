package app.view.adventure;

import app.enums.SeasonType;
import app.model.App;
import app.model.user.GameProgress;
import app.model.user.User;
import game.world.chapter.ChapterRegistry;

public final class SeasonProgressView {
    private SeasonProgressView() {
    }

    public static int playedLevels(SeasonType season) {
        if (season == null) {
            return 0;
        }
        User user = App.getCurrentUser();
        if (user == null || user.getGameProgress() == null) {
            return 0;
        }
        GameProgress progress = user.getGameProgress();
        return Math.max(0, progress.getHighestCompletedLevel(season));
    }

    public static int totalLevels(SeasonType season, int fallback) {
        int total = season == null ? 0 : ChapterRegistry.countForSeason(season);
        if (total <= 0) {
            total = Math.max(0, fallback);
        }
        return total;
    }

    public static int clampedPlayed(int played, int total) {
        int safePlayed = Math.max(0, played);
        if (total <= 0) {
            return safePlayed;
        }
        return Math.min(safePlayed, total);
    }
}

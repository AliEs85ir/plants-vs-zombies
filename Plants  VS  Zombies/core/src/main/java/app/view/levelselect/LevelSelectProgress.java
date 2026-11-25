package app.view.levelselect;

import app.enums.SeasonType;
import app.model.App;
import app.model.user.GameProgress;
import app.model.user.User;
import game.world.chapter.ChapterRegistry;

public final class LevelSelectProgress {
    public enum NodeState {
        LOCKED,
        UNLOCKED,
        NEWLY_UNLOCKED
    }

    private LevelSelectProgress() {
    }

    public static int totalLevels(SeasonType season, int configuredCount) {
        int configured = Math.max(0, configuredCount);
        int registered = season == null ? 0 : Math.max(0, ChapterRegistry.countForSeason(season));
        if (configured > 0) {
            return configured;
        }
        return registered;
    }

    public static int highestCompleted(SeasonType season) {
        User user = App.getCurrentUser();
        if (user == null || user.getGameProgress() == null) {
            return 0;
        }
        return Math.max(0, user.getGameProgress().getHighestCompletedLevel(season));
    }

    public static NodeState state(SeasonType season, int levelNumber, int totalLevels) {
        return state(progress(), season, levelNumber, totalLevels);
    }

    public static NodeState state(GameProgress progress, SeasonType season, int levelNumber, int totalLevels) {
        if (levelNumber < 1 || totalLevels <= 0 || levelNumber > totalLevels) {
            return NodeState.LOCKED;
        }
        int completed = 0;
        if (progress != null && season != null) {
            completed = Math.max(0, progress.getHighestCompletedLevel(season));
        }
        if (levelNumber <= completed) {
            return NodeState.UNLOCKED;
        }
        if (levelNumber == completed + 1) {
            return NodeState.NEWLY_UNLOCKED;
        }
        return NodeState.LOCKED;
    }

    private static GameProgress progress() {
        User user = App.getCurrentUser();
        if (user == null) {
            return null;
        }
        return user.getGameProgress();
    }
}

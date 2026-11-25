package game.world.chapter.rule.scoringgame;

import app.model.App;
import app.model.user.User;
import app.service.AppService;

public final class ScoringSession {
    private static int points;
    private static boolean active;
    private static boolean committed;
    private static int generation;

    private ScoringSession() {
    }

    public static int begin() {
        points = 0;
        active = true;
        committed = false;
        generation++;
        return generation;
    }

    public static void add(int amount) {
        if (!active || committed || amount <= 0) {
            return;
        }
        points += amount;
    }

    public static int points() {
        return points;
    }

    public static boolean isActive() {
        return active;
    }

    public static boolean finish() {
        return finishGeneration(generation);
    }

    public static boolean finishGeneration(int sessionId) {
        if (sessionId != generation || !active || committed) {
            return false;
        }
        committed = true;
        active = false;
        User user = App.getCurrentUser();
        if (user == null) {
            return false;
        }
        boolean improved = user.recordBestMioPoint(points);
        if (improved) {
            AppService.syncGameData();
        }
        return improved;
    }
}

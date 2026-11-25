package game.world.minigame;

import app.view.hud.notification.GameplayNotifications;
import com.badlogic.gdx.Gdx;
import game.world.GameWorld;

public final class MinigameLevelAnnouncer {
    private MinigameLevelAnnouncer() {
    }

    public static void announce(GameWorld world, int level, int total) {
        if (world != null) {
            world.addEvent("Minigame level " + level + " of " + total + ".");
        }
        if (Gdx.app == null) {
            return;
        }
        Gdx.app.postRunnable(() ->
            GameplayNotifications.show(GameplayNotifications.minigameLevel(level, total)));
    }
}

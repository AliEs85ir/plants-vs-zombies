package graphics.general.animation;

import game.component.AssetManager;
import game.engine.time.GameClock;
import pvz.libpvz.pam.PamPlayer;

/**
 * Converts real PAM clip duration to simulation ticks (10 ticks/second).
 */
public final class PamClipTicks {
    private PamClipTicks() {
    }

    public static int durationTicks(String pamPath, String clipName) {
        return durationTicks(AssetManager.player, pamPath, clipName);
    }

    public static int durationTicks(PamPlayer player, String pamPath, String clipName) {
        if (player == null || pamPath == null || pamPath.isBlank() || clipName == null || clipName.isBlank()) {
            return 0;
        }
        float seconds = player.clipDurationSeconds(pamPath, clipName);
        if (seconds <= 0f) {
            return 0;
        }
        return Math.max(1, (int) Math.ceil(seconds * GameClock.TICKS_PER_SECOND));
    }
}
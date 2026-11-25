package app.view.levelselect;

import com.badlogic.gdx.Gdx;
import graphics.general.animation.PamPathResolver;
import pvz.libpvz.pam.PamPlayer;

import java.util.List;

public final class LevelSelectClipResolver {
    private LevelSelectClipResolver() {
    }

    public static String resolve(PamPlayer player, String pamPath, String requestedClip) {
        if (player == null || pamPath == null || pamPath.isBlank()) {
            return null;
        }
        List<String> clips;
        try {
            clips = player.clips(pamPath);
        } catch (RuntimeException exception) {
            Gdx.app.error("LevelSelectClipResolver", "Could not list clips for " + pamPath, exception);
            return null;
        }
        return resolve(requestedClip, clips, pamPath);
    }

    public static String resolve(String requestedClip, List<String> availableClips, String pamPath) {
        if (availableClips == null || availableClips.isEmpty()) {
            return null;
        }
        if (requestedClip == null || requestedClip.isBlank()) {
            return null;
        }
        for (String clip : availableClips) {
            if (requestedClip.equals(clip)) {
                return clip;
            }
        }
        for (String clip : availableClips) {
            if (clip != null && requestedClip.equalsIgnoreCase(clip)) {
                return clip;
            }
        }
        return null;
    }

    public static String resolveOrLog(PamPlayer player, String pamPath, String requestedClip) {
        String resolved = resolve(player, pamPath, requestedClip);
        if (resolved == null) {
            List<String> clips = null;
            try {
                clips = player == null ? null : player.clips(pamPath);
            } catch (RuntimeException ignored) {
                clips = null;
            }
            Gdx.app.error(
                "LevelSelectClipResolver",
                "Missing clip '" + requestedClip + "' in " + safe(pamPath)
                    + (clips == null ? "" : "; available=" + clips)
            );
        }
        return resolved;
    }

    public static boolean ensureLoaded(PamPlayer player, String pamPath) {
        if (player == null) {
            Gdx.app.error("LevelSelectClipResolver", "PamPlayer is unavailable");
            return false;
        }
        if (pamPath == null || pamPath.isBlank()) {
            Gdx.app.error("LevelSelectClipResolver", "Empty PAM path");
            return false;
        }
        try {
            if (!PamPathResolver.get().exists(pamPath)) {
                Gdx.app.error("LevelSelectClipResolver", "Missing PAM: " + pamPath);
                return false;
            }
            player.loadSync(pamPath);
            return true;
        } catch (RuntimeException exception) {
            Gdx.app.error("LevelSelectClipResolver", "Failed to load PAM: " + pamPath, exception);
            return false;
        }
    }

    private static String safe(String pamPath) {
        return pamPath == null || pamPath.isBlank() ? "<unknown>" : pamPath;
    }
}

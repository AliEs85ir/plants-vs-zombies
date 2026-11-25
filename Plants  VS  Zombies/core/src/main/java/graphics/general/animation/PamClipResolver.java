package graphics.general.animation;

import pvz.libpvz.pam.PamPlayer;

import java.util.List;

public final class PamClipResolver {

    private PamClipResolver() {
    }

    public static String resolve(PamPlayer player, String pamPath, String requestedClip) {
        if (player == null || pamPath == null || pamPath.isBlank()) {
            return requestedClip;
        }
        return resolve(requestedClip, player.clips(pamPath));
    }

    public static String resolve(String requestedClip, List<String> availableClips) {
        if (availableClips == null || availableClips.isEmpty()) {
            return null;
        }
        if (requestedClip != null && !requestedClip.isBlank()) {
            for (String clip : availableClips) {
                if (requestedClip.equals(clip)) {
                    return clip;
                }
            }
        }
        for (String clip : availableClips) {
            if ("idle".equals(clip)) {
                return clip;
            }
        }
        for (String clip : availableClips) {
            if (clip != null && clip.length() >= 4 && clip.regionMatches(true, 0, "idle", 0, 4)) {
                return clip;
            }
        }
        return availableClips.get(0);
    }
}

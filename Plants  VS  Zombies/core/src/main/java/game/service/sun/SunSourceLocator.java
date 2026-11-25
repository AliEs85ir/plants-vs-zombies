package game.service.sun;

import common.model.Position;
import game.entity.sun.SunSpawnPoint;

/**
 * Converts PAM-pixel offsets from a plant visual center into world coordinates.
 * PlantRenderer draws PAM at the plant position with scale 1, so offsets are
 * multiplied by {@link #PAM_RENDER_SCALE} once. Do not apply a second scale
 * (Gold Bloom +240 must stay +240 world units at the current scale).
 */
public final class SunSourceLocator {
    /**
     * Existing in-world PAM draw scale used by PlantRenderer (identity).
     * Pixel offsets from plant JSON are in this same space after this multiply.
     */
    public static final double PAM_RENDER_SCALE = 1d;

    private SunSourceLocator() {
    }

    public static Position worldFromPamCenter(Position visualCenter, SunSpawnPoint offset) {
        if (visualCenter == null) {
            return null;
        }
        if (offset == null) {
            return visualCenter;
        }
        return new Position(
            visualCenter.getX() + offset.getOffsetX() * PAM_RENDER_SCALE,
            visualCenter.getY() + offset.getOffsetY() * PAM_RENDER_SCALE
        );
    }
}

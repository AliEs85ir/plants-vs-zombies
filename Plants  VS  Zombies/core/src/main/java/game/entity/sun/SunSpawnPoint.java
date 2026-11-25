package game.entity.sun;

/**
 * Pixel offset from a plant PAM/visual center. +x is right, +y is up.
 * These are not cell or world coordinates; convert with {@link game.service.sun.SunSourceLocator}.
 */
public final class SunSpawnPoint {
    private final double offsetX;
    private final double offsetY;

    public SunSpawnPoint(double offsetX, double offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    public double getOffsetX() {
        return offsetX;
    }

    public double getOffsetY() {
        return offsetY;
    }
}

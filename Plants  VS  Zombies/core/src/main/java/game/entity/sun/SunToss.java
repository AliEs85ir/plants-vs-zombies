package game.entity.sun;

import common.model.Position;

/**
 * Tick-based launch from source S to landing L.
 * Formula: P(t) = (1-t)S + tL + (0, H*4t(1-t)) with t in [0,1].
 * The vertical term is a parabola through 0 at t=0 and t=1 and peak H at t=0.5.
 * Works in both directions. Last tick uses t=1 (exact landing).
 */
public final class SunToss {
    private final double startX;
    private final double startY;
    private final double landX;
    private final double landY;
    private final double arcHeight;
    private final int durationTicks;
    private int elapsedTicks;

    public SunToss(
        double startX,
        double startY,
        double landX,
        double landY,
        double arcHeight,
        int durationTicks
    ) {
        this.startX = startX;
        this.startY = startY;
        this.landX = landX;
        this.landY = landY;
        this.arcHeight = Math.max(0d, arcHeight);
        this.durationTicks = Math.max(1, durationTicks);
        this.elapsedTicks = 0;
    }

    public void advance() {
        if (elapsedTicks < durationTicks) {
            elapsedTicks++;
        }
    }

    public boolean isFinished() {
        return elapsedTicks >= durationTicks;
    }

    public double progress() {
        return elapsedTicks / (double) durationTicks;
    }

    public Position pointAt(double t) {
        double clamped = Math.max(0d, Math.min(1d, t));
        double x = startX + (landX - startX) * clamped;
        double y = startY + (landY - startY) * clamped + arc(clamped);
        return new Position(x, y);
    }

    public Position landing() {
        return new Position(landX, landY);
    }

    public Position spawn() {
        return new Position(startX, startY);
    }

    public int getDurationTicks() {
        return durationTicks;
    }

    public int getElapsedTicks() {
        return elapsedTicks;
    }

    public double getArcHeight() {
        return arcHeight;
    }

    public double arc(double t) {
        return arcHeight * 4d * t * (1d - t);
    }
}

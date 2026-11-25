package app.view.greenhouse;

import app.view.greenhouse.config.GreenhouseConfig;

import java.time.Duration;

public final class GreenhouseTimerFormat {
    private GreenhouseTimerFormat() {
    }

    public static String format(Duration remaining, GreenhouseConfig.FormatsConfig formats) {
        Duration safe = remaining == null || remaining.isNegative() ? Duration.ZERO : remaining;
        long totalSeconds = Math.max(0L, safe.getSeconds());
        long hours = totalSeconds / 3600L;
        long minutes = (totalSeconds % 3600L) / 60L;
        long seconds = totalSeconds % 60L;
        if (hours > 0L) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }
        return String.format("%02d:%02d", minutes, seconds);
    }
}

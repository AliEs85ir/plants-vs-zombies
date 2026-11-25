package game.settings;

public final class GameplaySettings {
    public static final int MIN_GAME_SPEED = 1;
    public static final int MAX_GAME_SPEED = 3;
    public static final int DEFAULT_GAME_SPEED = 1;

    private boolean notificationsEnabled = true;
    private boolean screenShakeEnabled = true;
    private boolean debugMode = true;
    private boolean gridOverlayEnabled = false;
    private int gameSpeed = DEFAULT_GAME_SPEED;

    public boolean notificationsEnabled() {
        return notificationsEnabled;
    }

    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }

    public boolean screenShakeEnabled() {
        return screenShakeEnabled;
    }

    public void setScreenShakeEnabled(boolean screenShakeEnabled) {
        this.screenShakeEnabled = screenShakeEnabled;
    }

    public boolean debugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    public boolean gridOverlayEnabled() {
        return gridOverlayEnabled;
    }

    public void setGridOverlayEnabled(boolean gridOverlayEnabled) {
        this.gridOverlayEnabled = gridOverlayEnabled;
    }

    public int gameSpeed() {
        return gameSpeed;
    }

    public float gameSpeedMultiplier() {
        return (float) gameSpeed;
    }

    public void setGameSpeed(int gameSpeed) {
        this.gameSpeed = clampGameSpeed(gameSpeed);
    }

    public static int clampGameSpeed(int speed) {
        return Math.max(MIN_GAME_SPEED, Math.min(MAX_GAME_SPEED, speed));
    }
}

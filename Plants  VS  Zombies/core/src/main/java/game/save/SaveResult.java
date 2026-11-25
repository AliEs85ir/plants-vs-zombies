package game.save;

public final class SaveResult {
    private final boolean success;
    private final String message;
    private final LevelSaveData data;

    private SaveResult(boolean success, String message, LevelSaveData data) {
        this.success = success;
        this.message = message == null ? "" : message;
        this.data = data;
    }

    public static SaveResult ok(LevelSaveData data) {
        return new SaveResult(true, "", data);
    }

    public static SaveResult ok() {
        return new SaveResult(true, "", null);
    }

    public static SaveResult failure(String message) {
        return new SaveResult(false, message, null);
    }

    public boolean success() {
        return success;
    }

    public String message() {
        return message;
    }

    public LevelSaveData data() {
        return data;
    }
}

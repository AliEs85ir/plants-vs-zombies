package app.view.cursor;

public final class CursorScale {
    public static final float ORIGINAL = 1f;

    private float defaultScale = 0.5f;
    private float currentScale = 0.5f;

    public void setDefaultScale(float scale) {
        defaultScale = sanitize(scale);
    }

    public void setCurrentScale(float scale) {
        currentScale = sanitize(scale);
    }

    public void resetToDefault() {
        currentScale = defaultScale;
    }

    public float current() {
        return currentScale;
    }

    public float defaultValue() {
        return defaultScale;
    }

    private float sanitize(float scale) {
        return scale <= 0f ? ORIGINAL : scale;
    }
}

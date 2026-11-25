package app.view.hud.notification;

public enum TextSize {
    SMALL(0.82f),
    MEDIUM(1f),
    LARGE(1.18f),
    XLARGE(1.36f),
    XXLARGE(1.54f),
    XXXLARGE(1.72f),
    XXXXLARGE(1.9f),
    XXXXXLARGE(2.08f),
    XXXXXXLARGE(2.26f),
    XXXXXXXLARGE(2.35f),
    XXXXXXXXLARGE(2.44f),
    XXXXXXXXXLARGE(2.62f),
    XXXXXXXXXXLARGE(2.8f),
    XXXXXXXXXXXLARGE(2.98f);

    private final float fontScale;

    TextSize(float fontScale) {
        this.fontScale = fontScale;
    }

    public float fontScale() {
        return fontScale;
    }
}

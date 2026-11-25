package app.view.hud.notification;

public enum NotificationSize {
    SMALL(280f, 0.88f),
    MEDIUM(360f, 1f),
    LARGE(460f, 1.12f),
    XLARGE(560f, 1.24f),
    XXLARGE(660f, 1.36f),
    XXXLARGE(760f, 1.48f),
    XXXXLARGE(860f, 1.6f),
    XXXXXLARGE(960f, 1.72f),
    XXXXXXLARGE(1060f, 1.84f),
    XXXXXXXLARGE(1110f, 1.90f),
    XXXXXXXXLARGE(1160f, 1.96f);

    private final float width;
    private final float contentScale;

    NotificationSize(float width, float contentScale) {
        this.width = width;
        this.contentScale = contentScale;
    }

    public float width() {
        return width;
    }

    public float contentScale() {
        return contentScale;
    }
}

package app.view.hud.notification;

public enum NotificationAnchor {
    BOTTOM_LEFT,
    BOTTOM_CENTER,
    BOTTOM_RIGHT,
    TOP_LEFT,
    TOP_CENTER,
    TOP_RIGHT;

    public boolean isBottom() {
        return this == BOTTOM_LEFT || this == BOTTOM_CENTER || this == BOTTOM_RIGHT;
    }

    public boolean isLeft() {
        return this == BOTTOM_LEFT || this == TOP_LEFT;
    }

    public boolean isRight() {
        return this == BOTTOM_RIGHT || this == TOP_RIGHT;
    }

    public boolean isCenterX() {
        return this == BOTTOM_CENTER || this == TOP_CENTER;
    }
}

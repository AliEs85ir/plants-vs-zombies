package app.view.hud.notification;

final class ActiveNotification {
    final NotificationSpec spec;
    final NotificationView view;
    NotificationPhase phase = NotificationPhase.CREATED;
    float idleElapsed;
    float targetX;
    float targetY;

    ActiveNotification(NotificationSpec spec, NotificationView view) {
        this.spec = spec;
        this.view = view;
    }
}

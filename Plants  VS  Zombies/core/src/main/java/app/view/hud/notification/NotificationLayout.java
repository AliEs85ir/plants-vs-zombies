package app.view.hud.notification;

import java.util.List;

public class NotificationLayout {
    private float worldWidth;
    private float worldHeight;
    private float spacing = NotificationDefaults.STACK_SPACING;
    private float moveDuration = NotificationDefaults.LAYOUT_MOVE_DURATION;

    public NotificationLayout(float worldWidth, float worldHeight) {
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
    }

    public void setWorldSize(float worldWidth, float worldHeight) {
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
    }

    public void setSpacing(float spacing) {
        this.spacing = Math.max(0f, spacing);
    }

    public void setMoveDuration(float moveDuration) {
        this.moveDuration = Math.max(0f, moveDuration);
    }

    public float moveDuration() {
        return moveDuration;
    }

    public void apply(List<ActiveNotification> stacked, NotificationAnchor anchor) {
        if (stacked == null || stacked.isEmpty()) {
            return;
        }
        NotificationSpec originSpec = stacked.get(stacked.size() - 1).spec;
        float cursor = 0f;
        for (int i = stacked.size() - 1; i >= 0; i--) {
            ActiveNotification item = stacked.get(i);
            float x = resolveX(item.view.getWidth(), originSpec, anchor);
            float y = resolveY(item.view.getHeight(), originSpec, anchor, cursor);
            item.targetX = x;
            item.targetY = y;
            cursor += item.view.getHeight() + spacing;
        }
    }

    private float resolveX(float width, NotificationSpec spec, NotificationAnchor anchor) {
        float x;
        if (anchor.isRight()) {
            x = worldWidth - spec.padRight() - width;
        } else if (anchor.isCenterX()) {
            x = (worldWidth - width) * 0.5f;
        } else {
            x = spec.padLeft();
        }
        return clamp(x, 0f, Math.max(0f, worldWidth - width));
    }

    private float resolveY(float height, NotificationSpec spec, NotificationAnchor anchor, float stackOffset) {
        float y;
        if (anchor.isBottom()) {
            y = spec.padBottom() + stackOffset;
        } else {
            y = worldHeight - spec.padTop() - height - stackOffset;
        }
        return clamp(y, 0f, Math.max(0f, worldHeight - height));
    }

    private float clamp(float value, float min, float max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }
}

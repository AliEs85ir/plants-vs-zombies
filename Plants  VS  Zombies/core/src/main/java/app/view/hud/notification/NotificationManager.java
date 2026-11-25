package app.view.hud.notification;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class NotificationManager {
    private final WidgetGroup layer;
    private final NotificationLayout layout;
    private final NotificationAnimator animator;
    private final List<ActiveNotification> active = new ArrayList<>();
    private final List<NotificationSpec> queue = new ArrayList<>();
    private int maxVisible = NotificationDefaults.MAX_VISIBLE;

    public NotificationManager(Stage stage, float worldWidth, float worldHeight) {
        this.layout = new NotificationLayout(worldWidth, worldHeight);
        this.animator = new NotificationAnimator();
        this.layer = new WidgetGroup();
        this.layer.setFillParent(true);
        this.layer.setTouchable(Touchable.disabled);
        stage.addActor(layer);
        layer.toFront();
    }

    public void setMaxVisible(int maxVisible) {
        this.maxVisible = Math.max(1, maxVisible);
    }

    public void setSpacing(float spacing) {
        layout.setSpacing(spacing);
    }

    public void show(NotificationSpec spec) {
        if (spec == null) {
            return;
        }

        boolean hasText = spec.text() != null && !spec.text().isBlank();
        boolean hasIcon = spec.iconRegion() != null || spec.icon() != null;

        if (!hasText && !hasIcon) {
            return;
        }

        if (countLive() >= maxVisible) {
            enqueue(spec);
            return;
        }
        present(spec);
    }

    public void update(float delta) {
        if (active.isEmpty() && queue.isEmpty()) {
            return;
        }
        Iterator<ActiveNotification> iterator = active.iterator();
        while (iterator.hasNext()) {
            ActiveNotification item = iterator.next();
            if (item.phase == NotificationPhase.ENTERING) {
                continue;
            }
            if (item.phase == NotificationPhase.IDLE) {
                item.idleElapsed += delta;
                if (item.idleElapsed >= item.spec.resolvedDuration()) {
                    beginExit(item);
                }
            }
            if (item.phase == NotificationPhase.REMOVED) {
                item.view.remove();
                iterator.remove();
            }
        }
        drainQueue();
        layer.toFront();
    }

    public void resize(float worldWidth, float worldHeight) {
        layout.setWorldSize(worldWidth, worldHeight);
        relayout(false);
    }

    public void clear() {
        queue.clear();
        for (ActiveNotification item : active) {
            item.phase = NotificationPhase.REMOVED;
            item.view.remove();
        }
        active.clear();
    }

    private void present(NotificationSpec spec) {
        NotificationView view = new NotificationView(spec);
        ActiveNotification item = new ActiveNotification(spec, view);
        layer.addActor(view);
        active.add(item);
        relayout(true);
        item.phase = NotificationPhase.ENTERING;
        animator.playEnter(view);
        view.addAction(
            Actions.sequence(
                Actions.delay(spec.animation().enterDuration()),
                Actions.run(() -> {
                    if (item.phase == NotificationPhase.ENTERING) {
                        item.phase = NotificationPhase.IDLE;
                        item.idleElapsed = 0f;
                        animator.playIdle(view);
                    }
                })
            )
        );
    }

    private void beginExit(ActiveNotification item) {
        if (item.phase == NotificationPhase.EXITING || item.phase == NotificationPhase.REMOVED) {
            return;
        }
        item.phase = NotificationPhase.EXITING;
        animator.playExit(item.view, () -> {
            item.phase = NotificationPhase.REMOVED;
            relayout(true);
            drainQueue();
        });
    }

    private void relayout(boolean animated) {
        Map<NotificationAnchor, List<ActiveNotification>> stacks = new EnumMap<>(NotificationAnchor.class);
        for (ActiveNotification item : active) {
            if (item.phase == NotificationPhase.REMOVED) {
                continue;
            }
            stacks.computeIfAbsent(item.spec.anchor(), key -> new ArrayList<>()).add(item);
        }
        float duration = animated ? layout.moveDuration() : 0f;
        for (Map.Entry<NotificationAnchor, List<ActiveNotification>> entry : stacks.entrySet()) {
            layout.apply(entry.getValue(), entry.getKey());
            for (ActiveNotification item : entry.getValue()) {
                if (item.phase == NotificationPhase.CREATED) {
                    item.view.setPosition(item.targetX, item.targetY);
                } else {
                    animator.moveTo(item.view, item.targetX, item.targetY, duration);
                }
            }
        }
    }

    private void enqueue(NotificationSpec spec) {
        int index = 0;
        while (index < queue.size() && queue.get(index).priority().ordinal() >= spec.priority().ordinal()) {
            index++;
        }
        queue.add(index, spec);
    }

    private void drainQueue() {
        while (!queue.isEmpty() && countLive() < maxVisible) {
            present(queue.remove(0));
        }
    }

    private int countLive() {
        int count = 0;
        for (ActiveNotification item : active) {
            if (item.phase != NotificationPhase.REMOVED && item.phase != NotificationPhase.EXITING) {
                count++;
            }
        }
        return count;
    }
}

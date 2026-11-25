package app.view.hud.notification;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;

public class NotificationAnimator {
    public void playEnter(NotificationView view) {
        NotificationAnimationSpec animation = view.spec().animation();
        view.prepareEnter(animation.iconEnter, animation.textEnter);
        animateActor(view.iconActor(), animation.iconEnter, 0f, 0f, animation.iconEnter.endAlpha, animation.iconEnter.endScale);
        animateActor(view.textActor(), animation.textEnter, 0f, 0f, animation.textEnter.endAlpha, animation.textEnter.endScale);
    }

    public void playIdle(NotificationView view) {
        NotificationMotion idle = view.spec().animation().contentIdle;
        if (idle.duration <= 0f) {
            return;
        }
        Actor content = view.content();
        content.clearActions();
        content.setScale(idle.startScale);
        content.setPosition(0f, 0f);
        content.addAction(
            Actions.forever(
                Actions.sequence(
                    Actions.parallel(
                        Actions.scaleTo(idle.endScale, idle.endScale, idle.duration, idle.interpolation),
                        Actions.moveBy(idle.offsetX, idle.offsetY, idle.duration, idle.interpolation)
                    ),
                    Actions.parallel(
                        Actions.scaleTo(idle.startScale, idle.startScale, idle.duration, idle.interpolation),
                        Actions.moveBy(-idle.offsetX, -idle.offsetY, idle.duration, idle.interpolation)
                    )
                )
            )
        );
    }

    public void playExit(NotificationView view, Runnable onComplete) {
        view.resetContentTransform();
        view.restoreRestingChildren();
        NotificationAnimationSpec animation = view.spec().animation();
        animateActor(
            view.iconActor(),
            animation.iconExit,
            animation.iconExit.offsetX,
            animation.iconExit.offsetY,
            animation.iconExit.endAlpha,
            animation.iconExit.endScale
        );
        animateActor(
            view.textActor(),
            animation.textExit,
            animation.textExit.offsetX,
            animation.textExit.offsetY,
            animation.textExit.endAlpha,
            animation.textExit.endScale
        );
        float exitDuration = animation.exitDuration();
        view.addAction(
            Actions.sequence(
                Actions.delay(exitDuration),
                Actions.run(() -> {
                    if (onComplete != null) {
                        onComplete.run();
                    }
                })
            )
        );
    }

    public void moveTo(NotificationView view, float x, float y, float duration) {
        var actions = view.getActions();
        for (int i = actions.size - 1; i >= 0; i--) {
            if (actions.get(i) instanceof MoveToAction) {
                view.removeAction(actions.get(i));
            }
        }
        if (duration <= 0f) {
            view.setPosition(x, y);
            return;
        }
        view.addAction(Actions.moveTo(x, y, duration, Interpolation.smoother));
    }

    private void animateActor(
        Actor actor,
        NotificationMotion motion,
        float targetX,
        float targetY,
        float targetAlpha,
        float targetScale
    ) {
        actor.clearActions();
        actor.addAction(
            Actions.sequence(
                Actions.delay(motion.delay),
                Actions.parallel(
                    Actions.moveTo(targetX, targetY, motion.duration, motion.interpolation),
                    Actions.alpha(targetAlpha, motion.duration, Interpolation.fade),
                    Actions.scaleTo(targetScale, targetScale, motion.duration, motion.interpolation)
                )
            )
        );
    }
}

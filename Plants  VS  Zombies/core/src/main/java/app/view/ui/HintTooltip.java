package app.view.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public final class HintTooltip {

    public interface Sink {
        void showHint(String text);
        void clearHint();
    }

    private HintTooltip() {}

    public static void attach(Actor actor, String hint, Sink sink) {
        if (actor == null || sink == null || hint == null || hint.isEmpty()) return;

        actor.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer != -1) return;
                sink.showHint(hint);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (pointer != -1) return;
                sink.clearHint();
            }
        });
    }
}

package app.view.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import game.audio.AudioId;
import game.audio.AudioManager;

public final class UiSoundBinder {
    private UiSoundBinder() {
    }

    public static void bind(Stage stage) {
        if (stage == null) {
            return;
        }
        stage.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (actor instanceof Button button && !button.isDisabled()) {
                    AudioManager.play(AudioId.UI_CLICK);
                }
            }
        });
    }
}

package app.view.quest;

import app.view.ui.UiTheme;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;
import game.audio.AudioId;
import game.audio.AudioManager;
import game.component.AssetManager;

public final class SkinImageButtons {
    private SkinImageButtons() {
    }

    public static ImageButton create(String styleName, float width, float height, Runnable action) {
        Skin skin = AssetManager.skin;
        if (skin == null || styleName == null || styleName.isBlank() || !UiTheme.hasImageButtonStyle(styleName)) {
            return null;
        }
        ImageButton button = new ImageButton(skin, styleName);
        button.setName("skin-image-button-" + styleName);
        button.getImage().setScaling(Scaling.fit);
        if (button.getImageCell() != null) {
            button.getImageCell().grow();
        }
        button.setSize(Math.max(1f, width), Math.max(1f, height));
        if (action != null) {
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    AudioManager.play(AudioId.UI_CLICK);
                    action.run();
                }
            });
        }
        return button;
    }

    public static void place(Actor actor, float x, float y, float width, float height) {
        if (actor == null) {
            return;
        }
        actor.setBounds(x, y, Math.max(1f, width), Math.max(1f, height));
    }
}

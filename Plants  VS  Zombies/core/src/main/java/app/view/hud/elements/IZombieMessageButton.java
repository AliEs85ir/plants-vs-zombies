package app.view.hud.elements;

import app.view.ui.UiTheme;
import app.view.ui.text.TextAlign;
import app.view.ui.text.TextSpec;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import game.audio.AudioId;
import game.audio.AudioManager;

public final class IZombieMessageButton extends Table {
    private static final float SIZE = 52f;

    /**
     * TODO: set the message button background.
     * Put a skin drawable name here (or load a texture region and call setBackground)
     * when the chat-button art is ready. Leave this empty so the button stays
     * background-less for now.
     */
    private static final String BACKGROUND_DRAWABLE = null;

    public IZombieMessageButton(Runnable onOpen) {
        setTouchable(Touchable.enabled);
        applyBackground();
        add(TextSpec.of(UiTheme.LBL_MEDIUM)
            .align(TextAlign.CENTER)
            .build("MSG"))
            .grow();
        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                AudioManager.play(AudioId.UI_CLICK);
                if (onOpen != null) {
                    onOpen.run();
                }
            }
        });
    }

    private void applyBackground() {
        if (BACKGROUND_DRAWABLE == null || BACKGROUND_DRAWABLE.isBlank()) {
            return;
        }
        Drawable background = UiTheme.drawable(BACKGROUND_DRAWABLE);
        if (background != null) {
            setBackground(background);
        }
    }

    @Override
    public float getPrefWidth() {
        return SIZE;
    }

    @Override
    public float getPrefHeight() {
        return SIZE;
    }

    @Override
    public float getMinWidth() {
        return SIZE;
    }

    @Override
    public float getMinHeight() {
        return SIZE;
    }
}

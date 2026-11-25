package app.view.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public abstract class ModalOverlay extends Table {

    private static final float FADE_IN = 0.16f;

    private final Drawable scrim;
    private final boolean modal;

    protected ModalOverlay() {
        this(true);
    }

    protected ModalOverlay(boolean modal) {
        this.modal = modal;
        if (modal) {
            setFillParent(true);
            setTouchable(Touchable.enabled);
            addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }
            });
            scrim = UiTheme.drawable(UiTheme.BG_DIALOG);
        } else {
            setTouchable(Touchable.childrenOnly);
            scrim = null;
        }
        getColor().a = 0f;
        addAction(Actions.fadeIn(FADE_IN));
    }

    public boolean isModal() {
        return modal;
    }

    protected Color overlayColor() {
        return UiTheme.SCRIM;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float alpha = getColor().a * parentAlpha;
        if (scrim != null) {
            Color overlay = overlayColor();
            batch.setColor(overlay.r, overlay.g, overlay.b,
                           overlay.a * alpha);
            scrim.draw(batch, 0, 0, getWidth(), getHeight());
            batch.setColor(Color.WHITE);
        }
        super.draw(batch, parentAlpha);
    }

    public void close() {
        addAction(Actions.sequence(
            Actions.fadeOut(0.12f),
            Actions.removeActor()
        ));
    }

    public static final class PregameScreenSize {
        public static final float WIDTH  = 1280f;
        public static final float HEIGHT = 720f;
        private PregameScreenSize() {}
    }
}

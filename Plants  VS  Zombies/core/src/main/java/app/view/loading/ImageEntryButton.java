package app.view.loading;

import app.enums.cursor.CursorType;
import app.view.cursor.GameCursor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import game.audio.AudioId;
import game.audio.AudioManager;

public final class ImageEntryButton extends Image {
    private final GameCursor gameCursor;
    private final float baseScale;
    private final float hoverScale;
    private final float pressedScale;
    private boolean hovered;
    private boolean pressed;
    private Runnable action = () -> {
    };

    public ImageEntryButton(
        Texture texture,
        GameCursor gameCursor,
        float baseScale,
        float hoverScale,
        float pressedScale
    ) {
        super(new TextureRegionDrawable(new TextureRegion(texture)));
        this.gameCursor = gameCursor;
        this.baseScale = baseScale <= 0f ? 1f : baseScale;
        this.hoverScale = hoverScale <= 0f ? 1.03f : hoverScale;
        this.pressedScale = pressedScale <= 0f ? 0.97f : pressedScale;
        setOrigin(Align.center);
        setTouchable(Touchable.disabled);
        setColor(1f, 1f, 1f, 0f);
        setScale(this.baseScale);
        addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                hovered = true;
                applyVisualState();
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                hovered = false;
                pressed = false;
                applyVisualState();
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                boolean handled = super.touchDown(event, x, y, pointer, button);
                pressed = true;
                applyVisualState();
                return handled;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                pressed = false;
                applyVisualState();
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                AudioManager.play(AudioId.VOX_LOADINGBAR_ZOMBIE);
                action.run();
            }
        });
    }

    public void setAction(Runnable action) {
        this.action = action != null ? action : () -> {
        };
    }

    public void setInteractive(boolean interactive) {
        setTouchable(interactive ? Touchable.enabled : Touchable.disabled);
        if (!interactive) {
            hovered = false;
            pressed = false;
            applyVisualState();
        }
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();
        setOrigin(getWidth() * 0.5f, getHeight() * 0.5f);
    }

    private void applyVisualState() {
        if (pressed) {
            setScale(baseScale * pressedScale);
            if (gameCursor != null) {
                gameCursor.setType(CursorType.PRESSED);
            }
            return;
        }
        if (hovered) {
            setScale(baseScale * hoverScale);
            if (gameCursor != null) {
                gameCursor.setType(CursorType.HOVER);
            }
            return;
        }
        setScale(baseScale);
        if (gameCursor != null) {
            gameCursor.setType(CursorType.DEFAULT);
        }
    }
}

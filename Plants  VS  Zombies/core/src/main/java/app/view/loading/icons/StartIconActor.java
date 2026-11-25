package app.view.loading.icons;

import app.enums.cursor.CursorType;
import app.view.cursor.GameCursor;
import app.view.loading.icons.config.StartIconsConfig;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import game.audio.AudioId;
import game.audio.AudioManager;

public final class StartIconActor extends Image {
    private final StartIconsConfig.IconConfig definition;
    private final StartIconsConfig.HoverConfig hover;
    private final GameCursor gameCursor;
    private final Color restColor = new Color(1f, 1f, 1f, 1f);
    private final Color hoverColor = new Color(1f, 1f, 1f, 1f);
    private boolean hovered;
    private boolean pressed;
    private Runnable action = () -> {
    };

    public StartIconActor(
        Texture texture,
        StartIconsConfig.IconConfig definition,
        StartIconsConfig.HoverConfig hover,
        GameCursor gameCursor
    ) {
        super(new TextureRegionDrawable(new TextureRegion(texture)));
        this.definition = definition;
        this.hover = hover;
        this.gameCursor = gameCursor;
        float brightness = hover != null ? hover.brightness : 1.12f;
        hoverColor.set(brightness, brightness, Math.min(1.2f, brightness * 0.96f), 1f);
        setOrigin(Align.center);
        setTouchable(definition.clickable() ? Touchable.enabled : Touchable.disabled);
        setColor(restColor);
        addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                if (pointer != -1) {
                    return;
                }
                hovered = true;
                applyVisualState();
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
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
                if (!definition.clickable()) {
                    return;
                }
                AudioManager.play(AudioId.UI_CLICK);
                action.run();
            }
        });
    }

    public void setAction(Runnable action) {
        this.action = action != null ? action : () -> {
        };
    }

    public StartIconsConfig.IconConfig definition() {
        return definition;
    }

    public float hoverLift() {
        if (!hovered || pressed) {
            return 0f;
        }
        float extra = definition.hoverOffset;
        float base = hover != null ? hover.offsetY : 0f;
        return extra + base;
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();
        setOrigin(getWidth() * 0.5f, getHeight() * 0.5f);
    }

    private void applyVisualState() {
        float hoverScale = definition.hoverScale > 0f ? definition.hoverScale : (hover != null ? hover.scale : 1.08f);
        float pressedScale = hover != null ? hover.pressedScale : 0.96f;
        float duration = hover != null ? hover.duration : 0f;
        float targetScale = 1f;
        Color targetColor = restColor;
        if (pressed) {
            targetScale = pressedScale;
            targetColor = hoverColor;
            if (gameCursor != null) {
                gameCursor.setType(CursorType.PRESSED);
            }
        } else if (hovered) {
            targetScale = hoverScale;
            targetColor = hoverColor;
            if (gameCursor != null && definition.clickable()) {
                gameCursor.setType(CursorType.HOVER);
            }
        } else if (gameCursor != null) {
            gameCursor.setType(CursorType.DEFAULT);
        }
        clearActions();
        if (pressed || duration <= 0f) {
            setScale(targetScale);
            setColor(targetColor);
        } else {
            addAction(Actions.parallel(
                Actions.scaleTo(targetScale, targetScale, duration),
                Actions.color(targetColor, duration)
            ));
        }
        if (getParent() instanceof WidgetGroup group) {
            group.invalidate();
        }
    }
}

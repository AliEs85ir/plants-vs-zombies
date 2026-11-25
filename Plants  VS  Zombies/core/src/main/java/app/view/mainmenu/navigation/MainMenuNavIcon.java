package app.view.mainmenu.navigation;

import app.enums.cursor.CursorType;
import app.view.cursor.GameCursor;
import app.view.mainmenu.config.MainMenuConfig;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
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

public final class MainMenuNavIcon extends Image {
    private final MainMenuConfig.NavIconConfig definition;
    private final MainMenuConfig.NavigationAppearanceConfig appearance;
    private final GameCursor gameCursor;
    private boolean hovered;
    private boolean pressed;
    private boolean selected;
    private float visualLift;
    private Runnable action = () -> {
    };

    public MainMenuNavIcon(
        Texture texture,
        MainMenuConfig.NavIconConfig definition,
        MainMenuConfig.NavigationAppearanceConfig appearance,
        GameCursor gameCursor
    ) {
        super(new TextureRegionDrawable(new TextureRegion(texture)));
        this.definition = definition;
        this.appearance = appearance;
        this.gameCursor = gameCursor;
        setOrigin(Align.center);
        setTouchable(definition.clickable() ? Touchable.enabled : Touchable.disabled);
        applyVisualState(false);
        addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                if (pointer != -1) {
                    return;
                }
                hovered = true;
                applyVisualState(true);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                hovered = false;
                pressed = false;
                applyVisualState(true);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                boolean handled = super.touchDown(event, x, y, pointer, button);
                pressed = true;
                applyVisualState(false);
                return handled;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                pressed = false;
                applyVisualState(true);
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

    public void setSelected(boolean selected) {
        if (this.selected == selected) {
            return;
        }
        this.selected = selected;
        applyVisualState(true);
    }

    public boolean selected() {
        return selected;
    }

    public MainMenuConfig.NavIconConfig definition() {
        return definition;
    }

    public float layoutLift() {
        return visualLift;
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();
        setOrigin(getWidth() * 0.5f, getHeight() * 0.5f);
    }

    private void applyVisualState(boolean animate) {
        float darkness = appearance.normalDarkness;
        float targetScale = 1f;
        float targetLift = 0f;
        if (pressed) {
            targetScale = appearance.pressedScale;
            if (selected) {
                darkness = appearance.selectedDarkness;
                targetLift = appearance.selectedVerticalOffset;
            }
            if (gameCursor != null) {
                gameCursor.setType(CursorType.PRESSED);
            }
        } else if (selected) {
            darkness = appearance.selectedDarkness;
            targetScale = appearance.selectedScale();
            targetLift = appearance.selectedVerticalOffset;
            if (hovered && gameCursor != null && definition.clickable()) {
                gameCursor.setType(CursorType.HOVER);
            } else if (gameCursor != null && !hovered) {
                gameCursor.setType(CursorType.DEFAULT);
            }
        } else if (hovered) {
            targetScale = appearance.hoverScale;
            if (gameCursor != null && definition.clickable()) {
                gameCursor.setType(CursorType.HOVER);
            }
        } else if (gameCursor != null) {
            gameCursor.setType(CursorType.DEFAULT);
        }
        visualLift = targetLift;
        Color targetColor = darknessColor(darkness);
        clearActions();
        float duration = selected
            ? appearance.selectedDuration
            : appearance.hoverDuration;
        Interpolation easing = appearance.interpolation();
        if (!animate || pressed || duration <= 0f) {
            setScale(targetScale);
            setColor(targetColor);
        } else {
            addAction(Actions.parallel(
                Actions.scaleTo(targetScale, targetScale, duration, easing),
                Actions.color(targetColor, duration, easing)
            ));
        }
        if (getParent() instanceof WidgetGroup group) {
            group.invalidate();
        }
    }

    private static Color darknessColor(float darkness) {
        float tint = 1f - Math.max(0f, Math.min(1f, darkness));
        return new Color(tint, tint, tint, 1f);
    }
}

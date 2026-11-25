package app.view.ui.button;

import app.view.ui.UiTheme;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class ActionButton extends TextButton {

    private final ButtonTheme theme;
    private Runnable action;
    private boolean enabled = true;

    public ActionButton(String text, String styleName) {
        this(text, styleName, ButtonTheme.DEFAULT);
    }

    public ActionButton(String text, String styleName, ButtonTheme theme) {
        super(text, UiTheme.skin(), UiTheme.buttonStyleOrDefault(styleName));
        this.theme = (theme != null) ? theme : ButtonTheme.DEFAULT;
        setTransform(true);
        setOrigin(Align.center);
        applyTheme();
        installHover();
    }

    private void applyTheme() {
        getLabel().setFontScale(theme.labelScale);
        if (theme.textColor != null) {
            getLabel().setColor(theme.textColor);
        }
        pad(theme.padY, theme.padX, theme.padY, theme.padX);
        setColor(theme.normalColor);
    }

    private void installHover() {
        addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer != -1 || !enabled) return;
                clearActions();
                addAction(Actions.parallel(
                    Actions.scaleTo(theme.hoverScale, theme.hoverScale, theme.hoverDuration),
                    Actions.color(theme.hoverColor, theme.hoverDuration)
                ));
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (pointer != -1) return;
                clearActions();
                addAction(Actions.parallel(
                    Actions.scaleTo(1f, 1f, theme.hoverDuration),
                    Actions.color(enabled ? theme.normalColor : theme.disabledColor,
                                  theme.hoverDuration)
                ));
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (enabled) {
                    clearActions();
                    setColor(theme.pressedColor);
                }
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                if (enabled) {
                    setColor(isOver() ? theme.hoverColor : theme.normalColor);
                }
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (enabled && action != null) {
                    action.run();
                }
            }
        });
    }

    public ActionButton onClick(Runnable action) {
        this.action = action;
        return this;
    }

    public ButtonTheme getTheme() {
        return theme;
    }

    public void setEnabledState(boolean value) {
        if (this.enabled == value) return;
        this.enabled = value;
        setDisabled(!value);
        clearActions();
        setColor(value ? theme.normalColor : theme.disabledColor);
        setScale(1f);
    }

    public boolean isEnabledState() {
        return enabled;
    }
}

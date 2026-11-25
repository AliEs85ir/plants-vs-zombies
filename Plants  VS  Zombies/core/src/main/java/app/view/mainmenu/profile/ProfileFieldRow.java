package app.view.mainmenu.profile;

import app.view.ui.Solid;
import app.view.ui.UiTheme;
import app.view.ui.button.ActionButton;
import app.view.ui.button.ButtonTheme;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Disposable;

public final class ProfileFieldRow extends Table implements Disposable {
    private final ProfileFieldId fieldId;
    private final ProfileMenuConfig.FieldRowConfig row;
    private final Label valueLabel;
    private final TextField editor;
    private final ActionButton changeButton;
    private final Texture iconTexture;
    private String committedValue = "";
    private boolean editingValue;

    public ProfileFieldRow(
        ProfileMenuConfig config,
        ProfileMenuConfig.FieldDefinition definition,
        Skin skin,
        Runnable onChange,
        Runnable onConfirm
    ) {
        this.fieldId = ProfileFieldId.fromId(definition.id);
        this.row = config.fieldRow;
        left();
        iconTexture = ProfileIcons.load(definition.iconPath);
        Image icon = ProfileIcons.image(iconTexture, row.iconSize);
        Label title = new Label(definition.title, new Label.LabelStyle(
            ProfileUiFonts.resolve(row.titleFont, row.titleFontFallback),
            ProfileMenuConfig.color(row.titleColor, Color.valueOf("F4E7B0"))
        ));
        title.setFontScale(row.titleFontSize);
        title.setAlignment(ProfileMenuConfig.textAlign(row.titleAlignment));
        valueLabel = new Label("", new Label.LabelStyle(
            ProfileUiFonts.resolve(row.valueFont, row.valueFontFallback),
            ProfileMenuConfig.color(row.valueColor, Color.WHITE)
        ));
        valueLabel.setFontScale(row.valueFontSize);
        valueLabel.setAlignment(ProfileMenuConfig.textAlign(row.valueAlignment));
        editor = createEditor(skin, config.editing, onConfirm);
        editor.setVisible(false);
        Stack valueStack = new Stack();
        valueStack.add(valueLabel);
        valueStack.add(editor);
        changeButton = createChangeButton(onChange);
        add(icon).size(row.iconSize).padRight(row.iconTitleGap);
        add(title).width(row.titleWidth).left().padRight(row.titleValueGap);
        add(valueStack).width(row.valueWidth).height(row.height).left().padRight(row.valueChangeGap);
        add(changeButton).width(row.changeButtonWidth).height(row.changeButtonHeight).left();
    }

    public ProfileFieldId fieldId() {
        return fieldId;
    }

    public void setValue(String value) {
        committedValue = value == null ? "" : value;
        valueLabel.setText(committedValue);
        if (!editingValue) {
            editor.setText(committedValue);
        }
    }

    public String committedValue() {
        return committedValue;
    }

    public String editorText() {
        return editor.getText() == null ? "" : editor.getText();
    }

    public boolean isEditing() {
        return editingValue;
    }

    public void beginEdit() {
        editingValue = true;
        editor.setText(committedValue);
        editor.setVisible(true);
        valueLabel.setVisible(false);
        if (getStage() != null) {
            getStage().setKeyboardFocus(editor);
        }
        editor.setCursorPosition(editor.getText().length());
    }

    public void finishEdit() {
        editingValue = false;
        editor.setVisible(false);
        valueLabel.setVisible(true);
        if (getStage() != null && getStage().getKeyboardFocus() == editor) {
            getStage().setKeyboardFocus(null);
        }
    }

    public void setDimmed(boolean dimmed) {
        Color tint = dimmed
            ? ProfileMenuConfig.color(row.dimColor, Color.WHITE)
            : Color.WHITE;
        if (dimmed) {
            tint.a = row.dimOpacity;
        }
        for (Actor actor : getChildren()) {
            if (actor != changeButton) {
                actor.setColor(tint);
            }
        }
        setTouchable(dimmed ? Touchable.disabled : Touchable.enabled);
    }

    public void setChangeVisible(boolean visible) {
        changeButton.setVisible(visible);
        changeButton.setTouchable(visible && changeButton.isEnabledState() ? Touchable.enabled : Touchable.disabled);
    }

    public void setChangeEnabled(boolean enabled) {
        changeButton.setEnabledState(enabled);
        changeButton.setTouchable(enabled && changeButton.isVisible() ? Touchable.enabled : Touchable.disabled);
    }

    @Override
    public void dispose() {
        if (iconTexture != null) {
            iconTexture.dispose();
        }
    }

    private TextField createEditor(Skin skin, ProfileMenuConfig.EditingConfig editing, Runnable onConfirm) {
        TextField.TextFieldStyle style = new TextField.TextFieldStyle(skin.get(TextField.TextFieldStyle.class));
        style.font = ProfileUiFonts.resolve(editing.editorFont, editing.editorFontFallback);
        Color typedColor = ProfileMenuConfig.color(editing.editorColor, Color.BLACK);
        style.fontColor = typedColor;
        style.focusedFontColor = typedColor;
        style.disabledFontColor = typedColor;
        style.cursor = Solid.drawable(
            ProfileMenuConfig.color(editing.cursorColor, Color.valueOf("1A140E")),
            editing.cursorWidth,
            editing.cursorHeight
        );
        style.background = Solid.drawable(
            ProfileMenuConfig.color(editing.editorBackgroundColor, new Color(0.97f, 0.93f, 0.85f, 1f)),
            8f,
            row.height
        );
        TextField field = new TextField("", style);
        field.setBlinkTime(editing.cursorBlinkSeconds);
        field.setFocusTraversal(false);
        field.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ENTER || keycode == Input.Keys.NUMPAD_ENTER) {
                    onConfirm.run();
                    return true;
                }
                return false;
            }
        });
        return field;
    }

    private ActionButton createChangeButton(Runnable onChange) {
        ButtonTheme theme = new ButtonTheme()
            .size(row.changeButtonWidth, row.changeButtonHeight)
            .padding(row.changeButtonPadX, row.changeButtonPadY);
        theme.labelScale = row.changeButtonLabelScale;
        theme.hoverScale = row.changeButtonHoverScale;
        theme.hoverDuration = row.changeButtonHoverDuration;
        theme.disabledColor = ProfileMenuConfig.color(row.changeButtonDisabledColor, new Color(0.54f, 0.54f, 0.54f, 0.93f));
        ActionButton button = new ActionButton(
            row.changeLabel,
            UiTheme.buttonStyleOrDefault(row.changeButtonStyle),
            theme
        );
        button.onClick(onChange);
        return button;
    }
}

package app.view.auth;

import app.service.SecurityQuestionCatalog;
import app.view.mainmenu.settings.SettingsControls;
import app.view.ui.Solid;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import java.util.ArrayList;
import java.util.List;

public final class AuthField extends Table implements Disposable {
    private final AuthMenuConfig.FieldDefinition definition;
    private final AuthMenuConfig.FieldStyleConfig style;
    private final AuthMenuConfig.EditingConfig editing;
    private final Texture iconTexture;
    private final Label errorLabel;
    private TextField textField;
    private SelectBox<SelectItem> selectBox;
    private Table inputFrame;
    private final List<Runnable> changeListeners = new ArrayList<>();

    public AuthField(
        AuthMenuConfig config,
        AuthMenuConfig.FieldDefinition definition,
        Skin skin,
        Runnable onEnter
    ) {
        this.definition = definition;
        this.style = config.field;
        this.editing = config.editing;
        left().top();
        iconTexture = AuthIcons.load(definition.iconPath);
        Image icon = AuthIcons.image(iconTexture, style.iconSize);
        Label title = new Label(definition.label, new Label.LabelStyle(
            AuthUiFonts.resolve(style.labelFont, style.labelFontFallback),
            AuthMenuConfig.color(style.labelColor, Color.valueOf("F4E7B0"))
        ));
        title.setFontScale(style.labelFontSize);
        title.setAlignment(AuthMenuConfig.textAlign("left"));
        errorLabel = new Label("", new Label.LabelStyle(
            AuthUiFonts.resolve(style.errorFont, style.errorFontFallback),
            AuthMenuConfig.color(style.errorColor, Color.valueOf("FF6B5C"))
        ));
        errorLabel.setFontScale(style.errorFontSize);
        errorLabel.setWrap(true);
        errorLabel.setVisible(false);
        Actor input = createInput(skin, onEnter);
        float fieldWidth = definition.width > 0f ? definition.width : style.width;
        float fieldHeight = definition.height > 0f ? definition.height : style.height;
        inputFrame = new Table();
        inputFrame.setBackground(AuthDrawables.inputBackground(style, false));
        inputFrame.add(input)
            .grow()
            .pad(style.inputPadTop, style.inputPadLeft, style.inputPadBottom, style.inputPadRight);
        Table row = new Table();
        row.left();
        row.add(icon)
            .size(style.iconSize)
            .padRight(style.iconTitleGap)
            .padLeft(style.iconOffsetX)
            .padBottom(style.iconOffsetY);
        row.add(title).width(style.titleWidth).left().padRight(style.titleValueGap);
        row.add(inputFrame).width(fieldWidth).height(fieldHeight).left().pad(style.fieldPadding);
        add(row).left().growX();
        row();
        add(errorLabel)
            .left()
            .padLeft(style.iconSize + style.iconTitleGap + style.titleWidth + style.titleValueGap)
            .padTop(0f)
            .height(0f)
            .growX();
    }

    public String id() {
        return definition.id;
    }

    public boolean required() {
        return definition.required;
    }

    public boolean validationEnabled() {
        return definition.validationEnabled && style.validationEnabled;
    }

    public String value() {
        if (selectBox != null) {
            SelectItem selected = selectBox.getSelected();
            return selected == null ? "" : selected.value;
        }
        return textField == null || textField.getText() == null ? "" : textField.getText();
    }

    public boolean hasValue() {
        String current = value();
        if (selectBox != null) {
            return current != null && !current.isBlank() && !current.equals(definition.emptyValue);
        }
        return current != null && !current.isBlank();
    }

    public void setValue(String value) {
        if (selectBox != null) {
            String target = value == null ? "" : value;
            Array<SelectItem> items = selectBox.getItems();
            for (SelectItem item : items) {
                if (item.value.equals(target)) {
                    selectBox.setSelected(item);
                    return;
                }
            }
            if (items.size > 0) {
                selectBox.setSelectedIndex(0);
            }
            return;
        }
        if (textField != null) {
            textField.setText(value == null ? "" : value);
        }
    }

    public void clearValue() {
        if (selectBox != null) {
            if (selectBox.getItems().size > 0) {
                selectBox.setSelectedIndex(0);
            }
            return;
        }
        if (textField != null) {
            textField.setText("");
        }
    }

    public void setMasked(boolean masked) {
        if (textField == null) {
            return;
        }
        textField.setPasswordMode(masked);
        textField.setPasswordCharacter(style.maskChar());
    }

    public void focus() {
        if (getStage() == null) {
            return;
        }
        if (textField != null) {
            getStage().setKeyboardFocus(textField);
            textField.setCursorPosition(textField.getText().length());
        } else if (selectBox != null) {
            getStage().setKeyboardFocus(selectBox);
        }
    }

    public void showError(String message) {
        boolean show = message != null && !message.isBlank();
        errorLabel.setText(show ? message : "");
        errorLabel.setVisible(show);
        var cell = getCell(errorLabel);
        if (cell != null) {
            cell.height(show ? style.errorHeight : 0f);
            cell.padTop(show ? style.errorSpacing : 0f);
        }
        invalidateHierarchy();
    }

    public void clearError() {
        showError("");
    }

    public void onChange(Runnable listener) {
        if (listener != null) {
            changeListeners.add(listener);
        }
    }

    public boolean isTextInput() {
        return textField != null;
    }

    @Override
    public void dispose() {
        if (iconTexture != null) {
            iconTexture.dispose();
        }
    }

    private Actor createInput(Skin skin, Runnable onEnter) {
        String type = definition.type == null ? "text" : definition.type.trim().toLowerCase();
        if ("selection".equals(type) || "question".equals(type)) {
            return createSelect(skin);
        }
        return createText(skin, onEnter, type);
    }

    private Actor createText(Skin skin, Runnable onEnter, String type) {
        TextField.TextFieldStyle fieldStyle = new TextField.TextFieldStyle(skin.get(TextField.TextFieldStyle.class));
        fieldStyle.font = AuthUiFonts.resolve(style.inputFont, style.inputFontFallback);
        Color typedColor = AuthMenuConfig.color(style.inputColor, Color.valueOf("1A140E"));
        fieldStyle.fontColor = typedColor;
        fieldStyle.focusedFontColor = typedColor;
        fieldStyle.disabledFontColor = typedColor;
        fieldStyle.messageFont = AuthUiFonts.resolve(style.placeholderFont, style.placeholderFontFallback);
        Color placeholder = AuthMenuConfig.color(style.placeholderColor, Color.valueOf("8A8A8A"));
        placeholder.a = style.placeholderAlpha;
        fieldStyle.messageFontColor = placeholder;
        fieldStyle.cursor = Solid.drawable(
            AuthMenuConfig.color(editing.cursorColor, Color.valueOf("1A140E")),
            editing.cursorWidth,
            editing.cursorHeight
        );
        fieldStyle.background = null;
        fieldStyle.focusedBackground = null;
        fieldStyle.disabledBackground = null;
        textField = new TextField("", fieldStyle);
        textField.setMessageText(definition.placeholder);
        textField.setBlinkTime(editing.cursorBlinkSeconds);
        textField.setFocusTraversal(editing.focusTraversal);
        if ("password".equals(type) || definition.masked) {
            textField.setPasswordMode(true);
            textField.setPasswordCharacter(style.maskChar());
        }
        if ("integer".equals(type)) {
            textField.setTextFieldFilter((field, c) -> Character.isDigit(c));
        }
        textField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                clearError();
                notifyChange();
            }
        });
        textField.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if ((keycode == Input.Keys.ENTER || keycode == Input.Keys.NUMPAD_ENTER) && editing.enterAdvances) {
                    if (onEnter != null) {
                        onEnter.run();
                    }
                    return true;
                }
                return false;
            }
        });
        bindFocus(textField);
        return textField;
    }

    private Actor createSelect(Skin skin) {
        SelectBox.SelectBoxStyle source = skin.get(SelectBox.SelectBoxStyle.class);
        SelectBox.SelectBoxStyle boxStyle = new SelectBox.SelectBoxStyle(source);
        boxStyle.background = null;
        boxStyle.backgroundOver = null;
        boxStyle.backgroundOpen = null;
        boxStyle.font = AuthUiFonts.resolve(style.inputFont, style.inputFontFallback);
        boxStyle.fontColor = AuthMenuConfig.color(style.inputColor, Color.valueOf("1A140E"));
        if (boxStyle.listStyle == null) {
            boxStyle.listStyle = new com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle();
        } else {
            boxStyle.listStyle = new com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle(boxStyle.listStyle);
        }
        boxStyle.listStyle.font = boxStyle.font;
        boxStyle.listStyle.fontColorSelected = boxStyle.fontColor;
        boxStyle.listStyle.fontColorUnselected = boxStyle.fontColor;
        selectBox = new SelectBox<>(boxStyle);
        Array<SelectItem> items = new Array<>();
        if ("securityQuestions".equalsIgnoreCase(definition.optionsSource)) {
            for (SecurityQuestionCatalog.Entry entry : SecurityQuestionCatalog.list()) {
                items.add(new SelectItem(String.valueOf(entry.number), entry.display()));
            }
        } else {
            for (AuthMenuConfig.SelectOption option : definition.options) {
                items.add(new SelectItem(option.value, option.label));
            }
        }
        selectBox.setItems(items);
        if (items.size > 0) {
            selectBox.setSelectedIndex(0);
        }
        selectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                clearError();
                notifyChange();
            }
        });
        bindFocus(selectBox);
        return selectBox;
    }

    private void bindFocus(Actor actor) {
        actor.addListener(new FocusListener() {
            @Override
            public void keyboardFocusChanged(FocusEvent event, Actor focused, boolean isFocused) {
                if (inputFrame != null) {
                    inputFrame.setBackground(AuthDrawables.inputBackground(style, isFocused));
                }
            }
        });
    }

    private void notifyChange() {
        for (Runnable listener : changeListeners) {
            listener.run();
        }
    }

    public static CheckBoxBundle checkBox(
        AuthMenuConfig.CheckboxStyleConfig style,
        AuthMenuConfig.CheckboxDefinition definition
    ) {
        com.badlogic.gdx.scenes.scene2d.ui.CheckBox box = SettingsControls.checkBox(
            definition.label,
            style.style,
            AuthMenuConfig.color(style.hoverColor, Color.valueOf("FFF4C0"))
        );
        box.getLabel().setStyle(new Label.LabelStyle(
            AuthUiFonts.resolve(style.labelFont, "BRIANNETOD"),
            AuthMenuConfig.color(style.labelColor, Color.valueOf("E4DCC4"))
        ));
        box.getLabel().setFontScale(style.labelFontSize);
        if (box.getImageCell() != null) {
            box.getImageCell().size(style.size);
        }
        return new CheckBoxBundle(definition.id, definition.afterField, box);
    }

    public static final class CheckBoxBundle {
        public final String id;
        public final String afterField;
        public final com.badlogic.gdx.scenes.scene2d.ui.CheckBox box;

        public CheckBoxBundle(String id, String afterField, com.badlogic.gdx.scenes.scene2d.ui.CheckBox box) {
            this.id = id;
            this.afterField = afterField;
            this.box = box;
        }
    }

    private static final class SelectItem {
        final String value;
        final String label;

        SelectItem(String value, String label) {
            this.value = value;
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }
}

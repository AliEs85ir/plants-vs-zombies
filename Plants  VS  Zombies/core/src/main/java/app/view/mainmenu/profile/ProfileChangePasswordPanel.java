package app.view.mainmenu.profile;

import app.view.mainmenu.settings.SettingsControls;
import app.view.ui.Solid;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public final class ProfileChangePasswordPanel extends Table {
    private final ProfileMenuConfig.ChangePasswordConfig config;
    private final ProfileMenuConfig.EditingConfig editing;
    private final TextField newPassword;
    private final TextField repeatPassword;
    private final CheckBox showPassword;

    public ProfileChangePasswordPanel(ProfileMenuConfig menuConfig, Skin skin, Runnable onConfirm) {
        this.config = menuConfig.changePassword;
        this.editing = menuConfig.editing;
        left().top();
        newPassword = passwordField(skin, onConfirm);
        repeatPassword = passwordField(skin, onConfirm);
        addLabeledField(config.newPasswordTitle, newPassword);
        addLabeledField(config.repeatPasswordTitle, repeatPassword);
        showPassword = SettingsControls.checkBox(
            config.showPasswordLabel,
            config.checkboxStyle,
            ProfileMenuConfig.color(config.checkboxHoverColor, Color.valueOf("FFF4C0"))
        );
        showPassword.getLabel().setStyle(new Label.LabelStyle(
            ProfileUiFonts.resolve(config.checkboxLabelFont, "BRIANNETOD"),
            ProfileMenuConfig.color(config.checkboxLabelColor, Color.valueOf("E4DCC4"))
        ));
        showPassword.getLabel().setFontScale(config.checkboxLabelFontSize);
        if (showPassword.getImageCell() != null) {
            showPassword.getImageCell().size(config.checkboxSize);
        }
        showPassword.setChecked(false);
        applyMasking();
        showPassword.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                applyMasking();
            }
        });
        add(showPassword).left().padTop(config.checkboxTopPadding).height(Math.max(config.checkboxSize, 32f));
    }

    public void clearPasswords() {
        newPassword.setText("");
        repeatPassword.setText("");
        showPassword.setChecked(false);
        applyMasking();
    }

    public void focusNewPassword() {
        if (getStage() != null) {
            getStage().setKeyboardFocus(newPassword);
        }
    }

    public String newPasswordText() {
        return textOf(newPassword);
    }

    public String repeatPasswordText() {
        return textOf(repeatPassword);
    }

    private void addLabeledField(String titleText, TextField field) {
        Label title = new Label(titleText, new Label.LabelStyle(
            ProfileUiFonts.resolve(config.titleFont, config.titleFontFallback),
            ProfileMenuConfig.color(config.titleColor, Color.valueOf("F4E7B0"))
        ));
        title.setFontScale(config.titleFontSize);
        title.setAlignment(ProfileMenuConfig.textAlign(config.titleAlignment));
        add(title).left().padBottom(config.titleBottomPadding);
        row();
        add(field).width(config.fieldWidth).height(config.fieldHeight).left().padBottom(config.fieldSpacing);
        row();
    }

    private TextField passwordField(Skin skin, Runnable onConfirm) {
        TextField.TextFieldStyle style = new TextField.TextFieldStyle(skin.get(TextField.TextFieldStyle.class));
        style.font = ProfileUiFonts.resolve(config.inputFont, config.inputFontFallback);
        Color typedColor = ProfileMenuConfig.color(config.inputColor, Color.BLACK);
        style.fontColor = typedColor;
        style.focusedFontColor = typedColor;
        style.disabledFontColor = typedColor;
        style.cursor = Solid.drawable(
            ProfileMenuConfig.color(editing.cursorColor, Color.valueOf("1A140E")),
            editing.cursorWidth,
            editing.cursorHeight
        );
        style.background = Solid.drawable(
            ProfileMenuConfig.color(config.inputBackgroundColor, new Color(0.97f, 0.93f, 0.85f, 1f)),
            8f,
            config.fieldHeight
        );
        TextField field = new TextField("", style);
        field.setBlinkTime(editing.cursorBlinkSeconds);
        field.setFocusTraversal(false);
        field.setPasswordMode(true);
        field.setPasswordCharacter(maskChar());
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

    private void applyMasking() {
        boolean visible = showPassword.isChecked();
        newPassword.setPasswordMode(!visible);
        repeatPassword.setPasswordMode(!visible);
        newPassword.setPasswordCharacter(maskChar());
        repeatPassword.setPasswordCharacter(maskChar());
    }

    private char maskChar() {
        String mask = config.maskCharacter;
        if (mask == null || mask.isBlank()) {
            return '*';
        }
        return mask.charAt(0);
    }

    private static String textOf(TextField field) {
        return field.getText() == null ? "" : field.getText();
    }
}

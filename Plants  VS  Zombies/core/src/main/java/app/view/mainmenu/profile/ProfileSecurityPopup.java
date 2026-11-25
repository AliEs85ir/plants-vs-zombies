package app.view.mainmenu.profile;

import app.view.ui.ModalOverlay;
import app.view.ui.SkinPanel;
import app.view.ui.Solid;
import app.view.ui.UiTheme;
import app.view.ui.button.ActionButton;
import app.view.ui.button.ButtonTheme;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;

import java.util.function.Consumer;

/**
 * Modal security-question popup used before username/email profile updates.
 * Never logs or redisplays the submitted answer after verify/cancel.
 */
public final class ProfileSecurityPopup extends ModalOverlay {
    private final ProfileMenuConfig.SecurityPopupConfig popup;
    private final Color overlay;
    private final TextField answerField;
    private final Label errorLabel;
    private final ActionButton cancelButton;
    private final ActionButton verifyButton;
    private boolean submitting;

    public ProfileSecurityPopup(
        ProfileMenuConfig config,
        Skin skin,
        String questionText,
        Consumer<String> onVerify,
        Runnable onCancel
    ) {
        super(true);
        this.popup = config.securityPopup;
        Color configured = ProfileMenuConfig.color(popup.overlayColor, new Color(0f, 0f, 0f, popup.dimAlpha));
        this.overlay = new Color(configured.r, configured.g, configured.b, popup.dimAlpha);

        SkinPanel panel = new SkinPanel(
            popup.panelBackgroundDrawable,
            UiTheme.BG_TAB
        );
        panel.pad(popup.panelPadY, popup.panelPadX, popup.panelPadY, popup.panelPadX);
        panel.top();

        Label title = new Label(popup.title, new Label.LabelStyle(
            ProfileUiFonts.resolve(popup.titleFont, popup.titleFontFallback),
            ProfileMenuConfig.color(popup.titleColor, Color.valueOf("F4E7B0"))
        ));
        title.setFontScale(popup.titleFontSize);
        title.setAlignment(Align.center);
        panel.add(title).growX().center();
        panel.row();

        Label question = new Label(
            questionText == null ? "" : questionText,
            new Label.LabelStyle(
                ProfileUiFonts.resolve(popup.questionFont, popup.questionFontFallback),
                ProfileMenuConfig.color(popup.questionColor, Color.valueOf("F2E7C4"))
            )
        );
        question.setFontScale(popup.questionFontSize);
        question.setAlignment(ProfileMenuConfig.textAlign(popup.questionAlignment));
        question.setWrap(true);
        panel.add(question)
            .width(popup.panelWidth - popup.panelPadX * 2f)
            .padTop(10f)
            .center();
        panel.row();

        answerField = createAnswerField(skin);
        Table answerWrap = new Table();
        Drawable answerBg = UiTheme.drawableOrFallback(
            popup.answerBackgroundDrawable,
            UiTheme.BG_MULTIPLIER
        );
        if (answerBg != null) {
            answerWrap.setBackground(answerBg);
        } else {
            answerWrap.setBackground(Solid.drawable(new Color(0.97f, 0.93f, 0.85f, 1f), 8f, popup.answerFieldHeight));
        }
        answerWrap.pad(
            popup.answerInputPadTop,
            popup.answerInputPadLeft,
            popup.answerInputPadBottom,
            popup.answerInputPadRight
        );
        answerWrap.add(answerField).growX().height(popup.answerFieldHeight - popup.answerInputPadTop - popup.answerInputPadBottom);
        panel.add(answerWrap)
            .width(popup.answerFieldWidth)
            .height(popup.answerFieldHeight)
            .padTop(14f)
            .center();
        panel.row();

        errorLabel = new Label("", new Label.LabelStyle(
            ProfileUiFonts.resolve(popup.errorFont, popup.errorFontFallback),
            ProfileMenuConfig.color(popup.errorColor, Color.valueOf("FF6B5C"))
        ));
        errorLabel.setFontScale(popup.errorFontSize);
        errorLabel.setAlignment(Align.center);
        errorLabel.setWrap(true);
        errorLabel.setVisible(false);
        panel.add(errorLabel)
            .width(popup.panelWidth - popup.panelPadX * 2f)
            .padTop(popup.errorTopPadding)
            .center();
        panel.row();

        cancelButton = button(popup.cancelLabel, () -> {
            if (submitting) {
                return;
            }
            clearAnswer();
            if (onCancel != null) {
                onCancel.run();
            }
        });
        verifyButton = button(popup.verifyLabel, () -> {
            if (submitting) {
                return;
            }
            String answer = answerField.getText() == null ? "" : answerField.getText().trim();
            clearError();
            if (onVerify != null) {
                onVerify.accept(answer);
            }
        });

        Table buttons = new Table();
        buttons.add(cancelButton).width(popup.buttonWidth).height(popup.buttonHeight);
        buttons.add(verifyButton)
            .width(popup.buttonWidth)
            .height(popup.buttonHeight)
            .padLeft(popup.buttonGap);
        panel.add(buttons).padTop(popup.buttonTopPadding).center();

        add(panel)
            .size(popup.panelWidth, popup.panelHeight)
            .padLeft(popup.offsetX)
            .padBottom(popup.offsetY)
            .align(Align.center);
    }

    @Override
    protected Color overlayColor() {
        return overlay;
    }

    public void setSubmitting(boolean submitting) {
        this.submitting = submitting;
        cancelButton.setEnabledState(!submitting);
        verifyButton.setEnabledState(!submitting);
        answerField.setDisabled(submitting);
    }

    public boolean isSubmitting() {
        return submitting;
    }

    public void showError(String message) {
        errorLabel.setText(message == null ? "" : message);
        errorLabel.setVisible(true);
        clearAnswer();
        setSubmitting(false);
    }

    public void clearError() {
        errorLabel.setText("");
        errorLabel.setVisible(false);
    }

    public void focusAnswer() {
        if (getStage() != null) {
            getStage().setKeyboardFocus(answerField);
        }
    }

    private void clearAnswer() {
        answerField.setText("");
    }

    private TextField createAnswerField(Skin skin) {
        TextField.TextFieldStyle style = new TextField.TextFieldStyle(skin.get(TextField.TextFieldStyle.class));
        style.font = ProfileUiFonts.resolve(popup.answerFont, popup.answerFontFallback);
        Color typed = ProfileMenuConfig.color(popup.answerColor, Color.BLACK);
        style.fontColor = typed;
        style.focusedFontColor = typed;
        style.disabledFontColor = typed;
        style.background = null;
        style.focusedBackground = null;
        TextField field = new TextField("", style);
        field.setPasswordMode(true);
        field.setPasswordCharacter('*');
        field.setFocusTraversal(false);
        field.setMessageText("");
        return field;
    }

    private ActionButton button(String label, Runnable action) {
        ButtonTheme theme = new ButtonTheme().size(popup.buttonWidth, popup.buttonHeight);
        theme.labelScale = popup.buttonLabelScale;
        theme.hoverScale = popup.buttonHoverScale;
        theme.hoverDuration = popup.buttonHoverDuration;
        ActionButton button = new ActionButton(label, UiTheme.buttonStyleOrDefault(popup.buttonStyle), theme);
        button.onClick(action);
        return button;
    }
}

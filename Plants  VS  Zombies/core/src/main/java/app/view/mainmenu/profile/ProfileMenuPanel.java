package app.view.mainmenu.profile;

import app.controller.menu.ProfileMenuController;
import app.validator.AuthErrorCodes;
import app.model.App;
import app.model.user.User;
import app.service.AppService;
import app.view.screen.ScreenManager;
import app.view.ui.ScrollPanes;
import app.view.ui.UiTheme;
import app.view.ui.button.ActionButton;
import app.view.ui.button.ButtonTheme;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import common.Result;
import game.audio.AudioId;
import game.audio.AudioManager;
import game.settings.GameSettings;
import plants_vs_zombies.PlantsVSZombies;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ProfileMenuPanel extends WidgetGroup implements Disposable {
    private enum Mode {
        PROFILE,
        CHANGE_PASSWORD
    }

    private final ProfileMenuConfig config;
    private final Skin skin;
    private final Stage stage;
    private final PlantsVSZombies game;
    private final ProfileMenuController controller = new ProfileMenuController();
    private final List<ProfileFieldRow> rows = new ArrayList<>();
    private final Table fieldsTable = new Table();
    private final Table footerTable = new Table();
    private final Table content = new Table();
    private final ScrollPane scrollPane;
    private final Label errorLabel;
    private final Cell<Label> errorCell;
    private final Cell<Table> fieldsCell;
    private final Cell<ProfileChangePasswordPanel> passwordCell;
    private final ProfileChangePasswordPanel passwordPanel;
    private final ActionButton changePasswordButton;
    private final ActionButton logoutButton;
    private final ActionButton confirmButton;
    private final ActionButton cancelButton;
    private final ProfilePicturePanel picturePanel;
    private ProfileLogoutPopup logoutPopup;
    private ProfileSecurityPopup securityPopup;
    private ProfileFieldId pendingSecureField;
    private String pendingSecureValue;
    private Mode mode = Mode.PROFILE;
    private ProfileFieldRow editingRow;
    private boolean passwordChangeInFlight;
    private boolean disposed;

    public ProfileMenuPanel(Skin skin, Stage stage, PlantsVSZombies game) {
        this.config = ProfileMenuConfigLoader.load();
        this.skin = skin;
        this.stage = stage;
        this.game = game;
        setFillParent(false);
        setTransform(false);
        setTouchable(Touchable.childrenOnly);

        fieldsTable.top().left();
        boolean debug = GameSettings.gameplay().debugMode();
        for (ProfileMenuConfig.FieldDefinition definition : config.orderedFields()) {
            ProfileFieldId id = ProfileFieldId.fromId(definition.id);
            if (id == null) {
                continue;
            }
            ProfileFieldRow row = new ProfileFieldRow(
                config,
                definition,
                skin,
                () -> beginEdit(id),
                this::confirmEdit
            );
            row.setChangeVisible(true);
            row.setChangeEnabled(canChange(definition, debug));
            rows.add(row);
            fieldsTable.add(row).growX().left().padBottom(config.layout.fieldSpacing);
            fieldsTable.row();
        }
        if (fieldsTable.getCells().size > 0) {
            fieldsTable.getCells().peek().padBottom(0f);
        }
        errorLabel = new Label("", new Label.LabelStyle(
            ProfileUiFonts.resolve(config.error.font, config.error.fontFallback),
            ProfileMenuConfig.color(config.error.color, Color.valueOf("FF6B5C"))
        ));
        errorLabel.setFontScale(config.error.fontSize);
        errorLabel.setAlignment(ProfileMenuConfig.textAlign(config.error.alignment));
        errorLabel.setWrap(true);
        errorLabel.setVisible(false);

        passwordPanel = new ProfileChangePasswordPanel(config, skin, this::confirmPasswordChange);
        passwordPanel.setVisible(false);

        changePasswordButton = footerButton(config.footer.changePasswordLabel, this::enterPasswordMode);
        logoutButton = footerButton(config.footer.logoutLabel, this::showLogoutPopup);
        confirmButton = footerButton(config.footer.confirmLabel, this::confirmPasswordChange);
        cancelButton = footerButton(config.footer.cancelLabel, this::cancelPasswordMode);
        placeFooterButtons(false);

        content.top().left();
        fieldsCell = content.add(fieldsTable).growX().left();
        content.row();
        passwordCell = content.add(passwordPanel).growX().left().padTop(0f).height(0f);
        content.row();
        // Status/error slot shared by profile edits and password-change (must stay visible in both modes).
        errorCell = content.add(errorLabel)
            .growX()
            .left()
            .padTop(0f)
            .padLeft(config.error.offsetX)
            .padBottom(0f)
            .height(0f);
        content.row();
        content.add(footerTable)
            .left()
            .padTop(config.layout.footerTopPadding)
            .padLeft(config.footer.offsetX)
            .padBottom(config.footer.offsetY);

        scrollPane = ScrollPanes.verticalHidden(content);
        addActor(scrollPane);
        picturePanel = new ProfilePicturePanel(config, this::applyPicture);
        addActor(picturePanel);
        refreshValues();
        AppService.syncIdentityFromServer(this::refreshValues);
    }

    @Override
    public void layout() {
        float width = getWidth();
        float height = getHeight();
        float innerWidth = Math.max(80f, width - config.layout.paddingLeft - config.layout.paddingRight);
        float paneHeight = Math.max(80f, height - config.layout.paddingTop - config.layout.paddingBottom);
        float y = config.layout.paddingBottom + config.layout.offsetY;

        float pictureWidth = config.picture.panelWidth;
        float pictureGap = config.picture.panelGap;
        boolean showPicture = pictureWidth >= config.picture.minPanelWidth && innerWidth > 520f;
        if (showPicture && innerWidth < pictureWidth + pictureGap + 520f) {
            pictureWidth = Math.max(config.picture.minPanelWidth, innerWidth - 520f - pictureGap);
            showPicture = pictureWidth >= config.picture.minPanelWidth;
        }

        float fieldsBudget = showPicture ? Math.max(80f, innerWidth - pictureWidth - pictureGap) : innerWidth;
        float contentWidth = config.layout.contentWidth > 0f
            ? Math.min(config.layout.contentWidth, fieldsBudget)
            : Math.max(80f, fieldsBudget);
        int align = ProfileMenuConfig.align(config.layout.contentAlignment, Align.top | Align.left);
        float x = ((align & Align.right) != 0)
            ? width - contentWidth - config.layout.paddingRight - (showPicture ? pictureWidth + pictureGap : 0f)
            : config.layout.paddingLeft;
        if ((align & Align.center) == Align.center && (align & Align.left) == 0 && (align & Align.right) == 0) {
            float used = contentWidth + (showPicture ? pictureWidth + pictureGap : 0f);
            x = config.layout.paddingLeft + Math.max(0f, (innerWidth - used) * 0.5f);
        }
        x += config.layout.offsetX;
        scrollPane.setBounds(x, y, Math.max(1f, contentWidth), paneHeight);

        picturePanel.setVisible(showPicture);
        picturePanel.setTouchable(showPicture ? Touchable.childrenOnly : Touchable.disabled);
        if (showPicture) {
            float pictureX = x + contentWidth + pictureGap;
            float maxPictureX = width - config.layout.paddingRight - pictureWidth;
            if (pictureX > maxPictureX) {
                pictureX = Math.max(x + contentWidth + 8f, maxPictureX);
                pictureWidth = Math.max(1f, width - config.layout.paddingRight - pictureX);
            }
            picturePanel.setBounds(pictureX, y, Math.max(1f, pictureWidth), paneHeight);
        }
    }

    @Override
    public void dispose() {
        disposed = true;
        for (ProfileFieldRow row : rows) {
            row.dispose();
        }
        hideLogoutPopup();
        hideSecurityPopup();
    }

    @Override
    public boolean remove() {
        dispose();
        return super.remove();
    }

    private void beginEdit(ProfileFieldId id) {
        if (mode != Mode.PROFILE || editingRow != null) {
            return;
        }
        ProfileMenuConfig.FieldDefinition definition = definition(id);
        if (definition == null || !canChange(definition, GameSettings.gameplay().debugMode())) {
            return;
        }
        ProfileFieldRow target = row(id);
        if (target == null) {
            return;
        }
        hideError();
        editingRow = target;
        for (ProfileFieldRow row : rows) {
            row.setDimmed(row != target);
            row.setChangeEnabled(false);
        }
        changePasswordButton.setEnabledState(false);
        logoutButton.setEnabledState(false);
        picturePanel.setPickingEnabled(false);
        target.beginEdit();
    }

    private void confirmEdit() {
        if (editingRow == null || securityPopup != null) {
            return;
        }

        ProfileFieldId id = editingRow.fieldId();
        String value = editingRow.editorText();

        editingRow.setChangeEnabled(false);

        if (id == ProfileFieldId.USERNAME || id == ProfileFieldId.EMAIL) {
            Result local = id == ProfileFieldId.USERNAME
                ? controller.validateUsernameChange(value)
                : controller.validateEmailChange(value);
            if (!local.success()) {
                showError(local.message());
                AudioManager.play(AudioId.UI_ERROR);
                editingRow.setValue(editingRow.committedValue());
                endEdit();
                return;
            }
            pendingSecureField = id;
            pendingSecureValue = value == null ? "" : value.trim();
            showSecurityPopup();
            return;
        }

        applyField(id, value, null, safeCallback(result -> {
            if (result.success()) {
                hideError();
                refreshValues();
                endEdit();
                return;
            }

            showError(result.message());
            AudioManager.play(AudioId.UI_ERROR);

            if (editingRow != null) {
                editingRow.setValue(editingRow.committedValue());
            }

            endEdit();
        }));
    }

    private void endEdit() {
        if (editingRow != null) {
            editingRow.finishEdit();
            editingRow = null;
        }
        boolean debug = GameSettings.gameplay().debugMode();
        for (ProfileFieldRow row : rows) {
            row.setDimmed(false);
            ProfileMenuConfig.FieldDefinition definition = definition(row.fieldId());
            row.setChangeVisible(true);
            row.setChangeEnabled(definition != null && canChange(definition, debug));
        }
        changePasswordButton.setEnabledState(true);
        logoutButton.setEnabledState(true);
        picturePanel.setPickingEnabled(true);
    }

    private void applyField(
        ProfileFieldId id,
        String value,
        String securityAnswer,
        Consumer<Result> callback
    ) {
        switch (id) {
            case USERNAME -> controller.manageChangeUsername(value, securityAnswer, callback);

            case EMAIL -> controller.manageChangeEmail(value, securityAnswer, callback);

            case NICKNAME -> controller.manageChangeNickname(value, callback);

            case COINS -> callback.accept(controller.manageChangeCoins(value));

            case GEMS -> callback.accept(controller.manageChangeGems(value));

            default -> callback.accept(
                new Result(false, config.messages.invalidNumber)
            );
        }
    }

    private void showSecurityPopup() {
        hideSecurityPopup();
        String question = controller.securityQuestionText();
        securityPopup = new ProfileSecurityPopup(
            config,
            skin,
            question,
            this::verifySecurityAnswer,
            this::cancelSecurityPopup
        );
        stage.addActor(securityPopup);
        securityPopup.focusAnswer();
    }

    private void cancelSecurityPopup() {
        hideSecurityPopup();
        pendingSecureField = null;
        pendingSecureValue = null;
        if (editingRow != null) {
            editingRow.setValue(editingRow.committedValue());
        }
        endEdit();
    }

    private void hideSecurityPopup() {
        if (securityPopup != null) {
            securityPopup.remove();
            securityPopup = null;
        }
    }

    private void verifySecurityAnswer(String answer) {
        if (securityPopup == null || securityPopup.isSubmitting()) {
            return;
        }
        if (pendingSecureField == null || pendingSecureValue == null) {
            cancelSecurityPopup();
            return;
        }
        securityPopup.setSubmitting(true);
        ProfileFieldId id = pendingSecureField;
        String value = pendingSecureValue;
        applyField(id, value, answer, safeCallback(result -> {
            if (result.success()) {
                hideSecurityPopup();
                pendingSecureField = null;
                pendingSecureValue = null;
                hideError();
                refreshValues();
                endEdit();
                return;
            }

            AudioManager.play(AudioId.UI_ERROR);
            if (AuthErrorCodes.SECURITY_ANSWER_INCORRECT.equals(result.errorCode())
                || AuthErrorCodes.SECURITY_ANSWER_REQUIRED.equals(result.errorCode())) {
                if (securityPopup != null) {
                    securityPopup.showError(result.message());
                }
                return;
            }

            hideSecurityPopup();
            pendingSecureField = null;
            pendingSecureValue = null;
            showError(result.message());
            if (editingRow != null) {
                editingRow.setValue(editingRow.committedValue());
            }
            endEdit();
        }));
    }

    private void enterPasswordMode() {
        if (editingRow != null || mode == Mode.CHANGE_PASSWORD) {
            return;
        }
        mode = Mode.CHANGE_PASSWORD;
        hideError();
        setFieldsVisible(false);
        setPasswordPanelVisible(true);
        passwordPanel.clearPasswords();
        passwordPanel.focusNewPassword();
        placeFooterButtons(true);
        picturePanel.setPickingEnabled(false);
    }

    private void cancelPasswordMode() {
        passwordChangeInFlight = false;
        passwordPanel.clearPasswords();
        hideError();
        leavePasswordMode();
    }

    private void confirmPasswordChange() {
        if (mode != Mode.CHANGE_PASSWORD || passwordChangeInFlight) {
            return;
        }
        passwordChangeInFlight = true;
        confirmButton.setEnabledState(false);
        cancelButton.setEnabledState(false);

        controller.manageConfirmNewPassword(
            passwordPanel.newPasswordText(),
            passwordPanel.repeatPasswordText(),
            safeCallback(result -> {
                passwordChangeInFlight = false;
                confirmButton.setEnabledState(true);
                cancelButton.setEnabledState(true);

                if (result.success()) {
                    passwordPanel.clearPasswords();
                    leavePasswordMode();
                    showStatus(result.message(), true);
                    return;
                }

                showStatus(result.message(), false);
                AudioManager.play(AudioId.UI_ERROR);
                passwordPanel.clearPasswords();
            })
        );
    }

    private void leavePasswordMode() {
        mode = Mode.PROFILE;
        setPasswordPanelVisible(false);
        setFieldsVisible(true);
        placeFooterButtons(false);
        picturePanel.setPickingEnabled(true);
        refreshValues();
    }

    private void showLogoutPopup() {
        hideLogoutPopup();
        logoutPopup = new ProfileLogoutPopup(config, this::confirmLogout, this::hideLogoutPopup);
        stage.addActor(logoutPopup);
    }

    private void hideLogoutPopup() {
        if (logoutPopup != null) {
            logoutPopup.remove();
            logoutPopup = null;
        }
    }

    private void confirmLogout() {
        hideLogoutPopup();
        AppService.logout();
        ScreenManager.showLoading(game);
    }

    private void refreshValues() {
        User user = App.getCurrentUser();
        for (ProfileFieldRow row : rows) {
            row.setValue(displayValue(user, row.fieldId()));
        }
        picturePanel.setSelected(user == null ? null : user.getProfilePictureId());
    }

    private void applyPicture(String pictureId) {
        if (disposed) {
            return;
        }
        picturePanel.setPickingEnabled(false);
        controller.manageChangeProfilePicture(pictureId, safeCallback(result -> {
            picturePanel.setPickingEnabled(true);
            if (result.success()) {
                hideError();
                picturePanel.setSelected(
                    App.getCurrentUser() == null
                        ? pictureId
                        : App.getCurrentUser().getProfilePictureId()
                );
                return;
            }
            showError(result.message());
            AudioManager.play(AudioId.UI_ERROR);
            User user = App.getCurrentUser();
            picturePanel.setSelected(user == null ? null : user.getProfilePictureId());
        }));
    }

    private String displayValue(User user, ProfileFieldId id) {
        if (user == null) {
            return "";
        }
        return switch (id) {
            case USERNAME -> empty(user.getUsername());
            case NICKNAME -> empty(user.getNickname());
            case EMAIL -> empty(user.getEmail());
            case GAMES_PLAYED -> String.valueOf(user.getGamesPlayed());
            case COINS -> String.valueOf(user.getCoins());
            case GEMS -> String.valueOf(user.getGems());
            case CHAPTER_PASSED -> String.valueOf(
                user.getGameProgress() == null ? 0 : user.getGameProgress().getNumberOfLevelsCompleted()
            );
        };
    }

    private void placeFooterButtons(boolean passwordMode) {
        footerTable.clearChildren();
        footerTable.left();
        ActionButton left = passwordMode ? cancelButton : logoutButton;
        ActionButton right = passwordMode ? confirmButton : changePasswordButton;
        footerTable.add(left).width(config.footer.buttonWidth).height(config.footer.buttonHeight);
        footerTable.add(right)
            .width(config.footer.buttonWidth)
            .height(config.footer.buttonHeight)
            .padLeft(config.footer.buttonGap);
    }

    private ActionButton footerButton(String label, Runnable action) {
        ButtonTheme theme = new ButtonTheme()
            .size(config.footer.buttonWidth, config.footer.buttonHeight)
            .padding(config.footer.buttonPadX, config.footer.buttonPadY);
        theme.labelScale = config.footer.buttonLabelScale;
        theme.hoverScale = config.footer.buttonHoverScale;
        theme.hoverDuration = config.footer.buttonHoverDuration;
        ActionButton button = new ActionButton(
            label,
            UiTheme.buttonStyleOrDefault(config.footer.buttonStyle),
            theme
        );
        button.onClick(action);
        return button;
    }

    private Consumer<Result> safeCallback(Consumer<Result> callback) {
        return result -> {
            if (disposed || getStage() == null) {
                return;
            }
            callback.accept(result);
        };
    }

    private void showError(String message) {
        showStatus(message, false);
    }

    private void showStatus(String message, boolean success) {
        String text = message == null ? "" : message;
        errorLabel.setText(text);
        errorLabel.getStyle().fontColor = success
            ? Color.valueOf("7DFF8A")
            : ProfileMenuConfig.color(config.error.color, Color.valueOf("FF6B5C"));
        errorLabel.setVisible(true);
        int lines = 1;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '\n') {
                lines++;
            }
        }
        errorCell.padTop(config.error.topPadding)
            .padBottom(config.error.offsetY)
            .height(config.error.height * Math.max(1, lines));
        content.invalidateHierarchy();
    }

    private void hideError() {
        errorLabel.setText("");
        errorLabel.setVisible(false);
        errorCell.padTop(0f).padBottom(0f).height(0f);
        content.invalidateHierarchy();
    }

    private void setFieldsVisible(boolean visible) {
        fieldsTable.setVisible(visible);
        if (visible) {
            fieldsCell.height(Value.prefHeight);
        } else {
            fieldsCell.height(0f);
        }
        content.invalidateHierarchy();
    }

    private void setPasswordPanelVisible(boolean visible) {
        passwordPanel.setVisible(visible);
        if (visible) {
            passwordCell.padTop(config.layout.fieldSpacing).height(Value.prefHeight);
        } else {
            passwordCell.padTop(0f).height(0f);
        }
        content.invalidateHierarchy();
    }

    private ProfileFieldRow row(ProfileFieldId id) {
        for (ProfileFieldRow row : rows) {
            if (row.fieldId() == id) {
                return row;
            }
        }
        return null;
    }

    private ProfileMenuConfig.FieldDefinition definition(ProfileFieldId id) {
        for (ProfileMenuConfig.FieldDefinition definition : config.orderedFields()) {
            if (ProfileFieldId.fromId(definition.id) == id) {
                return definition;
            }
        }
        return null;
    }

    private static boolean canChange(ProfileMenuConfig.FieldDefinition definition, boolean debug) {
        return definition.editable || (definition.debugEditable && debug);
    }

    private static String empty(String value) {
        return value == null ? "" : value;
    }
}

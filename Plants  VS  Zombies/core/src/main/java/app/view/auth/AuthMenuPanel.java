package app.view.auth;

import app.controller.menu.LoginMenuController;
import app.controller.menu.SignupMenuController;
import app.enums.Menu;
import app.model.App;
import app.validator.ValidationMessages;
import app.view.loading.CoverImage;
import app.view.mainmenu.MenuTextures;
import app.view.screen.ScreenManager;
import app.view.ui.ScrollPanes;
import app.view.ui.SkinPanel;
import app.view.ui.UiTheme;
import app.view.ui.button.ActionButton;
import app.view.ui.button.ButtonTheme;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Disposable;
import common.Result;
import game.audio.AudioId;
import game.audio.AudioManager;
import plants_vs_zombies.PlantsVSZombies;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class AuthMenuPanel extends WidgetGroup implements Disposable {
    private final AuthMenuConfig config;
    private final Skin skin;
    private final PlantsVSZombies game;
    private final AuthViewState initialState;
    private final SignupMenuController signupController = new SignupMenuController();
    private final LoginMenuController loginController = new LoginMenuController();
    private final AuthRegistrationDraft draft = new AuthRegistrationDraft();
    private final CoverImage background = new CoverImage();
    private final SkinPanel mainPanel;
    private final SkinPanel secondaryPanel;
    private final Table contentTable = new Table();
    private final Label titleLabel;
    private final Label stepLabel;
    private final Label errorLabel;
    private final AuthSecondaryLink secondaryLink;
    private final Map<String, AuthField> fields = new LinkedHashMap<>();
    private final Map<String, ActionButton> buttons = new LinkedHashMap<>();
    private final Map<String, CheckBox> checkBoxes = new LinkedHashMap<>();
    private final List<Disposable> disposables = new ArrayList<>();
    private final Texture backgroundTexture;
    private AuthViewState state;
    private String recoveryEmail = "";
    private String recoveryAnswer = "";
    private String revealedQuestion = "";
    private Label questionRevealLabel;
    private Label termsLabel;
    private ScrollPane termsScroll;
    private CheckBox acceptTermsBox;

    public AuthMenuPanel(Skin skin, PlantsVSZombies game, AuthViewState initialState) {
        this.config = AuthMenuConfigLoader.load();
        this.skin = skin;
        this.game = game;
        this.initialState = initialState == null ? AuthViewState.SIGN_UP_STEP_1 : initialState;
        this.mainPanel = new SkinPanel(this.config.mainPanel.background, UiTheme.BG_GUARANTEE);
        this.secondaryPanel = new SkinPanel(this.config.secondaryPanel.background, UiTheme.BG_GUARANTEE);
        Drawable mainBackground = AuthDrawables.panelBackground(this.config.mainPanel);
        if (mainBackground != null) {
            this.mainPanel.setBackground(mainBackground);
        }
        this.mainPanel.padded(
            this.config.mainPanel.padTop,
            this.config.mainPanel.padLeft,
            this.config.mainPanel.padBottom,
            this.config.mainPanel.padRight
        );
        Drawable secondaryBackground = AuthDrawables.panelBackground(this.config.secondaryPanel);
        if (secondaryBackground != null) {
            this.secondaryPanel.setBackground(secondaryBackground);
        }
        this.secondaryPanel.padded(
            this.config.secondaryPanel.padTop,
            this.config.secondaryPanel.padLeft,
            this.config.secondaryPanel.padBottom,
            this.config.secondaryPanel.padRight
        );
        setFillParent(true);
        setTransform(false);
        setTouchable(Touchable.childrenOnly);

        backgroundTexture = MenuTextures.load(config.background.path, "AuthMenu");
        if (backgroundTexture != null) {
            background.setTexture(
                backgroundTexture,
                config.background.sourceWidth > 0f ? config.background.sourceWidth : backgroundTexture.getWidth(),
                config.background.sourceHeight > 0f ? config.background.sourceHeight : backgroundTexture.getHeight()
            );
            background.setOffsets(config.background.offsetX, config.background.offsetY);
        }
        background.setFillParent(true);
        addActor(background);

        titleLabel = label(
            config.title.font,
            config.title.fontFallback,
            config.title.fontSize,
            config.title.color,
            config.title.alignment
        );
        stepLabel = label(
            config.stepIndicator.font,
            config.stepIndicator.fontFallback,
            config.stepIndicator.fontSize,
            config.stepIndicator.color,
            config.stepIndicator.alignment
        );
        errorLabel = label(
            config.error.font,
            config.error.fontFallback,
            config.error.fontSize,
            config.error.color,
            config.error.alignment
        );
        errorLabel.setWrap(true);
        errorLabel.setVisible(false);
        secondaryLink = new AuthSecondaryLink(config.secondary);

        mainPanel.top().left();
        secondaryPanel.top().left();
        secondaryPanel.setName("auth-secondary");
        secondaryPanel.setTouchable(Touchable.enabled);
        secondaryPanel.add(secondaryLink).grow().fill().left().top();
        secondaryPanel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onSecondaryAction();
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    secondaryLink.setHovered(true);
                }
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (pointer == -1) {
                    secondaryLink.setHovered(false);
                }
            }
        });

        contentTable.top().left();
        Table titleRow = new Table();
        titleRow.left();
        titleRow.add(titleLabel)
            .left()
            .bottom()
            .padLeft(config.title.offsetX)
            .padTop(config.title.offsetY);
        titleRow.add(stepLabel)
            .left()
            .bottom()
            .padLeft(config.stepIndicator.padLeft)
            .padBottom(4f);
        mainPanel.add(titleRow)
            .left()
            .padBottom(config.title.bottomGap)
            .growX();
        mainPanel.row();
        mainPanel.add(contentTable).grow().left().top();
        if (config.error.showOnPanel) {
            mainPanel.row();
            mainPanel.add(errorLabel)
                .left()
                .padTop(config.error.topPadding)
                .padLeft(config.error.offsetX)
                .padBottom(config.error.offsetY)
                .height(config.error.height)
                .growX();
        }

        addActor(mainPanel);
        addActor(secondaryPanel);
        navigate(this.initialState, false);
    }

    public AuthViewState state() {
        return state;
    }

    @Override
    public void layout() {
        float worldWidth = getWidth();
        float worldHeight = getHeight();
        background.setBounds(0f, 0f, worldWidth, worldHeight);

        AuthMenuConfig.PanelConfig main = config.mainPanel;
        float mainX = main.alignToLeft ? main.marginLeft + main.offsetX : main.x + main.offsetX;
        float mainY = resolvePanelY(main, worldHeight);
        mainPanel.setBounds(mainX, mainY, main.width, main.height);
        mainPanel.validate();

        AuthMenuConfig.PanelConfig secondary = config.secondaryPanel;
        float secondaryX = secondary.alignToLeft ? secondary.marginLeft + secondary.offsetX : secondary.x + secondary.offsetX;
        float secondaryY = mainY - secondary.gapFromMain - secondary.height + secondary.offsetY;
        secondaryPanel.setBounds(secondaryX, secondaryY, secondary.width, secondary.height);
        secondaryPanel.validate();
    }

    @Override
    public void dispose() {
        for (Disposable disposable : disposables) {
            disposable.dispose();
        }
        disposables.clear();
        fields.clear();
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
    }

    private void navigate(AuthViewState next, boolean animate) {
        if (next == null) {
            return;
        }
        state = next;
        clearError();
        if (getStage() != null) {
            getStage().setKeyboardFocus(null);
        }
        rebuildContent();
        updateSecondary();
        refreshButtonStates();
        if (animate && config.transition.duration > 0f) {
            contentTable.getColor().a = config.transition.fadeMin;
            contentTable.setX(config.transition.slideAmount);
            contentTable.clearActions();
            contentTable.addAction(Actions.parallel(
                Actions.fadeIn(config.transition.duration),
                Actions.moveTo(0f, contentTable.getY(), config.transition.duration)
            ));
        }
        invalidateHierarchy();
    }

    private void rebuildContent() {
        for (AuthField field : fields.values()) {
            field.dispose();
        }
        fields.clear();
        buttons.clear();
        checkBoxes.clear();
        disposables.clear();
        contentTable.clearChildren();
        questionRevealLabel = null;
        termsLabel = null;
        termsScroll = null;
        acceptTermsBox = null;

        AuthMenuConfig.PageConfig page = config.page(state);
        titleLabel.setText(page.title);
        if (config.stepIndicator.visible && state.isSignUp()) {
            String stepText = page.stepLabel;
            if (stepText == null || stepText.isBlank()) {
                stepText = config.stepIndicator.format
                    .replace("{current}", String.valueOf(state.signUpStepNumber()))
                    .replace("{total}", "3");
            }
            stepLabel.setText(stepText);
            stepLabel.setVisible(true);
        } else {
            stepLabel.setText("");
            stepLabel.setVisible(false);
        }

        if (state == AuthViewState.SIGN_UP_STEP_3) {
            buildTerms(page);
        } else {
            for (AuthMenuConfig.FieldDefinition definition : page.orderedFields()) {
                AuthField field = new AuthField(config, definition, skin, this::onPrimaryEnter);
                field.onChange(() -> {
                    dismissPanelError();
                    refreshButtonStates();
                });
                if (state == AuthViewState.FORGOT_PASSWORD && "email".equals(definition.id)) {
                    field.onChange(this::revealSecurityQuestion);
                }
                fields.put(definition.id, field);
                disposables.add(field);
                contentTable.add(field).left().growX().padBottom(config.field.fieldSpacing);
                contentTable.row();
                if ("email".equals(definition.id) && state == AuthViewState.FORGOT_PASSWORD) {
                    questionRevealLabel = label(
                        config.field.labelFont,
                        config.field.labelFontFallback,
                        config.field.labelFontSize * 0.92f,
                        config.field.labelColor,
                        "left"
                    );
                    questionRevealLabel.setWrap(true);
                    questionRevealLabel.setText(revealedQuestion);
                    questionRevealLabel.setVisible(revealedQuestion != null && !revealedQuestion.isBlank());
                    contentTable.add(questionRevealLabel).left().growX().padBottom(config.field.fieldSpacing);
                    contentTable.row();
                }
                placeCheckboxesAfter(page, definition.id);
            }
            for (AuthMenuConfig.CheckboxDefinition definition : page.checkboxes) {
                if (definition.afterField != null && !definition.afterField.isBlank()) {
                    continue;
                }
                addCheckbox(definition);
            }
            if (state == AuthViewState.FORGOT_PASSWORD) {
                syncForgotAnswerVisibility();
            }
        }

        Table buttonRow = new Table();
        buttonRow.center();
        int buttonIndex = 0;
        int buttonCount = page.buttons.size();
        for (AuthMenuConfig.ButtonDefinition definition : page.buttons) {
            ActionButton button = createButton(definition.id, definition.label, definition.primary);
            String style = definition.style == null || definition.style.isBlank()
                ? config.button.style
                : definition.style;
            button.setStyle(UiTheme.skin().get(
                UiTheme.buttonStyleOrDefault(style),
                com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle.class
            ));
            float width = definition.width > 0f ? definition.width : config.button.width;
            float height = definition.height > 0f ? definition.height : config.button.height;
            button.onClick(() -> onButton(definition.id));
            buttons.put(definition.id, button);
            float rightPad = buttonIndex < buttonCount - 1 ? config.button.spacing : 0f;
            buttonRow.add(button).width(width).height(height).padRight(rightPad);
            buttonIndex++;
        }
        contentTable.add(buttonRow).center().expandX().padTop(config.button.topPadding);
        contentTable.row();
        applyDraftToFields();
        bindVisibilityCheckboxes();
        refreshButtonStates();
        invalidateHierarchy();
    }

    private void buildTerms(AuthMenuConfig.PageConfig page) {
        termsLabel = label(
            config.terms.font,
            config.terms.fontFallback,
            config.terms.fontSize,
            config.terms.color,
            config.terms.alignment
        );
        termsLabel.setWrap(true);
        termsLabel.setText(RegistrationTermsCatalog.loadText(config.terms.path));
        termsScroll = ScrollPanes.verticalHidden(termsLabel);
        termsScroll.setScrollingDisabled(true, false);
        termsScroll.addListener(new InputListener() {
            @Override
            public boolean scrolled(InputEvent event, float x, float y, float amountX, float amountY) {
                termsScroll.setScrollY(termsScroll.getScrollY() - amountY * config.terms.scrollSensitivity);
                event.stop();
                return true;
            }
        });
        contentTable.add(termsScroll).width(config.mainPanel.width - config.mainPanel.padLeft - config.mainPanel.padRight)
            .height(config.terms.height)
            .left()
            .padBottom(config.checkbox.topPadding);
        contentTable.row();

        String acceptLabel = page.termsCheckboxLabel;
        if (acceptLabel == null || acceptLabel.isBlank()) {
            acceptLabel = config.terms.checkboxLabel;
        }
        AuthMenuConfig.CheckboxDefinition accept = new AuthMenuConfig.CheckboxDefinition();
        accept.id = "acceptTerms";
        accept.label = acceptLabel;
        addCheckbox(accept);
        acceptTermsBox = checkBoxes.get("acceptTerms");
        if (acceptTermsBox != null) {
            acceptTermsBox.setChecked(draft.acceptedTerms());
            acceptTermsBox.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    draft.setAcceptedTerms(acceptTermsBox.isChecked());
                    refreshButtonStates();
                }
            });
        }
    }

    private void placeCheckboxesAfter(AuthMenuConfig.PageConfig page, String fieldId) {
        for (AuthMenuConfig.CheckboxDefinition definition : page.checkboxes) {
            if (fieldId.equals(definition.afterField)) {
                addCheckbox(definition);
            }
        }
    }

    private void addCheckbox(AuthMenuConfig.CheckboxDefinition definition) {
        AuthField.CheckBoxBundle bundle = AuthField.checkBox(config.checkbox, definition);
        checkBoxes.put(bundle.id, bundle.box);
        contentTable.add(bundle.box)
            .left()
            .padTop(config.checkbox.topPadding)
            .padBottom(config.checkbox.spacing)
            .height(Math.max(config.checkbox.size, 28f));
        contentTable.row();
    }

    private void bindVisibilityCheckboxes() {
        CheckBox showPassword = checkBoxes.get("showPassword");
        if (showPassword != null) {
            showPassword.setChecked(false);
            applyPasswordMask(!showPassword.isChecked());
            showPassword.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    applyPasswordMask(!showPassword.isChecked());
                }
            });
        }
        CheckBox showAnswer = checkBoxes.get("showAnswer");
        if (showAnswer != null) {
            showAnswer.setChecked(false);
            applyAnswerMask(!showAnswer.isChecked());
            showAnswer.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    applyAnswerMask(!showAnswer.isChecked());
                }
            });
        }
    }

    private void applyPasswordMask(boolean masked) {
        setFieldMasked("password", masked);
        setFieldMasked("passwordConfirm", masked);
    }

    private void applyAnswerMask(boolean masked) {
        setFieldMasked("answer", masked);
        setFieldMasked("answerConfirm", masked);
    }

    private void setFieldMasked(String id, boolean masked) {
        AuthField field = fields.get(id);
        if (field != null) {
            field.setMasked(masked);
        }
    }

    private void applyDraftToFields() {
        if (state == AuthViewState.SIGN_UP_STEP_1) {
            setField("username", draft.username());
            setField("nickname", draft.nickname());
            setField("email", draft.email());
            if (!draft.gender().isBlank()) {
                setField("gender", draft.gender());
            }
            setField("age", draft.age());
        } else if (state == AuthViewState.SIGN_UP_STEP_2) {
            setField("password", "");
            setField("passwordConfirm", "");
            if (!draft.questionNumber().isBlank()) {
                setField("question", draft.questionNumber());
            }
            setField("answer", "");
            setField("answerConfirm", "");
        }
    }

    private void setField(String id, String value) {
        AuthField field = fields.get(id);
        if (field != null) {
            field.setValue(value);
        }
    }

    private void refreshButtonStates() {
        ActionButton next = buttons.get("next");
        if (next != null) {
            next.setEnabledState(requiredFieldsFilled());
        }
        ActionButton login = buttons.get("login");
        if (login != null) {
            login.setEnabledState(requiredFieldsFilled());
        }
        ActionButton create = buttons.get("createAccount");
        if (create != null) {
            boolean accepted = acceptTermsBox != null && acceptTermsBox.isChecked();
            create.setEnabledState(accepted);
        }
        ActionButton enterNew = buttons.get("enterNewPassword");
        if (enterNew != null) {
            enterNew.setEnabledState(requiredFieldsFilled());
        }
        ActionButton change = buttons.get("changePassword");
        if (change != null) {
            change.setEnabledState(requiredFieldsFilled());
        }
    }

    private boolean requiredFieldsFilled() {
        for (AuthField field : fields.values()) {
            if (!field.isVisible()) {
                continue;
            }
            if (field.required() && !field.hasValue()) {
                return false;
            }
        }
        return true;
    }

    private void onPrimaryEnter() {
        if (buttons.containsKey("next") && buttons.get("next").isEnabledState()) {
            onButton("next");
        } else if (buttons.containsKey("login") && buttons.get("login").isEnabledState()) {
            onButton("login");
        } else if (buttons.containsKey("createAccount") && buttons.get("createAccount").isEnabledState()) {
            onButton("createAccount");
        } else if (buttons.containsKey("enterNewPassword") && buttons.get("enterNewPassword").isEnabledState()) {
            onButton("enterNewPassword");
        } else if (buttons.containsKey("changePassword") && buttons.get("changePassword").isEnabledState()) {
            onButton("changePassword");
        }
    }

    private void onButton(String id) {
        switch (id) {
            case "next" -> handleNext();
            case "back" -> handleBack();
            case "createAccount" -> handleCreateAccount();
            case "login" -> handleLogin();
            case "forgotPassword" -> navigate(AuthViewState.FORGOT_PASSWORD, true);
            case "enterNewPassword" -> handleForgotVerify();
            case "changePassword" -> handlePasswordReset();
            default -> {
            }
        }
    }

    private void handleNext() {
        if (state == AuthViewState.SIGN_UP_STEP_1) {
            captureStep1();
            signupController.validateIdentity(
                draft.username(),
                draft.nickname(),
                draft.email(),
                draft.gender(),
                draft.age(),
                result -> Gdx.app.postRunnable(() -> {
                    if (!result.success()) {
                        failAndClearStep1(result.message());
                        return;
                    }
                    navigate(AuthViewState.SIGN_UP_STEP_2, true);
                })
            );
            return;
        }
        if (state == AuthViewState.SIGN_UP_STEP_2) {
            captureStep2();
            Result password = signupController.validatePasswordPair(draft.password(), draft.passwordConfirm());
            if (!password.success()) {
                failAndClearStep2(password.message());
                return;
            }
            Result security = signupController.validateSecurity(
                draft.questionNumber(),
                draft.answer(),
                draft.answerConfirm()
            );
            if (!security.success()) {
                failAndClearStep2(security.message());
                return;
            }
            navigate(AuthViewState.SIGN_UP_STEP_3, true);
        }
    }

    private void handleBack() {
        if (state == AuthViewState.SIGN_UP_STEP_2) {
            draft.clearStep2();
            navigate(AuthViewState.SIGN_UP_STEP_1, true);
            return;
        }
        if (state == AuthViewState.SIGN_UP_STEP_3) {
            navigate(AuthViewState.SIGN_UP_STEP_2, true);
            return;
        }
        if (state == AuthViewState.FORGOT_PASSWORD) {
            recoveryEmail = "";
            revealedQuestion = "";
            navigate(AuthViewState.LOGIN, true);
            return;
        }
        if (state == AuthViewState.PASSWORD_RESET) {
            recoveryEmail = "";
            revealedQuestion = "";
            navigate(AuthViewState.LOGIN, true);
        }
    }

    private void handleCreateAccount() {
        if (acceptTermsBox == null || !acceptTermsBox.isChecked()) { refreshButtonStates(); return; }
        draft.setAcceptedTerms(true);

        signupController.finalizeRegister(
            draft.username(), draft.password(), draft.passwordConfirm(),
            draft.nickname(), draft.email(), draft.gender(),
            draft.questionNumber(), draft.answer(), draft.answerConfirm(), draft.age(),
            result -> Gdx.app.postRunnable(() -> {
                if (!result.success()) {
                    showError(result.message());
                    AudioManager.play(AudioId.UI_ERROR);
                    return;
                }
                draft.clearAll();
                App.setCurrentMenu(Menu.LoginMenu);
                navigate(AuthViewState.LOGIN, true);
            })
        );
    }

    private void handleLogin() {
        String username = valueOf("username");
        String password = valueOf("password");
        boolean stayLoggedIn = checkBoxes.get("stayLoggedIn") != null &&
            checkBoxes.get("stayLoggedIn").isChecked();

        loginController.manageLogin(username, password, stayLoggedIn,
            result -> Gdx.app.postRunnable(() -> {
                if (!result.success()) {
                    AudioManager.play(AudioId.UI_ERROR);
                    clearFields();
                    refreshButtonStates();
                    showError(result.message());
                    return;
                }
                App.setCurrentMenu(Menu.MainMenu);
                ScreenManager.showMainMenu(game);
            })
        );
    }

    private void handleForgotVerify() {
        String email = valueOf("email");
        AuthField answerField = fields.get("answer");

        if (revealedQuestion == null || revealedQuestion.isBlank()) {
            loginController.manageForgotPasswordByEmail(email, result -> Gdx.app.postRunnable(() -> {
                if (!result.success()) {
                    AudioManager.play(AudioId.UI_ERROR);
                    clearFields();
                    revealedQuestion = "";
                    syncForgotAnswerVisibility();
                    refreshButtonStates();
                    showError(result.message());
                    return;
                }
                revealedQuestion = result.message();
                syncForgotAnswerVisibility();
                refreshButtonStates();
            }));
            return;
        }

        if (answerField == null || !answerField.isVisible() || !answerField.hasValue()) {
            refreshButtonStates();
            return;
        }

        recoveryEmail = email;
        recoveryAnswer = valueOf("answer");
        navigate(AuthViewState.PASSWORD_RESET, true);
    }

    private void revealSecurityQuestion() {
        if (state != AuthViewState.FORGOT_PASSWORD) {
            return;
        }
        String email = valueOf("email");
        if (email == null || email.trim().isEmpty()) {
            return;
        }

        loginController.manageForgotPasswordByEmail(email, result -> Gdx.app.postRunnable(() -> {
            if (!result.success()) {
                revealedQuestion = "";
                syncForgotAnswerVisibility();
                return;
            }
            revealedQuestion = result.message();
            syncForgotAnswerVisibility();
        }));
    }

    private void syncForgotAnswerVisibility() {
        if (state != AuthViewState.FORGOT_PASSWORD) {
            return;
        }
        boolean show = revealedQuestion != null && !revealedQuestion.isBlank();
        if (questionRevealLabel != null) {
            questionRevealLabel.setText(show ? questionText(revealedQuestion) : "");
            setShown(questionRevealLabel, show, config.field.fieldSpacing);
        }
        AuthField answer = fields.get("answer");
        if (answer != null) {
            setShown(answer, show, config.field.fieldSpacing);
            if (!show) {
                answer.clearValue();
            }
        }
        CheckBox showAnswer = checkBoxes.get("showAnswer");
        if (showAnswer != null) {
            setShown(showAnswer, show, config.checkbox.spacing);
            if (!show) {
                showAnswer.setChecked(false);
            }
        }
        if (contentTable != null) {
            contentTable.invalidateHierarchy();
        }
    }

    private void setShown(Actor actor, boolean show, float bottomPad) {
        actor.setVisible(show);
        var cell = contentTable.getCell(actor);
        if (cell == null) {
            return;
        }
        if (show) {
            cell.height(com.badlogic.gdx.scenes.scene2d.ui.Value.minHeight);
            cell.padBottom(bottomPad);
        } else {
            cell.height(0f);
            cell.padBottom(0f);
            cell.padTop(0f);
        }
    }

    private static String questionText(String message) {
        if (message == null) {
            return "";
        }
        String prefix = ValidationMessages.active().securityQuestionPrefix;
        if (prefix != null && !prefix.isBlank() && message.startsWith(prefix)) {
            return message.substring(prefix.length()).trim();
        }
        return message.trim();
    }

    private void handlePasswordReset() {
        String password = valueOf("password");
        String confirm = valueOf("passwordConfirm");

        loginController.resetPasswordByEmail(
            recoveryEmail,
            recoveryAnswer,
            password,
            confirm,
            result -> Gdx.app.postRunnable(() -> {
                if (!result.success()) {
                    AudioManager.play(AudioId.UI_ERROR);
                    clearFields();
                    refreshButtonStates();
                    showError(result.message());
                    return;
                }

                recoveryEmail = "";
                recoveryAnswer = "";
                clearFields();
                navigate(AuthViewState.LOGIN, true);
            })
        );
    }

    private void captureStep1() {
        draft.setUsername(valueOf("username"));
        draft.setNickname(valueOf("nickname"));
        draft.setEmail(valueOf("email"));
        draft.setGender(valueOf("gender"));
        draft.setAge(valueOf("age"));
    }

    private void captureStep2() {
        draft.setPassword(valueOf("password"));
        draft.setPasswordConfirm(valueOf("passwordConfirm"));
        draft.setQuestionNumber(valueOf("question"));
        draft.setAnswer(valueOf("answer"));
        draft.setAnswerConfirm(valueOf("answerConfirm"));
    }

    private void failAndClearStep1(String message) {
        AudioManager.play(AudioId.UI_ERROR);
        clearFields();
        draft.setUsername("");
        draft.setNickname("");
        draft.setEmail("");
        draft.setGender("");
        draft.setAge("");
        refreshButtonStates();
        showError(message);
    }

    private void failAndClearStep2(String message) {
        AudioManager.play(AudioId.UI_ERROR);
        draft.clearStep2();
        clearFields();
        refreshButtonStates();
        showError(message);
    }

    private void clearFields() {
        for (AuthField field : fields.values()) {
            field.clearValue();
        }
    }

    private String valueOf(String id) {
        AuthField field = fields.get(id);
        return field == null ? "" : field.value().trim();
    }

    private void showError(String message) {
        String text = message == null ? "" : message;
        ValidationMessages messages = ValidationMessages.active();
        String fieldId = messages.fieldIdFor(text);
        for (AuthField field : fields.values()) {
            field.clearError();
        }
        errorLabel.setText("");
        errorLabel.setVisible(false);
        if (text.isBlank()) {
            return;
        }
        AuthField nearField = fieldId == null || fieldId.isBlank() ? null : fields.get(fieldId);
        if (nearField == null || !nearField.isVisible()) {
            nearField = firstVisibleField();
        }
        if (nearField != null) {
            nearField.showError(text);
        }
        if (config.error.showOnPanel) {
            errorLabel.setText(text);
            errorLabel.setVisible(true);
            errorLabel.setAlignment(AuthMenuConfig.textAlign(config.error.alignment));
        }
    }

    private AuthField firstVisibleField() {
        for (AuthField field : fields.values()) {
            if (field.isVisible()) {
                return field;
            }
        }
        return null;
    }

    private void clearError() {
        errorLabel.setText("");
        errorLabel.setVisible(false);
        for (AuthField field : fields.values()) {
            field.clearError();
        }
    }

    private void dismissPanelError() {
        errorLabel.setText("");
        errorLabel.setVisible(false);
    }

    private void updateSecondary() {
        if (state.isSignUp()) {
            secondaryLink.setTexts(config.secondary.signUpPrompt, config.secondary.signUpAction);
            return;
        }
        if (state == AuthViewState.LOGIN) {
            secondaryLink.setTexts(config.secondary.loginPrompt, config.secondary.loginAction);
            return;
        }
        if (state == AuthViewState.FORGOT_PASSWORD) {
            secondaryLink.setTexts(config.secondary.forgotPrompt, config.secondary.forgotAction);
            return;
        }
        secondaryLink.setTexts(config.secondary.resetPrompt, config.secondary.resetAction);
    }

    private void onSecondaryAction() {
        if (state.isSignUp()) {
            draft.clearAll();
            App.setCurrentMenu(Menu.LoginMenu);
            navigate(AuthViewState.LOGIN, true);
            return;
        }
        if (state == AuthViewState.LOGIN) {
            draft.clearAll();
            App.setCurrentMenu(Menu.SignupMenu);
            navigate(AuthViewState.SIGN_UP_STEP_1, true);
            return;
        }
        recoveryEmail = "";
        revealedQuestion = "";
        navigate(AuthViewState.LOGIN, true);
    }

    private ActionButton createButton(String id, String label, boolean primary) {
        ButtonTheme theme = new ButtonTheme()
            .size(config.button.width, config.button.height)
            .padding(config.button.padX, config.button.padY);
        theme.labelScale = config.button.labelScale;
        theme.hoverScale = config.button.hoverScale;
        theme.hoverDuration = config.button.hoverDuration;
        theme.disabledColor = AuthMenuConfig.color(config.button.disabledColor, new Color(0.54f, 0.54f, 0.54f, 0.93f));
        String style = primary ? config.button.style : "purple";
        return new ActionButton(label, UiTheme.buttonStyleOrDefault(style), theme);
    }

    private Label label(String font, String fallback, float size, String color, String alignment) {
        Label created = new Label("", new Label.LabelStyle(
            AuthUiFonts.resolve(font, fallback),
            AuthMenuConfig.color(color, Color.WHITE)
        ));
        created.setFontScale(size);
        created.setAlignment(AuthMenuConfig.textAlign(alignment));
        created.setWrap(false);
        return created;
    }

    private float resolvePanelY(AuthMenuConfig.PanelConfig panel, float worldHeight) {
        String align = panel.verticalAlign == null ? "center" : panel.verticalAlign.toLowerCase();
        return switch (align) {
            case "bottom" -> panel.y + panel.offsetY;
            case "top" -> worldHeight - panel.height - panel.y + panel.offsetY;
            default -> (worldHeight - panel.height) * 0.5f + panel.offsetY;
        };
    }
}

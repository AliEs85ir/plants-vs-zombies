package app.view.auth;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AuthMenuConfig {
    public BackgroundConfig background = new BackgroundConfig();
    public PanelConfig mainPanel = new PanelConfig();
    public PanelConfig secondaryPanel = createSecondaryPanel();
    public TitleConfig title = new TitleConfig();
    public StepIndicatorConfig stepIndicator = new StepIndicatorConfig();
    public FieldStyleConfig field = new FieldStyleConfig();
    public EditingConfig editing = new EditingConfig();
    public ErrorConfig error = new ErrorConfig();
    public ButtonStyleConfig button = new ButtonStyleConfig();
    public CheckboxStyleConfig checkbox = new CheckboxStyleConfig();
    public TermsStyleConfig terms = new TermsStyleConfig();
    public TransitionConfig transition = new TransitionConfig();
    public PagesConfig pages = new PagesConfig();
    public SecondaryTextsConfig secondary = new SecondaryTextsConfig();

    private static PanelConfig createSecondaryPanel() {
        PanelConfig panel = new PanelConfig();
        panel.y = 40f;
        panel.height = 120f;
        panel.anchorToMain = true;
        panel.gapFromMain = 16f;
        return panel;
    }

    public AuthMenuConfig sanitize() {
        if (background == null) {
            background = new BackgroundConfig();
        }
        if (mainPanel == null) {
            mainPanel = new PanelConfig();
        }
        if (secondaryPanel == null) {
            secondaryPanel = new PanelConfig();
        }
        if (title == null) {
            title = new TitleConfig();
        }
        if (stepIndicator == null) {
            stepIndicator = new StepIndicatorConfig();
        }
        if (field == null) {
            field = new FieldStyleConfig();
        }
        if (editing == null) {
            editing = new EditingConfig();
        }
        if (error == null) {
            error = new ErrorConfig();
        }
        if (button == null) {
            button = new ButtonStyleConfig();
        }
        if (checkbox == null) {
            checkbox = new CheckboxStyleConfig();
        }
        if (terms == null) {
            terms = new TermsStyleConfig();
        }
        if (transition == null) {
            transition = new TransitionConfig();
        }
        if (pages == null) {
            pages = new PagesConfig();
        }
        if (secondary == null) {
            secondary = new SecondaryTextsConfig();
        }
        background.sanitize();
        applyMainPanelDefaults(mainPanel);
        applySecondaryPanelDefaults(secondaryPanel);
        title.sanitize();
        stepIndicator.sanitize();
        field.sanitize();
        editing.sanitize();
        error.sanitize();
        button.sanitize();
        checkbox.sanitize();
        terms.sanitize();
        transition.sanitize();
        pages.sanitize();
        secondary.sanitize();
        return this;
    }

    public PageConfig page(AuthViewState state) {
        if (state == null) {
            return pages.login;
        }
        return switch (state) {
            case SIGN_UP_STEP_1 -> pages.signUpStep1;
            case SIGN_UP_STEP_2 -> pages.signUpStep2;
            case SIGN_UP_STEP_3 -> pages.signUpStep3;
            case LOGIN -> pages.login;
            case FORGOT_PASSWORD -> pages.forgotPassword;
            case PASSWORD_RESET -> pages.passwordReset;
        };
    }

    public static Color color(String hex, Color fallback) {
        if (hex == null || hex.isBlank()) {
            return fallback.cpy();
        }
        String value = hex.trim();
        if (value.startsWith("#")) {
            value = value.substring(1);
        }
        try {
            return Color.valueOf(value);
        } catch (Exception exception) {
            return fallback.cpy();
        }
    }

    public static int align(String value, int fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return switch (value.trim().toLowerCase(Locale.ROOT)) {
            case "left", "left_top", "topleft" -> Align.left | Align.top;
            case "right", "right_top", "topright" -> Align.right | Align.top;
            case "center", "top" -> Align.top | Align.center;
            case "bottom" -> Align.bottom | Align.center;
            case "bottom_left", "bottomleft" -> Align.bottom | Align.left;
            case "bottom_right", "bottomright" -> Align.bottom | Align.right;
            default -> fallback;
        };
    }

    public static int textAlign(String value) {
        if (value == null || value.isBlank()) {
            return Align.left;
        }
        return switch (value.trim().toLowerCase(Locale.ROOT)) {
            case "center" -> Align.center;
            case "right" -> Align.right;
            default -> Align.left;
        };
    }

    static String text(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    static float positive(float value, float fallback) {
        return value <= 0f ? fallback : value;
    }

    static float nonNegative(float value, float fallback) {
        return value < 0f ? fallback : value;
    }

    private static void applyMainPanelDefaults(PanelConfig panel) {
        panel.sanitize(
            48f,
            36f,
            560f,
            500f,
            "image_ui_generic_guaranteed_bg_10",
            true,
            16f
        );
        panel.anchorToMain = false;
    }

    private static void applySecondaryPanelDefaults(PanelConfig panel) {
        panel.sanitize(
            48f,
            40f,
            560f,
            120f,
            "image_ui_generic_guaranteed_bg_10",
            true,
            16f
        );
        if (!panel.anchorToMain && panel.y == 40f) {
            panel.anchorToMain = true;
        }
    }

    public static class BackgroundConfig {
        public String path = "menu/bg/signup.png";
        public float sourceWidth = 1920f;
        public float sourceHeight = 1080f;
        public float offsetX;
        public float offsetY;

        public void sanitize() {
            path = text(path, "menu/bg/signup.png");
            sourceWidth = positive(sourceWidth, 1920f);
            sourceHeight = positive(sourceHeight, 1080f);
        }
    }

    public static class PanelConfig {
        public float x = 48f;
        public float y = 36f;
        public float width = 560f;
        public float height = 500f;
        public float offsetX;
        public float offsetY;
        public float marginLeft = 48f;
        public String verticalAlign = "top";
        public float padLeft = 28f;
        public float padRight = 28f;
        public float padTop = 22f;
        public float padBottom = 22f;
        public String background = "image_ui_generic_guaranteed_bg_10";
        public float alpha = 0.2f;
        public float cornerRadius = 8f;
        public boolean alignToLeft = true;
        public boolean anchorToMain;
        public float gapFromMain = 16f;

        public void sanitize() {
            sanitize(x, y, width, height, background, alignToLeft, gapFromMain);
        }

        public void sanitize(
            float defaultX,
            float defaultY,
            float defaultWidth,
            float defaultHeight,
            String defaultBackground,
            boolean defaultAlignToLeft,
            float defaultGap
        ) {
            if (x == 0f) {
                x = defaultX;
            }
            if (y == 0f) {
                y = defaultY;
            }
            width = positive(width, defaultWidth);
            height = positive(height, defaultHeight);
            marginLeft = nonNegative(marginLeft, 48f);
            verticalAlign = text(verticalAlign, "center");
            padLeft = nonNegative(padLeft, 28f);
            padRight = nonNegative(padRight, 28f);
            padTop = nonNegative(padTop, 22f);
            padBottom = nonNegative(padBottom, 22f);
            background = text(background, defaultBackground);
            if (alpha < 0f || alpha > 1f) {
                alpha = 0.2f;
            }
            cornerRadius = positive(cornerRadius, 8f);
            gapFromMain = nonNegative(gapFromMain, defaultGap);
        }
    }

    public static class TitleConfig {
        public String font = "FBUSV8C5EI_1_outline";
        public String fontFallback = "FBUSV8C5EI_1";
        public float fontSize = 1.40f;
        public String color = "F4E7B0";
        public String alignment = "left";
        public float offsetX;
        public float offsetY;
        public float bottomGap = 10f;

        public void sanitize() {
            font = text(font, "FBUSV8C5EI_1_outline");
            fontFallback = text(fontFallback, "FBUSV8C5EI_1");
            fontSize = positive(fontSize, 1.40f);
            color = text(color, "F4E7B0");
            alignment = text(alignment, "left");
            bottomGap = nonNegative(bottomGap, 10f);
        }
    }

    public static class StepIndicatorConfig {
        public boolean visible = true;
        public String format = "{current} / {total}";
        public String font = "BRIANNETOD";
        public String fontFallback = "BRIANNETOD";
        public float fontSize = 1.20f;
        public String color = "E4DCC4";
        public String alignment = "left";
        public float padLeft = 24f;
        public float bottomGap = 12f;

        public void sanitize() {
            format = text(format, "{current} / {total}");
            font = text(font, "BRIANNETOD");
            fontFallback = text(fontFallback, "BRIANNETOD");
            fontSize = positive(fontSize, 1.20f);
            color = text(color, "E4DCC4");
            alignment = text(alignment, "left");
            padLeft = nonNegative(padLeft, 24f);
            bottomGap = nonNegative(bottomGap, 12f);
        }
    }

    public static class FieldStyleConfig {
        public String layout = "row";
        public float width = 260f;
        public float height = 36f;
        public float iconSize = 31f;
        public float iconOffsetX;
        public float iconOffsetY;
        public float iconTitleGap = 10f;
        public float titleWidth = 150f;
        public float titleValueGap = 12f;
        public float fieldPadding = 4f;
        public float fieldSpacing = 8f;
        public String labelFont = "BRIANNETOD";
        public String labelFontFallback = "BRIANNETOD";
        public float labelFontSize = 1.35f;
        public String labelColor = "F4E7B0";
        public String placeholderFont = "BRIANNETOD";
        public String placeholderFontFallback = "BRIANNETOD";
        public float placeholderFontSize = 0.90f;
        public String placeholderColor = "8A8A8A";
        public float placeholderAlpha = 0.72f;
        public String inputFont = "BRIANNETOD";
        public String inputFontFallback = "BRIANNETOD";
        public float inputFontSize = 0.90f;
        public String inputColor = "1A140E";
        public String inputBackgroundDrawable = "image_ui_if_bundle_reward_multiplier_bg_10";
        public String inputBackgroundColor = "F7EED8FF";
        public String focusedBackgroundColor = "FFF6D8FF";
        public float inputCornerRadius = 6f;
        public float inputPadLeft = 14f;
        public float inputPadRight = 14f;
        public float inputPadTop = 6f;
        public float inputPadBottom = 6f;
        public String errorFont = "BRIANNETOD";
        public String errorFontFallback = "BRIANNETOD";
        public float errorFontSize = 0.72f;
        public String errorColor = "FF6B5C";
        public float errorHeight = 16f;
        public float errorSpacing = 2f;
        public String maskCharacter = "*";
        public boolean validationEnabled = true;

        public void sanitize() {
            layout = text(layout, "row");
            width = positive(width, 260f);
            height = positive(height, 36f);
            iconSize = positive(iconSize, 31f);
            iconTitleGap = nonNegative(iconTitleGap, 10f);
            titleWidth = positive(titleWidth, 150f);
            titleValueGap = nonNegative(titleValueGap, 12f);
            fieldPadding = nonNegative(fieldPadding, 4f);
            fieldSpacing = nonNegative(fieldSpacing, 8f);
            labelFont = text(labelFont, "BRIANNETOD");
            labelFontFallback = text(labelFontFallback, "BRIANNETOD");
            labelFontSize = positive(labelFontSize, 1.35f);
            labelColor = text(labelColor, "F4E7B0");
            placeholderFont = text(placeholderFont, "BRIANNETOD");
            placeholderFontFallback = text(placeholderFontFallback, "BRIANNETOD");
            placeholderFontSize = positive(placeholderFontSize, 0.90f);
            placeholderColor = text(placeholderColor, "8A8A8A");
            if (placeholderAlpha < 0f || placeholderAlpha > 1f) {
                placeholderAlpha = 0.72f;
            }
            inputFont = text(inputFont, "BRIANNETOD");
            inputFontFallback = text(inputFontFallback, "BRIANNETOD");
            inputFontSize = positive(inputFontSize, 0.90f);
            inputColor = text(inputColor, "1A140E");
            inputBackgroundDrawable = text(inputBackgroundDrawable, "image_ui_if_bundle_reward_multiplier_bg_10");
            inputBackgroundColor = text(inputBackgroundColor, "F7EED8FF");
            focusedBackgroundColor = text(focusedBackgroundColor, "FFF6D8FF");
            inputCornerRadius = positive(inputCornerRadius, 6f);
            inputPadLeft = nonNegative(inputPadLeft, 14f);
            inputPadRight = nonNegative(inputPadRight, 14f);
            inputPadTop = nonNegative(inputPadTop, 6f);
            inputPadBottom = nonNegative(inputPadBottom, 6f);
            errorFont = text(errorFont, "BRIANNETOD");
            errorFontFallback = text(errorFontFallback, "BRIANNETOD");
            errorFontSize = positive(errorFontSize, 0.72f);
            errorColor = text(errorColor, "FF6B5C");
            errorHeight = positive(errorHeight, 16f);
            errorSpacing = nonNegative(errorSpacing, 2f);
            maskCharacter = text(maskCharacter, "*");
            if (maskCharacter.length() > 1) {
                maskCharacter = maskCharacter.substring(0, 1);
            }
        }

        public char maskChar() {
            return maskCharacter.charAt(0);
        }
    }

    public static class EditingConfig {
        public float cursorBlinkSeconds = 0.32f;
        public float cursorWidth = 2f;
        public float cursorHeight = 22f;
        public String cursorColor = "1A140E";
        public boolean focusTraversal = true;
        public boolean enterAdvances = true;

        public void sanitize() {
            cursorBlinkSeconds = positive(cursorBlinkSeconds, 0.32f);
            cursorWidth = positive(cursorWidth, 2f);
            cursorHeight = positive(cursorHeight, 22f);
            cursorColor = text(cursorColor, "1A140E");
        }
    }

    public static class ErrorConfig {
        public String font = "BRIANNETOD";
        public String fontFallback = "BRIANNETOD";
        public float fontSize = 0.84f;
        public String color = "FF6B5C";
        public String alignment = "left";
        public float topPadding = 8f;
        public float height = 36f;
        public float offsetX;
        public float offsetY;
        public boolean nearField = true;
        public boolean showOnPanel;

        public void sanitize() {
            font = text(font, "BRIANNETOD");
            fontFallback = text(fontFallback, "BRIANNETOD");
            fontSize = positive(fontSize, 0.84f);
            color = text(color, "FF6B5C");
            alignment = text(alignment, "left");
            topPadding = nonNegative(topPadding, 8f);
            height = positive(height, 36f);
        }
    }

    public static class ButtonStyleConfig {
        public float width = 210f;
        public float height = 38f;
        public float spacing = 12f;
        public float padX = 8f;
        public float padY = 2f;
        public float labelScale = 0.72f;
        public float hoverScale = 1.05f;
        public float hoverDuration = 0.10f;
        public String style = "brown";
        public String disabledColor = "8A8A8AEE";
        public float topPadding = 10f;

        public void sanitize() {
            width = positive(width, 210f);
            height = positive(height, 38f);
            spacing = nonNegative(spacing, 12f);
            labelScale = positive(labelScale, 0.72f);
            hoverScale = positive(hoverScale, 1.05f);
            hoverDuration = nonNegative(hoverDuration, 0.10f);
            style = text(style, "brown");
            disabledColor = text(disabledColor, "8A8A8AEE");
            topPadding = nonNegative(topPadding, 10f);
        }
    }

    public static class CheckboxStyleConfig {
        public float size = 28f;
        public String style = "default";
        public String labelFont = "BRIANNETOD";
        public float labelFontSize = 0.78f;
        public String labelColor = "E4DCC4";
        public String hoverColor = "FFF4C0";
        public float topPadding = 6f;
        public float spacing = 8f;

        public void sanitize() {
            size = positive(size, 28f);
            style = text(style, "default");
            labelFont = text(labelFont, "BRIANNETOD");
            labelFontSize = positive(labelFontSize, 0.78f);
            labelColor = text(labelColor, "E4DCC4");
            hoverColor = text(hoverColor, "FFF4C0");
            topPadding = nonNegative(topPadding, 6f);
            spacing = nonNegative(spacing, 8f);
        }
    }

    public static class TermsStyleConfig {
        public String font = "BRIANNETOD";
        public String fontFallback = "BRIANNETOD";
        public float fontSize = 1.25f;
        public String color = "F2E7C4";
        public String alignment = "left";
        public float height = 210f;
        public float scrollSensitivity = 28f;
        public String path = "ui/registration_terms.json";
        public String checkboxLabel = "I accept the lawn laws and gardener terms.";

        public void sanitize() {
            font = text(font, "BRIANNETOD");
            fontFallback = text(fontFallback, "BRIANNETOD");
            fontSize = positive(fontSize, 1.25f);
            color = text(color, "F2E7C4");
            alignment = text(alignment, "left");
            height = positive(height, 210f);
            scrollSensitivity = positive(scrollSensitivity, 28f);
            path = text(path, "ui/registration_terms.json");
            checkboxLabel = text(checkboxLabel, "I accept the lawn laws and gardener terms.");
        }
    }

    public static class TransitionConfig {
        public float duration = 0.18f;
        public float slideAmount = 18f;
        public float fadeMin = 0f;

        public void sanitize() {
            duration = nonNegative(duration, 0.18f);
            slideAmount = nonNegative(slideAmount, 18f);
            if (fadeMin < 0f || fadeMin > 1f) {
                fadeMin = 0f;
            }
        }
    }

    public static class PagesConfig {
        public PageConfig signUpStep1 = new PageConfig();
        public PageConfig signUpStep2 = new PageConfig();
        public PageConfig signUpStep3 = new PageConfig();
        public PageConfig login = new PageConfig();
        public PageConfig forgotPassword = new PageConfig();
        public PageConfig passwordReset = new PageConfig();

        public void sanitize() {
            if (signUpStep1 == null) {
                signUpStep1 = new PageConfig();
            }
            if (signUpStep2 == null) {
                signUpStep2 = new PageConfig();
            }
            if (signUpStep3 == null) {
                signUpStep3 = new PageConfig();
            }
            if (login == null) {
                login = new PageConfig();
            }
            if (forgotPassword == null) {
                forgotPassword = new PageConfig();
            }
            if (passwordReset == null) {
                passwordReset = new PageConfig();
            }
            signUpStep1.sanitize("Sign Up", "1 / 3", defaultSignUpStep1());
            signUpStep2.sanitize("Sign Up", "2 / 3", defaultSignUpStep2());
            signUpStep3.sanitize("Sign Up", "3 / 3", defaultSignUpStep3());
            signUpStep3.termsVisible = true;
            login.sanitize("Login", "", defaultLogin());
            forgotPassword.sanitize("Forgot Password", "", defaultForgot());
            passwordReset.sanitize("Change Password", "", defaultReset());
        }
    }

    public static class PageConfig {
        public String title = "";
        public String stepLabel = "";
        public ArrayList<FieldDefinition> fields = new ArrayList<>();
        public ArrayList<ButtonDefinition> buttons = new ArrayList<>();
        public ArrayList<CheckboxDefinition> checkboxes = new ArrayList<>();
        public boolean termsVisible;
        public String termsCheckboxLabel = "";

        public void sanitize(String defaultTitle, String defaultStep, PageDefaults defaults) {
            title = text(title, defaultTitle);
            stepLabel = stepLabel == null ? defaultStep : stepLabel;
            if (fields == null) {
                fields = new ArrayList<>();
            }
            if (buttons == null) {
                buttons = new ArrayList<>();
            }
            if (checkboxes == null) {
                checkboxes = new ArrayList<>();
            }
            fields.removeIf(item -> item == null || item.id == null || item.id.isBlank());
            buttons.removeIf(item -> item == null || item.id == null || item.id.isBlank());
            checkboxes.removeIf(item -> item == null || item.id == null || item.id.isBlank());
            if (fields.isEmpty()) {
                fields.addAll(defaults.fields);
            }
            if (buttons.isEmpty()) {
                buttons.addAll(defaults.buttons);
            }
            if (checkboxes.isEmpty()) {
                checkboxes.addAll(defaults.checkboxes);
            }
            for (FieldDefinition definition : fields) {
                definition.sanitize();
            }
            for (ButtonDefinition definition : buttons) {
                definition.sanitize();
            }
            for (CheckboxDefinition definition : checkboxes) {
                definition.sanitize();
            }
            termsCheckboxLabel = termsCheckboxLabel == null ? "" : termsCheckboxLabel;
        }

        public List<FieldDefinition> orderedFields() {
            return List.copyOf(fields);
        }
    }

    public static class FieldDefinition {
        public String id = "";
        public String type = "text";
        public String label = "";
        public String iconPath = "";
        public String placeholder = "";
        public boolean required = true;
        public boolean masked;
        public boolean validationEnabled = true;
        public float width;
        public float height;
        public ArrayList<SelectOption> options = new ArrayList<>();
        public String optionsSource = "";
        public String emptyValue = "";

        public void sanitize() {
            id = id == null ? "" : id.trim();
            type = text(type, "text");
            label = text(label, id);
            iconPath = iconPath == null ? "" : iconPath.trim();
            placeholder = placeholder == null ? "" : placeholder;
            optionsSource = optionsSource == null ? "" : optionsSource.trim();
            emptyValue = emptyValue == null ? "" : emptyValue;
            if (options == null) {
                options = new ArrayList<>();
            }
            options.removeIf(option -> option == null || option.value == null || option.value.isBlank());
            for (SelectOption option : options) {
                option.sanitize();
            }
        }
    }

    public static class SelectOption {
        public String value = "";
        public String label = "";

        public void sanitize() {
            value = value == null ? "" : value.trim();
            label = text(label, value);
        }
    }

    public static class ButtonDefinition {
        public String id = "";
        public String label = "";
        public String style = "";
        public float width;
        public float height;
        public boolean primary;

        public void sanitize() {
            id = id == null ? "" : id.trim();
            label = text(label, id);
            style = style == null ? "" : style.trim();
        }
    }

    public static class CheckboxDefinition {
        public String id = "";
        public String label = "";
        public String afterField = "";

        public void sanitize() {
            id = id == null ? "" : id.trim();
            label = text(label, id);
            afterField = afterField == null ? "" : afterField.trim();
        }
    }

    public static class SecondaryTextsConfig {
        public String signUpPrompt = "If you have already registered, ";
        public String signUpAction = "click to enter the garden.";
        public String loginPrompt = "If you haven't registered yet as a new gardener, ";
        public String loginAction = "click to go to the registration menu.";
        public String forgotPrompt = "Click to ";
        public String forgotAction = "return to the login menu.";
        public String forgotActionHighlight = "return to the login menu.";
        public String resetPrompt = "Click to ";
        public String resetAction = "return to the login menu.";
        public String resetActionHighlight = "return to the login menu.";
        public String promptFont = "BRIANNETOD";
        public float promptFontSize = 1.25f;
        public String promptColor = "E4DCC4";
        public String actionColor = "F4C95F";
        public String actionHoverColor = "FFE08A";
        public float gap = 0f;
        public boolean wrap = true;

        public void sanitize() {
            signUpPrompt = text(signUpPrompt, "If you have already registered, ");
            signUpAction = text(signUpAction, "click to enter the garden.");
            loginPrompt = text(loginPrompt, "If you haven't registered yet as a new gardener, ");
            loginAction = text(loginAction, "click to go to the registration menu.");
            forgotPrompt = text(forgotPrompt, "Click to ");
            forgotAction = text(forgotAction, "return to the login menu.");
            forgotActionHighlight = text(forgotActionHighlight, "return to the login menu.");
            resetPrompt = resetPrompt == null ? "" : resetPrompt;
            resetAction = text(resetAction, "return to the login menu.");
            resetActionHighlight = text(resetActionHighlight, "return to the login menu.");
            promptFont = text(promptFont, "BRIANNETOD");
            promptFontSize = positive(promptFontSize, 1.25f);
            promptColor = text(promptColor, "E4DCC4");
            actionColor = text(actionColor, "F4C95F");
            actionHoverColor = text(actionHoverColor, "FFE08A");
            gap = nonNegative(gap, 0f);
        }
    }

    static final class PageDefaults {
        final List<FieldDefinition> fields;
        final List<ButtonDefinition> buttons;
        final List<CheckboxDefinition> checkboxes;

        PageDefaults(
            List<FieldDefinition> fields,
            List<ButtonDefinition> buttons,
            List<CheckboxDefinition> checkboxes
        ) {
            this.fields = fields;
            this.buttons = buttons;
            this.checkboxes = checkboxes;
        }
    }

    private static PageDefaults defaultSignUpStep1() {
        List<FieldDefinition> fields = new ArrayList<>();
        fields.add(field("username", "text", "Username", "menu/icon/username_field", "AliEs85", false));
        fields.add(field("nickname", "text", "Nickname", "menu/icon/nickname_field", "AliEs85.ir", false));
        fields.add(field("email", "text", "Email", "menu/icon/email_field", "alies855.ir@gmail.com", false));
        FieldDefinition gender = field("gender", "selection", "Gender", "menu/icon/gender_field", "", false);
        gender.options.add(option("male", "Male"));
        gender.options.add(option("female", "Female"));
        fields.add(gender);
        fields.add(field("age", "integer", "Age", "menu/icon/age_field", "20", false));
        List<ButtonDefinition> buttons = new ArrayList<>();
        buttons.add(button("next", "Next", "brown", true));
        return new PageDefaults(fields, buttons, new ArrayList<>());
    }

    private static PageDefaults defaultSignUpStep2() {
        List<FieldDefinition> fields = new ArrayList<>();
        fields.add(field("password", "password", "Password", "menu/icon/password_field", "ffa45#84AmiMo4", true));
        fields.add(field("passwordConfirm", "password", "Confirm Password", "menu/icon/password2_field", "", true));
        FieldDefinition question = field(
            "question",
            "question",
            "Security Question",
            "menu/icon/question_field",
            "",
            false
        );
        question.optionsSource = "securityQuestions";
        fields.add(question);
        fields.add(field("answer", "password", "Security Answer", "menu/icon/answer_field", "", true));
        fields.add(field("answerConfirm", "password", "Confirm Answer", "menu/icon/answer2_field", "", true));
        List<ButtonDefinition> buttons = new ArrayList<>();
        buttons.add(button("back", "Back", "purple", false));
        buttons.add(button("next", "Next", "brown", true));
        List<CheckboxDefinition> checkboxes = new ArrayList<>();
        checkboxes.add(checkbox("showPassword", "Show Password", "passwordConfirm"));
        checkboxes.add(checkbox("showAnswer", "Show Answer", "answerConfirm"));
        return new PageDefaults(fields, buttons, checkboxes);
    }

    private static PageDefaults defaultSignUpStep3() {
        List<ButtonDefinition> buttons = new ArrayList<>();
        buttons.add(button("back", "Back", "purple", false));
        buttons.add(button("createAccount", "Create New Gardener", "green", true));
        List<CheckboxDefinition> checkboxes = new ArrayList<>();
        checkboxes.add(checkbox("acceptTerms", "I accept the lawn laws and gardener terms.", ""));
        return new PageDefaults(new ArrayList<>(), buttons, checkboxes);
    }

    private static PageDefaults defaultLogin() {
        List<FieldDefinition> fields = new ArrayList<>();
        fields.add(field("username", "text", "Username", "menu/icon/username_field", "", false));
        fields.add(field("password", "password", "Password", "menu/icon/password_field", "", true));
        List<ButtonDefinition> buttons = new ArrayList<>();
        buttons.add(button("forgotPassword", "Forgot Password", "purple", false));
        buttons.add(button("login", "Login", "brown", true));
        List<CheckboxDefinition> checkboxes = new ArrayList<>();
        checkboxes.add(checkbox("showPassword", "Show Password", "password"));
        checkboxes.add(checkbox("stayLoggedIn", "Keep me logged in", "password"));
        return new PageDefaults(fields, buttons, checkboxes);
    }

    private static PageDefaults defaultForgot() {
        List<FieldDefinition> fields = new ArrayList<>();
        fields.add(field("email", "text", "Email", "menu/icon/email_field", "", false));
        fields.add(field("answer", "password", "Security Answer", "menu/icon/answer_field", "", true));
        List<ButtonDefinition> buttons = new ArrayList<>();
        buttons.add(button("back", "Back", "purple", false));
        buttons.add(button("enterNewPassword", "Enter New Password", "brown", true));
        List<CheckboxDefinition> checkboxes = new ArrayList<>();
        checkboxes.add(checkbox("showAnswer", "Show Answer", "answer"));
        return new PageDefaults(fields, buttons, checkboxes);
    }

    private static PageDefaults defaultReset() {
        List<FieldDefinition> fields = new ArrayList<>();
        fields.add(field("password", "password", "New Password", "menu/icon/password_field", "", true));
        fields.add(field("passwordConfirm", "password", "Confirm New Password", "menu/icon/password2_field", "", true));
        List<ButtonDefinition> buttons = new ArrayList<>();
        buttons.add(button("back", "Back", "purple", false));
        buttons.add(button("changePassword", "Change Password", "brown", true));
        List<CheckboxDefinition> checkboxes = new ArrayList<>();
        checkboxes.add(checkbox("showPassword", "Show Password", "passwordConfirm"));
        return new PageDefaults(fields, buttons, checkboxes);
    }

    private static FieldDefinition field(
        String id,
        String type,
        String label,
        String iconPath,
        String placeholder,
        boolean masked
    ) {
        FieldDefinition definition = new FieldDefinition();
        definition.id = id;
        definition.type = type;
        definition.label = label;
        definition.iconPath = iconPath;
        definition.placeholder = placeholder;
        definition.masked = masked;
        definition.required = true;
        return definition;
    }

    private static SelectOption option(String value, String label) {
        SelectOption option = new SelectOption();
        option.value = value;
        option.label = label;
        return option;
    }

    private static ButtonDefinition button(String id, String label, String style, boolean primary) {
        ButtonDefinition definition = new ButtonDefinition();
        definition.id = id;
        definition.label = label;
        definition.style = style;
        definition.primary = primary;
        return definition;
    }

    private static CheckboxDefinition checkbox(String id, String label, String afterField) {
        CheckboxDefinition definition = new CheckboxDefinition();
        definition.id = id;
        definition.label = label;
        definition.afterField = afterField;
        return definition;
    }
}

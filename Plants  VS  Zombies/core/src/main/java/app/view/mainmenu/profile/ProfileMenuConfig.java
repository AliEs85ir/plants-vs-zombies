package app.view.mainmenu.profile;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProfileMenuConfig {
    public LayoutConfig layout = new LayoutConfig();
    public FieldRowConfig fieldRow = new FieldRowConfig();
    public EditingConfig editing = new EditingConfig();
    public ErrorConfig error = new ErrorConfig();
    public MessagesConfig messages = new MessagesConfig();
    public ArrayList<FieldDefinition> fields = new ArrayList<>();
    public FooterConfig footer = new FooterConfig();
    public ChangePasswordConfig changePassword = new ChangePasswordConfig();
    public LogoutPopupConfig logoutPopup = new LogoutPopupConfig();
    public PictureConfig picture = new PictureConfig();
    public SecurityPopupConfig securityPopup = new SecurityPopupConfig();

    public ProfileMenuConfig sanitize() {
        if (layout == null) {
            layout = new LayoutConfig();
        }
        if (fieldRow == null) {
            fieldRow = new FieldRowConfig();
        }
        if (editing == null) {
            editing = new EditingConfig();
        }
        if (error == null) {
            error = new ErrorConfig();
        }
        if (messages == null) {
            messages = new MessagesConfig();
        }
        if (fields == null) {
            fields = new ArrayList<>();
        }
        if (footer == null) {
            footer = new FooterConfig();
        }
        if (changePassword == null) {
            changePassword = new ChangePasswordConfig();
        }
        if (logoutPopup == null) {
            logoutPopup = new LogoutPopupConfig();
        }
        if (picture == null) {
            picture = new PictureConfig();
        }
        if (securityPopup == null) {
            securityPopup = new SecurityPopupConfig();
        }
        layout.sanitize();
        fieldRow.sanitize();
        editing.sanitize();
        error.sanitize();
        messages.sanitize();
        footer.sanitize();
        changePassword.sanitize();
        logoutPopup.sanitize();
        picture.sanitize();
        securityPopup.sanitize();
        fields.removeIf(field -> field == null || field.id == null || field.id.isBlank());
        if (fields.isEmpty()) {
            fields.addAll(defaultFields());
        }
        for (FieldDefinition field : fields) {
            field.sanitize();
        }
        return this;
    }

    public List<FieldDefinition> orderedFields() {
        return List.copyOf(fields);
    }

    public static class LayoutConfig {
        public float contentWidth = 720f;
        public String contentAlignment = "top";
        public float paddingLeft = 36f;
        public float paddingRight = 36f;
        public float paddingTop = 8f;
        public float paddingBottom = 16f;
        public float offsetX;
        public float offsetY;
        public float fieldSpacing = 10.5f;
        public float footerTopPadding = 8f;

        public void sanitize() {
            if (contentWidth <= 0f) {
                contentWidth = 720f;
            }
            contentAlignment = text(contentAlignment, "top");
            if (fieldSpacing < 0f) {
                fieldSpacing = 10.5f;
            }
            if (footerTopPadding < 0f) {
                footerTopPadding = 8f;
            }
        }
    }

    public static class FieldRowConfig {
        public float height = 44.1f;
        public float iconSize = 33.6f;
        public float iconTitleGap = 12.6f;
        public float titleWidth = 176.4f;
        public float titleValueGap = 16.8f;
        public float valueWidth = 294f;
        public float valueChangeGap = 16.8f;
        public float changeButtonWidth = 138.6f;
        public float changeButtonHeight = 33.6f;
        public String titleFont = "BRIANNETOD";
        public String titleFontFallback = "BRIANNETOD";
        public float titleFontSize = 1.16f;
        public String titleColor = "F4E7B0";
        public String titleAlignment = "left";
        public String valueFont = "BRIANNETOD";
        public String valueFontFallback = "BRIANNETOD";
        public float valueFontSize = 1.11f;
        public String valueColor = "FFFFFF";
        public String valueAlignment = "left";
        public String dimColor = "FFFFFF";
        public float dimOpacity = 0.38f;
        public String changeLabel = "Change";
        public String changeButtonStyle = "brown";
        public float changeButtonPadX = 6f;
        public float changeButtonPadY = 1f;
        public float changeButtonLabelScale = 0.70f;
        public float changeButtonHoverScale = 1.05f;
        public float changeButtonHoverDuration = 0.10f;
        public String changeButtonDisabledColor = "8A8A8AEE";

        public void sanitize() {
            if (height <= 0f) {
                height = 44.1f;
            }
            if (iconSize <= 0f) {
                iconSize = 33.6f;
            }
            if (titleWidth <= 0f) {
                titleWidth = 176.4f;
            }
            if (valueWidth <= 0f) {
                valueWidth = 294f;
            }
            if (changeButtonWidth <= 0f) {
                changeButtonWidth = 138.6f;
            }
            if (changeButtonHeight <= 0f) {
                changeButtonHeight = 33.6f;
            }
            titleFont = text(titleFont, "BRIANNETOD");
            titleFontFallback = text(titleFontFallback, "BRIANNETOD");
            if (titleFontSize <= 0f) {
                titleFontSize = 1.16f;
            }
            titleColor = text(titleColor, "F4E7B0");
            titleAlignment = text(titleAlignment, "left");
            valueFont = text(valueFont, "BRIANNETOD");
            valueFontFallback = text(valueFontFallback, "BRIANNETOD");
            if (valueFontSize <= 0f) {
                valueFontSize = 1.11f;
            }
            valueColor = text(valueColor, "FFFFFF");
            valueAlignment = text(valueAlignment, "left");
            dimColor = text(dimColor, "FFFFFF");
            if (dimOpacity < 0f || dimOpacity > 1f) {
                dimOpacity = 0.38f;
            }
            changeLabel = text(changeLabel, "Change");
            changeButtonStyle = text(changeButtonStyle, "brown");
            if (changeButtonLabelScale <= 0f) {
                changeButtonLabelScale = 0.70f;
            }
            if (changeButtonHoverScale <= 0f) {
                changeButtonHoverScale = 1.05f;
            }
            if (changeButtonHoverDuration < 0f) {
                changeButtonHoverDuration = 0.10f;
            }
            changeButtonDisabledColor = text(changeButtonDisabledColor, "8A8A8AEE");
        }
    }

    public static class EditingConfig {
        public float cursorBlinkSeconds = 0.32f;
        public float cursorWidth = 2f;
        public float cursorHeight = 23f;
        public String cursorColor = "1A140E";
        public String editorFont = "BRIANNETOD";
        public String editorFontFallback = "BRIANNETOD";
        public float editorFontSize = 1.11f;
        public String editorColor = "000000";
        public String editorBackgroundColor = "F7EED8FF";

        public void sanitize() {
            if (cursorBlinkSeconds <= 0f) {
                cursorBlinkSeconds = 0.32f;
            }
            if (cursorWidth <= 0f) {
                cursorWidth = 2f;
            }
            if (cursorHeight <= 0f) {
                cursorHeight = 23f;
            }
            cursorColor = text(cursorColor, "1A140E");
            editorFont = text(editorFont, "BRIANNETOD");
            editorFontFallback = text(editorFontFallback, "BRIANNETOD");
            if (editorFontSize <= 0f) {
                editorFontSize = 1.11f;
            }
            editorColor = text(editorColor, "000000");
            editorBackgroundColor = text(editorBackgroundColor, "F7EED8FF");
        }
    }

    public static class ErrorConfig {
        public String font = "BRIANNETOD";
        public String fontFallback = "BRIANNETOD";
        public float fontSize = 1.02f;
        public String color = "FF6B5C";
        public String alignment = "left";
        public float topPadding = 8f;
        public float height = 29.4f;
        public float offsetX;
        public float offsetY;

        public void sanitize() {
            font = text(font, "BRIANNETOD");
            fontFallback = text(fontFallback, "BRIANNETOD");
            if (fontSize <= 0f) {
                fontSize = 1.02f;
            }
            color = text(color, "FF6B5C");
            alignment = text(alignment, "left");
            if (height <= 0f) {
                height = 29.4f;
            }
        }
    }

    public static class MessagesConfig {
        public String passwordMismatch = "Password and its confirmation are not equal.\nPlease enter password again.";
        public String invalidNumber = "Value must be a non-negative integer.";
        public String logoutConfirm = "Are you sure you want to log out?";

        public void sanitize() {
            passwordMismatch = text(
                passwordMismatch,
                "Password and its confirmation are not equal.\nPlease enter password again."
            );
            invalidNumber = text(invalidNumber, "Value must be a non-negative integer.");
            logoutConfirm = text(logoutConfirm, "Are you sure you want to log out?");
        }
    }

    public static class FieldDefinition {
        public String id = "";
        public String title = "";
        public String iconPath = "";
        public boolean editable;
        public boolean debugEditable;

        public void sanitize() {
            id = id == null ? "" : id.trim();
            title = text(title, id);
            iconPath = iconPath == null ? "" : iconPath.trim();
        }
    }

    public static class FooterConfig {
        public float buttonWidth = 220f;
        public float buttonHeight = 36f;
        public float buttonGap = 18f;
        public String buttonStyle = "purple";
        public float buttonPadX = 8f;
        public float buttonPadY = 2f;
        public float buttonLabelScale = 0.74f;
        public float buttonHoverScale = 1.05f;
        public float buttonHoverDuration = 0.10f;
        public String alignment = "left";
        public float offsetX;
        public float offsetY;
        public String changePasswordLabel = "Change Password";
        public String logoutLabel = "Logout";
        public String confirmLabel = "Confirm";
        public String cancelLabel = "Cancel";

        public void sanitize() {
            if (buttonWidth <= 0f) {
                buttonWidth = 220f;
            }
            if (buttonHeight <= 0f) {
                buttonHeight = 36f;
            }
            if (buttonGap < 0f) {
                buttonGap = 18f;
            }
            buttonStyle = text(buttonStyle, "purple");
            if (buttonLabelScale <= 0f) {
                buttonLabelScale = 0.74f;
            }
            if (buttonHoverScale <= 0f) {
                buttonHoverScale = 1.05f;
            }
            if (buttonHoverDuration < 0f) {
                buttonHoverDuration = 0.10f;
            }
            alignment = text(alignment, "left");
            changePasswordLabel = text(changePasswordLabel, "Change Password");
            logoutLabel = text(logoutLabel, "Logout");
            confirmLabel = text(confirmLabel, "Confirm");
            cancelLabel = text(cancelLabel, "Cancel");
        }
    }

    public static class ChangePasswordConfig {
        public float fieldWidth = 420f;
        public float fieldHeight = 36f;
        public float fieldSpacing = 14f;
        public String titleFont = "FBUSV8C5EI_1_outline";
        public String titleFontFallback = "FBUSV8C5EI_1";
        public float titleFontSize = 0.80f;
        public String titleColor = "F4E7B0";
        public String titleAlignment = "left";
        public float titleBottomPadding = 6f;
        public String newPasswordTitle = "New Password";
        public String repeatPasswordTitle = "Repeat New Password";
        public String maskCharacter = "*";
        public String showPasswordLabel = "Show Password";
        public float checkboxSize = 32f;
        public String checkboxStyle = "default";
        public String checkboxLabelFont = "BRIANNETOD";
        public float checkboxLabelFontSize = 0.82f;
        public String checkboxLabelColor = "E4DCC4";
        public String checkboxHoverColor = "FFF4C0";
        public float checkboxTopPadding = 12f;
        public String inputFont = "BRIANNETOD";
        public String inputFontFallback = "BRIANNETOD";
        public float inputFontSize = 0.92f;
        public String inputColor = "000000";
        public String inputBackgroundColor = "F7EED8FF";

        public void sanitize() {
            if (fieldWidth <= 0f) {
                fieldWidth = 420f;
            }
            if (fieldHeight <= 0f) {
                fieldHeight = 36f;
            }
            if (fieldSpacing < 0f) {
                fieldSpacing = 14f;
            }
            titleFont = text(titleFont, "FBUSV8C5EI_1_outline");
            titleFontFallback = text(titleFontFallback, "FBUSV8C5EI_1");
            if (titleFontSize <= 0f) {
                titleFontSize = 0.80f;
            }
            titleColor = text(titleColor, "F4E7B0");
            titleAlignment = text(titleAlignment, "left");
            newPasswordTitle = text(newPasswordTitle, "New Password");
            repeatPasswordTitle = text(repeatPasswordTitle, "Repeat New Password");
            maskCharacter = text(maskCharacter, "*");
            if (maskCharacter.length() > 1) {
                maskCharacter = maskCharacter.substring(0, 1);
            }
            showPasswordLabel = text(showPasswordLabel, "Show Password");
            if (checkboxSize <= 0f) {
                checkboxSize = 32f;
            }
            checkboxStyle = text(checkboxStyle, "default");
            checkboxLabelFont = text(checkboxLabelFont, "BRIANNETOD");
            if (checkboxLabelFontSize <= 0f) {
                checkboxLabelFontSize = 0.82f;
            }
            checkboxLabelColor = text(checkboxLabelColor, "E4DCC4");
            checkboxHoverColor = text(checkboxHoverColor, "FFF4C0");
            inputFont = text(inputFont, "BRIANNETOD");
            inputFontFallback = text(inputFontFallback, "BRIANNETOD");
            if (inputFontSize <= 0f) {
                inputFontSize = 0.92f;
            }
            inputColor = text(inputColor, "000000");
            inputBackgroundColor = text(inputBackgroundColor, "F7EED8FF");
        }
    }

    public static class LogoutPopupConfig {
        public String overlayColor = "00000099";
        public float panelWidth = 460f;
        public float panelHeight = 210f;
        public String panelColor = "1A140EE6";
        public float panelPadX = 28f;
        public float panelPadY = 22f;
        public String messageFont = "BRIANNETOD";
        public String messageFontFallback = "BRIANNETOD";
        public float messageFontSize = 0.92f;
        public String messageColor = "F2E7C4";
        public String messageAlignment = "center";
        public float buttonWidth = 150f;
        public float buttonHeight = 36f;
        public float buttonGap = 16f;
        public float buttonTopPadding = 22f;
        public String confirmLabel = "Confirm";
        public String cancelLabel = "Cancel";
        public String buttonStyle = "purple";
        public float buttonLabelScale = 0.74f;
        public float buttonHoverScale = 1.05f;
        public float buttonHoverDuration = 0.10f;

        public void sanitize() {
            overlayColor = text(overlayColor, "00000099");
            if (panelWidth <= 0f) {
                panelWidth = 460f;
            }
            if (panelHeight <= 0f) {
                panelHeight = 210f;
            }
            panelColor = text(panelColor, "1A140EE6");
            messageFont = text(messageFont, "BRIANNETOD");
            messageFontFallback = text(messageFontFallback, "BRIANNETOD");
            if (messageFontSize <= 0f) {
                messageFontSize = 0.92f;
            }
            messageColor = text(messageColor, "F2E7C4");
            messageAlignment = text(messageAlignment, "center");
            if (buttonWidth <= 0f) {
                buttonWidth = 150f;
            }
            if (buttonHeight <= 0f) {
                buttonHeight = 36f;
            }
            confirmLabel = text(confirmLabel, "Confirm");
            cancelLabel = text(cancelLabel, "Cancel");
            buttonStyle = text(buttonStyle, "purple");
            if (buttonLabelScale <= 0f) {
                buttonLabelScale = 0.74f;
            }
            if (buttonHoverScale <= 0f) {
                buttonHoverScale = 1.05f;
            }
            if (buttonHoverDuration < 0f) {
                buttonHoverDuration = 0.10f;
            }
        }
    }

    public static class PictureConfig {
        public float panelWidth = 332f;
        public float panelGap = 24f;
        public float minPanelWidth = 188f;
        public String panelColor = "1A140ED4";
        public float panelPadX = 16f;
        public float panelPadY = 14f;
        public String title = "Profile Picture";
        public String subtitle = "Pick a picture";
        public String titleFont = "BRIANNETOD";
        public String titleFontFallback = "BRIANNETOD";
        public float titleFontSize = 1.08f;
        public String titleColor = "F4E7B0";
        public String subtitleFont = "BRIANNETOD";
        public String subtitleFontFallback = "BRIANNETOD";
        public float subtitleFontSize = 0.78f;
        public String subtitleColor = "E4DCC4";
        public float previewSize = 118f;
        public float previewBottomPadding = 12f;
        public int gridColumns = 4;
        public float cellSize = 62f;
        public float cellGap = 8f;
        public String slotColor = "00000066";
        public String selectionBackgroundDrawable = "image_ui_generic_guaranteed_bg_10";
        public float selectionBackgroundAlpha = 0.85f;
        public String selectedBorderColor = "F4C84AFF";
        public float selectedBorderWidth = 3f;
        public float hoverScale = 1.08f;
        public float hoverDuration = 0.10f;

        public void sanitize() {
            if (panelWidth <= 0f) {
                panelWidth = 332f;
            }
            if (panelGap < 0f) {
                panelGap = 24f;
            }
            if (minPanelWidth <= 0f) {
                minPanelWidth = 188f;
            }
            panelColor = text(panelColor, "1A140ED4");
            if (panelPadX < 0f) {
                panelPadX = 16f;
            }
            if (panelPadY < 0f) {
                panelPadY = 14f;
            }
            title = text(title, "Profile Picture");
            subtitle = text(subtitle, "Pick a picture");
            titleFont = text(titleFont, "BRIANNETOD");
            titleFontFallback = text(titleFontFallback, "BRIANNETOD");
            if (titleFontSize <= 0f) {
                titleFontSize = 1.08f;
            }
            titleColor = text(titleColor, "F4E7B0");
            subtitleFont = text(subtitleFont, "BRIANNETOD");
            subtitleFontFallback = text(subtitleFontFallback, "BRIANNETOD");
            if (subtitleFontSize <= 0f) {
                subtitleFontSize = 0.78f;
            }
            subtitleColor = text(subtitleColor, "E4DCC4");
            if (previewSize <= 0f) {
                previewSize = 118f;
            }
            if (gridColumns < 2) {
                gridColumns = 4;
            }
            if (cellSize <= 0f) {
                cellSize = 62f;
            }
            if (cellGap < 0f) {
                cellGap = 8f;
            }
            slotColor = text(slotColor, "00000066");
            selectionBackgroundDrawable = text(
                selectionBackgroundDrawable,
                "image_ui_generic_guaranteed_bg_10"
            );
            if (selectionBackgroundAlpha < 0f || selectionBackgroundAlpha > 1f) {
                selectionBackgroundAlpha = 0.85f;
            }
            selectedBorderColor = text(selectedBorderColor, "F4C84AFF");
            if (selectedBorderWidth < 0f) {
                selectedBorderWidth = 3f;
            }
            if (hoverScale <= 0f) {
                hoverScale = 1.08f;
            }
            if (hoverDuration < 0f) {
                hoverDuration = 0.10f;
            }
        }
    }


    public static class SecurityPopupConfig {
        public String overlayColor = "000000A8";
        public float dimAlpha = 0.66f;
        public float panelWidth = 520f;
        public float panelHeight = 280f;
        public float panelPadX = 28f;
        public float panelPadY = 22f;
        public String panelBackgroundDrawable = "image_ui_mainmenu_mm_settings_tab_10";
        public String title = "Security Question";
        public String titleFont = "BRIANNETOD";
        public String titleFontFallback = "BRIANNETOD";
        public float titleFontSize = 1.05f;
        public String titleColor = "F4E7B0";
        public String questionFont = "BRIANNETOD";
        public String questionFontFallback = "BRIANNETOD";
        public float questionFontSize = 0.92f;
        public String questionColor = "F2E7C4";
        public String questionAlignment = "center";
        public float answerFieldWidth = 420f;
        public float answerFieldHeight = 40f;
        public String answerBackgroundDrawable = "image_ui_if_bundle_reward_multiplier_bg_10";
        public float answerInputPadLeft = 14f;
        public float answerInputPadRight = 10f;
        public float answerInputPadTop = 4f;
        public float answerInputPadBottom = 4f;
        public String answerFont = "BRIANNETOD";
        public String answerFontFallback = "BRIANNETOD";
        public float answerFontSize = 0.92f;
        public String answerColor = "000000";
        public String errorFont = "BRIANNETOD";
        public String errorFontFallback = "BRIANNETOD";
        public float errorFontSize = 0.86f;
        public String errorColor = "FF6B5C";
        public float errorTopPadding = 10f;
        public float buttonWidth = 150f;
        public float buttonHeight = 36f;
        public float buttonGap = 16f;
        public float buttonTopPadding = 18f;
        public String cancelLabel = "Cancel";
        public String verifyLabel = "Verify";
        public String buttonStyle = "purple";
        public float buttonLabelScale = 0.74f;
        public float buttonHoverScale = 1.05f;
        public float buttonHoverDuration = 0.10f;
        public float offsetX;
        public float offsetY;

        public void sanitize() {
            overlayColor = text(overlayColor, "000000A8");
            if (dimAlpha < 0f || dimAlpha > 1f) {
                dimAlpha = 0.66f;
            }
            if (panelWidth <= 0f) {
                panelWidth = 520f;
            }
            if (panelHeight <= 0f) {
                panelHeight = 280f;
            }
            panelBackgroundDrawable = text(
                panelBackgroundDrawable,
                "image_ui_mainmenu_mm_settings_tab_10"
            );
            title = text(title, "Security Question");
            titleFont = text(titleFont, "BRIANNETOD");
            titleFontFallback = text(titleFontFallback, "BRIANNETOD");
            if (titleFontSize <= 0f) {
                titleFontSize = 1.05f;
            }
            titleColor = text(titleColor, "F4E7B0");
            questionFont = text(questionFont, "BRIANNETOD");
            questionFontFallback = text(questionFontFallback, "BRIANNETOD");
            if (questionFontSize <= 0f) {
                questionFontSize = 0.92f;
            }
            questionColor = text(questionColor, "F2E7C4");
            questionAlignment = text(questionAlignment, "center");
            if (answerFieldWidth <= 0f) {
                answerFieldWidth = 420f;
            }
            if (answerFieldHeight <= 0f) {
                answerFieldHeight = 40f;
            }
            answerBackgroundDrawable = text(
                answerBackgroundDrawable,
                "image_ui_if_bundle_reward_multiplier_bg_10"
            );
            if (answerInputPadLeft < 0f) {
                answerInputPadLeft = 14f;
            }
            if (answerInputPadRight < 0f) {
                answerInputPadRight = 10f;
            }
            answerFont = text(answerFont, "BRIANNETOD");
            answerFontFallback = text(answerFontFallback, "BRIANNETOD");
            if (answerFontSize <= 0f) {
                answerFontSize = 0.92f;
            }
            answerColor = text(answerColor, "000000");
            errorFont = text(errorFont, "BRIANNETOD");
            errorFontFallback = text(errorFontFallback, "BRIANNETOD");
            if (errorFontSize <= 0f) {
                errorFontSize = 0.86f;
            }
            errorColor = text(errorColor, "FF6B5C");
            if (buttonWidth <= 0f) {
                buttonWidth = 150f;
            }
            if (buttonHeight <= 0f) {
                buttonHeight = 36f;
            }
            cancelLabel = text(cancelLabel, "Cancel");
            verifyLabel = text(verifyLabel, "Verify");
            buttonStyle = text(buttonStyle, "purple");
            if (buttonLabelScale <= 0f) {
                buttonLabelScale = 0.74f;
            }
            if (buttonHoverScale <= 0f) {
                buttonHoverScale = 1.05f;
            }
            if (buttonHoverDuration < 0f) {
                buttonHoverDuration = 0.10f;
            }
        }
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

    private static String text(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private static List<FieldDefinition> defaultFields() {
        List<FieldDefinition> defaults = new ArrayList<>();
        defaults.add(field("username", "Username", "menu/icon/username_field", true, false));
        defaults.add(field("nickname", "Nickname", "menu/icon/nickname_field", true, false));
        defaults.add(field("email", "Email", "menu/icon/email_field", true, false));
        defaults.add(field("gamesPlayed", "Games Played", "menu/icon/games_played_field", false, false));
        defaults.add(field("coins", "Coins", "menu/icon/coins_field", false, true));
        defaults.add(field("gems", "Gems", "menu/icon/gems_field", false, true));
        defaults.add(field("chapterPassed", "Chapter Passed", "menu/icon/chapter_field", false, false));
        return defaults;
    }

    private static FieldDefinition field(
        String id,
        String title,
        String iconPath,
        boolean editable,
        boolean debugEditable
    ) {
        FieldDefinition definition = new FieldDefinition();
        definition.id = id;
        definition.title = title;
        definition.iconPath = iconPath;
        definition.editable = editable;
        definition.debugEditable = debugEditable;
        return definition;
    }
}

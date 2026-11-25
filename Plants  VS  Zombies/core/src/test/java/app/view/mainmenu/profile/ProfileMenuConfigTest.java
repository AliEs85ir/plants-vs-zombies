package app.view.mainmenu.profile;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProfileMenuConfigTest {
    @Test
    void sanitizeFillsDefaultFieldOrderAndMessages() {
        ProfileMenuConfig config = new ProfileMenuConfig().sanitize();
        assertEquals(7, config.orderedFields().size());
        assertEquals("username", config.orderedFields().get(0).id);
        assertEquals("nickname", config.orderedFields().get(1).id);
        assertEquals("email", config.orderedFields().get(2).id);
        assertEquals("gamesPlayed", config.orderedFields().get(3).id);
        assertEquals("coins", config.orderedFields().get(4).id);
        assertEquals("gems", config.orderedFields().get(5).id);
        assertEquals("chapterPassed", config.orderedFields().get(6).id);
        assertTrue(config.orderedFields().get(0).editable);
        assertFalse(config.orderedFields().get(3).editable);
        assertTrue(config.orderedFields().get(4).debugEditable);
        assertFalse(config.orderedFields().get(6).debugEditable);
        assertEquals("Change Password", config.footer.changePasswordLabel);
        assertEquals("Logout", config.footer.logoutLabel);
        assertEquals("*", config.changePassword.maskCharacter);
        assertEquals("brown", config.fieldRow.changeButtonStyle);
        assertEquals("FFFFFF", config.fieldRow.valueColor);
        assertEquals("F4E7B0", config.fieldRow.titleColor);
        assertEquals("000000", config.editing.editorColor);
        assertTrue(config.fieldRow.titleFontSize > config.fieldRow.valueFontSize);
        assertTrue(config.layout.footerTopPadding <= 10f);
        assertEquals("Profile Picture", config.picture.title);
        assertEquals("Pick a picture", config.picture.subtitle);
        assertEquals(4, config.picture.gridColumns);
        assertTrue(config.picture.panelWidth > 200f);
        assertTrue(config.picture.previewSize > config.picture.cellSize);
    }

    @Test
    void blankValuesAreReplacedWithNamedDefaults() {
        ProfileMenuConfig config = new ProfileMenuConfig();
        config.footer.changePasswordLabel = " ";
        config.changePassword.newPasswordTitle = "";
        config.messages.logoutConfirm = null;
        config.fields.clear();
        config.sanitize();
        assertEquals("Change Password", config.footer.changePasswordLabel);
        assertEquals("New Password", config.changePassword.newPasswordTitle);
        assertEquals("Are you sure you want to log out?", config.messages.logoutConfirm);
        assertEquals(7, config.orderedFields().size());
    }

    @Test
    void blankPictureLabelsAreReplacedWithDefaults() {
        ProfileMenuConfig config = new ProfileMenuConfig();
        config.picture.title = " ";
        config.picture.subtitle = "";
        config.picture.gridColumns = 1;
        config.sanitize();
        assertEquals("Profile Picture", config.picture.title);
        assertEquals("Pick a picture", config.picture.subtitle);
        assertEquals(4, config.picture.gridColumns);
    }

    @Test
    void avatarSelectionBackgroundDefaultsAreConfigurable() {
        ProfileMenuConfig config = new ProfileMenuConfig().sanitize();
        assertEquals("image_ui_generic_guaranteed_bg_10", config.picture.selectionBackgroundDrawable);
        assertEquals(0.85f, config.picture.selectionBackgroundAlpha, 0.0001f);
        config.picture.selectionBackgroundDrawable = " ";
        config.picture.selectionBackgroundAlpha = 2f;
        config.sanitize();
        assertEquals("image_ui_generic_guaranteed_bg_10", config.picture.selectionBackgroundDrawable);
        assertEquals(0.85f, config.picture.selectionBackgroundAlpha, 0.0001f);
    }

    @Test
    void securityPopupDefaultsMatchProfileLocale() {
        ProfileMenuConfig config = new ProfileMenuConfig().sanitize();
        assertEquals("Cancel", config.securityPopup.cancelLabel);
        assertEquals("Verify", config.securityPopup.verifyLabel);
        assertEquals("image_ui_mainmenu_mm_settings_tab_10", config.securityPopup.panelBackgroundDrawable);
        assertEquals("image_ui_if_bundle_reward_multiplier_bg_10", config.securityPopup.answerBackgroundDrawable);
        assertEquals(14f, config.securityPopup.answerInputPadLeft, 0.0001f);
        assertEquals(0.66f, config.securityPopup.dimAlpha, 0.0001f);
    }
}

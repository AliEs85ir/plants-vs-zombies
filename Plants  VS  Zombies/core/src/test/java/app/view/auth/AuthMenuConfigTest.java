package app.view.auth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthMenuConfigTest {
    @Test
    void sanitizeProvidesConfigurablePagesAndFields() {
        AuthMenuConfig config = new AuthMenuConfig().sanitize();
        assertEquals("menu/bg/signup.png", config.background.path);
        assertEquals("image_ui_generic_guaranteed_bg_10", config.mainPanel.background);
        assertEquals("Sign Up", config.pages.signUpStep1.title);
        assertEquals("Login", config.pages.login.title);
        assertEquals("Forgot Password", config.pages.forgotPassword.title);
        assertEquals("Change Password", config.pages.passwordReset.title);
        assertEquals(5, config.pages.signUpStep1.orderedFields().size());
        assertEquals("username", config.pages.signUpStep1.orderedFields().get(0).id);
        assertEquals("age", config.pages.signUpStep1.orderedFields().get(4).id);
        assertEquals("male", config.pages.signUpStep1.orderedFields().get(3).options.get(0).value);
        assertEquals("Create New Gardener", config.pages.signUpStep3.buttons.get(1).label);
        assertTrue(config.pages.signUpStep3.termsVisible);
        assertEquals("*", config.field.maskCharacter);
        assertTrue(config.transition.duration > 0f);
        assertFalse(config.secondary.signUpAction.isBlank());
        assertTrue(config.secondary.signUpAction.toLowerCase().contains("click"));
        assertEquals("stayLoggedIn", config.pages.login.checkboxes.get(1).id);
        assertEquals("Keep me logged in", config.pages.login.checkboxes.get(1).label);
        assertEquals(0.2f, config.mainPanel.alpha, 0.001f);
        assertEquals(8f, config.mainPanel.cornerRadius, 0.001f);
    }

    @Test
    void blankPageTitlesAreRestored() {
        AuthMenuConfig config = new AuthMenuConfig();
        config.pages.login.title = " ";
        config.pages.forgotPassword.title = "";
        config.sanitize();
        assertEquals("Login", config.pages.login.title);
        assertEquals("Forgot Password", config.pages.forgotPassword.title);
    }
}

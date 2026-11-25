package app.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordRulesTest {
    private ValidationMessages messages;

    @BeforeEach
    void setUp() {
        messages = ValidationMessages.defaults();
        ValidationMessages.setActive(messages);
    }

    @Test
    void listsEveryFailedReason() {
        List<String> failures = PasswordRules.failures("short");
        assertTrue(failures.contains(messages.passwordTooShort));
        assertTrue(failures.contains(messages.passwordNeedUpper));
        assertTrue(failures.contains(messages.passwordNeedDigit));
        assertTrue(failures.contains(messages.passwordNeedSpecial));
        assertTrue(PasswordRules.isStrong("Abcd1234!"));
        assertFalse(PasswordRules.isStrong("Abcd1234"));
    }

    @Test
    void missingUpperHasReason() {
        List<String> failures = PasswordRules.failures("abcd1234!");
        assertTrue(failures.contains(messages.passwordNeedUpper));
        assertFalse(failures.contains(messages.passwordTooShort));
    }

    @Test
    void disallowedCharacterReportsInvalidFormat() {
        List<String> failures = PasswordRules.failures("Abcd1234@");
        assertTrue(failures.contains(messages.invalidPassword));
        assertFalse(PasswordRules.hasOnlyAllowedChars("Abcd1234@"));
        assertTrue(PasswordRules.hasOnlyAllowedChars("Abcd1234!"));
    }

    @Test
    void spaceIsNotAllowedSpecial() {
        assertFalse(PasswordRules.hasOnlyAllowedChars("Abcd1234 "));
        assertTrue(PasswordRules.failures("Abcd1234 ").contains(messages.invalidPassword));
    }

    @Test
    void mismatchCodeIsStructured() {
        assertEquals(AuthErrorCodes.PASSWORD_MISMATCH, AuthErrorCodes.PASSWORD_MISMATCH);
        assertFalse("PASSWORD_MISMATCH".equals("Password and its confirmation are not equal."));
    }
}

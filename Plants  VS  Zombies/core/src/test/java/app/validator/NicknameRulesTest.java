package app.validator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NicknameRulesTest {
    @Test
    void lengthAndUsernameCharset() {
        assertTrue(NicknameRules.isValid("Ali", "other"));
        assertTrue(NicknameRules.isValid("علی-نام", "other"));
        assertFalse(NicknameRules.isValid("ab", "other"));
        assertFalse(NicknameRules.isValid("same name!", "same name!"));
        assertTrue(NicknameRules.isValid("Same-Name", "Same-Name"));
    }
}

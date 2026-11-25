package app.validator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmailValidatorTest {
    @Test
    void acceptsSimpleEmail() {
        assertTrue(EmailValidator.isValid("gardener@example.com"));
    }

    @Test
    void rejectsMissingAtOrDoubleAt() {
        assertFalse(EmailValidator.isValid("gardenerexample.com"));
        assertFalse(EmailValidator.isValid("a@b@c.com"));
    }

    @Test
    void rejectsConsecutiveDotsAndBadTld() {
        assertFalse(EmailValidator.isValid("a..b@example.com"));
        assertFalse(EmailValidator.isValid("user@example.c"));
        assertFalse(EmailValidator.isValid("user@example"));
    }

    @Test
    void rejectsSpecials() {
        assertFalse(EmailValidator.isValid("user!name@example.com"));
        assertFalse(EmailValidator.isValid("user@exam ple.com"));
    }
}

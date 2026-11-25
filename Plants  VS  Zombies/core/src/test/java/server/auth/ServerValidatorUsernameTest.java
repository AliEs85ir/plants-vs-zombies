package server.auth;

import app.validator.AppValidator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServerValidatorUsernameTest {
    @Test
    void acceptsLettersDigitsHyphen() {
        assertNull(ServerValidator.validateUsername("Gardener-1"));
        assertTrue(AppValidator.isUsernameCorrect("Gardener-1"));
    }

    @Test
    void rejectsUnderscoreSpaceAtDot() {
        assertNotNull(ServerValidator.validateUsername("bad_name"));
        assertNotNull(ServerValidator.validateUsername("bad name"));
        assertNotNull(ServerValidator.validateUsername("bad@name"));
        assertNotNull(ServerValidator.validateUsername("bad.name"));
        assertFalse(AppValidator.isUsernameCorrect("bad_name"));
        assertFalse(AppValidator.isUsernameCorrect("bad name"));
    }
}

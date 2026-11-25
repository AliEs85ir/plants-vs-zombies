package app.controller.menu;

import app.model.App;
import app.model.user.ProfilePictures;
import app.model.user.User;
import app.validator.AppValidator;
import app.validator.AuthErrorCodes;
import app.validator.ValidationMessages;
import common.Result;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProfileMenuControllerTest {
    private final ProfileMenuController controller = new ProfileMenuController();

//    @BeforeEach
//    void setUp() {
//        User current = new User("tester", "pass", "nick", "tester@mail.com", "male", 1, "answer");
//        User other = new User("taken", "pass", "other", "other@mail.com", "male", 1, "answer");
//        App.setAllUsers(new ArrayList<>(List.of(current, other)));
//        App.setCurrentUser(current);
//    }
//
//    @AfterEach
//    void tearDown() {
//        App.setCurrentUser(null);
//        App.setAllUsers(new ArrayList<>());
//    }

//    @Test
//    void duplicateUsernameIsRejectedWithSignupRule() {
//        Result result = controller.manageChangeUsername("taken");
//        assertFalse(result.success());
//        assertEquals("Suggested username already exist.", result.message());
//        assertEquals("tester", App.getCurrentUser().getUsername());
//    }

    @Test
    void nonNegativeIntegerValidationAcceptsZeroAndRejectsNegative() {
        assertTrue(AppValidator.isNonNegativeInteger("0"));
        assertTrue(AppValidator.isNonNegativeInteger("999999"));
        assertFalse(AppValidator.isNonNegativeInteger("-1"));
        assertFalse(AppValidator.isNonNegativeInteger("1.5"));
        assertFalse(AppValidator.isNonNegativeInteger("abc"));
    }

    @Test
    void profilePictureChangeRequiresLoginAndKnownId() {
        App.setCurrentUser(null);
        Result missingUser = controller.manageChangeProfilePicture(ProfilePictures.DEFAULT_ID);
        assertFalse(missingUser.success());

        User user = new User("tester", "pass", "nick", "tester@mail.com", "male", 1, "answer");
        App.setCurrentUser(user);
        try {
            Result unknown = controller.manageChangeProfilePicture("not-a-picture");
            assertFalse(unknown.success());
            assertEquals(null, user.getProfilePictureId());
        } finally {
            App.setCurrentUser(null);
        }
    }


    @Test
    void manageConfirmNewPassword_mismatchShowsExistingLanguage() {
        ValidationMessages messages = ValidationMessages.defaults();
        ValidationMessages.setActive(messages);
        Result[] box = new Result[1];
        controller.manageConfirmNewPassword("Abcd1234!", "Abcd1234?", r -> box[0] = r);
        assertFalse(box[0].success());
        assertEquals(AuthErrorCodes.PASSWORD_MISMATCH, box[0].errorCode());
        assertEquals(messages.passwordMismatch, box[0].message());
    }

    @Test
    void manageConfirmNewPassword_weakIncludesReasons() {
        ValidationMessages messages = ValidationMessages.defaults();
        ValidationMessages.setActive(messages);
        Result[] box = new Result[1];
        controller.manageConfirmNewPassword("short", "short", r -> box[0] = r);
        assertFalse(box[0].success());
        assertEquals(AuthErrorCodes.INVALID_PASSWORD, box[0].errorCode());
        assertTrue(box[0].message().contains(messages.passwordTooShort));
        assertTrue(box[0].message().contains(messages.passwordNeedUpper));
    }

}

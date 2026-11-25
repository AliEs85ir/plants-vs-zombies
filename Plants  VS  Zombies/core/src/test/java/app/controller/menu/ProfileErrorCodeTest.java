package app.controller.menu;

import app.model.App;
import app.model.user.User;
import app.validator.AuthErrorCodes;
import common.Result;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ProfileErrorCodeTest {
    @Test
    void passwordMismatchUsesCodeNotMessageEquals() {
        User user = new User("tester", "pass", "nick", "tester@mail.com", "male", 1, "answer");
        App.setCurrentUser(user);
        try {
            ProfileMenuController controller = new ProfileMenuController();
            AtomicReference<Result> box = new AtomicReference<>();
            controller.manageConfirmNewPassword("Abcd1234!", "other", box::set);
            assertFalse(box.get().success());
            assertEquals(AuthErrorCodes.PASSWORD_MISMATCH, box.get().errorCode());
        } finally {
            App.setCurrentUser(null);
        }
    }

    @Test
    void nicknameMatchingUsernameRejectsSpecials() {
        User user = new User("tester!", "pass", "nick", "tester@mail.com", "male", 1, "answer");
        App.setCurrentUser(user);
        try {
            ProfileMenuController controller = new ProfileMenuController();
            AtomicReference<Result> box = new AtomicReference<>();
            controller.manageChangeNickname("tester!", box::set);
            assertFalse(box.get().success());
            assertEquals(AuthErrorCodes.INVALID_NICKNAME, box.get().errorCode());
        } finally {
            App.setCurrentUser(null);
        }
    }

    @Test
    void usernameInvalidUsesCode() {
        User user = new User("tester", "pass", "nick", "tester@mail.com", "male", 1, "answer");
        App.setCurrentUser(user);
        try {
            ProfileMenuController controller = new ProfileMenuController();
            AtomicReference<Result> box = new AtomicReference<>();
            controller.manageChangeUsername("bad_name", box::set);
            assertFalse(box.get().success());
            assertEquals(AuthErrorCodes.USERNAME_INVALID, box.get().errorCode());
        } finally {
            App.setCurrentUser(null);
        }
    }

    @Test
    void emailInvalidUsesCode() {
        User user = new User("tester", "pass", "nick", "tester@mail.com", "male", 1, "answer");
        App.setCurrentUser(user);
        try {
            ProfileMenuController controller = new ProfileMenuController();
            AtomicReference<Result> box = new AtomicReference<>();
            controller.manageChangeEmail("not-an-email", box::set);
            assertFalse(box.get().success());
            assertEquals(AuthErrorCodes.EMAIL_INVALID, box.get().errorCode());
        } finally {
            App.setCurrentUser(null);
        }
    }
}

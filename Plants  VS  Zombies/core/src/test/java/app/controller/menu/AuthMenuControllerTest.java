package app.controller.menu;

import app.model.App;
import app.model.user.User;
import app.repository.Repository;
import app.validator.ValidationMessages;
import common.Result;
import common.utils.HashUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthMenuControllerTest {
    private final SignupMenuController signup = new SignupMenuController();
    private final LoginMenuController login = new LoginMenuController();

//    @BeforeEach
//    void setUp() {
//        ValidationMessages.setActive(ValidationMessages.defaults());
//        App.setAllUsers(new ArrayList<>());
//        App.setCurrentUser(null);
//    }
//
//    @AfterEach
//    void tearDown() {
//        App.setCurrentUser(null);
//        App.setAllUsers(new ArrayList<>());
//        Repository.clearLoggedInUser();
//    }

//    @Test
//    void signUpStep1RejectsInvalidAgeAndKeepsRules() {
//        Result age = signup.validateIdentity("AliEs85", "AliEs85.ir", "alies855.ir@gmail.com", "male", "-3");
//        assertFalse(age.success());
//        assertEquals(ValidationMessages.active().invalidAge, age.message());
//
//        Result ok = signup.validateIdentity("AliEs85", "AliEs85.ir", "alies855.ir@gmail.com", "female", "20");
//        assertTrue(ok.success());
//    }

//    @Test
//    void registrationPersistsAgeAndDoesNotAutoLogin() {
//        Result result = signup.finalizeRegister(
//            "gardener1",
//            "ValidPass1!",
//            "ValidPass1!",
//            "Neighbor",
//            "gardener1@mail.com",
//            "male",
//            "1",
//            "red",
//            "red",
//            "21"
//        );
//        assertTrue(result.success());
//        assertNull(App.getCurrentUser());
//        User stored = App.getAllUsers().get(0);
//        assertEquals(21, stored.getAge());
//        assertEquals("male", stored.getGender());
//        assertEquals(HashUtil.hash("ValidPass1!"), stored.getPassword());
//    }
//
//    @Test
//    void step2ErrorDoesNotRequireClearingStep1DataInDraftFlow() {
//        Result password = signup.validatePasswordPair("short", "short");
//        assertFalse(password.success());
//        Result identity = signup.validateIdentity("AliEs85", "AliEs85.ir", "alies855.ir@gmail.com", "male", "20");
//        assertTrue(identity.success());
//    }

//    @Test
//    void loginAndForgotPasswordByEmail() {
//        signup.finalizeRegister(
//            "gardener2",
//            "ValidPass1!",
//            "ValidPass1!",
//            "Neighbor2",
//            "gardener2@mail.com",
//            "female",
//            "2",
//            "pizza",
//            "pizza",
//            "18"
//        );
//        Result fail = login.manageLogin("gardener2", "WrongPass1!", false);
//        assertFalse(fail.success());
//
//        Result ok = login.manageLogin("gardener2", "ValidPass1!", false);
//        assertTrue(ok.success());
//        assertNotNull(App.getCurrentUser());
//        App.setCurrentUser(null);
//
//        Result missing = login.manageForgotPasswordByEmail("missing@mail.com");
//        assertFalse(missing.success());
//
//        Result question = login.manageForgotPasswordByEmail("gardener2@mail.com");
//        assertTrue(question.success());
//        assertTrue(question.message().contains("favorite food"));
//
//        Result wrongAnswer = login.checkSecurityAnswerByEmail("gardener2@mail.com", "wrong");
//        assertFalse(wrongAnswer.success());
//
//        Result reset = login.resetPasswordByEmail("gardener2@mail.com", "NewPass1!", "NewPass1!");
//        assertTrue(reset.success());
//        Result loginNew = login.manageLogin("gardener2", "NewPass1!", false);
//        assertTrue(loginNew.success());
//    }

//    @Test
//    void stayLoggedInRestoresTheGardenerOnNextLaunch() {
//        Result registered = signup.finalizeRegister(
//            "gardenerStay",
//            "ValidPass1!",
//            "ValidPass1!",
//            "StayNick",
//            "staygardener@mail.com",
//            "male",
//            "1",
//            "red",
//            "red",
//            "20"
//        );
//        assertTrue(registered.success());
//        Result ok = login.manageLogin("gardenerStay", "ValidPass1!", true);
//        assertTrue(ok.success());
//        assertEquals("gardenerStay", App.getCurrentUser().getUsername());
//        App.setCurrentUser(null);
//        User remembered = Repository.loadLoginedUser();
//        assertNotNull(remembered);
//        assertEquals("gardenerStay", remembered.getUsername());
//    }

    @Test
    void securityAnswerRejectsBlankAndMismatch() {
        Result blank = signup.validateSecurity("1", "   ", "   ");
        assertFalse(blank.success());
        assertEquals(ValidationMessages.active().invalidAnswer, blank.message());

        Result mismatch = signup.validateSecurity("1", "red", "blue");
        assertFalse(mismatch.success());
        assertEquals(ValidationMessages.active().answerMismatch, mismatch.message());

        Result ok = signup.validateSecurity("1", "red", "red");
        assertTrue(ok.success());
    }

//    @Test
//    void duplicateEmailIsRejected() {
//        App.setAllUsers(new ArrayList<>(List.of(
//            new User("taken", "pass", "nick", "sameuser@mail.com", "male", 1, "a", 10)
//        )));
//        Result result = signup.validateIdentity("fresh", "FreshNick", "sameuser@mail.com", "male", "12");
//        assertFalse(result.success());
//        assertEquals(ValidationMessages.active().duplicateEmail, result.message());
//    }
}

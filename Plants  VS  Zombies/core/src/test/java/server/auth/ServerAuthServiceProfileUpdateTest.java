package server.auth;

import app.validator.AuthErrorCodes;
import common.utils.HashUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import server.network.Packets;
import server.quest.QuestProgressStore;
import server.user.ServerUserRepository;
import server.user.UserAccount;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServerAuthServiceProfileUpdateTest {
    @TempDir
    Path tempDir;

    private ServerUserRepository repository;
    private ServerAuthService auth;
    private Path usersFile;

    @BeforeEach
    void setUp() {
        usersFile = tempDir.resolve("server_users.json");
        repository = new ServerUserRepository(usersFile);
        auth = new ServerAuthService(repository, new QuestProgressStore());
        seed("gardener", "gardener@mail.com", "blue");
        seed("takenuser", "taken@mail.com", "green");
    }

    @AfterEach
    void tearDown() {
        // tempDir cleaned by JUnit
    }

    @Test
    void missingSecurityAnswerDoesNotUpdateUsername() {
        Packets.ProfileUpdateRequest req = new Packets.ProfileUpdateRequest();
        req.newUsername = "new-gardener";
        Packets.ProfileResponse response = auth.updateProfile(req, "gardener");
        assertFalse(response.success);
        assertEquals(AuthErrorCodes.SECURITY_ANSWER_REQUIRED, response.errorCode);
        assertEquals("gardener", repository.findByUsername("gardener").orElseThrow().getUsername());
    }

    @Test
    void wrongSecurityAnswerKeepsOriginalValues() {
        Packets.ProfileUpdateRequest req = new Packets.ProfileUpdateRequest();
        req.newUsername = "new-gardener";
        req.newEmail = "fresh@mail.com";
        req.securityAnswer = "wrong";
        Packets.ProfileResponse response = auth.updateProfile(req, "gardener");
        assertFalse(response.success);
        assertEquals(AuthErrorCodes.SECURITY_ANSWER_INCORRECT, response.errorCode);
        UserAccount account = repository.findByUsername("gardener").orElseThrow();
        assertEquals("gardener", account.getUsername());
        assertEquals("gardener@mail.com", account.getEmail());
    }

    @Test
    void correctAnswerUpdatesUsernameAtomicallyAndPersists() {
        Packets.ProfileUpdateRequest req = new Packets.ProfileUpdateRequest();
        req.newUsername = "new-gardener";
        req.securityAnswer = "blue";
        Packets.ProfileResponse response = auth.updateProfile(req, "gardener");
        assertTrue(response.success);
        assertTrue(repository.findByUsername("gardener").isEmpty());
        UserAccount updated = repository.findByUsername("new-gardener").orElseThrow();
        assertEquals("new-gardener", updated.getUsername());
        assertEquals("gardener@mail.com", updated.getEmail());

        ServerUserRepository reloaded = new ServerUserRepository(usersFile);
        assertTrue(reloaded.findByUsername("new-gardener").isPresent());
        assertTrue(reloaded.findByUsername("gardener").isEmpty());
    }

    @Test
    void correctAnswerUpdatesEmailAtomicallyAndPersists() {
        Packets.ProfileUpdateRequest req = new Packets.ProfileUpdateRequest();
        req.newEmail = "fresh@mail.com";
        req.securityAnswer = "blue";
        Packets.ProfileResponse response = auth.updateProfile(req, "gardener");
        assertTrue(response.success);
        UserAccount updated = repository.findByUsername("gardener").orElseThrow();
        assertEquals("fresh@mail.com", updated.getEmail());

        ServerUserRepository reloaded = new ServerUserRepository(usersFile);
        assertEquals("fresh@mail.com", reloaded.findByUsername("gardener").orElseThrow().getEmail());
    }

    @Test
    void uniquenessRejectionDoesNotPartialUpdate() {
        Packets.ProfileUpdateRequest req = new Packets.ProfileUpdateRequest();
        req.newUsername = "takenuser";
        req.securityAnswer = "blue";
        Packets.ProfileResponse response = auth.updateProfile(req, "gardener");
        assertFalse(response.success);
        assertEquals(AuthErrorCodes.USERNAME_ALREADY_EXISTS, response.errorCode);
        assertEquals("gardener", repository.findByUsername("gardener").orElseThrow().getUsername());
    }

    @Test
    void emailUniquenessRejectionUsesCode() {
        Packets.ProfileUpdateRequest req = new Packets.ProfileUpdateRequest();
        req.newEmail = "taken@mail.com";
        req.securityAnswer = "blue";
        Packets.ProfileResponse response = auth.updateProfile(req, "gardener");
        assertFalse(response.success);
        assertEquals(AuthErrorCodes.EMAIL_ALREADY_EXISTS, response.errorCode);
        assertEquals("gardener@mail.com", repository.findByUsername("gardener").orElseThrow().getEmail());
    }

    @Test
    void invalidUsernameRejectedWithCode() {
        Packets.ProfileUpdateRequest req = new Packets.ProfileUpdateRequest();
        req.newUsername = "bad_name";
        req.securityAnswer = "blue";
        Packets.ProfileResponse response = auth.updateProfile(req, "gardener");
        assertFalse(response.success);
        assertEquals(AuthErrorCodes.USERNAME_INVALID, response.errorCode);
        assertNotEquals("bad_name", repository.findByUsername("gardener").orElseThrow().getUsername());
    }

    @Test
    void strongNewPasswordUpdatesHash() {
        UserAccount before = repository.findByUsername("gardener").orElseThrow();
        String oldHash = before.getPasswordHash();
        Packets.ProfileUpdateRequest req = new Packets.ProfileUpdateRequest();
        req.newPassword = "NewPass9!";
        Packets.ProfileResponse response = auth.updateProfile(req, "gardener");
        assertTrue(response.success);
        UserAccount updated = repository.findByUsername("gardener").orElseThrow();
        assertNotEquals(oldHash, updated.getPasswordHash());
        assertTrue(HashUtil.verify("NewPass9!", updated.getPasswordHash()));
        assertFalse(HashUtil.verify("Abcd1234!", updated.getPasswordHash()));
    }

    @Test
    void weakNewPasswordDoesNotUpdateHash() {
        String oldHash = repository.findByUsername("gardener").orElseThrow().getPasswordHash();
        Packets.ProfileUpdateRequest req = new Packets.ProfileUpdateRequest();
        req.newPassword = "weak";
        Packets.ProfileResponse response = auth.updateProfile(req, "gardener");
        assertFalse(response.success);
        assertTrue(response.message != null && !response.message.isBlank());
        assertEquals(oldHash, repository.findByUsername("gardener").orElseThrow().getPasswordHash());
    }

    @Test
    void profileDataIncludesSecurityQuestionNumber() {
        Packets.ProfileResponse response = auth.loadProfile(profileRequest("gardener"));
        assertTrue(response.success);
        assertEquals(1, response.profileData.securityQuestionNumber);
    }

    private void seed(String username, String email, String answer) {
        UserAccount account = new UserAccount();
        account.setUsername(username);
        account.setPasswordHash(HashUtil.hash("Abcd1234!"));
        account.setNickname(username + "-nick");
        account.setEmail(email);
        account.setGender("male");
        account.setSecurityQuestionNumber(1);
        account.setSecurityAnswerHash(HashUtil.hash(answer));
        account.setGems(150);
        account.setDifficulty(3);
        assertTrue(repository.add(account));
    }


    @Test
    void emailChangeAfterUsernameChangeInSameSession() {
        Packets.ProfileUpdateRequest rename = new Packets.ProfileUpdateRequest();
        rename.newUsername = "renamed-gardener";
        rename.securityAnswer = "blue";
        Packets.ProfileResponse renamed = auth.updateProfile(rename, "gardener");
        assertTrue(renamed.success);
        assertEquals("renamed-gardener", renamed.profileData.username);

        Packets.ProfileUpdateRequest emailReq = new Packets.ProfileUpdateRequest();
        emailReq.newEmail = "after-rename@mail.com";
        emailReq.securityAnswer = "blue";
        Packets.ProfileResponse emailRes = auth.updateProfile(emailReq, "renamed-gardener");
        assertTrue(emailRes.success, emailRes.message);
        assertEquals("after-rename@mail.com", emailRes.profileData.email);

        assertTrue(repository.findByUsername("gardener").isEmpty());
        UserAccount account = repository.findByUsername("renamed-gardener").orElseThrow();
        assertEquals("after-rename@mail.com", account.getEmail());
    }

    @Test
    void nicknameAndCharacterPersistAfterRelogin() {
        Packets.ProfileUpdateRequest nickReq = new Packets.ProfileUpdateRequest();
        nickReq.newNickname = "CoolNick";
        assertTrue(auth.updateProfile(nickReq, "gardener").success);

        Packets.ProfileUpdateRequest picReq = new Packets.ProfileUpdateRequest();
        picReq.newProfilePictureId = "IMAGE_UI_JOUST_AVATARS_AVATAR_7";
        Packets.ProfileResponse picRes = auth.updateProfile(picReq, "gardener");
        assertTrue(picRes.success, picRes.message);
        assertEquals("IMAGE_UI_JOUST_AVATARS_AVATAR_7", picRes.profileData.profilePictureId);

        Packets.LoginRequest login = new Packets.LoginRequest();
        login.username = "gardener";
        login.password = "Abcd1234!";
        Packets.LoginResponse loggedIn = auth.login(login);
        assertTrue(loggedIn.success);
        assertEquals("CoolNick", loggedIn.profileData.nickname);
        assertEquals("IMAGE_UI_JOUST_AVATARS_AVATAR_7", loggedIn.profileData.profilePictureId);
    }

    @Test
    void loginWithNewUsernameWorksAndOldUsernameRejected() {
        Packets.ProfileUpdateRequest rename = new Packets.ProfileUpdateRequest();
        rename.newUsername = "fresh-name";
        rename.securityAnswer = "blue";
        assertTrue(auth.updateProfile(rename, "gardener").success);

        Packets.LoginRequest oldLogin = new Packets.LoginRequest();
        oldLogin.username = "gardener";
        oldLogin.password = "Abcd1234!";
        assertFalse(auth.login(oldLogin).success);

        Packets.LoginRequest newLogin = new Packets.LoginRequest();
        newLogin.username = "fresh-name";
        newLogin.password = "Abcd1234!";
        Packets.LoginResponse ok = auth.login(newLogin);
        assertTrue(ok.success);
        assertEquals("fresh-name", ok.profileData.username);
    }

    @Test
    void sessionTokenSurvivesUsernameRename() {
        Packets.LoginRequest login = new Packets.LoginRequest();
        login.username = "gardener";
        login.password = "Abcd1234!";
        Packets.LoginResponse loggedIn = auth.login(login);
        assertTrue(loggedIn.success);
        String token = loggedIn.sessionToken;

        Packets.ProfileUpdateRequest rename = new Packets.ProfileUpdateRequest();
        rename.newUsername = "token-user";
        rename.securityAnswer = "blue";
        assertTrue(auth.updateProfile(rename, "gardener").success);

        Packets.SessionRestoreRequest restore = new Packets.SessionRestoreRequest();
        restore.sessionToken = token;
        Packets.LoginResponse restored = auth.restoreSession(restore);
        assertTrue(restored.success, restored.message);
        assertEquals("token-user", restored.profileData.username);
    }

    @Test
    void forgotPasswordWrongAndRightAnswer() {
        Packets.ForgotPasswordRequest forgot = new Packets.ForgotPasswordRequest();
        forgot.email = "gardener@mail.com";
        Packets.ForgotPasswordResponse question = auth.forgotPassword(forgot);
        assertTrue(question.success);
        assertTrue(question.securityQuestionText != null && !question.securityQuestionText.isBlank());

        Packets.ResetPasswordRequest wrong = new Packets.ResetPasswordRequest();
        wrong.email = "gardener@mail.com";
        wrong.securityAnswer = "not-blue";
        wrong.newPassword = "NewPass9!";
        assertFalse(auth.resetPassword(wrong).success);

        Packets.ResetPasswordRequest right = new Packets.ResetPasswordRequest();
        right.email = "gardener@mail.com";
        right.securityAnswer = "blue";
        right.newPassword = "NewPass9!";
        assertTrue(auth.resetPassword(right).success);

        Packets.LoginRequest login = new Packets.LoginRequest();
        login.username = "gardener";
        login.password = "NewPass9!";
        assertTrue(auth.login(login).success);
    }

    @Test
    void registerLoginRoundTrip() {
        Packets.RegisterRequest req = new Packets.RegisterRequest();
        req.username = "newbie01";
        req.password = "Abcd1234!";
        req.nickname = "Newbie";
        req.email = "newbie01@mail.com";
        req.gender = "male";
        req.securityQuestion = "1";
        req.age = "18";
        req.securityAnswer = "sky";
        Packets.RegisterResponse registered = auth.register(req);
        assertTrue(registered.success, registered.message);

        Packets.LoginRequest login = new Packets.LoginRequest();
        login.username = "newbie01";
        login.password = "Abcd1234!";
        Packets.LoginResponse loggedIn = auth.login(login);
        assertTrue(loggedIn.success);
        assertEquals("newbie01", loggedIn.profileData.username);
        assertEquals("newbie01@mail.com", loggedIn.profileData.email);
    }

    @Test
    void unauthenticatedProfileUpdateRejected() {
        Packets.ProfileUpdateRequest req = new Packets.ProfileUpdateRequest();
        req.newNickname = "x";
        Packets.ProfileResponse response = auth.updateProfile(req, null);
        assertFalse(response.success);
        assertEquals(AuthErrorCodes.NOT_AUTHENTICATED, response.errorCode);
    }


    private static Packets.ProfileRequest profileRequest(String username) {
        Packets.ProfileRequest request = new Packets.ProfileRequest();
        request.username = username;
        return request;
    }
}

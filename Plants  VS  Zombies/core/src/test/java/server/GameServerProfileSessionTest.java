package server;

import app.validator.AuthErrorCodes;
import common.utils.HashUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import server.auth.ServerAuthService;
import server.network.ClientState;
import server.network.Packets;
import server.quest.QuestProgressStore;
import server.user.ServerUserRepository;
import server.user.UserAccount;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Simulates the connection-scoped RemoteClient identity refresh that GameServer
 * must perform after a successful username rename, and session rebind after reconnect.
 */
class GameServerProfileSessionTest {
    @TempDir
    Path tempDir;

    private ServerUserRepository repository;
    private ServerAuthService auth;
    private RemoteClient remoteClient;

    @BeforeEach
    void setUp() {
        repository = new ServerUserRepository(tempDir.resolve("server_users.json"));
        auth = new ServerAuthService(repository, new QuestProgressStore());
        UserAccount account = new UserAccount();
        account.setUsername("alice");
        account.setPasswordHash(HashUtil.hash("Abcd1234!"));
        account.setNickname("Alice");
        account.setEmail("alice@mail.com");
        account.setGender("female");
        account.setSecurityQuestionNumber(1);
        account.setSecurityAnswerHash(HashUtil.hash("rose"));
        account.setGems(150);
        account.setDifficulty(3);
        assertTrue(repository.add(account));

        remoteClient = new RemoteClient(null, ClientState.REGISTERED);
        remoteClient.setUsername("alice");
    }

    @Test
    void remoteClientUsernameRefreshedSoEmailWorksAfterRename() {
        Packets.ProfileUpdateRequest rename = new Packets.ProfileUpdateRequest();
        rename.newUsername = "alice2";
        rename.securityAnswer = "rose";
        Packets.ProfileResponse renamed = auth.updateProfile(rename, remoteClient.getUsername());
        assertTrue(renamed.success, renamed.message);

        if (renamed.profileData != null && renamed.profileData.username != null) {
            remoteClient.setUsername(renamed.profileData.username);
        }
        assertEquals("alice2", remoteClient.getUsername());

        Packets.ProfileUpdateRequest emailReq = new Packets.ProfileUpdateRequest();
        emailReq.newEmail = "alice2@mail.com";
        emailReq.securityAnswer = "rose";
        Packets.ProfileResponse emailRes = auth.updateProfile(emailReq, remoteClient.getUsername());
        assertTrue(emailRes.success, emailRes.message);
        assertEquals("alice2@mail.com", emailRes.profileData.email);
    }

    @Test
    void staleRemoteUsernameWouldFailWithoutRefresh() {
        Packets.ProfileUpdateRequest rename = new Packets.ProfileUpdateRequest();
        rename.newUsername = "alice3";
        rename.securityAnswer = "rose";
        assertTrue(auth.updateProfile(rename, "alice").success);

        Packets.ProfileUpdateRequest emailReq = new Packets.ProfileUpdateRequest();
        emailReq.newEmail = "stale@mail.com";
        emailReq.securityAnswer = "rose";
        Packets.ProfileResponse emailRes = auth.updateProfile(emailReq, remoteClient.getUsername());
        assertEquals(false, emailRes.success);
        assertEquals(AuthErrorCodes.ACCOUNT_NOT_FOUND, emailRes.errorCode);
    }

    @Test
    void sessionTokenRebindsNamelessRemoteClientSoUsernameUpdateIsAuthenticated() {
        Packets.LoginRequest login = new Packets.LoginRequest();
        login.username = "alice";
        login.password = "Abcd1234!";
        Packets.LoginResponse loggedIn = auth.login(login);
        assertTrue(loggedIn.success, loggedIn.message);
        String token = loggedIn.sessionToken;

        remoteClient = new RemoteClient(null, ClientState.NAMELESS);
        assertEquals(null, remoteClient.getUsername());

        String username = remoteClient.getUsername();
        if (username == null || username.isBlank()) {
            Packets.SessionRestoreRequest restore = new Packets.SessionRestoreRequest();
            restore.sessionToken = token;
            Packets.LoginResponse restored = auth.restoreSession(restore);
            assertTrue(restored.success, restored.message);
            remoteClient.setUsername(restored.profileData.username);
            remoteClient.setSessionToken(restored.sessionToken);
            remoteClient.setClientState(ClientState.REGISTERED);
            username = remoteClient.getUsername();
        }

        Packets.ProfileUpdateRequest rename = new Packets.ProfileUpdateRequest();
        rename.newUsername = "alice-renamed";
        rename.securityAnswer = "rose";
        rename.sessionToken = token;
        Packets.ProfileResponse response = auth.updateProfile(rename, username);
        assertTrue(response.success, response.message);
        assertEquals("alice-renamed", response.profileData.username);
    }

    @Test
    void missingAuthUsernameReturnsNotAuthenticated() {
        Packets.ProfileUpdateRequest rename = new Packets.ProfileUpdateRequest();
        rename.newUsername = "nobody";
        rename.securityAnswer = "rose";
        Packets.ProfileResponse response = auth.updateProfile(rename, null);
        assertEquals(false, response.success);
        assertEquals(AuthErrorCodes.NOT_AUTHENTICATED, response.errorCode);
    }
}
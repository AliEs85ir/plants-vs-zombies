package app.service;

import app.model.App;
import app.model.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import server.network.Packets;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AppServiceApplyProfileTest {
    private static final String USER = "ApplyProfileUser";

    @AfterEach
    void tearDown() throws Exception {
        App.setCurrentUser(null);
        App.setCurrentProgress(null);
        Path dir = ProgressService.progressFile(USER).getParent();
        if (dir != null && Files.exists(dir)) {
            Files.walk(dir)
                .sorted((a, b) -> b.compareTo(a))
                .forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (Exception ignored) {
                    }
                });
        }
    }

    @Test
    void applyProfileCopiesNicknameAndEmailIntoCurrentUserForUiBinding() {
        Packets.ProfileData profile = new Packets.ProfileData();
        profile.username = USER;
        profile.nickname = "GreenThumb";
        profile.email = "gardener@mail.com";
        profile.gender = "male";
        profile.age = 20;
        profile.coins = 10;
        profile.gems = 150;
        profile.difficulty = 3;
        profile.securityQuestionNumber = 1;

        AppService.applyProfile(profile);

        User current = App.getCurrentUser();
        assertNotNull(current);
        assertEquals(USER, current.getUsername());
        assertEquals("GreenThumb", current.getNickname());
        assertEquals("gardener@mail.com", current.getEmail());
    }

    @Test
    void applyProfileKeepsServerIdentityWhenLocalProgressHasStaleOrBlankIdentity() {
        User local = new User();
        local.setUsername(USER);
        local.setCoins(42);
        assertTrue(ProgressService.saveUserProgress(local));

        Packets.ProfileData profile = new Packets.ProfileData();
        profile.username = USER;
        profile.nickname = "ServerNick";
        profile.email = "server@mail.com";
        profile.gender = "female";
        profile.age = 22;
        profile.coins = 42;
        profile.gems = 150;
        profile.difficulty = 3;
        profile.securityQuestionNumber = 1;

        AppService.applyProfile(profile);

        User current = App.getCurrentUser();
        assertNotNull(current);
        assertEquals("ServerNick", current.getNickname());
        assertEquals("server@mail.com", current.getEmail());
        assertEquals("female", current.getGender());
        assertEquals(42, current.getCoins());
    }
}
package app.service;

import app.enums.Color;
import app.enums.quest.QuestCategory;
import app.enums.quest.QuestName;
import app.model.greenhouse.MarigoldPlant;
import app.model.user.User;
import game.entity.quest.Quest;
import game.service.quest.QuestFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import server.network.GameStateSerializer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ProgressServiceTest {
    private static final String USER_A = "PersistUserA";
    private static final String USER_B = "PersistUserB";

    @AfterEach
    void cleanup() throws Exception {
        deleteQuiet(ProgressService.progressFile(USER_A).getParent());
        deleteQuiet(ProgressService.progressFile(USER_B).getParent());
    }

    @Test
    void saveAndLoadPreservesCoinsGreenhouseAndPlantFood() {
        User user = new User(USER_A, "x", "nick", "a@test.com", "male", 1, "ans", 20);
        user.setCoins(1250);
        user.setGems(80);
        user.getFoodInventory().setAmount(3);
        LocalDateTime planted = LocalDateTime.now().minusMinutes(30);
        LocalDateTime ready = LocalDateTime.now().plusMinutes(30);
        user.getGreenhouse().getPot(1, 1).setPlant(new MarigoldPlant(Color.YELLOW, 50, planted, ready));

        assertTrue(ProgressService.saveUserProgress(user));

        User loaded = new User(USER_A, "x", "nick", "a@test.com", "male", 1, "ans", 20);
        loaded.setCoins(0);
        loaded.setGems(0);
        assertTrue(ProgressService.loadUserProgress(loaded));

        assertEquals(1250, loaded.getCoins());
        assertEquals(80, loaded.getGems());
        assertEquals(3, loaded.getFoodInventory().getAmount());
        assertNotNull(loaded.getGreenhouse().getPot(1, 1).getPlant());
        assertEquals(ready, loaded.getGreenhouse().getPot(1, 1).getPlant().getReadyAt());
    }

    @Test
    void usersDoNotShareProgress() {
        User a = new User(USER_A, "x", "a", "a@test.com", "male", 1, "ans", 20);
        a.setCoins(1000);
        ProgressService.saveUserProgress(a);

        User b = new User(USER_B, "x", "b", "b@test.com", "male", 1, "ans", 20);
        b.setCoins(5);
        ProgressService.saveUserProgress(b);

        User loadedB = new User(USER_B, "x", "b", "b@test.com", "male", 1, "ans", 20);
        loadedB.setCoins(0);
        assertTrue(ProgressService.loadUserProgress(loadedB));
        assertEquals(5, loadedB.getCoins());
    }

    @Test
    void corruptedSaveDoesNotCrashAndReturnsFalse() throws Exception {
        Path file = ProgressService.progressFile(USER_A);
        Files.createDirectories(file.getParent());
        Files.writeString(file, "{not-valid-json");

        User user = new User(USER_A, "x", "nick", "a@test.com", "male", 1, "ans", 20);
        assertFalse(ProgressService.loadUserProgress(user));
        assertFalse(Files.exists(file));
    }

    @Test
    void saveAndLoadPreservesQuestProgress() {
        User user = new User(USER_A, "x", "nick", "a@test.com", "male", 1, "ans", 20);
        Quest sunQuest = QuestFactory.createDailySunCollector();
        sunQuest.restoreProgress(1250, 0, false);
        user.getQuestManager().addQuest(sunQuest);

        assertTrue(ProgressService.saveUserProgress(user));

        User loaded = new User(USER_A, "x", "nick", "a@test.com", "male", 1, "ans", 20);
        assertTrue(ProgressService.loadUserProgress(loaded));

        Quest restored = loaded.getQuestManager().getQuestsByCategory(QuestCategory.DAILY)
            .stream()
            .filter(quest -> quest.getName() == QuestName.DAILY_SUN_COLLECTOR)
            .findFirst()
            .orElse(null);
        assertNotNull(restored);
        assertNotNull(restored.getCondition());
        assertEquals(1250, restored.getCondition().getCurrentProgress());
        assertEquals(0, restored.getCurrentTierIndex());
        assertFalse(restored.isCompleted());
    }

    @Test
    void saveAndLoadPreservesProfilePicture() {
        User user = new User(USER_A, "x", "nick", "a@test.com", "male", 1, "ans", 20);
        user.setProfilePictureId("IMAGE_UI_JOUST_AVATARS_AVATAR_7");
        assertTrue(ProgressService.saveUserProgress(user));

        User loaded = new User(USER_A, "x", "nick", "a@test.com", "male", 1, "ans", 20);
        assertTrue(ProgressService.loadUserProgress(loaded));
        assertEquals("IMAGE_UI_JOUST_AVATARS_AVATAR_7", loaded.getProfilePictureId());
    }

    @Test
    void missingFieldsUseDefaultsWithoutCrash() {
        String legacy = "{\"version\":1,\"coins\":42}";
        User user = new User(USER_A, "x", "nick", "a@test.com", "male", 1, "ans", 20);
        user.setCoins(0);
        assertTrue(GameStateSerializer.applyToUser(user, legacy));
        assertEquals(42, user.getCoins());
        assertNotNull(user.getGreenhouse());
        assertNotNull(user.getQuestManager());
    }


    @Test
    void progressDoesNotOverwriteAccountIdentityFields() {
        User user = new User(USER_A, "x", "nick", "a@test.com", "male", 1, "ans", 20);
        user.setProfilePictureId("IMAGE_UI_JOUST_AVATARS_AVATAR_7");
        assertTrue(ProgressService.saveUserProgress(user));

        User serverAuthoritative = new User("server-name", "x", "server-nick", "server@test.com", "female", 1, "ans", 30);
        serverAuthoritative.setProfilePictureId("IMAGE_UI_JOUST_AVATARS_AVATAR_3");
        // load from USER_A file path requires matching username for ProgressService; apply JSON directly
        String json = server.network.GameStateSerializer.serialize(user);
        assertTrue(server.network.GameStateSerializer.applyToUser(serverAuthoritative, json));
        assertEquals("server-name", serverAuthoritative.getUsername());
        assertEquals("server-nick", serverAuthoritative.getNickname());
        assertEquals("server@test.com", serverAuthoritative.getEmail());
        assertEquals("female", serverAuthoritative.getGender());
        assertEquals(30, serverAuthoritative.getAge());
        // picture already set on server-authoritative user is preserved
        assertEquals("IMAGE_UI_JOUST_AVATARS_AVATAR_3", serverAuthoritative.getProfilePictureId());
    }


    @Test
    void blankIdentityFieldsAreHydratedFromProgress() {
        User saved = new User(USER_A, "x", "SavedNick", "saved@test.com", "male", 1, "ans", 20);
        assertTrue(ProgressService.saveUserProgress(saved));

        User blank = new User();
        blank.setUsername(USER_A);
        assertTrue(ProgressService.loadUserProgress(blank));
        assertEquals("SavedNick", blank.getNickname());
        assertEquals("saved@test.com", blank.getEmail());
    }
    private static void deleteQuiet(Path dir) throws Exception {
        if (dir == null || !Files.exists(dir)) {
            return;
        }
        try (var stream = Files.walk(dir)) {
            stream.sorted((a, b) -> b.compareTo(a)).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (Exception ignored) {
                }
            });
        }
    }
}

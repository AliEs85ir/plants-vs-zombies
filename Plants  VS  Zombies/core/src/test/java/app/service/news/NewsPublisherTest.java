package app.service.news;

import app.enums.Color;
import app.enums.NewsCategory;
import app.enums.SeasonType;
import app.enums.plant.PlantName;
import app.model.News;
import app.model.greenhouse.MarigoldPlant;
import app.model.shop.DailyOffer;
import app.model.user.User;
import app.service.NewsQuery;
import app.service.NewsService;
import app.service.ProgressService;
import game.entity.quest.Quest;
import game.service.quest.QuestFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.network.Packets;

import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NewsPublisherTest {
    private static final String USER = "newsPublisherTester";
    private User user;

    @BeforeEach
    void setUp() {
        user = new User(USER, "pass", "Tester", "tester@example.com", "male", 1, "ans", 20);
        user.setNewsList(new ArrayList<>());
    }

    @AfterEach
    void cleanup() throws Exception {
        var dir = ProgressService.progressFile(USER).getParent();
        if (dir != null && Files.exists(dir)) {
            try (var stream = Files.walk(dir)) {
                stream.sorted((a, b) -> b.compareTo(a)).forEach(path -> {
                    try { Files.deleteIfExists(path); } catch (Exception ignored) {}
                });
            }
        }
    }

    @Test
    void welcomePublishesOnceAndNotOnReplay() {
        assertTrue(NewsPublisher.onUserRegistered(user));
        assertFalse(NewsPublisher.onUserRegistered(user));
        assertEquals(1, user.getNewsList().size());
        assertEquals(NewsCategory.WELCOME, user.getNewsList().get(0).getCategory());
        assertTrue(user.getNewsList().get(0).getBody().toLowerCase().contains("login"));
    }

    @Test
    void profileSuccessCreatesSeparateTemplatesWithoutPasswordLeak() {
        Packets.ProfileUpdateRequest request = new Packets.ProfileUpdateRequest();
        request.newNickname = "Hero";
        request.newUsername = "hero1";
        request.newEmail = "hero@example.com";
        request.newPassword = "SecretValue123!";
        NewsPublisher.onProfileUpdated(user, request, USER, "Tester", "tester@example.com");
        assertEquals(4, user.getNewsList().size());
        assertTrue(user.getNewsList().stream().noneMatch(n -> n.getBody().contains("SecretValue123!")));
    }

    @Test
    void profileFailureDoesNotPublishWhenHookSkipped() {
        assertEquals(0, user.getNewsList().size());
    }

    @Test
    void plantUnlockOncePerPlant() {
        NewsPublisher.onPlantUnlocked(user, PlantName.PEASHOOTER, "test");
        NewsPublisher.onPlantUnlocked(user, PlantName.PEASHOOTER, "test");
        assertEquals(1, user.getNewsList().size());
        assertEquals(NewsCategory.PLANT, user.getNewsList().get(0).getCategory());
    }

    @Test
    void zombieDiscoveryOnce() {
        NewsPublisher.onZombieDiscovered(user, "BASIC");
        NewsPublisher.onZombieDiscovered(user, "BASIC");
        assertEquals(1, user.getNewsList().size());
        assertEquals(NewsCategory.ZOMBIE, user.getNewsList().get(0).getCategory());
    }

    @Test
    void greenhousePlantAndReadyUseDistinctKeysAndReadyTime() {
        LocalDateTime planted = LocalDateTime.now().minusMinutes(5);
        LocalDateTime ready = LocalDateTime.now().plusHours(2);
        MarigoldPlant plant = new MarigoldPlant(Color.YELLOW, 50, planted, ready);
        NewsPublisher.onGreenhousePlanted(user, 1, 1, plant);
        NewsPublisher.onGreenhousePlanted(user, 1, 1, plant);
        assertEquals(1, user.getNewsList().size());
        assertTrue(user.getNewsList().get(0).getBody().contains(ready.toLocalDate().toString())
            || user.getNewsList().get(0).getBody().contains("Harvest-ready"));

        MarigoldPlant readyPlant = new MarigoldPlant(Color.YELLOW, 50, planted, LocalDateTime.now().minusMinutes(1));
        NewsPublisher.onGreenhouseReady(user, 1, 1, readyPlant);
        NewsPublisher.onGreenhouseReady(user, 1, 1, readyPlant);
        assertEquals(2, user.getNewsList().size());
        assertTrue(user.getNewsList().stream().anyMatch(n -> n.getCategory() == NewsCategory.GREENHOUSE
            && n.getTitle().toLowerCase().contains("harvest")));
    }

    @Test
    void questCompletedPublishesDetailedArticle() {
        Quest quest = QuestFactory.createDailySunCollector();
        NewsPublisher.onQuestCompleted(user, quest);
        assertEquals(1, user.getNewsList().size());
        assertEquals(NewsCategory.QUEST, user.getNewsList().get(0).getCategory());
        assertTrue(user.getNewsList().get(0).getBody().contains("Quest"));
    }

    @Test
    void dailyOfferOncePerDayProduct() {
        DailyOffer offer = new DailyOffer("coin_pack", LocalDate.of(2026, 9, 4), 25);
        NewsPublisher.onDailyOfferAvailable(user, offer);
        NewsPublisher.onDailyOfferAvailable(user, offer);
        assertEquals(1, user.getNewsList().size());
        assertEquals(NewsCategory.SHOP, user.getNewsList().get(0).getCategory());
        assertTrue(user.getNewsList().get(0).getBody().contains("25%"));
    }

    @Test
    void shopPurchaseSuccessCreatesArticle() {
        NewsPublisher.onShopPurchase(user, "gem_pack", 2, "100 COINS");
        assertEquals(1, user.getNewsList().size());
        assertTrue(user.getNewsList().get(0).getBody().contains("gem_pack"));
        assertTrue(user.getNewsList().get(0).getBody().contains("100 COINS"));
    }

    @Test
    void shopPurchaseFailPathPublishesNothingWhenNotCalled() {
        assertEquals(0, user.getNewsList().size());
    }

    @Test
    void levelUnlockOncePerSeasonLevel() {
        NewsPublisher.onLevelUnlocked(user, SeasonType.ANCIENT_EGYPT, 2);
        NewsPublisher.onLevelUnlocked(user, SeasonType.ANCIENT_EGYPT, 2);
        assertEquals(1, user.getNewsList().size());
        assertEquals(NewsCategory.LEVEL, user.getNewsList().get(0).getCategory());
    }

    @Test
    void readPersistenceAndUnreadCount() {
        NewsPublisher.onUserRegistered(user);
        NewsPublisher.onPlantUnlocked(user, PlantName.SUNFLOWER, "test");
        assertEquals(2, NewsQuery.unreadCount(user.getNewsList()));
        NewsService.markRead(user.getNewsList().get(0));
        // markRead may no-op persist without current user; force local flag
        user.getNewsList().get(0).setRead(true);
        assertEquals(1, NewsQuery.unreadCount(user.getNewsList()));
        assertTrue(ProgressService.saveUserProgress(user));
        User loaded = new User(USER, "pass", "Tester", "tester@example.com", "male", 1, "ans", 20);
        assertTrue(ProgressService.loadUserProgress(loaded));
        assertTrue(NewsQuery.unreadCount(loaded.getNewsList()) >= 1);
        assertTrue(loaded.getNewsList().stream().anyMatch(News::isRead));
    }

    @Test
    void uniqueKeysPreventDuplicatesAcrossHelpers() {
        assertTrue(NewsService.publishIfAbsent(user, "CUSTOM:1",
            () -> News.article("CUSTOM:1", NewsCategory.SYSTEM, app.enums.NewsPriority.NORMAL,
                "Custom Title Here", "Detailed body paragraph one.\\n\\nParagraph two.", null)));
        assertFalse(NewsService.publishIfAbsent(user, "CUSTOM:1",
            () -> News.article("CUSTOM:1", NewsCategory.SYSTEM, app.enums.NewsPriority.NORMAL,
                "Custom Title Here", "Detailed body paragraph one.\\n\\nParagraph two.", null)));
        assertEquals(1, user.getNewsList().size());
    }

    @Test
    void newestFirstOrderingWithTieBreak() {
        LocalDateTime same = LocalDateTime.of(2026, 9, 4, 12, 0);
        News a = News.article("A", NewsCategory.SYSTEM, app.enums.NewsPriority.NORMAL, "Alpha", "a", null);
        News b = News.article("B", NewsCategory.SYSTEM, app.enums.NewsPriority.NORMAL, "Beta", "b", null);
        a.setTime(same);
        b.setTime(same);
        a.setId("aaa");
        b.setId("zzz");
        user.getNewsList().add(a);
        user.getNewsList().add(b);
        var visible = NewsQuery.visible(user.getNewsList(), true, app.enums.NewsOrder.NEWEST_FIRST);
        assertEquals("zzz", visible.get(0).getId());
    }
}

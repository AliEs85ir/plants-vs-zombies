package app.service.news;

import app.enums.SeasonType;
import app.enums.plant.PlantName;
import app.model.App;
import app.model.News;
import app.model.greenhouse.Greenhouse;
import app.model.greenhouse.GreenhousePlant;
import app.model.greenhouse.GreenhousePot;
import app.model.shop.DailyOffer;
import app.model.user.User;
import app.service.NewsService;
import app.service.progression.ProgressionService.ProgressionDelta;
import common.model.Position;
import game.entity.quest.Quest;
import game.entity.quest.QuestReward;
import server.network.Packets;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Single entry point for event → newspaper article creation.
 * Call only after the underlying operation has already succeeded.
 */
public final class NewsPublisher {
    private NewsPublisher() {
    }

    public static boolean onUserRegistered(User user) {
        if (user == null) {
            return false;
        }
        return NewsService.publishIfAbsent(user, NewsKeys.userRegistered(),
            () -> NewsTemplates.welcome(user.getUsername(), user.getNickname()));
    }

    public static void onProfileUpdated(
        User user,
        Packets.ProfileUpdateRequest request,
        String previousUsername,
        String previousNickname,
        String previousEmail
    ) {
        if (user == null || request == null) {
            return;
        }
        if (request.newNickname != null && !request.newNickname.isBlank()) {
            NewsService.publishIfAbsent(user, NewsKeys.profileChange("nickname", request.newNickname),
                () -> NewsTemplates.profileNickname(previousNickname, request.newNickname));
        }
        if (request.newUsername != null && !request.newUsername.isBlank()) {
            NewsService.publishIfAbsent(user, NewsKeys.profileChange("username", request.newUsername),
                () -> NewsTemplates.profileUsername(previousUsername, request.newUsername));
        }
        if (request.newEmail != null && !request.newEmail.isBlank()) {
            NewsService.publishIfAbsent(user, NewsKeys.profileChange("email", request.newEmail),
                () -> NewsTemplates.profileEmail(previousEmail, request.newEmail));
        }
        if (request.newPassword != null && !request.newPassword.isBlank()) {
            NewsService.publishIfAbsent(user, NewsKeys.profileChange("password",
                    String.valueOf(System.currentTimeMillis() / 60_000L)),
                NewsTemplates::profilePassword);
        }
    }

    public static void onPlantUnlocked(User user, PlantName plant, String source) {
        if (user == null || plant == null) {
            return;
        }
        NewsService.publishIfAbsent(user, NewsKeys.plantUnlock(plant.name()),
            () -> NewsTemplates.plantUnlock(plant, source));
    }

    public static void onZombieDiscovered(User user, String alias) {
        if (user == null || alias == null || alias.isBlank()) {
            return;
        }
        NewsService.publishIfAbsent(user, NewsKeys.zombieDiscovery(alias),
            () -> NewsTemplates.zombieDiscovery(alias));
    }

    public static void onGreenhousePlanted(User user, int x, int y, GreenhousePlant plant) {
        if (user == null || plant == null) {
            return;
        }
        LocalDateTime readyAt = plant.getReadyAt();
        String readyKey = readyAt == null ? LocalDateTime.now().toString() : readyAt.toString();
        NewsService.publishIfAbsent(user, NewsKeys.greenhousePlanted(x, y, readyKey),
            () -> NewsTemplates.greenhousePlanted(x, y, plant.getPlantName(), readyAt));
    }

    public static void onGreenhouseReady(User user, int x, int y, GreenhousePlant plant) {
        if (user == null || plant == null) {
            return;
        }
        LocalDateTime readyAt = plant.getReadyAt();
        String readyKey = readyAt == null ? "ready" : readyAt.toString();
        NewsService.publishIfAbsent(user, NewsKeys.greenhouseReady(x, y, readyKey),
            () -> NewsTemplates.greenhouseReady(x, y, plant.getPlantName(), readyAt));
    }

    /** Catch-up for pots that became ready while offline or between menu visits. */
    public static void refreshGreenhouseReadyNews(User user) {
        if (user == null || user.getGreenhouse() == null) {
            return;
        }
        Greenhouse greenhouse = user.getGreenhouse();
        GreenhousePot[][] grid = greenhouse.getPots();
        if (grid == null) {
            return;
        }
        for (GreenhousePot[] row : grid) {
            if (row == null) {
                continue;
            }
            for (GreenhousePot pot : row) {
                if (pot == null || pot.getPlant() == null) {
                    continue;
                }
                if (!pot.isPlantReady()) {
                    continue;
                }
                Position position = pot.getPosition();
                if (position == null) {
                    continue;
                }
                onGreenhouseReady(user, (int) position.getX(), (int) position.getY(), pot.getPlant());
            }
        }
    }

    public static void onQuestCompleted(User user, Quest quest) {
        if (user == null || quest == null) {
            return;
        }
        String questName = quest.getName() == null ? "Quest" : quest.getName().toString();
        String key = questName + ":T" + quest.getCurrentTierIndex() + ":" + (quest.isCompleted() ? "DONE" : "CLAIM");
        NewsService.publishIfAbsent(user, NewsKeys.questCompleted(key),
            () -> NewsTemplates.questCompleted(questName, rewardSummary(quest)));
    }

    public static void onDailyOfferAvailable(User user, DailyOffer offer) {
        if (user == null || offer == null || offer.getDate() == null) {
            return;
        }
        NewsService.publishIfAbsent(user, NewsKeys.dailyOffer(offer.getDate().toString(), offer.getProductId()),
            () -> NewsTemplates.dailyOffer(offer.getDate(), offer.getProductId(), offer.getDiscountPercent()));
    }

    public static void onShopPurchase(User user, String productId, int quantity, String priceText) {
        if (user == null) {
            return;
        }
        String txId = UUID.randomUUID().toString();
        NewsService.publishIfAbsent(user, NewsKeys.shopPurchase(txId),
            () -> NewsTemplates.shopPurchase(txId, productId, quantity, priceText));
    }

    public static void onLevelUnlocked(User user, SeasonType season, int levelNumber) {
        if (user == null || season == null || levelNumber < 1) {
            return;
        }
        NewsService.publishIfAbsent(user, NewsKeys.levelUnlock(season.name(), levelNumber),
            () -> NewsTemplates.levelUnlock(season, levelNumber));
    }

    public static void onProgressionDelta(User user, ProgressionDelta delta, SeasonType season) {
        if (user == null || delta == null) {
            return;
        }
        for (PlantName plant : delta.getNewlyUnlockedPlants()) {
            onPlantUnlocked(user, plant, "stage progression");
        }
        for (String alias : delta.getNewlyDiscoveredZombies()) {
            onZombieDiscovered(user, alias);
        }
        if (delta.isUnlockedNextStage() && delta.getNextStageNumber() != null) {
            onLevelUnlocked(user, season, delta.getNextStageNumber());
        }
    }

    public static void onCurrentUserMenuOpened() {
        User user = App.getCurrentUser();
        if (user == null) {
            return;
        }
        NewsService.ensureReady(user);
        refreshGreenhouseReadyNews(user);
    }

    private static String rewardSummary(Quest quest) {
        if (quest.getRewards() == null || quest.getRewards().isEmpty()) {
            return "standard quest rewards";
        }
        StringBuilder sb = new StringBuilder();
        for (QuestReward reward : quest.getRewards()) {
            if (reward == null || reward.type() == null) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(reward.amount()).append(' ').append(reward.type().name().toLowerCase());
        }
        return sb.length() == 0 ? "standard quest rewards" : sb.toString();
    }
}

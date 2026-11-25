package app.service.news;

import app.enums.NewsCategory;
import app.enums.NewsPriority;
import app.enums.SeasonType;
import app.enums.plant.PlantName;
import app.model.News;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

/** English newspaper article templates. UI must not hardcode event text. */
public final class NewsTemplates {
    private static final DateTimeFormatter TIME =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.ROOT);

    private NewsTemplates() {
    }

    public static News welcome(String username, String nickname) {
        String name = displayName(nickname, username);
        String body = ""
            + "Welcome to the neighborhood, " + name + ".\n\n"
            + "Your newspaper is ready. From now on, important lawn events will appear here as full articles: "
            + "plant unlocks, greenhouse harvests, quest rewards, shop purchases, and newly discovered zombies.\n\n"
            + "Start with sunflowers, keep a shooter ready, and check this paper whenever the warning icon appears on the News tab.\n\n"
            + "This welcome note is written once for your account and will not repeat on every login.";
        return News.article(
            NewsKeys.userRegistered(),
            NewsCategory.WELCOME,
            NewsPriority.SPECIAL,
            "Welcome to the Neighborhood",
            body,
            News.meta("eventType", "USER_REGISTERED", "username", nullToEmpty(username))
        );
    }

    public static News profileNickname(String oldValue, String newValue) {
        String body = ""
            + "Your profile nickname was updated successfully.\n\n"
            + "Previous nickname: " + display(oldValue) + "\n"
            + "New nickname: " + display(newValue) + "\n\n"
            + "The change is saved to your account. Other players and menus will show the new nickname wherever it appears.";
        return News.article(
            NewsKeys.profileChange("nickname", newValue),
            NewsCategory.PROFILE,
            NewsPriority.NORMAL,
            "Nickname Updated Successfully",
            body,
            News.meta("eventType", "PROFILE_NICKNAME", "field", "nickname", "newValue", nullToEmpty(newValue))
        );
    }

    public static News profileUsername(String oldValue, String newValue) {
        String body = ""
            + "Your account username was changed successfully.\n\n"
            + "Previous username: " + display(oldValue) + "\n"
            + "New username: " + display(newValue) + "\n\n"
            + "Local progress was remapped to the new username folder. Use the new username the next time you sign in.";
        return News.article(
            NewsKeys.profileChange("username", newValue),
            NewsCategory.PROFILE,
            NewsPriority.IMPORTANT,
            "Username Changed on Your Account",
            body,
            News.meta("eventType", "PROFILE_USERNAME", "field", "username", "newValue", nullToEmpty(newValue))
        );
    }

    public static News profileEmail(String oldValue, String newValue) {
        String body = ""
            + "Your account email address was updated successfully.\n\n"
            + "Previous email: " + display(oldValue) + "\n"
            + "New email: " + display(newValue) + "\n\n"
            + "Recovery and account messages will use the new address. No password value is stored in this newspaper.";
        return News.article(
            NewsKeys.profileChange("email", newValue),
            NewsCategory.PROFILE,
            NewsPriority.NORMAL,
            "Email Address Updated Successfully",
            body,
            News.meta("eventType", "PROFILE_EMAIL", "field", "email", "newValue", nullToEmpty(newValue))
        );
    }

    public static News profilePassword() {
        String body = ""
            + "Your account password was changed successfully.\n\n"
            + "For your safety, this newspaper never records the password value itself. "
            + "If you did not request this change, secure the account through the profile recovery options immediately.\n\n"
            + "Future logins will require the new password.";
        return News.article(
            NewsKeys.profileChange("password", String.valueOf(System.currentTimeMillis() / 60_000L)),
            NewsCategory.PROFILE,
            NewsPriority.IMPORTANT,
            "Password Changed Successfully",
            body,
            News.meta("eventType", "PROFILE_PASSWORD", "field", "password")
        );
    }

    public static News plantUnlock(PlantName plant, String source) {
        String plantName = plant == null ? "Unknown Plant" : humanize(plant.name());
        String body = ""
            + plantName + " is now unlocked for your account.\n\n"
            + "What happened: a plant unlock event finished successfully"
            + (source == null || source.isBlank() ? "." : " via " + source + ".") + "\n\n"
            + "What changed: " + plantName + " was added to your unlocked plant roster and can appear in plant selection when the chapter allows it.\n\n"
            + "Next: open the collection or plant chooser before the next lawn to add it to your loadout.";
        String keyPlant = plant == null ? "UNKNOWN" : plant.name();
        return News.article(
            NewsKeys.plantUnlock(keyPlant),
            NewsCategory.PLANT,
            NewsPriority.IMPORTANT,
            plantName + " Joins Your Garden Roster",
            body,
            News.meta("eventType", "PLANT_UNLOCK", "plant", keyPlant, "source", nullToEmpty(source))
        );
    }

    public static News zombieDiscovery(String alias) {
        String zombieName = humanize(alias);
        String body = ""
            + "First discovery recorded for " + zombieName + ".\n\n"
            + "What happened: this zombie type was seen on your lawn for the first time and added to your almanac progress.\n\n"
            + "What changed: " + zombieName + " is marked as discovered. Later encounters will not create another first-discovery article.\n\n"
            + "Next: review the collection when you return to the main menu to read its entry.";
        return News.article(
            NewsKeys.zombieDiscovery(alias),
            NewsCategory.ZOMBIE,
            NewsPriority.NORMAL,
            "First Sighting of " + zombieName,
            body,
            News.meta("eventType", "ZOMBIE_DISCOVERY", "zombie", nullToEmpty(alias))
        );
    }

    public static News greenhousePlanted(int x, int y, String plantName, LocalDateTime readyAt) {
        String readyText = readyAt == null ? "unknown" : readyAt.format(TIME);
        String body = ""
            + "A greenhouse planting finished at pot (" + x + ", " + y + ").\n\n"
            + "Plant: " + display(plantName) + "\n"
            + "Harvest-ready time: " + readyText + "\n\n"
            + "What changed: the pot is now growing. When real time reaches the harvest-ready moment, a separate harvest article will appear.\n\n"
            + "You can spend diamonds to speed growth, or wait until the ready time above.";
        String plantedAtKey = readyAt == null ? "unknown" : readyAt.toString();
        return News.article(
            NewsKeys.greenhousePlanted(x, y, plantedAtKey),
            NewsCategory.GREENHOUSE,
            NewsPriority.NORMAL,
            "Greenhouse Plant Growing in Pot " + x + "," + y,
            body,
            News.meta(
                "eventType", "GREENHOUSE_PLANTED",
                "potX", String.valueOf(x),
                "potY", String.valueOf(y),
                "plant", nullToEmpty(plantName),
                "readyAt", readyText
            )
        );
    }

    public static News greenhouseReady(int x, int y, String plantName, LocalDateTime readyAt) {
        String readyText = readyAt == null ? LocalDateTime.now().format(TIME) : readyAt.format(TIME);
        String body = ""
            + "A greenhouse plant is ready to harvest.\n\n"
            + "Pot: (" + x + ", " + y + ")\n"
            + "Plant: " + display(plantName) + "\n"
            + "Ready since: " + readyText + "\n\n"
            + "What happened: growth finished for this planting cycle. If you were offline, this catch-up article still fires once for the cycle.\n\n"
            + "Next action: open the greenhouse and collect the pot reward before planting again.";
        return News.article(
            NewsKeys.greenhouseReady(x, y, readyText),
            NewsCategory.GREENHOUSE,
            NewsPriority.IMPORTANT,
            "Greenhouse Harvest Ready at Pot " + x + "," + y,
            body,
            News.meta(
                "eventType", "GREENHOUSE_READY",
                "potX", String.valueOf(x),
                "potY", String.valueOf(y),
                "plant", nullToEmpty(plantName),
                "readyAt", readyText
            )
        );
    }

    public static News questCompleted(String questName, String rewardSummary) {
        String body = ""
            + "Quest reward stage completed.\n\n"
            + "Quest: " + display(questName) + "\n"
            + "Rewards: " + display(rewardSummary) + "\n\n"
            + "What happened: the quest reached a collectible completion tier and rewards were granted.\n\n"
            + "Mid-progress updates are not published here — only ready-to-collect / completed reward events appear in the newspaper.";
        return News.article(
            NewsKeys.questCompleted(questName),
            NewsCategory.QUEST,
            NewsPriority.IMPORTANT,
            "Quest Rewards Collected: " + display(questName),
            body,
            News.meta("eventType", "QUEST_COMPLETED", "quest", nullToEmpty(questName), "rewards", nullToEmpty(rewardSummary))
        );
    }

    public static News dailyOffer(LocalDate date, String productId, int discountPercent) {
        String day = date == null ? LocalDate.now().toString() : date.toString();
        String body = ""
            + "Today's special shop offer is available.\n\n"
            + "Offer date: " + day + "\n"
            + "Product: " + display(productId) + "\n"
            + "Discount: " + discountPercent + "%\n\n"
            + "What changed: the daily offer source of truth refreshed for this calendar day. "
            + "Only one newspaper article is created per offer day.\n\n"
            + "Next: visit the shop before the day rolls over if you want the discounted deal.";
        return News.article(
            NewsKeys.dailyOffer(day, productId),
            NewsCategory.SHOP,
            NewsPriority.SPECIAL,
            "Daily Special Offer Is Live Today",
            body,
            News.meta(
                "eventType", "DAILY_OFFER",
                "date", day,
                "productId", nullToEmpty(productId),
                "discountPercent", String.valueOf(discountPercent)
            )
        );
    }

    public static News shopPurchase(String txId, String productId, int quantity, String priceText) {
        String body = ""
            + "A shop purchase completed successfully.\n\n"
            + "Product: " + display(productId) + "\n"
            + "Quantity: " + quantity + "\n"
            + "Price: " + display(priceText) + "\n"
            + "Transaction: " + display(txId) + "\n\n"
            + "What happened: payment and item application both succeeded. Failed or cancelled attempts never create this article.\n\n"
            + "Your wallet and inventory already reflect this purchase.";
        return News.article(
            NewsKeys.shopPurchase(txId),
            NewsCategory.SHOP,
            NewsPriority.NORMAL,
            "Shop Purchase Confirmed",
            body,
            News.meta(
                "eventType", "SHOP_PURCHASE",
                "txId", nullToEmpty(txId),
                "productId", nullToEmpty(productId),
                "quantity", String.valueOf(quantity),
                "price", nullToEmpty(priceText)
            )
        );
    }

    public static News levelUnlock(SeasonType season, int levelNumber) {
        String seasonName = season == null ? "Adventure" : humanize(season.name());
        String body = ""
            + "A new level is now unlocked.\n\n"
            + "Season: " + seasonName + "\n"
            + "Level: " + levelNumber + "\n\n"
            + "What happened: clearing the previous stage opened the next lawn on the world map.\n\n"
            + "Next action: choose Adventure and start level " + levelNumber + " when you are ready.";
        return News.article(
            NewsKeys.levelUnlock(season == null ? "UNKNOWN" : season.name(), levelNumber),
            NewsCategory.LEVEL,
            NewsPriority.IMPORTANT,
            seasonName + " Level " + levelNumber + " Is Unlocked",
            body,
            News.meta(
                "eventType", "LEVEL_UNLOCK",
                "season", season == null ? "" : season.name(),
                "level", String.valueOf(levelNumber)
            )
        );
    }

    public static String humanize(String raw) {
        if (raw == null || raw.isBlank()) {
            return "Unknown";
        }
        String[] parts = raw.replace('-', '_').split("_");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (part.isBlank()) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(Character.toUpperCase(part.charAt(0)));
            if (part.length() > 1) {
                sb.append(part.substring(1).toLowerCase(Locale.ROOT));
            }
        }
        return sb.length() == 0 ? raw : sb.toString();
    }

    private static String displayName(String nickname, String username) {
        if (nickname != null && !nickname.isBlank()) {
            return nickname.strip();
        }
        if (username != null && !username.isBlank()) {
            return username.strip();
        }
        return "Neighbor";
    }

    private static String display(String value) {
        return value == null || value.isBlank() ? "(none)" : value.strip();
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}

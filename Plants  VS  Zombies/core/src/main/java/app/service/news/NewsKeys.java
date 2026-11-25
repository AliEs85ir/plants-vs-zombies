package app.service.news;

import java.util.Locale;

public final class NewsKeys {
    private NewsKeys() {
    }

    public static String userRegistered() {
        return "USER_REGISTERED";
    }

    public static String profileChange(String field, String value) {
        return "PROFILE_CHANGE:" + safe(field) + ":" + safe(value);
    }

    public static String plantUnlock(String plantId) {
        return "PLANT_UNLOCK:" + safe(plantId);
    }

    public static String zombieDiscovery(String alias) {
        return "ZOMBIE_DISCOVERY:" + safe(alias);
    }

    public static String greenhousePlanted(int x, int y, String plantedOrReadyKey) {
        return "GREENHOUSE_PLANTED:" + x + ":" + y + ":" + safe(plantedOrReadyKey);
    }

    public static String greenhouseReady(int x, int y, String readyKey) {
        return "GREENHOUSE_READY:" + x + ":" + y + ":" + safe(readyKey);
    }

    public static String questCompleted(String questId) {
        return "QUEST_COMPLETED:" + safe(questId);
    }

    public static String dailyOffer(String date, String productId) {
        return "DAILY_OFFER:" + safe(date) + ":" + safe(productId);
    }

    public static String shopPurchase(String txId) {
        return "SHOP_PURCHASE:" + safe(txId);
    }

    public static String levelUnlock(String season, int level) {
        return "LEVEL_UNLOCK:" + safe(season) + ":" + level;
    }

    private static String safe(String value) {
        if (value == null || value.isBlank()) {
            return "UNKNOWN";
        }
        return value.strip().toUpperCase(Locale.ROOT).replace(' ', '_');
    }
}

package app.view.shop.config;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ShopMessagesConfig {
    public Map<String, String> messages = new HashMap<>();

    public ShopMessagesConfig sanitize() {
        if (messages == null) {
            messages = new HashMap<>();
        }
        putDefault("insufficientCoins", "Not enough coins.");
        putDefault("insufficientGems", "Not enough gems.");
        putDefault("productUnavailable", "This product is not available.");
        putDefault("purchaseFailed", "Purchase failed.");
        putDefault("purchaseSuccess", "Purchase successful.");
        return this;
    }

    private void putDefault(String key, String value) {
        if (!messages.containsKey(key) || messages.get(key) == null || messages.get(key).isBlank()) {
            messages.put(key, value);
        }
    }
}

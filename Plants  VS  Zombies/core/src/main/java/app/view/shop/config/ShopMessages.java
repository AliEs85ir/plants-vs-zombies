package app.view.shop.config;

public final class ShopMessages {
    private static ShopMessagesConfig active;

    private ShopMessages() {
    }

    public static void reload() {
        active = ShopMessagesConfigLoader.load();
    }

    public static String get(String key) {
        if (active == null) {
            reload();
        }
        if (active.messages == null) {
            return key;
        }
        String value = active.messages.get(key);
        if (value == null || value.isBlank()) {
            return key;
        }
        return value;
    }
}

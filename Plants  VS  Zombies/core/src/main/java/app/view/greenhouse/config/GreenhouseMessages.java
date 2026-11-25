package app.view.greenhouse.config;

public final class GreenhouseMessages {
    private static GreenhouseMessagesConfig active;

    private GreenhouseMessages() {
    }

    public static GreenhouseMessagesConfig active() {
        if (active == null) {
            active = GreenhouseMessagesConfigLoader.load();
        }
        return active;
    }

    public static void setActive(GreenhouseMessagesConfig config) {
        active = config == null
            ? GreenhouseMessagesConfig.defaults().sanitize()
            : config.sanitize();
    }

    public static String get(String key) {
        return active().get(key);
    }

    public static String get(String key, Object... args) {
        String template = get(key);
        if (template == null || template.isBlank()) {
            return key == null ? "" : key;
        }
        if (args == null || args.length == 0) {
            return template;
        }
        try {
            return String.format(template, args);
        } catch (Exception exception) {
            return template;
        }
    }
}

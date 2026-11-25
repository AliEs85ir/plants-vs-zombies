package app.view.greenhouse.config;

import app.service.ConfigFiles;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public final class GreenhouseMessagesConfigLoader {
    public static final String CONFIG_PATH = "ui/greenhouse_messages.json";

    private GreenhouseMessagesConfigLoader() {
    }

    public static GreenhouseMessagesConfig load() {
        return load(CONFIG_PATH);
    }

    public static GreenhouseMessagesConfig load(String path) {
        File file = ConfigFiles.resolve(path);
        if (file == null || !file.isFile()) {
            return GreenhouseMessagesConfig.defaults().sanitize();
        }
        try (FileReader reader = new FileReader(file)) {
            Type type = new TypeToken<HashMap<String, String>>() {
            }.getType();
            Map<String, String> raw = new Gson().fromJson(reader, type);
            GreenhouseMessagesConfig config = new GreenhouseMessagesConfig();
            if (raw != null) {
                config.messages = new HashMap<>(raw);
            }
            return config.sanitize();
        } catch (Exception exception) {
            return GreenhouseMessagesConfig.defaults().sanitize();
        }
    }
}

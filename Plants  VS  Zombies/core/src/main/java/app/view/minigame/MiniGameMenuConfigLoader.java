package app.view.minigame;

import app.service.ConfigFiles;
import app.view.loading.InternalFiles;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public final class MiniGameMenuConfigLoader {
    public static final String CONFIG_PATH = "ui/mini_game_menu.json";

    private MiniGameMenuConfigLoader() {
    }

    public static MiniGameMenuConfig load() {
        return load(CONFIG_PATH);
    }

    public static MiniGameMenuConfig load(String path) {
        ResolvedSource source = read(path);
        if (source == null || source.text == null || source.text.isBlank()) {
            logError("Missing config: " + path);
            return new MiniGameMenuConfig().sanitize();
        }
        try {
            Gson gson = new GsonBuilder().create();
            MiniGameMenuConfig config = gson.fromJson(source.text, MiniGameMenuConfig.class);
            if (config == null) {
                logError("Could not parse " + source.origin);
                return new MiniGameMenuConfig().sanitize();
            }
            log("Loaded MiniGame menu config from " + source.origin);
            return config.sanitize();
        } catch (Exception exception) {
            logError("Failed to load " + source.origin + ": " + exception.getMessage());
            return new MiniGameMenuConfig().sanitize();
        }
    }

    private static ResolvedSource read(String path) {
        try {
            if (Gdx.files != null) {
                FileHandle file = InternalFiles.resolve(path);
                if (file != null && file.exists()) {
                    return new ResolvedSource(file.readString("UTF-8"), file.path());
                }
            }
        } catch (Throwable exception) {
            logError("Could not read " + path + " through Gdx: " + exception.getMessage());
        }
        File file = ConfigFiles.resolve(path);
        if (file == null || !file.isFile()) {
            return null;
        }
        try {
            return new ResolvedSource(Files.readString(file.toPath(), StandardCharsets.UTF_8), file.getAbsolutePath());
        } catch (Exception exception) {
            logError("Could not read " + file.getAbsolutePath() + ": " + exception.getMessage());
            return null;
        }
    }

    private static void log(String message) {
        if (Gdx.app != null) {
            Gdx.app.log("MiniGameMenuConfigLoader", message);
        }
    }

    private static void logError(String message) {
        if (Gdx.app != null) {
            Gdx.app.error("MiniGameMenuConfigLoader", message);
        }
    }

    private record ResolvedSource(String text, String origin) {
    }
}

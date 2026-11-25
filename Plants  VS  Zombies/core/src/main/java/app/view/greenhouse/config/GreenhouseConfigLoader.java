package app.view.greenhouse.config;

import app.service.ConfigFiles;
import app.view.loading.InternalFiles;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;

public final class GreenhouseConfigLoader {
    public static final String CONFIG_PATH = "ui/greenhouse.json";

    private GreenhouseConfigLoader() {
    }

    public static GreenhouseConfig load() {
        return load(CONFIG_PATH);
    }

    public static GreenhouseConfig load(String path) {
        GreenhouseConfig fromGdx = loadWithGdx(path);
        if (fromGdx != null) {
            return fromGdx;
        }
        return loadWithFile(path);
    }

    private static GreenhouseConfig loadWithGdx(String path) {
        try {
            if (Gdx.files == null) {
                return null;
            }
            FileHandle file = InternalFiles.resolve(path);
            if (file == null || !file.exists()) {
                Gdx.app.error("GreenhouseConfigLoader", "Missing config: " + path);
                return null;
            }
            Json json = new Json();
            json.setIgnoreUnknownFields(true);
            json.setElementType(GreenhouseConfig.class, "potOffsets", GreenhouseConfig.PotOffsetConfig.class);
            json.setElementType(GreenhouseConfig.GridConfig.class, "unlockCosts", Integer.class);
            GreenhouseConfig config = json.fromJson(GreenhouseConfig.class, file);
            if (config == null) {
                Gdx.app.error("GreenhouseConfigLoader", "Could not parse " + path);
                return null;
            }
            return config.sanitize();
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static GreenhouseConfig loadWithFile(String path) {
        File file = ConfigFiles.resolve(path);
        if (file == null || !file.isFile()) {
            return new GreenhouseConfig().sanitize();
        }
        try (FileReader reader = new FileReader(file)) {
            Gson gson = new GsonBuilder().create();
            GreenhouseConfig config = gson.fromJson(reader, GreenhouseConfig.class);
            if (config == null) {
                return new GreenhouseConfig().sanitize();
            }
            return config.sanitize();
        } catch (Exception exception) {
            return new GreenhouseConfig().sanitize();
        }
    }
}

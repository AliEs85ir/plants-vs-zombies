package app.view.greenhouse.config;

import app.service.ConfigFiles;
import app.view.loading.InternalFiles;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileReader;

public final class GreenhousePlantVisualsConfigLoader {
    public static final String CONFIG_PATH = "ui/greenhouse_plants.json";

    private GreenhousePlantVisualsConfigLoader() {
    }

    public static GreenhousePlantVisualsConfig load() {
        return load(CONFIG_PATH);
    }

    public static GreenhousePlantVisualsConfig load(String path) {
        GreenhousePlantVisualsConfig fromFile = loadWithFile(path);
        if (fromFile != null) {
            return fromFile;
        }
        return loadWithGdx(path);
    }

    private static GreenhousePlantVisualsConfig loadWithFile(String path) {
        File file = ConfigFiles.resolve(path);
        if (file == null || !file.isFile()) {
            return null;
        }
        try (FileReader reader = new FileReader(file)) {
            Gson gson = new GsonBuilder().create();
            GreenhousePlantVisualsConfig config = gson.fromJson(reader, GreenhousePlantVisualsConfig.class);
            if (config == null) {
                return new GreenhousePlantVisualsConfig().sanitize();
            }
            return config.sanitize();
        } catch (Exception exception) {
            return new GreenhousePlantVisualsConfig().sanitize();
        }
    }

    private static GreenhousePlantVisualsConfig loadWithGdx(String path) {
        try {
            if (Gdx.files == null) {
                return new GreenhousePlantVisualsConfig().sanitize();
            }
            FileHandle file = InternalFiles.resolve(path);
            if (file == null || !file.exists()) {
                Gdx.app.error("GreenhousePlantVisualsConfigLoader", "Missing config: " + path);
                return new GreenhousePlantVisualsConfig().sanitize();
            }
            Gson gson = new GsonBuilder().create();
            GreenhousePlantVisualsConfig config = gson.fromJson(file.readString(), GreenhousePlantVisualsConfig.class);
            if (config == null) {
                return new GreenhousePlantVisualsConfig().sanitize();
            }
            return config.sanitize();
        } catch (Throwable ignored) {
            return new GreenhousePlantVisualsConfig().sanitize();
        }
    }
}

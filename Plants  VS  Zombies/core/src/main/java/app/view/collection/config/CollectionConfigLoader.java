package app.view.collection.config;

import app.service.ConfigFiles;
import app.view.loading.InternalFiles;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;

public final class CollectionConfigLoader {
    public static final String CONFIG_PATH = "ui/collection.json";
    private static final Gson GSON = new GsonBuilder().create();

    private CollectionConfigLoader() {
    }

    public static CollectionConfig load() {
        return load(CONFIG_PATH);
    }

    public static CollectionConfig load(String path) {
        CollectionConfig fromGdx = loadWithGdx(path);
        if (fromGdx != null) {
            return fromGdx;
        }
        return loadWithFile(path);
    }

    private static CollectionConfig loadWithGdx(String path) {
        try {
            if (Gdx.files == null) {
                return null;
            }
            FileHandle file = InternalFiles.resolve(path);
            if (file == null || !file.exists()) {
                Gdx.app.error("CollectionConfigLoader", "Missing config: " + path);
                return null;
            }
            CollectionConfig config = GSON.fromJson(file.readString("UTF-8"), CollectionConfig.class);
            if (config == null) {
                return null;
            }
            config.sanitize();
            Gdx.app.log(
                "CollectionConfigLoader",
                "Loaded " + file.path()
                    + " levelFontScale=" + config.card.levelFontScale
                    + " title=" + config.colors.title
                    + " body=" + config.colors.body
                    + " statTitle=" + config.colors.statTitle
                    + " currentValue=" + config.colors.currentValue
            );
            return config;
        } catch (Throwable exception) {
            if (Gdx.app != null) {
                Gdx.app.error("CollectionConfigLoader", "Failed to parse " + path, exception);
            }
            return null;
        }
    }

    private static CollectionConfig loadWithFile(String path) {
        File file = ConfigFiles.resolve(path);
        if (file == null || !file.isFile()) {
            return new CollectionConfig().sanitize();
        }
        try (FileReader reader = new FileReader(file)) {
            CollectionConfig config = GSON.fromJson(reader, CollectionConfig.class);
            return config == null ? new CollectionConfig().sanitize() : config.sanitize();
        } catch (Exception exception) {
            return new CollectionConfig().sanitize();
        }
    }
}

package app.view.loading.icons.config;

import app.view.loading.InternalFiles;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

public final class StartIconsConfigLoader {
    public static final String CONFIG_PATH = "ui/start_icons.json";

    private StartIconsConfigLoader() {
    }

    public static StartIconsConfig load() {
        return load(CONFIG_PATH);
    }

    public static StartIconsConfig load(String path) {
        FileHandle file = InternalFiles.resolve(path);
        if (!file.exists()) {
            Gdx.app.error("StartIconsConfigLoader", "Missing config: " + path);
            return new StartIconsConfig().sanitize();
        }
        try {
            Json json = new Json();
            json.setIgnoreUnknownFields(true);
            json.setElementType(StartIconsConfig.class, "icons", StartIconsConfig.IconConfig.class);
            StartIconsConfig config = json.fromJson(StartIconsConfig.class, file);
            if (config == null) {
                Gdx.app.error("StartIconsConfigLoader", "Could not parse " + path);
                return new StartIconsConfig().sanitize();
            }
            return config.sanitize();
        } catch (Exception exception) {
            Gdx.app.error("StartIconsConfigLoader", "Failed to load " + path, exception);
            return new StartIconsConfig().sanitize();
        }
    }
}

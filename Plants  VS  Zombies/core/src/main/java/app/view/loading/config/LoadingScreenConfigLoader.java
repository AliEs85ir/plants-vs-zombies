package app.view.loading.config;

import app.view.loading.InternalFiles;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

public final class LoadingScreenConfigLoader {
    public static final String CONFIG_PATH = "ui/loading_screen.json";

    private LoadingScreenConfigLoader() {
    }

    public static LoadingScreenConfig load() {
        FileHandle file = InternalFiles.resolve(CONFIG_PATH);
        if (!file.exists()) {
            Gdx.app.error("LoadingScreenConfigLoader", "Missing config: " + CONFIG_PATH);
            return new LoadingScreenConfig().sanitize();
        }
        try {
            Json json = new Json();
            json.setIgnoreUnknownFields(true);
            LoadingScreenConfig config = json.fromJson(LoadingScreenConfig.class, file);
            if (config == null) {
                Gdx.app.error("LoadingScreenConfigLoader", "Could not parse " + CONFIG_PATH);
                return new LoadingScreenConfig().sanitize();
            }
            return config.sanitize();
        } catch (Exception exception) {
            Gdx.app.error("LoadingScreenConfigLoader", "Failed to load " + CONFIG_PATH, exception);
            return new LoadingScreenConfig().sanitize();
        }
    }
}

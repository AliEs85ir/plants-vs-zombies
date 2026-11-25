package app.view.mainmenu.config;

import app.view.loading.InternalFiles;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

public final class MainMenuConfigLoader {
    public static final String CONFIG_PATH = "ui/main_menu.json";

    private MainMenuConfigLoader() {
    }

    public static MainMenuConfig load() {
        return load(CONFIG_PATH);
    }

    public static MainMenuConfig load(String path) {
        FileHandle file = InternalFiles.resolve(path);
        if (file == null || !file.exists()) {
            Gdx.app.error("MainMenuConfigLoader", "Missing config: " + path);
            return new MainMenuConfig().sanitize();
        }
        try {
            Json json = new Json();
            json.setIgnoreUnknownFields(true);
            json.setElementType(MainMenuConfig.NavigationConfig.class, "icons", MainMenuConfig.NavIconConfig.class);
            json.setElementType(MainMenuConfig.GameMenuConfig.class, "cards", MainMenuConfig.GameCardConfig.class);
            MainMenuConfig config = json.fromJson(MainMenuConfig.class, file);
            if (config == null) {
                Gdx.app.error("MainMenuConfigLoader", "Could not parse " + path);
                return new MainMenuConfig().sanitize();
            }
            return config.sanitize();
        } catch (Exception exception) {
            Gdx.app.error("MainMenuConfigLoader", "Failed to load " + path, exception);
            return new MainMenuConfig().sanitize();
        }
    }
}

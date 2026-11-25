package app.view.mainmenu.settings;

import app.view.loading.InternalFiles;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

public final class SettingsMenuConfigLoader {
    public static final String CONFIG_PATH = "ui/settings_menu.json";

    private SettingsMenuConfigLoader() {
    }

    public static SettingsMenuConfig load() {
        return load(CONFIG_PATH);
    }

    public static SettingsMenuConfig load(String path) {
        FileHandle file = InternalFiles.resolve(path);
        if (file == null || !file.exists()) {
            Gdx.app.error("SettingsMenuConfigLoader", "Missing config: " + path);
            return new SettingsMenuConfig().sanitize();
        }
        try {
            Json json = new Json();
            json.setIgnoreUnknownFields(true);
            SettingsMenuConfig config = json.fromJson(SettingsMenuConfig.class, file);
            if (config == null) {
                Gdx.app.error("SettingsMenuConfigLoader", "Could not parse " + path);
                return new SettingsMenuConfig().sanitize();
            }
            return config.sanitize();
        } catch (Exception exception) {
            Gdx.app.error("SettingsMenuConfigLoader", "Failed to load " + path, exception);
            return new SettingsMenuConfig().sanitize();
        }
    }
}

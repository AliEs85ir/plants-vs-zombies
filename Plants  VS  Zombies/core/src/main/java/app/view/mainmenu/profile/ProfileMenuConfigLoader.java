package app.view.mainmenu.profile;

import app.view.loading.InternalFiles;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

public final class ProfileMenuConfigLoader {
    public static final String CONFIG_PATH = "ui/profile_menu.json";

    private ProfileMenuConfigLoader() {
    }

    public static ProfileMenuConfig load() {
        return load(CONFIG_PATH);
    }

    public static ProfileMenuConfig load(String path) {
        FileHandle file = InternalFiles.resolve(path);
        if (file == null || !file.exists()) {
            Gdx.app.error("ProfileMenuConfigLoader", "Missing config: " + path);
            return new ProfileMenuConfig().sanitize();
        }
        try {
            Json json = new Json();
            json.setIgnoreUnknownFields(true);
            ProfileMenuConfig config = json.fromJson(ProfileMenuConfig.class, file);
            if (config == null) {
                Gdx.app.error("ProfileMenuConfigLoader", "Could not parse " + path);
                return new ProfileMenuConfig().sanitize();
            }
            return config.sanitize();
        } catch (Exception exception) {
            Gdx.app.error("ProfileMenuConfigLoader", "Failed to load " + path, exception);
            return new ProfileMenuConfig().sanitize();
        }
    }
}

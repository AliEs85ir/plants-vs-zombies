package app.view.auth;

import app.view.loading.InternalFiles;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

public final class AuthMenuConfigLoader {
    public static final String CONFIG_PATH = "ui/auth_menu.json";

    private AuthMenuConfigLoader() {
    }

    public static AuthMenuConfig load() {
        return load(CONFIG_PATH);
    }

    public static AuthMenuConfig load(String path) {
        FileHandle file = InternalFiles.resolve(path);
        if (file == null || !file.exists()) {
            Gdx.app.error("AuthMenuConfigLoader", "Missing config: " + path);
            return new AuthMenuConfig().sanitize();
        }
        try {
            Json json = new Json();
            json.setIgnoreUnknownFields(true);
            AuthMenuConfig config = json.fromJson(AuthMenuConfig.class, file);
            if (config == null) {
                Gdx.app.error("AuthMenuConfigLoader", "Could not parse " + path);
                return new AuthMenuConfig().sanitize();
            }
            return config.sanitize();
        } catch (Exception exception) {
            Gdx.app.error("AuthMenuConfigLoader", "Failed to load " + path, exception);
            return new AuthMenuConfig().sanitize();
        }
    }
}

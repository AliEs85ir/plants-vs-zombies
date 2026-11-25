package app.view.adventure;

import app.view.loading.InternalFiles;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

public final class AdventureHudConfigLoader {
    public static final String CONFIG_PATH = "ui/adventure_hud.json";

    private AdventureHudConfigLoader() {
    }

    public static AdventureHudConfig load() {
        return load(CONFIG_PATH);
    }

    public static AdventureHudConfig load(String path) {
        FileHandle file = InternalFiles.resolve(path);
        if (file == null || !file.exists()) {
            Gdx.app.error("AdventureHudConfigLoader", "Missing config: " + path);
            return new AdventureHudConfig().sanitize();
        }
        try {
            Json json = new Json();
            json.setIgnoreUnknownFields(true);
            json.setElementType(AdventureHudConfig.ResourceHudConfig.class, "extra", AdventureHudConfig.ResourceKindConfig.class);
            AdventureHudConfig config = json.fromJson(AdventureHudConfig.class, file);
            if (config == null) {
                Gdx.app.error("AdventureHudConfigLoader", "Could not parse " + path);
                return new AdventureHudConfig().sanitize();
            }
            return config.sanitize();
        } catch (Exception exception) {
            Gdx.app.error("AdventureHudConfigLoader", "Failed to load " + path, exception);
            return new AdventureHudConfig().sanitize();
        }
    }
}

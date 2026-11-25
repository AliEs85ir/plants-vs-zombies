package app.view.adventure;

import app.view.loading.InternalFiles;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

public final class AdventureMenuConfigLoader {
    public static final String CONFIG_PATH = "ui/adventure_menu.json";

    private AdventureMenuConfigLoader() {
    }

    public static AdventureMenuConfig load() {
        return load(CONFIG_PATH);
    }

    public static AdventureMenuConfig load(String path) {
        FileHandle file = InternalFiles.resolve(path);
        if (file == null || !file.exists()) {
            Gdx.app.error("AdventureMenuConfigLoader", "Missing config: " + path);
            return new AdventureMenuConfig().sanitize();
        }
        try {
            Json json = new Json();
            json.setIgnoreUnknownFields(true);
            json.setElementType(AdventureMenuConfig.class, "chapters", AdventureMenuConfig.ChapterConfig.class);
            AdventureMenuConfig config = json.fromJson(AdventureMenuConfig.class, file);
            if (config == null) {
                Gdx.app.error("AdventureMenuConfigLoader", "Could not parse " + path);
                return new AdventureMenuConfig().sanitize();
            }
            return config.sanitize();
        } catch (Exception exception) {
            Gdx.app.error("AdventureMenuConfigLoader", "Failed to load " + path, exception);
            return new AdventureMenuConfig().sanitize();
        }
    }
}

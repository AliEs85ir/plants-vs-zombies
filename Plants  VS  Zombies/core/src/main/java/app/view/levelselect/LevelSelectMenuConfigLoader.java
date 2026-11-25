package app.view.levelselect;

import app.view.loading.InternalFiles;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

public final class LevelSelectMenuConfigLoader {
    public static final String CONFIG_PATH = "ui/level_select_menu.json";

    private LevelSelectMenuConfigLoader() {
    }

    public static LevelSelectMenuConfig load() {
        return load(CONFIG_PATH);
    }

    public static LevelSelectMenuConfig load(String path) {
        FileHandle file = InternalFiles.resolve(path);
        if (file == null || !file.exists()) {
            Gdx.app.error("LevelSelectMenuConfigLoader", "Missing config: " + path);
            return new LevelSelectMenuConfig().sanitize();
        }
        try {
            Json json = new Json();
            json.setIgnoreUnknownFields(true);
            json.setElementType(LevelSelectMenuConfig.class, "chapters", LevelSelectMenuConfig.ChapterConfig.class);
            LevelSelectMenuConfig config = json.fromJson(LevelSelectMenuConfig.class, file);
            if (config == null) {
                Gdx.app.error("LevelSelectMenuConfigLoader", "Could not parse " + path);
                return new LevelSelectMenuConfig().sanitize();
            }
            return config.sanitize();
        } catch (Exception exception) {
            Gdx.app.error("LevelSelectMenuConfigLoader", "Failed to load " + path, exception);
            return new LevelSelectMenuConfig().sanitize();
        }
    }
}

package app.view.mainmenu.news;

import app.view.loading.InternalFiles;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

public final class NewsMenuConfigLoader {
    public static final String CONFIG_PATH = "ui/news_menu.json";

    private NewsMenuConfigLoader() {
    }

    public static NewsMenuConfig load() {
        return load(CONFIG_PATH);
    }

    public static NewsMenuConfig load(String path) {
        FileHandle file = InternalFiles.resolve(path);
        if (file == null || !file.exists()) {
            Gdx.app.error("NewsMenuConfigLoader", "Missing config: " + path);
            return new NewsMenuConfig().sanitize();
        }
        try {
            Json json = new Json();
            json.setIgnoreUnknownFields(true);
            NewsMenuConfig config = json.fromJson(NewsMenuConfig.class, file);
            if (config == null) {
                Gdx.app.error("NewsMenuConfigLoader", "Could not parse " + path);
                return new NewsMenuConfig().sanitize();
            }
            return config.sanitize();
        } catch (Exception exception) {
            Gdx.app.error("NewsMenuConfigLoader", "Failed to load " + path, exception);
            return new NewsMenuConfig().sanitize();
        }
    }
}

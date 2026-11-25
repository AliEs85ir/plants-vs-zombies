package app.view.network;

import app.view.loading.InternalFiles;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

public final class NetworkMenuConfigLoader {
    public static final String CONFIG_PATH = "ui/network_menu.json";

    private NetworkMenuConfigLoader() {
    }

    public static NetworkMenuConfig load() {
        return load(CONFIG_PATH);
    }

    public static NetworkMenuConfig load(String path) {
        FileHandle file = InternalFiles.resolve(path);
        if (file == null || !file.exists()) {
            Gdx.app.error("NetworkMenuConfigLoader", "Missing config: " + path);
            return new NetworkMenuConfig().sanitize();
        }
        try {
            Json json = new Json();
            json.setIgnoreUnknownFields(true);
            json.setElementType(NetworkMenuConfig.MinigamesConfig.class, "games", NetworkMenuConfig.GameConfig.class);
            json.setElementType(NetworkMenuConfig.RankingConfig.class, "entries", NetworkMenuConfig.RankEntryConfig.class);
            NetworkMenuConfig config = json.fromJson(NetworkMenuConfig.class, file);
            if (config == null) {
                Gdx.app.error("NetworkMenuConfigLoader", "Could not parse " + path);
                return new NetworkMenuConfig().sanitize();
            }
            return config.sanitize();
        } catch (Exception exception) {
            Gdx.app.error("NetworkMenuConfigLoader", "Failed to load " + path, exception);
            return new NetworkMenuConfig().sanitize();
        }
    }
}

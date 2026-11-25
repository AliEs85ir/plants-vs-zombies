package app.view.shop.config;

import app.service.ConfigFiles;
import app.view.loading.InternalFiles;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;

public final class ShopConfigLoader {
    public static final String CONFIG_PATH = "ui/shop.json";

    private ShopConfigLoader() {
    }

    public static ShopConfig load() {
        return load(CONFIG_PATH);
    }

    public static ShopConfig load(String path) {
        ShopConfig fromGdx = loadWithGdx(path);
        if (fromGdx != null) {
            return fromGdx;
        }
        return loadWithFile(path);
    }

    private static ShopConfig loadWithGdx(String path) {
        try {
            if (Gdx.files == null) {
                return null;
            }
            FileHandle file = InternalFiles.resolve(path);
            if (file == null || !file.exists()) {
                Gdx.app.error("ShopConfigLoader", "Missing config: " + path);
                return null;
            }
            Json json = new Json();
            json.setIgnoreUnknownFields(true);
            ShopConfig config = json.fromJson(ShopConfig.class, file);
            return config == null ? null : config.sanitize();
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static ShopConfig loadWithFile(String path) {
        File file = ConfigFiles.resolve(path);
        if (file == null || !file.isFile()) {
            return new ShopConfig().sanitize();
        }
        try (FileReader reader = new FileReader(file)) {
            Gson gson = new GsonBuilder().create();
            ShopConfig config = gson.fromJson(reader, ShopConfig.class);
            return config == null ? new ShopConfig().sanitize() : config.sanitize();
        } catch (Exception exception) {
            return new ShopConfig().sanitize();
        }
    }
}

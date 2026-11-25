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

public final class ShopCatalogConfigLoader {
    public static final String CONFIG_PATH = "ui/shop_catalog.json";

    private ShopCatalogConfigLoader() {
    }

    public static ShopCatalogConfig load() {
        return load(CONFIG_PATH);
    }

    public static ShopCatalogConfig load(String path) {
        ShopCatalogConfig fromGdx = loadWithGdx(path);
        if (fromGdx != null) {
            return fromGdx;
        }
        return loadWithFile(path);
    }

    private static ShopCatalogConfig loadWithGdx(String path) {
        try {
            if (Gdx.files == null) {
                return null;
            }
            FileHandle file = InternalFiles.resolve(path);
            if (file == null || !file.exists()) {
                Gdx.app.error("ShopCatalogConfigLoader", "Missing config: " + path);
                return null;
            }
            Json json = new Json();
            json.setIgnoreUnknownFields(true);
            ShopCatalogConfig config = json.fromJson(ShopCatalogConfig.class, file);
            if (config == null) {
                return null;
            }
            return config.sanitize();
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static ShopCatalogConfig loadWithFile(String path) {
        File file = ConfigFiles.resolve(path);
        if (file == null || !file.isFile()) {
            return new ShopCatalogConfig().sanitize();
        }
        try (FileReader reader = new FileReader(file)) {
            Gson gson = new GsonBuilder().create();
            ShopCatalogConfig config = gson.fromJson(reader, ShopCatalogConfig.class);
            if (config == null) {
                return new ShopCatalogConfig().sanitize();
            }
            return config.sanitize();
        } catch (Exception exception) {
            return new ShopCatalogConfig().sanitize();
        }
    }
}

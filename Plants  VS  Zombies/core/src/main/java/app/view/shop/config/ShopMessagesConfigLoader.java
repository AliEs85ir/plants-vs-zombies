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

public final class ShopMessagesConfigLoader {
    public static final String CONFIG_PATH = "ui/shop_messages.json";

    private ShopMessagesConfigLoader() {
    }

    public static ShopMessagesConfig load() {
        return load(CONFIG_PATH);
    }

    public static ShopMessagesConfig load(String path) {
        ShopMessagesConfig fromGdx = loadWithGdx(path);
        if (fromGdx != null) {
            return fromGdx;
        }
        return loadWithFile(path);
    }

    private static ShopMessagesConfig loadWithGdx(String path) {
        try {
            if (Gdx.files == null) {
                return null;
            }
            FileHandle file = InternalFiles.resolve(path);
            if (file == null || !file.exists()) {
                return null;
            }
            Json json = new Json();
            json.setIgnoreUnknownFields(true);
            ShopMessagesConfig config = json.fromJson(ShopMessagesConfig.class, file);
            return config == null ? null : config.sanitize();
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static ShopMessagesConfig loadWithFile(String path) {
        File file = ConfigFiles.resolve(path);
        if (file == null || !file.isFile()) {
            return new ShopMessagesConfig().sanitize();
        }
        try (FileReader reader = new FileReader(file)) {
            Gson gson = new GsonBuilder().create();
            ShopMessagesConfig config = gson.fromJson(reader, ShopMessagesConfig.class);
            return config == null ? new ShopMessagesConfig().sanitize() : config.sanitize();
        } catch (Exception exception) {
            return new ShopMessagesConfig().sanitize();
        }
    }
}

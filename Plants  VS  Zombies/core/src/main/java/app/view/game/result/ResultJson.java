package app.view.game.result;

import app.service.ConfigFiles;
import app.view.loading.InternalFiles;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

final class ResultJson {
    private static final Map<String, Object> CACHE = new HashMap<>();

    private ResultJson() {
    }

    @SuppressWarnings("unchecked")
    static <T> T loadCached(String path, Class<T> type, Supplier<T> fallback) {
        Object cached = CACHE.get(path);
        if (type.isInstance(cached)) {
            return (T) cached;
        }
        T loaded = load(path, type, fallback);
        CACHE.put(path, loaded);
        return loaded;
    }

    static <T> T load(String path, Class<T> type, Supplier<T> fallback) {
        T fromGdx = loadWithGdx(path, type);
        if (fromGdx != null) {
            return fromGdx;
        }
        T fromFile = loadWithFile(path, type);
        if (fromFile != null) {
            return fromFile;
        }
        return fallback.get();
    }

    private static <T> T loadWithGdx(String path, Class<T> type) {
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
            return json.fromJson(type, file);
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static <T> T loadWithFile(String path, Class<T> type) {
        File file = ConfigFiles.resolve(path);
        if (file == null || !file.isFile()) {
            return null;
        }
        try (FileReader reader = new FileReader(file)) {
            Gson gson = new GsonBuilder().create();
            return gson.fromJson(reader, type);
        } catch (Exception ignored) {
            return null;
        }
    }
}

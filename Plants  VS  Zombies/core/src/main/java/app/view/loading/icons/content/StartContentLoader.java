package app.view.loading.icons.content;

import app.view.loading.InternalFiles;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

public final class StartContentLoader {
    private StartContentLoader() {
    }

    public static <T> T load(String path, Class<T> type, Class<?> listElementType, String listField, T fallback) {
        FileHandle file = InternalFiles.resolve(path);
        if (!file.exists()) {
            Gdx.app.error("StartContentLoader", "Missing content: " + path);
            return fallback;
        }
        try {
            Json json = new Json();
            json.setIgnoreUnknownFields(true);
            if (listElementType != null && listField != null) {
                json.setElementType(type, listField, listElementType);
            }
            T loaded = json.fromJson(type, file);
            return loaded != null ? loaded : fallback;
        } catch (Exception exception) {
            Gdx.app.error("StartContentLoader", "Failed to load " + path, exception);
            return fallback;
        }
    }
}

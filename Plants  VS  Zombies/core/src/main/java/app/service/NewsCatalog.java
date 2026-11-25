package app.service;

import app.model.News;
import app.view.loading.InternalFiles;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Optional static catalog. Event-generated newspaper articles are the source of truth.
 * Placeholder/demo articles are no longer injected into user progress.
 */
public final class NewsCatalog {
    public static final String CATALOG_PATH = "news/catalog.json";

    private NewsCatalog() {
    }

    public static List<News> load() {
        try (Reader reader = openReader()) {
            if (reader != null) {
                return fromJson(reader);
            }
        } catch (Exception exception) {
            log("Could not read news catalog: " + exception.getMessage());
        }
        return defaults();
    }

    private static Reader openReader() throws Exception {
        try {
            if (Gdx.files != null) {
                FileHandle handle = InternalFiles.resolve(CATALOG_PATH);
                if (handle != null && handle.exists()) {
                    return handle.reader("UTF-8");
                }
            }
        } catch (Throwable ignored) {
        }
        File local = new File("assets", CATALOG_PATH.replace('/', File.separatorChar));
        if (local.isFile()) {
            return Files.newBufferedReader(local.toPath(), StandardCharsets.UTF_8);
        }
        return null;
    }

    private static List<News> fromJson(Reader reader) {
        CatalogFile file = gson().fromJson(reader, CatalogFile.class);
        List<News> items = new ArrayList<>();
        if (file == null || file.items == null) {
            return defaults();
        }
        for (News news : file.items) {
            if (news == null) {
                continue;
            }
            news.sanitize();
            if (news.getUniqueKey() == null || news.getUniqueKey().isBlank()) {
                continue;
            }
            items.add(news);
        }
        return items;
    }

    /** Empty by design: newspaper content is event-driven. */
    public static List<News> defaults() {
        return new ArrayList<>();
    }

    private static Gson gson() {
        return new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, type, context) -> new JsonPrimitive(src.toString()))
            .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, type, context) -> LocalDateTime.parse(json.getAsString()))
            .create();
    }

    private static void log(String message) {
        try {
            if (Gdx.app != null) {
                Gdx.app.error("NewsCatalog", message);
                return;
            }
        } catch (Throwable ignored) {
        }
        System.err.println("NewsCatalog: " + message);
    }

    private static final class CatalogFile {
        @SerializedName("items")
        private List<News> items;
    }
}

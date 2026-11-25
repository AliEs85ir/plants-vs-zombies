package app.service.plant;

import app.service.ConfigFiles;
import app.view.loading.InternalFiles;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;

public final class PlantUnlockPriceCatalogLoader {
    public static final String CONFIG_PATH = "ui/collection_plant_unlock_prices.json";

    private static final Gson GSON = new GsonBuilder().create();
    private static PlantUnlockPriceCatalog cached;

    private PlantUnlockPriceCatalogLoader() {
    }

    public static PlantUnlockPriceCatalog get() {
        if (cached == null) {
            cached = load();
        }
        return cached;
    }

    public static void clearCache() {
        cached = null;
    }

    public static void overrideCatalog(PlantUnlockPriceCatalog catalog) {
        cached = catalog == null ? new PlantUnlockPriceCatalog().sanitize() : catalog.sanitize();
    }

    public static PlantUnlockPriceCatalog load() {
        PlantUnlockPriceCatalog fromGdx = loadWithGdx();
        if (fromGdx != null) {
            return fromGdx;
        }
        PlantUnlockPriceCatalog fromFile = loadWithFile();
        if (fromFile != null) {
            return fromFile;
        }
        return new PlantUnlockPriceCatalog().sanitize();
    }

    public static PlantUnlockPriceCatalog parse(String json) {
        if (json == null || json.isBlank()) {
            return new PlantUnlockPriceCatalog().sanitize();
        }
        try (Reader reader = new StringReader(json)) {
            PlantUnlockPriceCatalog catalog = GSON.fromJson(reader, PlantUnlockPriceCatalog.class);
            return catalog == null ? new PlantUnlockPriceCatalog().sanitize() : catalog.sanitize();
        } catch (Exception e) {
            return new PlantUnlockPriceCatalog().sanitize();
        }
    }

    private static PlantUnlockPriceCatalog loadWithGdx() {
        try {
            if (Gdx.files == null) {
                return null;
            }
            FileHandle file = InternalFiles.resolve(CONFIG_PATH);
            if (file == null || !file.exists()) {
                return null;
            }
            return parse(file.readString("UTF-8"));
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static PlantUnlockPriceCatalog loadWithFile() {
        File file = ConfigFiles.resolve(CONFIG_PATH);
        if (file == null || !file.isFile()) {
            return null;
        }
        try (FileReader reader = new FileReader(file)) {
            PlantUnlockPriceCatalog catalog = GSON.fromJson(reader, PlantUnlockPriceCatalog.class);
            return catalog == null ? new PlantUnlockPriceCatalog().sanitize() : catalog.sanitize();
        } catch (Exception e) {
            return new PlantUnlockPriceCatalog().sanitize();
        }
    }
}
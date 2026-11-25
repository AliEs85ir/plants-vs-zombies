package game.entity.plant.upgrade;

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

public final class PlantUpgradeConfigLoader {
    public static final String CONFIG_PATH = "plants/plant_upgrades.json";

    private static final Gson GSON = new GsonBuilder().create();
    private static PlantUpgradeCatalog cached;

    private PlantUpgradeConfigLoader() {
    }

    public static PlantUpgradeCatalog get() {
        if (cached == null) {
            cached = loadAndValidate();
        }
        return cached;
    }

    public static void overrideCatalog(PlantUpgradeCatalog catalog) {
        cached = catalog == null ? null : catalog.validate();
    }

    public static void clearCache() {
        cached = null;
    }

    public static PlantUpgradeCatalog parse(String json) {
        if (json == null || json.isBlank()) {
            throw new PlantUpgradeConfigException("plant_upgrades.json is empty.");
        }
        try (Reader reader = new StringReader(json)) {
            return parse(reader);
        } catch (PlantUpgradeConfigException e) {
            throw e;
        } catch (Exception e) {
            throw new PlantUpgradeConfigException("Failed to parse plant_upgrades.json: " + e.getMessage(), e);
        }
    }

    private static PlantUpgradeCatalog loadAndValidate() {
        PlantUpgradeCatalog fromGdx = loadWithGdx();
        if (fromGdx != null) {
            return fromGdx.validate();
        }
        PlantUpgradeCatalog fromFile = loadWithFile();
        if (fromFile != null) {
            return fromFile.validate();
        }
        throw new PlantUpgradeConfigException(
            "Could not load plant upgrade configuration from " + CONFIG_PATH + "."
        );
    }

    private static PlantUpgradeCatalog loadWithGdx() {
        try {
            if (Gdx.files == null) {
                return null;
            }
            FileHandle file = InternalFiles.resolve(CONFIG_PATH);
            if (file == null || !file.exists()) {
                return null;
            }
            return parse(file.readString("UTF-8"));
        } catch (PlantUpgradeConfigException e) {
            throw e;
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static PlantUpgradeCatalog loadWithFile() {
        File file = ConfigFiles.resolve(CONFIG_PATH);
        if (file == null || !file.isFile()) {
            return null;
        }
        try (FileReader reader = new FileReader(file)) {
            return parse(reader);
        } catch (PlantUpgradeConfigException e) {
            throw e;
        } catch (Exception e) {
            throw new PlantUpgradeConfigException(
                "Failed to read plant_upgrades.json: " + e.getMessage(),
                e
            );
        }
    }

    private static PlantUpgradeCatalog parse(Reader reader) {
        PlantUpgradeCatalog catalog = GSON.fromJson(reader, PlantUpgradeCatalog.class);
        if (catalog == null) {
            throw new PlantUpgradeConfigException("plant_upgrades.json did not contain a catalog.");
        }
        return catalog.validate();
    }
}

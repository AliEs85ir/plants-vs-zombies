package app.view.hud.elements.plantcard;

import app.enums.plant.PlantName;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import game.component.AssetManager;

import java.util.EnumMap;
import java.util.Map;

public class PlantCardConfigLoader {

    public static class PlantCardConfig {
        public String bgKey = "IMAGE_UI_PACKETS_MODERNDAY";
        public String textureKey = "";
        public float scale = 1.0f;
        public float offsetX = 0.0f;
        public float offsetY = 0.0f;
        public float cropLeft = 0.0f;
        public float cropRight = 0.0f;
        public float cropTop = 0.0f;
        public float cropBottom = 0.0f;

        public TextureRegion cachedBgRegion;
        public TextureRegion cachedPlantRegion;
    }

    private static final Map<PlantName, PlantCardConfig> CONFIG_MAP = new EnumMap<>(PlantName.class);
    private static boolean loaded = false;

    public static synchronized void load() {
        if (loaded) return;
        try {
            Json json = new Json();
            JsonValue root = json.fromJson(null, Gdx.files.internal("plants/plant_cards.json"));

            if (root != null) {
                for (JsonValue child = root.child; child != null; child = child.next) {
                    try {
                        PlantName plantName = PlantName.valueOf(child.name);
                        PlantCardConfig config = json.readValue(PlantCardConfig.class, child);

                        if (config.textureKey == null || config.textureKey.isEmpty()) {
                            config.textureKey = "IMAGE_UI_PACKETS_" + plantName.name();
                        }

                        prepareCachedRegions(config, plantName);
                        CONFIG_MAP.put(plantName, config);
                    } catch (IllegalArgumentException e) {
                        Gdx.app.error("PlantCardConfigLoader", "Invalid PlantName in JSON config: " + child.name);
                    }
                }
            }

            Gdx.app.log("PlantCardConfigLoader", "Successfully loaded configs for " + CONFIG_MAP.size() + " plants.");
        } catch (Exception e) {
            Gdx.app.error("PlantCardConfigLoader", "Failed to load plant_cards.json file!", e);
        }
        loaded = true;
    }

    private static void prepareCachedRegions(PlantCardConfig config, PlantName plantName) {
        config.cachedBgRegion = AssetManager.textureBank.region(config.bgKey);
        if (config.cachedBgRegion == null) {
            Gdx.app.error("PlantCardConfigLoader", "Missing background texture key: " + config.bgKey + " for " + plantName);
            config.cachedBgRegion = AssetManager.textureBank.region("IMAGE_UI_PACKETS_MODERNDAY");
        }

        TextureRegion rawPlant = AssetManager.textureBank.region(config.textureKey);
        if (rawPlant == null) {
            Gdx.app.error("PlantCardConfigLoader", "Missing plant texture key: " + config.textureKey + " for " + plantName);
            config.cachedPlantRegion = null;
            return;
        }

        if (config.cropLeft + config.cropRight >= 100f || config.cropTop + config.cropBottom >= 100f) {
            Gdx.app.error("PlantCardConfigLoader", "Invalid crop values (>100%) for plant: " + plantName);
            config.cachedPlantRegion = rawPlant;
            return;
        }

        int origX = rawPlant.getRegionX();
        int origY = rawPlant.getRegionY();
        int origW = rawPlant.getRegionWidth();
        int origH = rawPlant.getRegionHeight();

        int cutLeft = Math.round(origW * (config.cropLeft / 100f));
        int cutRight = Math.round(origW * (config.cropRight / 100f));
        int cutTop = Math.round(origH * (config.cropTop / 100f));
        int cutBottom = Math.round(origH * (config.cropBottom / 100f));

        int newW = Math.max(1, origW - cutLeft - cutRight);
        int newH = Math.max(1, origH - cutTop - cutBottom);

        config.cachedPlantRegion = new TextureRegion(
            rawPlant.getTexture(),
            origX + cutLeft,
            origY + cutTop,
            newW,
            newH
        );
    }

    public static PlantCardConfig getConfig(PlantName plantName) {
        if (!loaded) load();
        PlantCardConfig config = CONFIG_MAP.get(plantName);
        if (config == null) {
            Gdx.app.error("PlantCardConfigLoader", "No config found for " + plantName + ", fallback created.");
            config = new PlantCardConfig();
            config.bgKey = "IMAGE_UI_PACKETS_MODERNDAY";
            config.textureKey = "IMAGE_UI_PACKETS_" + plantName.name();
            prepareCachedRegions(config, plantName);
        }
        return config;
    }
}

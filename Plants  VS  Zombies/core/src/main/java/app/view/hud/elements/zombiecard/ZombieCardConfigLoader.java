package app.view.hud.elements.zombiecard;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import game.component.AssetManager;

import java.util.HashMap;
import java.util.Map;

public class ZombieCardConfigLoader {
    public static class ZombieCardConfig {
        public String bgKey = "IMAGE_UI_PACKETS_EMPTY_PACKET";
        public String textureKey = "";
        public float scale = 1.0f;
        public float offsetX = 0.0f;
        public float offsetY = 0.0f;
        public float cropLeft = 0.0f;
        public float cropRight = 0.0f;
        public float cropTop = 0.0f;
        public float cropBottom = 0.0f;

        public TextureRegion cachedBgRegion;
        public TextureRegion cachedZombieRegion;
    }

    private static final Map<String, ZombieCardConfig> CONFIG_MAP = new HashMap<>();
    private static boolean loaded = false;

    public static synchronized void load() {
        if (loaded) return;
        try {
            Json json = new Json();
            JsonValue root = json.fromJson(null, Gdx.files.internal("zombies/zombie_cards.json"));

            if (root != null) {
                for (JsonValue child = root.child; child != null; child = child.next) {
                    try {
                        String alias = child.name;
                        ZombieCardConfig config = json.readValue(ZombieCardConfig.class, child);


                        prepareCachedRegions(config, alias);
                        CONFIG_MAP.put(alias, config);
                    } catch (IllegalArgumentException e) {
                        Gdx.app.error("ZombieCardConfigLoader", "Invalid zombie name in JSON config: " + child.name);
                    }
                }
            }

            Gdx.app.log("ZombieCardConfigLoader", "Successfully loaded configs for " + CONFIG_MAP.size() + " zombies.");
        } catch (Exception e) {
            Gdx.app.error("ZombieCardConfigLoader", "Failed to load zombie_cards.json file!", e);
        }
        loaded = true;
    }

    private static void prepareCachedRegions(ZombieCardConfig config, String alias) {
        config.cachedBgRegion = AssetManager.textureBank.region(config.bgKey);
        if (config.cachedBgRegion == null) {
            Gdx.app.error("ZombieCardConfigLoader", "Missing background texture key: " + config.bgKey + " for " + alias);
            config.cachedBgRegion = AssetManager.textureBank.region("IMAGE_UI_PACKETS_MODERNDAY");
        }

        TextureRegion rawZombie = AssetManager.textureBank.region(config.textureKey);
        if (rawZombie == null) {
            Gdx.app.error("ZombieCardConfigLoader", "Missing zombie texture key: " + config.textureKey + " for " + alias);
            config.cachedZombieRegion = null;
            return;
        }

        if (config.cropLeft + config.cropRight >= 100f || config.cropTop + config.cropBottom >= 100f) {
            Gdx.app.error("PlantCardConfigLoader", "Invalid crop values (>100%) for zombie: " + alias);
            config.cachedZombieRegion = rawZombie;
            return;
        }

        int origX = rawZombie.getRegionX();
        int origY = rawZombie.getRegionY();
        int origW = rawZombie.getRegionWidth();
        int origH = rawZombie.getRegionHeight();

        int cutLeft = Math.round(origW * (config.cropLeft / 100f));
        int cutRight = Math.round(origW * (config.cropRight / 100f));
        int cutTop = Math.round(origH * (config.cropTop / 100f));
        int cutBottom = Math.round(origH * (config.cropBottom / 100f));

        int newW = Math.max(1, origW - cutLeft - cutRight);
        int newH = Math.max(1, origH - cutTop - cutBottom);

        config.cachedZombieRegion = new TextureRegion(
            rawZombie.getTexture(),
            origX + cutLeft,
            origY + cutTop,
            newW,
            newH
        );
    }

    public static ZombieCardConfig getConfig(String alias) {
        if (!loaded) load();
        ZombieCardConfig config = CONFIG_MAP.get(alias);
        if (config == null) {
            Gdx.app.error("PlantCardConfigLoader", "No config found for " + alias + ", fallback created.");
            config = new ZombieCardConfig();
            config.bgKey = "IMAGE_UI_PACKETS_MODERNDAY";
            config.textureKey = "IMAGE_UI_PACKETS_" + alias;
            prepareCachedRegions(config, alias);
        }
        return config;
    }
}

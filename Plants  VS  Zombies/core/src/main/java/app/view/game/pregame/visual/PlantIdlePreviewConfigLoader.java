package app.view.game.pregame.visual;

import app.enums.plant.PlantName;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import graphics.general.animation.PamPathResolver;
import graphics.general.animation.loader.PlantAnimationProfileLoader;
import graphics.plant.PlantAnimationProfile;

import java.util.EnumMap;
import java.util.Map;

public final class PlantIdlePreviewConfigLoader {

    public static class PlantIdlePreviewConfig {
        public String pamPath = "";
        public String clip = "idle";
        public boolean loop = true;
        public float scale = 1.0f;
        public float offsetX = 0.0f;
        public float offsetY = 0.0f;

        public PlantIdlePreviewConfig copy() {
            PlantIdlePreviewConfig copy = new PlantIdlePreviewConfig();
            copy.pamPath = pamPath;
            copy.clip = clip;
            copy.loop = loop;
            copy.scale = scale;
            copy.offsetX = offsetX;
            copy.offsetY = offsetY;
            return copy;
        }
    }

    private static final Map<PlantName, PlantIdlePreviewConfig> CONFIG_MAP =
        new EnumMap<>(PlantName.class);
    private static boolean loaded = false;

    private PlantIdlePreviewConfigLoader() {
    }

    public static synchronized void load() {
        if (loaded) {
            return;
        }
        try {
            Json json = new Json();
            JsonValue root = json.fromJson(null, Gdx.files.internal("plants/plant_idle_preview.json"));
            if (root != null) {
                for (JsonValue child = root.child; child != null; child = child.next) {
                    try {
                        PlantName plantName = PlantName.valueOf(child.name);
                        PlantIdlePreviewConfig config =
                            json.readValue(PlantIdlePreviewConfig.class, child);
                        fillFromAnimationProfile(plantName, config);
                        CONFIG_MAP.put(plantName, config);
                    } catch (IllegalArgumentException e) {
                        Gdx.app.error(
                            "PlantIdlePreviewConfigLoader",
                            "Invalid PlantName in plant_idle_preview.json: " + child.name
                        );
                    }
                }
            }
            Gdx.app.log(
                "PlantIdlePreviewConfigLoader",
                "Loaded idle preview configs for " + CONFIG_MAP.size() + " plants."
            );
        } catch (Exception e) {
            Gdx.app.error(
                "PlantIdlePreviewConfigLoader",
                "Failed to load plant_idle_preview.json",
                e
            );
        }
        loaded = true;
    }

    public static PlantIdlePreviewConfig getConfig(PlantName plantName) {
        if (!loaded) {
            load();
        }
        PlantIdlePreviewConfig config = CONFIG_MAP.get(plantName);
        if (config == null) {
            config = new PlantIdlePreviewConfig();
            fillFromAnimationProfile(plantName, config);
            CONFIG_MAP.put(plantName, config);
        }
        return config;
    }

    private static void fillFromAnimationProfile(PlantName plantName, PlantIdlePreviewConfig config) {
        PamPathResolver resolver = PamPathResolver.get();
        if (config.pamPath != null && !config.pamPath.isEmpty()) {
            String resolved = resolver.resolve(config.pamPath);
            if (resolved != null && !resolved.equals(config.pamPath)) {
                Gdx.app.log(
                    "PlantIdlePreviewConfigLoader",
                    "Resolved idle preview PAM for " + plantName + ": "
                        + config.pamPath + " -> " + resolved
                );
                config.pamPath = resolved;
            }
        }
        boolean needsPath = config.pamPath == null
            || config.pamPath.isEmpty()
            || !resolver.exists(config.pamPath);
        boolean needsClip = config.clip == null || config.clip.isEmpty();
        if (!needsPath && !needsClip) {
            return;
        }
        try {
            PlantAnimationProfile profile = PlantAnimationProfileLoader.loadFor(plantName);
            if (profile == null) {
                return;
            }
            if (needsPath && profile.getPamPath() != null && !profile.getPamPath().isEmpty()) {
                config.pamPath = profile.getPamPath();
            }
            if (needsClip) {
                config.clip = PlantAnimationProfileLoader.idleClipOf(profile);
            }
        } catch (Exception e) {
            Gdx.app.error(
                "PlantIdlePreviewConfigLoader",
                "Could not fill idle preview from animation profile for " + plantName,
                e
            );
        }
    }
}

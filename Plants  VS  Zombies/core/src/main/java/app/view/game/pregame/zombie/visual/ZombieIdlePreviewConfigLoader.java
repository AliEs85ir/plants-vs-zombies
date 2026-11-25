package app.view.game.pregame.zombie.visual;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import graphics.general.animation.loader.ZombieAnimationProfileLoader;
import graphics.zombie.ZombieAnimationProfile;
import graphics.zombie.ZombieAnimationVariant;
import graphics.zombie.ZombieVisualState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ZombieIdlePreviewConfigLoader {
    public static class ZombieIdlePreviewConfig {
        public String pamPath = "";
        public String clip = "idle";
        public boolean loop = true;
        public float scale = 1.0f;
        public float offsetX = 0.0f;
        public float offsetY = 0.0f;

        public ZombieIdlePreviewConfig copy() {
            ZombieIdlePreviewConfig copy = new ZombieIdlePreviewConfig();
            copy.pamPath = pamPath;
            copy.clip = clip;
            copy.loop = loop;
            copy.scale = scale;
            copy.offsetX = offsetX;
            copy.offsetY = offsetY;
            return copy;
        }
    }

    private static final Map<String, ZombieIdlePreviewConfigLoader.ZombieIdlePreviewConfig> CONFIG_MAP =
        new HashMap<>();
    private static boolean loaded = false;

    private ZombieIdlePreviewConfigLoader() {
    }

    public static synchronized void load() {
        if (loaded) {
            return;
        }
        try {
            Json json = new Json();
            JsonValue root = json.fromJson(null, Gdx.files.internal("zombies/zombie_idle_preview.json"));
            if (root != null) {
                for (JsonValue child = root.child; child != null; child = child.next) {
                    try {
                        String alias = child.name;
                        ZombieIdlePreviewConfigLoader.ZombieIdlePreviewConfig config =
                            json.readValue(ZombieIdlePreviewConfigLoader.ZombieIdlePreviewConfig.class, child);
                        fillFromAnimationProfile(alias, config);
                        CONFIG_MAP.put(alias, config);
                    } catch (IllegalArgumentException e) {
                        Gdx.app.error(
                            "ZombieIdlePreviewConfigLoader",
                            "Invalid ZombieName in zombie_idle_preview.json: " + child.name
                        );
                    }
                }
            }
            Gdx.app.log(
                "ZombieIdlePreviewConfigLoader",
                "Loaded idle preview configs for " + CONFIG_MAP.size() + " zombies."
            );
        } catch (Exception e) {
            Gdx.app.error(
                "ZombieIdlePreviewConfigLoader",
                "Failed to load plant_idle_preview.json",
                e
            );
        }
        loaded = true;
    }

    public static ZombieIdlePreviewConfigLoader.ZombieIdlePreviewConfig getConfig(String alias) {
        if (!loaded) {
            load();
        }
        ZombieIdlePreviewConfigLoader.ZombieIdlePreviewConfig config = CONFIG_MAP.get(alias);
        if (config == null) {
            config = new ZombieIdlePreviewConfigLoader.ZombieIdlePreviewConfig();
            fillFromAnimationProfile(alias, config);
            CONFIG_MAP.put(alias, config);
        }
        return config;
    }

    private static void fillFromAnimationProfile(String alias, ZombieIdlePreviewConfigLoader.ZombieIdlePreviewConfig config) {
        boolean needsPath = config.pamPath == null || config.pamPath.isEmpty();
        boolean needsClip = config.clip == null || config.clip.isEmpty();
        if (!needsPath && !needsClip) {
            return;
        }
        try {
            ZombieAnimationProfile profile = ZombieAnimationProfileLoader.loadSpecific(alias);
            if (profile == null) {
                return;
            }
            if (needsPath && profile.getPamPath() != null) {
                config.pamPath = profile.getPamPath();
            }
            if (needsClip) {
                List<ZombieAnimationVariant> idles = profile.getVariants(ZombieVisualState.IDLE);
                if (!idles.isEmpty()) {
                    config.clip = idles.get(0).getClipName();
                }
            }
        } catch (Exception e) {
            Gdx.app.error(
                "ZombieIdlePreviewConfigLoader",
                "Could not fill idle preview from animation profile for " + alias,
                e
            );
        }
    }
}

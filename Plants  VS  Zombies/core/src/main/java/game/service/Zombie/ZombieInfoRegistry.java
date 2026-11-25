package game.service.Zombie;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ZombieInfoRegistry {

    private static final String JSON_PATH = "zombies/zombie_info.json";
    private static final String FALLBACK_DESCRIPTION = "No description available yet.";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_RARITY = "rarity";

    private static final Map<String, ZombieInfo> REGISTRY = new HashMap<>();
    private static boolean loaded = false;

    private ZombieInfoRegistry() {}

    public static synchronized void load() {
        if (loaded) return;
        loaded = true;

        try {
            if (!Gdx.files.internal(JSON_PATH).exists()) {
                Gdx.app.error("ZombieInfoRegistry", "Missing asset: " + JSON_PATH);
                return;
            }

            Json json = new Json();
            JsonValue root = json.fromJson(null, Gdx.files.internal(JSON_PATH));
            if (root == null) return;

            for (JsonValue child = root.child; child != null; child = child.next) {
                try {
                    String alias = child.name;
                    REGISTRY.put(alias, parse(alias, child));
                } catch (IllegalArgumentException e) {
                    Gdx.app.error("ZombieInfoRegistry", "Unknown ZombieName in JSON: " + child.name);
                }
            }

            Gdx.app.log("ZombieInfoRegistry", "Loaded info for " + REGISTRY.size() + " zombies.");
        } catch (Exception e) {
            Gdx.app.error("ZombieInfoRegistry", "Failed to load " + JSON_PATH, e);
        }
    }

    private static ZombieInfo parse(String alias, JsonValue node) {
        String description = node.getString(KEY_DESCRIPTION, FALLBACK_DESCRIPTION);
        String rarity = node.getString(KEY_RARITY, "");

        Map<String, String> extras = new LinkedHashMap<>();
        for (JsonValue field = node.child; field != null; field = field.next) {
            if (KEY_DESCRIPTION.equals(field.name)
                || KEY_RARITY.equals(field.name)) continue;
            extras.put(field.name, field.asString());
        }

        return new ZombieInfo(alias, description, rarity, extras);
    }


    public static ZombieInfo get(String alias) {
        if (!loaded) load();
        ZombieInfo info = REGISTRY.get(alias);
        if (info == null) {
            info = new ZombieInfo(alias, FALLBACK_DESCRIPTION);
            REGISTRY.put(alias, info);
        }
        return info;
    }

    public static boolean has(String alias) {
        if (!loaded) load();
        return REGISTRY.containsKey(alias);
    }
}

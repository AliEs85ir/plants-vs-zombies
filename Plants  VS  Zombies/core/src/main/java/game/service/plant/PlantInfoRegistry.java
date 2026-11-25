package game.service.plant;

import app.enums.plant.PlantName;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import game.entity.plant.PlantDefinition;
import game.entity.plant.PlantInfo;
import game.entity.plant.PlantRole;
import game.entity.registry.PlantRegistry;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class PlantInfoRegistry {

    private static final String JSON_PATH = "plants/plant_info.json";
    private static final String FALLBACK_DESCRIPTION = "No description available yet.";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_RARITY = "rarity";
    private static final String KEY_ROLE = "role";
    private static final int HIGH_HEALTH_THRESHOLD = 400;

    private static final Map<PlantName, PlantInfo> REGISTRY = new EnumMap<>(PlantName.class);
    private static boolean loaded = false;

    private PlantInfoRegistry() {}

    public static synchronized void load() {
        if (loaded) return;
        loaded = true;

        try {
            if (!Gdx.files.internal(JSON_PATH).exists()) {
                Gdx.app.error("PlantInfoRegistry", "Missing asset: " + JSON_PATH);
                return;
            }

            Json json = new Json();
            JsonValue root = json.fromJson(null, Gdx.files.internal(JSON_PATH));
            if (root == null) return;

            for (JsonValue child = root.child; child != null; child = child.next) {
                try {
                    PlantName plantName = PlantName.valueOf(child.name);
                    REGISTRY.put(plantName, parse(plantName, child));
                } catch (IllegalArgumentException e) {
                    Gdx.app.error("PlantInfoRegistry", "Unknown PlantName in JSON: " + child.name);
                }
            }

            Gdx.app.log("PlantInfoRegistry", "Loaded info for " + REGISTRY.size() + " plants.");
        } catch (Exception e) {
            Gdx.app.error("PlantInfoRegistry", "Failed to load " + JSON_PATH, e);
        }
    }

    private static PlantInfo parse(PlantName plantName, JsonValue node) {
        String description = node.getString(KEY_DESCRIPTION, FALLBACK_DESCRIPTION);
        String rarity = node.getString(KEY_RARITY, "");
        PlantRole role = PlantRole.parse(node.getString(KEY_ROLE, null));
        if (role == null) {
            role = inferRole(plantName);
        }

        Map<String, String> extras = new LinkedHashMap<>();
        for (JsonValue field = node.child; field != null; field = field.next) {
            if (KEY_DESCRIPTION.equals(field.name)
                || KEY_RARITY.equals(field.name)
                || KEY_ROLE.equals(field.name)) continue;
            extras.put(field.name, field.asString());
        }

        return new PlantInfo(plantName, description, rarity, role, extras);
    }

    private static PlantRole inferRole(PlantName plantName) {
        PlantDefinition definition = PlantRegistry.get(plantName);
        if (definition == null) return PlantRole.OFFENSE;

        if (definition.getAttackDamage() <= 0) {
            return definition.getMaxHealth() >= HIGH_HEALTH_THRESHOLD
                ? PlantRole.DEFENSE : PlantRole.SUPPORT;
        }
        return PlantRole.OFFENSE;
    }

    public static PlantInfo get(PlantName plantName) {
        if (!loaded) load();
        PlantInfo info = REGISTRY.get(plantName);
        if (info == null) {
            info = new PlantInfo(plantName, FALLBACK_DESCRIPTION, "",
                                 inferRole(plantName), null);
            REGISTRY.put(plantName, info);
        }
        return info;
    }

    public static boolean has(PlantName plantName) {
        if (!loaded) load();
        return REGISTRY.containsKey(plantName);
    }
}

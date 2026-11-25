package game.entity.registry;

import app.enums.plant.PlantName;
import game.entity.plant.PlantDefinition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class PlantRegistry {

    private static final Map<PlantName, PlantDefinition> DEFINITIONS = new HashMap<>();

    private PlantRegistry() {
        PlantDefinitionLoader.load();
    }

    public static void register(PlantDefinition definition) {
        DEFINITIONS.put(definition.getName(), definition);
    }

    public static PlantDefinition get(PlantName name) {
        return DEFINITIONS.get(name);
    }

    public static ArrayList<PlantDefinition> getPlantDefinitions() {
        return new ArrayList<>(DEFINITIONS.values());
    }

    public static Map<PlantName, PlantDefinition> getDefinitions() {
        return Collections.unmodifiableMap(DEFINITIONS);
    }

    public static void clear() {
        DEFINITIONS.clear();
    }

    public static boolean contains(PlantName name) {
        return DEFINITIONS.containsKey(name);
    }
}

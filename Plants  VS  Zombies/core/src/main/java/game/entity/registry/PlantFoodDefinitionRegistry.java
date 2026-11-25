package game.entity.registry;

import app.enums.plant.PlantName;
import game.entity.plant.plantfood.PlantFoodDefinition;

import java.util.EnumMap;
import java.util.Map;

public final class PlantFoodDefinitionRegistry {
    private static final Map<PlantName, PlantFoodDefinition> DEFINITIONS = new EnumMap<>(PlantName.class);

    private PlantFoodDefinitionRegistry() {
    }

    public static void register(PlantFoodDefinition definition) {
        if (definition != null && definition.getPlantName() != null) {
            DEFINITIONS.put(definition.getPlantName(), definition);
        }
    }

    public static PlantFoodDefinition get(PlantName name) {
        return DEFINITIONS.get(name);
    }

    public static boolean isImplemented(PlantName name) {
        return name != null && DEFINITIONS.containsKey(name);
    }
}

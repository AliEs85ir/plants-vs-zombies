package game.service.plant;

import app.enums.plant.PlantName;
import game.entity.plant.ability.PlantAbility;
import game.entity.plant.ability.plantfood.AreaPulsePlantFoodAbility;
import game.entity.plant.ability.plantfood.DurationPlantFoodAbility;
import game.entity.plant.ability.plantfood.HealPlantFoodAbility;
import game.entity.plant.ability.plantfood.LaneDivertPlantFoodAbility;
import game.entity.plant.ability.plantfood.RapidFirePlantFoodAbility;
import game.entity.plant.ability.plantfood.SunBurstPlantFoodAbility;
import game.entity.plant.plantfood.PlantFoodDefinition;
import game.entity.registry.PlantFoodDefinitionRegistry;

public final class PlantFoodAbilityFactory {
    private PlantFoodAbilityFactory() {
    }

    public static PlantAbility create(PlantName name) {
        PlantFoodDefinition definition = PlantFoodDefinitionRegistry.get(name);
        if (definition == null) {
            return null;
        }
        return switch (definition.getAbilityType()) {
            case SUN_BURST, GROWTH_SUN_BURST -> new SunBurstPlantFoodAbility(definition);
            case RAPID_FIRE -> new RapidFirePlantFoodAbility(definition);
            case AREA_PULSE -> new AreaPulsePlantFoodAbility(definition);
            case HEAL -> new HealPlantFoodAbility(definition);
            case LANE_DIVERT -> new LaneDivertPlantFoodAbility(definition);
            case EMPOWER, FIRE_LINE -> new DurationPlantFoodAbility(definition);
        };
    }
}

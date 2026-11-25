package game.service.plant;

import app.enums.plant.PlantName;
import game.engine.time.GameClock;
import game.entity.plant.ability.ModifierProjectileAbility;
import game.entity.plant.ability.PlantAbility;
import game.entity.plant.ability.SunBeanAbility;
import game.entity.plant.ability.WallNutAbility;
import game.entity.plant.ability.plantfood.DurationPlantFoodAbility;
import game.entity.plant.ability.plantfood.HealPlantFoodAbility;
import game.entity.plant.plantfood.PlantFoodAbilityType;
import game.entity.plant.plantfood.PlantFoodDefinition;
import game.entity.plant.upgrade.PlantUpgradeConfigLoader;
import game.entity.registry.PlantDefinitionLoader;
import game.entity.registry.PlantFoodDefinitionLoader;
import game.entity.registry.PlantFoodDefinitionRegistry;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SunBeanTorchwoodAbilityWiringTest {
    @BeforeAll
    static void loadDefinitions() {
        PlantDefinitionLoader.load();
        PlantFoodDefinitionLoader.load();
        PlantUpgradeConfigLoader.get();
    }

    @Test
    void sunBeanWiresDamageSunAndHealPlantFood() {
        ArrayList<PlantAbility> abilities = AbilityFactory.getAbilities(PlantName.SUN_BEAN);
        assertTrue(abilities.stream().anyMatch(SunBeanAbility.class::isInstance));
        assertTrue(abilities.stream().anyMatch(HealPlantFoodAbility.class::isInstance));
        assertFalse(abilities.stream().anyMatch(WallNutAbility.class::isInstance));
        assertEquals(5, new SunBeanAbility().sunValue(null));
        assertTrue(SunBeanAbility.TOSS_DURATION_TICKS >= 3);
        assertTrue(SunBeanAbility.TOSS_DURATION_TICKS <= 5);
        PlantFoodDefinition definition = PlantFoodDefinitionRegistry.get(PlantName.SUN_BEAN);
        assertNotNull(definition);
        assertEquals(PlantFoodAbilityType.HEAL, definition.getAbilityType());
        assertTrue(definition.isRestoreFullHealth());
        assertInstanceOf(HealPlantFoodAbility.class, PlantFoodAbilityFactory.create(PlantName.SUN_BEAN));
    }

    @Test
    void torchwoodWiresGenericProjectileTransformAndSixSecondEmpower() {
        ArrayList<PlantAbility> abilities = AbilityFactory.getAbilities(PlantName.TORCHWOOD);
        assertTrue(abilities.stream().anyMatch(ModifierProjectileAbility.class::isInstance));
        assertTrue(abilities.stream().anyMatch(DurationPlantFoodAbility.class::isInstance));
        PlantFoodDefinition definition = PlantFoodDefinitionRegistry.get(PlantName.TORCHWOOD);
        assertNotNull(definition);
        assertEquals(PlantFoodAbilityType.EMPOWER, definition.getAbilityType());
        assertEquals(6 * GameClock.TICKS_PER_SECOND, definition.getDurationTicks());
        assertInstanceOf(DurationPlantFoodAbility.class, PlantFoodAbilityFactory.create(PlantName.TORCHWOOD));
    }
}

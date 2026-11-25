package game.service.plant;

import app.enums.plant.PlantName;
import game.entity.plant.HealthSegments;
import game.entity.plant.Plant;
import game.entity.plant.PlantHealth;
import game.entity.plant.ability.EatingMeleeAbility;
import game.entity.plant.ability.GarlicAbility;
import game.entity.plant.ability.PlantAbility;
import game.entity.plant.ability.WallNutAbility;
import game.entity.plant.ability.plantfood.HealPlantFoodAbility;
import game.entity.plant.ability.plantfood.LaneDivertPlantFoodAbility;
import game.entity.plant.plantfood.PlantFoodAbilityType;
import game.entity.plant.plantfood.PlantFoodDefinition;
import game.entity.plant.upgrade.PlantUpgradeConfigLoader;
import game.entity.registry.PlantDefinitionLoader;
import game.entity.registry.PlantFoodDefinitionLoader;
import game.entity.registry.PlantFoodDefinitionRegistry;
import graphics.plant.PlantConditions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefensiveNutAbilityWiringTest {
    @BeforeAll
    static void loadDefinitions() {
        PlantDefinitionLoader.load();
        PlantFoodDefinitionLoader.load();
        PlantUpgradeConfigLoader.get();
    }

    @Test
    void wallNutAndTallNutUseHealPlantFoodWithoutWallNutAbility() {
        ArrayList<PlantAbility> wallNut = AbilityFactory.getAbilities(PlantName.WALL_NUT);
        assertTrue(wallNut.stream().anyMatch(HealPlantFoodAbility.class::isInstance));
        assertFalse(wallNut.stream().anyMatch(WallNutAbility.class::isInstance));
        assertFalse(wallNut.stream().anyMatch(EatingMeleeAbility.class::isInstance));

        ArrayList<PlantAbility> tallNut = AbilityFactory.getAbilities(PlantName.TALL_NUT);
        assertTrue(tallNut.stream().anyMatch(HealPlantFoodAbility.class::isInstance));
        assertFalse(tallNut.stream().anyMatch(WallNutAbility.class::isInstance));
        assertFalse(tallNut.stream().anyMatch(EatingMeleeAbility.class::isInstance));
    }

    @Test
    void endurianUsesEatingMeleeAndHealPlantFood() {
        ArrayList<PlantAbility> abilities = AbilityFactory.getAbilities(PlantName.ENDURIAN);
        EatingMeleeAbility melee = abilities.stream()
            .filter(EatingMeleeAbility.class::isInstance)
            .map(EatingMeleeAbility.class::cast)
            .findFirst()
            .orElse(null);
        assertNotNull(melee);
        assertEquals(EatingMeleeAbility.ATTACK_INTERVAL_TICKS, melee.getAttackIntervalTicks());
        assertEquals(3, melee.getAttackIntervalTicks());
        assertEquals(EatingMeleeAbility.DAMAGE_DELAY_TICKS, melee.getDamageDelayTicks());
        assertEquals(1, melee.getDamageDelayTicks());
        assertTrue(abilities.stream().anyMatch(HealPlantFoodAbility.class::isInstance));
        assertFalse(abilities.stream().anyMatch(WallNutAbility.class::isInstance));
    }

    @Test
    void plantFoodHealAmountsAreRegisteredOnExistingDefinitionRegistry() {
        assertHeal(PlantName.WALL_NUT, 4000);
        assertHeal(PlantName.TALL_NUT, 8000);
        assertHeal(PlantName.ENDURIAN, 3000);
        assertInstanceOf(HealPlantFoodAbility.class, PlantFoodAbilityFactory.create(PlantName.WALL_NUT));
        assertInstanceOf(HealPlantFoodAbility.class, PlantFoodAbilityFactory.create(PlantName.TALL_NUT));
        assertInstanceOf(HealPlantFoodAbility.class, PlantFoodAbilityFactory.create(PlantName.ENDURIAN));
    }

    @Test
    void garlicUsesDedicatedAbilityAndLaneDivertPlantFood() {
        ArrayList<PlantAbility> garlic = AbilityFactory.getAbilities(PlantName.GARLIC);
        assertTrue(garlic.stream().anyMatch(GarlicAbility.class::isInstance));
        assertTrue(garlic.stream().anyMatch(LaneDivertPlantFoodAbility.class::isInstance));
        assertFalse(garlic.stream().anyMatch(WallNutAbility.class::isInstance));
        assertFalse(garlic.stream().anyMatch(EatingMeleeAbility.class::isInstance));
        assertFalse(garlic.stream().anyMatch(HealPlantFoodAbility.class::isInstance));
    }

    @Test
    void healthSegmentConditionReadsPlantHealthNotAnimation() {
        Plant plant = new Plant();
        plant.setHealth(new PlantHealth(4000));
        assertTrue(PlantConditions.healthSegment(0, HealthSegments.QUARTERS).matches(plant));

        plant.getHealth().setCurrentHealth(3000);
        assertTrue(PlantConditions.healthSegment(1, HealthSegments.QUARTERS).matches(plant));

        plant.getHealth().setCurrentHealth(2000);
        assertTrue(PlantConditions.healthSegment(2, HealthSegments.QUARTERS).matches(plant));

        plant.getHealth().setCurrentHealth(1000);
        assertTrue(PlantConditions.healthSegment(3, HealthSegments.QUARTERS).matches(plant));
    }

    @Test
    void endurianAttackIntervalUsesPlantDefinitionTicks() {
        Plant plant = PlantFactory.create(PlantName.ENDURIAN);
        assertEquals(EatingMeleeAbility.ATTACK_INTERVAL_TICKS, (int) plant.getActionInterval());
        assertEquals(20, plant.getAttackDamage());
    }

    private static void assertHeal(PlantName name, int healAmount) {
        assertTrue(PlantFoodDefinitionRegistry.isImplemented(name));
        PlantFoodDefinition definition = PlantFoodDefinitionRegistry.get(name);
        assertNotNull(definition);
        assertEquals(PlantFoodAbilityType.HEAL, definition.getAbilityType());
        assertEquals(healAmount, definition.getHealAmount());
    }
}

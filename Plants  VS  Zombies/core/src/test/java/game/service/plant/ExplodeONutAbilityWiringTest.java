package game.service.plant;

import app.enums.plant.PlantName;
import game.engine.time.GameClock;
import game.entity.plant.HealthSegments;
import game.entity.plant.Plant;
import game.entity.plant.ability.AreaMeleeAbility;
import game.entity.plant.ability.ExplodeAbility;
import game.entity.plant.ability.PlantAbility;
import game.entity.plant.ability.WallNutAbility;
import game.entity.plant.ability.plantfood.HealPlantFoodAbility;
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

class ExplodeONutAbilityWiringTest {
    @BeforeAll
    static void loadDefinitions() {
        PlantDefinitionLoader.load();
        PlantFoodDefinitionLoader.load();
        PlantUpgradeConfigLoader.get();
    }

    @Test
    void explodeONutUsesDeathExplosionAndHealPlantFood() {
        ArrayList<PlantAbility> abilities = AbilityFactory.getAbilities(PlantName.EXPLODE_O_NUT);
        ExplodeAbility explode = find(abilities, ExplodeAbility.class);
        assertNotNull(explode);
        assertFalse(explode.hasDetonated());
        assertEquals(AreaMeleeAbility.CELL_RADIUS_3X3, ExplodeAbility.EXPLOSION_CELL_RADIUS);
        assertTrue(abilities.stream().anyMatch(HealPlantFoodAbility.class::isInstance));
        assertFalse(abilities.stream().anyMatch(WallNutAbility.class::isInstance));
        assertInstanceOf(HealPlantFoodAbility.class, PlantFoodAbilityFactory.create(PlantName.EXPLODE_O_NUT));
    }

    @Test
    void plantFoodRestoresHealthAndDoesNotExplode() {
        assertTrue(PlantFoodDefinitionRegistry.isImplemented(PlantName.EXPLODE_O_NUT));
        PlantFoodDefinition definition = PlantFoodDefinitionRegistry.get(PlantName.EXPLODE_O_NUT);
        assertNotNull(definition);
        assertEquals(PlantFoodAbilityType.HEAL, definition.getAbilityType());
        assertTrue(definition.isRestoreFullHealth());
        assertEquals(0, definition.getHealAmount());
        assertEquals(
            (int) (PlantName.EXPLODE_O_NUT.getPlantFoodDuration() * GameClock.TICKS_PER_SECOND),
            definition.getDurationTicks()
        );

        Plant plant = PlantFactory.create(PlantName.EXPLODE_O_NUT);
        int max = plant.getHealth().getMaxHealth();
        plant.getHealth().setCurrentHealth(max * 0.20d);
        plant.heal(max);
        assertEquals(max, plant.getHealth().getCurrentHealth(), 0.0001);
        assertEquals(max, plant.getHealth().getMaxHealth());
        assertTrue(PlantConditions.healthSegment(0, HealthSegments.QUARTERS).matches(plant));
    }

    @Test
    void healthQuartersDriveDamageClipsFromEffectiveMaxHealth() {
        Plant plant = PlantFactory.create(PlantName.EXPLODE_O_NUT);
        int max = plant.getHealth().getMaxHealth();
        assertTrue(PlantConditions.healthSegment(0, HealthSegments.QUARTERS).matches(plant));

        plant.getHealth().setCurrentHealth(max * 0.75d);
        assertTrue(PlantConditions.healthSegment(1, HealthSegments.QUARTERS).matches(plant));

        plant.getHealth().setCurrentHealth(max * 0.50d);
        assertTrue(PlantConditions.healthSegment(2, HealthSegments.QUARTERS).matches(plant));

        plant.getHealth().setCurrentHealth(max * 0.25d);
        assertTrue(PlantConditions.healthSegment(3, HealthSegments.QUARTERS).matches(plant));
    }

    @Test
    void handleDeathDetonatesExactlyOnceWithoutRemovingDuringAbility() {
        Plant plant = PlantFactory.create(PlantName.EXPLODE_O_NUT);
        ExplodeAbility explode = new ExplodeAbility();
        plant.getHealth().setCurrentHealth(0);
        assertTrue(plant.getHealth().isDead());

        assertFalse(explode.handleDeath(plant, null, 0));
        assertTrue(explode.hasDetonated());
        assertFalse(explode.handleDeath(plant, null, 1));
        assertFalse(explode.handleDeath(plant, null, 2));
        assertTrue(explode.hasDetonated());
    }

    private static <T> T find(ArrayList<PlantAbility> abilities, Class<T> type) {
        return abilities.stream()
            .filter(type::isInstance)
            .map(type::cast)
            .findFirst()
            .orElse(null);
    }
}

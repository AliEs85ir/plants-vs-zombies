package game.service.plant;

import app.enums.plant.PlantName;
import game.engine.time.GameClock;
import game.entity.plant.ability.AdjacentMeleeAbility;
import game.entity.plant.ability.AttackAbility;
import game.entity.plant.ability.ChomperAbility;
import game.entity.plant.ability.MeleeAbility;
import game.entity.plant.ability.PlantAbility;
import game.entity.plant.ability.plantfood.AreaPulsePlantFoodAbility;
import game.entity.plant.ability.plantfood.RapidFirePlantFoodAbility;
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

class BonkChoyAbilityWiringTest {
    @BeforeAll
    static void loadDefinitions() {
        PlantDefinitionLoader.load();
        PlantFoodDefinitionLoader.load();
        PlantUpgradeConfigLoader.get();
    }

    @Test
    void bonkChoyUsesAdjacentMeleeAndAreaPulsePlantFood() {
        ArrayList<PlantAbility> abilities = AbilityFactory.getAbilities(PlantName.BONK_CHOY);

        assertTrue(abilities.stream().anyMatch(AdjacentMeleeAbility.class::isInstance));
        assertTrue(abilities.stream().anyMatch(AreaPulsePlantFoodAbility.class::isInstance));
        assertFalse(abilities.stream().anyMatch(MeleeAbility.class::isInstance));
        assertFalse(abilities.stream().anyMatch(AttackAbility.class::isInstance));
    }

    @Test
    void bonkChoyPlantFoodIsRegisteredOnExistingDefinitionRegistry() {
        assertTrue(PlantFoodDefinitionRegistry.isImplemented(PlantName.BONK_CHOY));
        PlantFoodDefinition definition = PlantFoodDefinitionRegistry.get(PlantName.BONK_CHOY);
        assertNotNull(definition);
        assertEquals(PlantFoodAbilityType.AREA_PULSE, definition.getAbilityType());
        assertEquals(4 * GameClock.TICKS_PER_SECOND, definition.getDurationTicks());
        assertEquals(1, definition.getFireIntervalTicks());
        assertEquals(0, definition.getFireStartDelayTicks());
        assertEquals(1, definition.getAreaRadius());
        assertEquals(0.25f, definition.getAreaPercent());
        assertEquals(4.0f, PlantName.BONK_CHOY.getPlantFoodDuration());
        assertInstanceOf(AreaPulsePlantFoodAbility.class, PlantFoodAbilityFactory.create(PlantName.BONK_CHOY));
    }

    @Test
    void peashooterAndChomperAbilitiesAreUnchanged() {
        ArrayList<PlantAbility> peashooter = AbilityFactory.getAbilities(PlantName.PEASHOOTER);
        assertTrue(peashooter.stream().anyMatch(AttackAbility.class::isInstance));
        assertTrue(peashooter.stream().anyMatch(RapidFirePlantFoodAbility.class::isInstance));
        assertFalse(peashooter.stream().anyMatch(AdjacentMeleeAbility.class::isInstance));

        ArrayList<PlantAbility> chomper = AbilityFactory.getAbilities(PlantName.CHOMPER);
        assertTrue(chomper.stream().anyMatch(ChomperAbility.class::isInstance));
        assertFalse(chomper.stream().anyMatch(MeleeAbility.class::isInstance));
        assertFalse(chomper.stream().anyMatch(AdjacentMeleeAbility.class::isInstance));
        assertFalse(chomper.stream().anyMatch(AreaPulsePlantFoodAbility.class::isInstance));
    }
}

package game.service.plant;

import app.enums.plant.PlantName;
import game.engine.time.GameClock;
import game.entity.plant.ability.AdjacentMeleeAbility;
import game.entity.plant.ability.AreaMeleeAbility;
import game.entity.plant.ability.AttackAbility;
import game.entity.plant.ability.ChomperAbility;
import game.entity.plant.ability.MeleeAbility;
import game.entity.plant.ability.PlantAbility;
import game.entity.plant.ability.PlantTargeting;
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

class PhatBeetAbilityWiringTest {
    @BeforeAll
    static void loadDefinitions() {
        PlantDefinitionLoader.load();
        PlantFoodDefinitionLoader.load();
        PlantUpgradeConfigLoader.get();
    }

    @Test
    void phatBeetUsesAreaMeleeAndAreaPulsePlantFood() {
        ArrayList<PlantAbility> abilities = AbilityFactory.getAbilities(PlantName.PHAT_BEET);

        assertTrue(abilities.stream().anyMatch(AreaMeleeAbility.class::isInstance));
        assertTrue(abilities.stream().anyMatch(AreaPulsePlantFoodAbility.class::isInstance));
        assertFalse(abilities.stream().anyMatch(MeleeAbility.class::isInstance));
        assertFalse(abilities.stream().anyMatch(AdjacentMeleeAbility.class::isInstance));
        assertFalse(abilities.stream().anyMatch(AttackAbility.class::isInstance));
    }

    @Test
    void phatBeetPlantFoodDamagesAllZombiesAfterSixTicks() {
        assertTrue(PlantFoodDefinitionRegistry.isImplemented(PlantName.PHAT_BEET));
        PlantFoodDefinition definition = PlantFoodDefinitionRegistry.get(PlantName.PHAT_BEET);
        assertNotNull(definition);
        assertEquals(PlantFoodAbilityType.AREA_PULSE, definition.getAbilityType());
        assertEquals((int) (PlantName.PHAT_BEET.getPlantFoodDuration() * GameClock.TICKS_PER_SECOND), definition.getDurationTicks());
        assertEquals(6, definition.getFireStartDelayTicks());
        assertEquals(definition.getDurationTicks(), definition.getFireIntervalTicks());
        assertEquals(PlantTargeting.ALL_LAWN_RADIUS, definition.getAreaRadius());
        assertEquals("phatbeets pf pulse", definition.getParticleEffectId());
        assertEquals(3.0f, PlantName.PHAT_BEET.getPlantFoodDuration());
        assertInstanceOf(AreaPulsePlantFoodAbility.class, PlantFoodAbilityFactory.create(PlantName.PHAT_BEET));
    }

    @Test
    void existingMeleeAndShooterWiringIsUnchanged() {
        ArrayList<PlantAbility> bonkChoy = AbilityFactory.getAbilities(PlantName.BONK_CHOY);
        assertTrue(bonkChoy.stream().anyMatch(AdjacentMeleeAbility.class::isInstance));
        assertTrue(bonkChoy.stream().anyMatch(AreaPulsePlantFoodAbility.class::isInstance));
        assertFalse(bonkChoy.stream().anyMatch(AreaMeleeAbility.class::isInstance));

        ArrayList<PlantAbility> peashooter = AbilityFactory.getAbilities(PlantName.PEASHOOTER);
        assertTrue(peashooter.stream().anyMatch(AttackAbility.class::isInstance));
        assertTrue(peashooter.stream().anyMatch(RapidFirePlantFoodAbility.class::isInstance));
        assertFalse(peashooter.stream().anyMatch(AreaMeleeAbility.class::isInstance));

        ArrayList<PlantAbility> chomper = AbilityFactory.getAbilities(PlantName.CHOMPER);
        assertTrue(chomper.stream().anyMatch(ChomperAbility.class::isInstance));
        assertFalse(chomper.stream().anyMatch(MeleeAbility.class::isInstance));
        assertFalse(chomper.stream().anyMatch(AreaMeleeAbility.class::isInstance));
        assertFalse(chomper.stream().anyMatch(AreaPulsePlantFoodAbility.class::isInstance));
    }

    @Test
    void cellRadiusTreatsNegativeAsAllLawnAndHandlesNullWorld() {
        assertTrue(PlantTargeting.zombiesInCellRadius(null, null, PlantTargeting.ALL_LAWN_RADIUS).isEmpty());
        assertTrue(PlantTargeting.zombiesInCellRadius(null, null, 1).isEmpty());
    }
}

package game.service.plant;

import app.enums.plant.PlantName;
import app.enums.plant.PlantTag;
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
import game.entity.plant.upgrade.PlantUpgradeKeys;
import game.entity.registry.PlantDefinitionLoader;
import game.entity.registry.PlantFoodDefinitionLoader;
import game.entity.registry.PlantFoodDefinitionRegistry;
import game.entity.registry.PlantRegistry;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WasabiWhipAbilityWiringTest {
    @BeforeAll
    static void loadDefinitions() {
        PlantDefinitionLoader.load();
        PlantFoodDefinitionLoader.load();
        PlantUpgradeConfigLoader.get();
    }

    @Test
    void wasabiWhipUsesSimultaneousAdjacentMeleeAndAreaPulsePlantFood() {
        ArrayList<PlantAbility> abilities = AbilityFactory.getAbilities(PlantName.WASABI_WHIP);

        AdjacentMeleeAbility melee = abilities.stream()
            .filter(AdjacentMeleeAbility.class::isInstance)
            .map(AdjacentMeleeAbility.class::cast)
            .findFirst()
            .orElse(null);
        assertNotNull(melee);
        assertTrue(melee.punchesSimultaneousSides());
        assertEquals(AdjacentMeleeAbility.WINDUP_DAMAGE_DELAY_TICKS, melee.getDamageDelayTicks());
        assertEquals(3, melee.getDamageDelayTicks());
        assertTrue(abilities.stream().anyMatch(AreaPulsePlantFoodAbility.class::isInstance));
        assertFalse(abilities.stream().anyMatch(MeleeAbility.class::isInstance));
        assertFalse(abilities.stream().anyMatch(AttackAbility.class::isInstance));
        assertFalse(abilities.stream().anyMatch(ChomperAbility.class::isInstance));
    }

    @Test
    void wasabiWhipPlantFoodIsRegisteredOnExistingDefinitionRegistry() {
        assertTrue(PlantFoodDefinitionRegistry.isImplemented(PlantName.WASABI_WHIP));
        PlantFoodDefinition definition = PlantFoodDefinitionRegistry.get(PlantName.WASABI_WHIP);
        assertNotNull(definition);
        assertEquals(PlantFoodAbilityType.AREA_PULSE, definition.getAbilityType());
        assertEquals(4 * GameClock.TICKS_PER_SECOND, definition.getDurationTicks());
        assertEquals(1, definition.getFireIntervalTicks());
        assertEquals(0, definition.getFireStartDelayTicks());
        assertEquals(1, definition.getAreaRadius());
        assertEquals(0.25f, definition.getAreaPercent());
        assertEquals(4.0f, PlantName.WASABI_WHIP.getPlantFoodDuration());
        assertEquals(
            (int) (PlantName.WASABI_WHIP.getPlantFoodDuration() * GameClock.TICKS_PER_SECOND),
            definition.getDurationTicks()
        );
        assertEquals(definition.getDurationTicks(), definition.getAnimationDurationTicks());
        assertInstanceOf(AreaPulsePlantFoodAbility.class, PlantFoodAbilityFactory.create(PlantName.WASABI_WHIP));
    }

    @Test
    void wasabiWhipKeepsFireTagAndExistingHeatDuration() {
        assertTrue(PlantRegistry.get(PlantName.WASABI_WHIP).hasTag(PlantTag.FIRE));
        assertEquals(4 * GameClock.TICKS_PER_SECOND, AttackService.STANDARD_FIRE_DURATION_TICKS);
        assertEquals(PlantUpgradeKeys.EXTRA_RANGE, "extraRange");
        assertEquals(0, PlantUpgradeService.getModifiers(PlantName.WASABI_WHIP).extraRange());
    }

    @Test
    void neighboringMeleeAndShooterWiringIsUnchanged() {
        ArrayList<PlantAbility> bonkChoy = AbilityFactory.getAbilities(PlantName.BONK_CHOY);
        AdjacentMeleeAbility bonkMelee = bonkChoy.stream()
            .filter(AdjacentMeleeAbility.class::isInstance)
            .map(AdjacentMeleeAbility.class::cast)
            .findFirst()
            .orElse(null);
        assertNotNull(bonkMelee);
        assertFalse(bonkMelee.punchesSimultaneousSides());
        assertEquals(1, bonkMelee.getDamageDelayTicks());
        assertTrue(bonkChoy.stream().anyMatch(AreaPulsePlantFoodAbility.class::isInstance));
        assertFalse(bonkChoy.stream().anyMatch(MeleeAbility.class::isInstance));

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

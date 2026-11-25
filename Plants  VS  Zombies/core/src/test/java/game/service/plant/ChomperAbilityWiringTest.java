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
import game.entity.plant.upgrade.PlantUpgradeConfigLoader;
import game.entity.plant.upgrade.PlantUpgradeKeys;
import game.entity.registry.PlantDefinitionLoader;
import game.entity.registry.PlantFoodDefinitionLoader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChomperAbilityWiringTest {
    @BeforeAll
    static void loadDefinitions() {
        PlantDefinitionLoader.load();
        PlantFoodDefinitionLoader.load();
        PlantUpgradeConfigLoader.get();
    }

    @Test
    void chomperUsesDedicatedAbilityNotGenericMelee() {
        ArrayList<PlantAbility> abilities = AbilityFactory.getAbilities(PlantName.CHOMPER);

        assertTrue(abilities.stream().anyMatch(ChomperAbility.class::isInstance));
        assertFalse(abilities.stream().anyMatch(MeleeAbility.class::isInstance));
        assertFalse(abilities.stream().anyMatch(AdjacentMeleeAbility.class::isInstance));
        assertFalse(abilities.stream().anyMatch(AreaMeleeAbility.class::isInstance));
        assertFalse(abilities.stream().anyMatch(AttackAbility.class::isInstance));
        assertFalse(abilities.stream().anyMatch(AreaPulsePlantFoodAbility.class::isInstance));
    }

    @Test
    void digestDurationUsesTickClockAndExistingUpgradeKey() {
        assertEquals(10 * GameClock.TICKS_PER_SECOND, 100);
        assertEquals(0, PlantUpgradeService.getModifiers(PlantName.CHOMPER).digestTimeReductionTicks());
        assertEquals(
            PlantUpgradeKeys.DIGEST_TIME_REDUCTION_SECONDS,
            "digestTimeReductionSeconds"
        );
    }

    @Test
    void frontCellTargetingHandlesNullWorld() {
        assertTrue(PlantTargeting.zombiesInCell(null, null).isEmpty());
        assertEquals(null, PlantTargeting.firstZombieInFrontCell(null, null));
        assertEquals(null, PlantTargeting.frontCell(null, null));
    }

    @Test
    void neighboringMeleePlantsStayOnTheirAbilities() {
        ArrayList<PlantAbility> bonkChoy = AbilityFactory.getAbilities(PlantName.BONK_CHOY);
        assertTrue(bonkChoy.stream().anyMatch(AdjacentMeleeAbility.class::isInstance));
        assertFalse(bonkChoy.stream().anyMatch(ChomperAbility.class::isInstance));

        ArrayList<PlantAbility> phatBeet = AbilityFactory.getAbilities(PlantName.PHAT_BEET);
        assertTrue(phatBeet.stream().anyMatch(AreaMeleeAbility.class::isInstance));
        assertFalse(phatBeet.stream().anyMatch(ChomperAbility.class::isInstance));

        ArrayList<PlantAbility> wasabi = AbilityFactory.getAbilities(PlantName.WASABI_WHIP);
        assertTrue(wasabi.stream().anyMatch(AdjacentMeleeAbility.class::isInstance));
        assertFalse(wasabi.stream().anyMatch(MeleeAbility.class::isInstance));
        assertFalse(wasabi.stream().anyMatch(ChomperAbility.class::isInstance));
    }
}

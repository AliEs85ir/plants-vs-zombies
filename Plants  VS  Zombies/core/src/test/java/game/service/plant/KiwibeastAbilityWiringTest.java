package game.service.plant;

import app.enums.plant.PlantGameplayPhase;
import app.enums.plant.PlantName;
import game.engine.time.GameClock;
import game.entity.plant.Plant;
import game.entity.plant.ability.AdjacentMeleeAbility;
import game.entity.plant.ability.AreaMeleeAbility;
import game.entity.plant.ability.AttackAbility;
import game.entity.plant.ability.ChomperAbility;
import game.entity.plant.ability.GrowthAbility;
import game.entity.plant.ability.MeleeAbility;
import game.entity.plant.ability.PlantAbility;
import game.entity.plant.ability.PlantAbilitySupport;
import game.entity.plant.ability.PlantTargeting;
import game.entity.plant.ability.plantfood.AreaPulsePlantFoodAbility;
import game.entity.plant.plantfood.PlantFoodAbilityType;
import game.entity.plant.plantfood.PlantFoodDefinition;
import game.entity.plant.upgrade.PlantUpgradeConfigLoader;
import game.entity.registry.PlantDefinitionLoader;
import game.entity.registry.PlantFoodDefinitionLoader;
import game.entity.registry.PlantFoodDefinitionRegistry;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KiwibeastAbilityWiringTest {
    @BeforeAll
    static void loadDefinitions() {
        PlantDefinitionLoader.load();
        PlantFoodDefinitionLoader.load();
        PlantUpgradeConfigLoader.get();
    }

    @Test
    void kiwibeastUsesGrowthAreaMeleeAndScheduledPlantFood() {
        ArrayList<PlantAbility> abilities = AbilityFactory.getAbilities(PlantName.KIWIBEAST);

        GrowthAbility growth = find(abilities, GrowthAbility.class);
        AreaMeleeAbility melee = find(abilities, AreaMeleeAbility.class);
        assertNotNull(growth);
        assertNotNull(melee);
        assertEquals(GrowthAbility.DEFAULT_MAX_STAGE, growth.getMaxStage());
        assertEquals(12 * GameClock.TICKS_PER_SECOND, growth.getStageDurationTicks());
        assertEquals(AreaMeleeAbility.KIWIBEAST_DAMAGE_DELAY_TICKS, melee.getDamageDelayTicks());
        assertEquals(11, melee.getDamageDelayTicks());
        assertEquals(AreaMeleeAbility.CELL_RADIUS_3X3, melee.getAreaRadius());
        assertEquals(1, melee.getAreaRadius());
        assertEquals(2, melee.getAttackCues().size());
        assertEquals(AreaMeleeAbility.KIWIBEAST_PULSE_DELAY_TICKS, melee.getAttackCues().get(0).delayTicks);
        assertEquals("kiwibeast attack pulse", melee.getAttackCues().get(0).effectId);
        assertEquals(AreaMeleeAbility.KIWIBEAST_TILE_HIT_DELAY_TICKS, melee.getAttackCues().get(1).delayTicks);
        assertEquals("kiwibeast tile hit", melee.getAttackCues().get(1).effectId);
        assertTrue(abilities.stream().anyMatch(AreaPulsePlantFoodAbility.class::isInstance));
        assertFalse(abilities.stream().anyMatch(MeleeAbility.class::isInstance));
        assertFalse(abilities.stream().anyMatch(AdjacentMeleeAbility.class::isInstance));
        assertFalse(abilities.stream().anyMatch(AttackAbility.class::isInstance));
        assertFalse(abilities.stream().anyMatch(ChomperAbility.class::isInstance));
    }

    @Test
    void kiwibeastPlantFoodUsesExactGlobalPulseSchedule() {
        assertTrue(PlantFoodDefinitionRegistry.isImplemented(PlantName.KIWIBEAST));
        PlantFoodDefinition definition = PlantFoodDefinitionRegistry.get(PlantName.KIWIBEAST);
        assertNotNull(definition);
        assertEquals(PlantFoodAbilityType.AREA_PULSE, definition.getAbilityType());
        assertTrue(definition.isForceMaxGrowth());
        assertEquals(PlantTargeting.ALL_LAWN_RADIUS, definition.getAreaRadius());
        assertEquals(List.of(5, 12, 22), definition.getEffectTicks());
        assertEquals(List.of(10, 17, 27), definition.getPulseTicks());
        assertEquals(28, definition.getDurationTicks());
        assertEquals("kiwibeast pf pulse", definition.getParticleEffectId());
        assertEquals("kiwibeast tile hit", definition.getSecondaryParticleEffectId());
        assertInstanceOf(AreaPulsePlantFoodAbility.class, PlantFoodAbilityFactory.create(PlantName.KIWIBEAST));
    }

    @Test
    void growthAdvancesOnSimulationTicksNotAnimation() {
        Plant plant = PlantFactory.create(PlantName.KIWIBEAST);
        plant.setPlantedTick(0);
        GrowthAbility growth = GrowthAbility.ofDurationSeconds(
            GrowthAbility.DEFAULT_MAX_STAGE,
            GrowthAbility.STAGE_DURATION_SECONDS
        );

        growth.update(plant, null, 0);
        assertEquals(1, plant.getStage());
        assertFalse(growth.isTransforming());

        growth.update(plant, null, 12 * GameClock.TICKS_PER_SECOND);
        assertTrue(growth.isTransforming());
        assertEquals(PlantGameplayPhase.TRANSFORMING, plant.getGameplayPhase());
        assertEquals(1, plant.getStage());

        growth.update(plant, null, 12 * GameClock.TICKS_PER_SECOND + 40);
        assertEquals(2, plant.getStage());
        assertEquals(PlantGameplayPhase.DEFAULT, plant.getGameplayPhase());
        assertFalse(growth.isTransforming());

        growth.update(plant, null, 24 * GameClock.TICKS_PER_SECOND + 40);
        assertTrue(growth.isTransforming());
        assertEquals(2, plant.getStage());

        growth.update(plant, null, 24 * GameClock.TICKS_PER_SECOND + 80);
        assertEquals(3, plant.getStage());
        assertFalse(growth.isTransforming());

        growth.update(plant, null, 40 * GameClock.TICKS_PER_SECOND);
        assertEquals(3, plant.getStage());
        assertFalse(growth.isTransforming());
    }

    @Test
    void plantFoodForceMaxGrowthStopsFurtherGrowth() {
        Plant plant = PlantFactory.create(PlantName.KIWIBEAST);
        plant.setPlantedTick(0);
        GrowthAbility growth = GrowthAbility.ofDurationSeconds(
            GrowthAbility.DEFAULT_MAX_STAGE,
            GrowthAbility.STAGE_DURATION_SECONDS
        );
        growth.update(plant, null, 0);
        PlantAbilitySupport.forceMaxGrowth(plant, 3);
        growth.update(plant, null, 12 * GameClock.TICKS_PER_SECOND);
        assertEquals(3, plant.getStage());
        assertFalse(growth.isTransforming());
    }

    @Test
    void neighboringMeleeWiringIsUnchanged() {
        ArrayList<PlantAbility> phatBeet = AbilityFactory.getAbilities(PlantName.PHAT_BEET);
        AreaMeleeAbility phatMelee = find(phatBeet, AreaMeleeAbility.class);
        assertNotNull(phatMelee);
        assertEquals(5, phatMelee.getDamageDelayTicks());
        assertEquals(1, phatMelee.getAttackCues().size());
        assertEquals(0, phatMelee.getAttackCues().get(0).delayTicks);

        ArrayList<PlantAbility> bonkChoy = AbilityFactory.getAbilities(PlantName.BONK_CHOY);
        assertTrue(bonkChoy.stream().anyMatch(AdjacentMeleeAbility.class::isInstance));
        assertFalse(bonkChoy.stream().anyMatch(GrowthAbility.class::isInstance));

        ArrayList<PlantAbility> wasabi = AbilityFactory.getAbilities(PlantName.WASABI_WHIP);
        assertTrue(wasabi.stream().anyMatch(AdjacentMeleeAbility.class::isInstance));
        assertFalse(wasabi.stream().anyMatch(GrowthAbility.class::isInstance));
        assertFalse(wasabi.stream().anyMatch(AreaMeleeAbility.class::isInstance));
    }

    private static <T> T find(ArrayList<PlantAbility> abilities, Class<T> type) {
        return abilities.stream()
            .filter(type::isInstance)
            .map(type::cast)
            .findFirst()
            .orElse(null);
    }
}

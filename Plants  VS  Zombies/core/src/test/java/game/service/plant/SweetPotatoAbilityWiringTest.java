package game.service.plant;

import app.enums.plant.PlantName;
import game.engine.time.GameClock;
import game.entity.plant.HealthSegments;
import game.entity.plant.Plant;
import game.entity.plant.ability.GarlicAbility;
import game.entity.plant.ability.PlantAbility;
import game.entity.plant.ability.SweetPotatoAbility;
import game.entity.plant.ability.WallNutAbility;
import game.entity.plant.ability.plantfood.HealPlantFoodAbility;
import game.entity.plant.ability.plantfood.LaneDivertPlantFoodAbility;
import game.entity.plant.plantfood.PlantFoodAbilityType;
import game.entity.plant.plantfood.PlantFoodDefinition;
import game.entity.plant.upgrade.PlantUpgradeConfigLoader;
import game.entity.registry.PlantDefinitionLoader;
import game.entity.registry.PlantFoodDefinitionLoader;
import game.entity.registry.PlantFoodDefinitionRegistry;
import game.entity.zombie.ZombieLaneSwitch;
import graphics.plant.PlantConditions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SweetPotatoAbilityWiringTest {
    @BeforeAll
    static void loadDefinitions() {
        PlantDefinitionLoader.load();
        PlantFoodDefinitionLoader.load();
        PlantUpgradeConfigLoader.get();
    }

    @Test
    void sweetPotatoUsesLanePullAbilityAndLaneDivertPlantFood() {
        ArrayList<PlantAbility> abilities = AbilityFactory.getAbilities(PlantName.SWEET_POTATO);
        assertTrue(abilities.stream().anyMatch(SweetPotatoAbility.class::isInstance));
        assertTrue(abilities.stream().anyMatch(LaneDivertPlantFoodAbility.class::isInstance));
        assertFalse(abilities.stream().anyMatch(WallNutAbility.class::isInstance));
        assertFalse(abilities.stream().anyMatch(GarlicAbility.class::isInstance));
        assertFalse(abilities.stream().anyMatch(HealPlantFoodAbility.class::isInstance));
        assertInstanceOf(LaneDivertPlantFoodAbility.class, PlantFoodAbilityFactory.create(PlantName.SWEET_POTATO));
    }

    @Test
    void plantFoodRestoresHealthAndPullsAdjacentLanesWithoutNormalRange() {
        assertTrue(PlantFoodDefinitionRegistry.isImplemented(PlantName.SWEET_POTATO));
        PlantFoodDefinition definition = PlantFoodDefinitionRegistry.get(PlantName.SWEET_POTATO);
        assertNotNull(definition);
        assertEquals(PlantFoodAbilityType.LANE_DIVERT, definition.getAbilityType());
        assertTrue(definition.isRestoreFullHealth());
        assertTrue(definition.isAttractToPlantLane());
        assertTrue(definition.isIncludeAdjacentLanes());
        assertEquals(List.of(0), definition.getPulseTicks());
        assertEquals(
            (int) (PlantName.SWEET_POTATO.getPlantFoodDuration() * GameClock.TICKS_PER_SECOND),
            definition.getDurationTicks()
        );
        assertEquals(0, definition.getHealAmount());
    }

    @Test
    void garlicPlantFoodStillDivertsInsteadOfAttracting() {
        PlantFoodDefinition garlic = PlantFoodDefinitionRegistry.get(PlantName.GARLIC);
        assertNotNull(garlic);
        assertFalse(garlic.isAttractToPlantLane());
        assertFalse(garlic.isIncludeAdjacentLanes());
    }

    @Test
    void attractRangeIsTwelveCellsAroundThePlant() {
        assertEquals(12, SweetPotatoAbility.attractCellCount());
        assertTrue(SweetPotatoAbility.inAttractRange(2, 3, 2, 3));
        assertTrue(SweetPotatoAbility.inAttractRange(2, 3, 1, 2));
        assertTrue(SweetPotatoAbility.inAttractRange(2, 3, 3, 5));
        assertFalse(SweetPotatoAbility.inAttractRange(2, 3, 0, 3));
        assertFalse(SweetPotatoAbility.inAttractRange(2, 3, 2, 1));
        assertFalse(SweetPotatoAbility.inAttractRange(2, 3, 2, 6));
        assertFalse(SweetPotatoAbility.inAttractRange(2, 3, 2, 3 + SweetPotatoAbility.ATTRACT_COL_AHEAD + 1));
    }

    @Test
    void healthQuartersUseEffectiveMaxHealthRatio() {
        Plant plant = PlantFactory.create(PlantName.SWEET_POTATO);
        int max = plant.getHealth().getMaxHealth();
        assertTrue(max > 0);
        assertTrue(PlantConditions.healthSegment(0, HealthSegments.QUARTERS).matches(plant));

        plant.getHealth().setCurrentHealth(max * 0.75d);
        assertTrue(PlantConditions.healthSegment(1, HealthSegments.QUARTERS).matches(plant));

        plant.getHealth().setCurrentHealth(max * 0.50d);
        assertTrue(PlantConditions.healthSegment(2, HealthSegments.QUARTERS).matches(plant));

        plant.getHealth().setCurrentHealth(max * 0.25d);
        assertTrue(PlantConditions.healthSegment(3, HealthSegments.QUARTERS).matches(plant));

        plant.heal(max);
        assertEquals(max, plant.getHealth().getCurrentHealth(), 0.0001);
        assertEquals(max, plant.getHealth().getMaxHealth());
        assertTrue(PlantConditions.healthSegment(0, HealthSegments.QUARTERS).matches(plant));
    }

    @Test
    void pullSpeedIsFasterThanForwardMovement() {
        assertEquals(2.5f, ZombieLaneSwitch.LANE_PULL_SPEED_MULTIPLIER, 0.0001f);
        assertTrue(ZombieLaneSwitch.LANE_PULL_SPEED_MULTIPLIER >= 2f);
        assertTrue(ZombieLaneSwitch.LANE_PULL_SPEED_MULTIPLIER <= 3f);
    }
}

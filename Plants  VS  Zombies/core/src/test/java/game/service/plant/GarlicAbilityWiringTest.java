package game.service.plant;

import app.enums.plant.PlantName;
import game.engine.time.GameClock;
import game.entity.plant.ability.GarlicAbility;
import game.entity.plant.ability.PlantAbility;
import game.entity.plant.ability.WallNutAbility;
import game.entity.plant.ability.plantfood.LaneDivertPlantFoodAbility;
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

class GarlicAbilityWiringTest {
    @BeforeAll
    static void loadDefinitions() {
        PlantDefinitionLoader.load();
        PlantFoodDefinitionLoader.load();
        PlantUpgradeConfigLoader.get();
    }

    @Test
    void garlicWiresBiteDivertAndLaneDivertPlantFood() {
        ArrayList<PlantAbility> abilities = AbilityFactory.getAbilities(PlantName.GARLIC);
        assertTrue(abilities.stream().anyMatch(GarlicAbility.class::isInstance));
        assertTrue(abilities.stream().anyMatch(LaneDivertPlantFoodAbility.class::isInstance));
        assertFalse(abilities.stream().anyMatch(WallNutAbility.class::isInstance));
        assertInstanceOf(LaneDivertPlantFoodAbility.class, PlantFoodAbilityFactory.create(PlantName.GARLIC));
    }

    @Test
    void garlicPlantFoodUsesTickScheduleAndRelativeProjectileOffset() {
        assertTrue(PlantFoodDefinitionRegistry.isImplemented(PlantName.GARLIC));
        PlantFoodDefinition definition = PlantFoodDefinitionRegistry.get(PlantName.GARLIC);
        assertNotNull(definition);
        assertEquals(PlantFoodAbilityType.LANE_DIVERT, definition.getAbilityType());
        assertTrue(definition.isRestoreFullHealth());
        assertEquals(List.of(12), definition.getEffectTicks());
        assertEquals(List.of(16), definition.getPulseTicks());
        assertEquals(100f, definition.getEffectOffsetX(), 0.0001f);
        assertEquals(-4f, definition.getEffectOffsetY(), 0.0001f);
        assertEquals("garlic plantfood projectile", definition.getParticleEffectId());
        assertTrue(definition.getDurationTicks() >= 16);
        assertEquals(
            (int) (PlantName.GARLIC.getPlantFoodDuration() * GameClock.TICKS_PER_SECOND),
            definition.getDurationTicks()
        );
    }
}

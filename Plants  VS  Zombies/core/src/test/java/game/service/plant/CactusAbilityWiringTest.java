package game.service.plant;

import app.enums.plant.PlantName;
import common.model.Position;
import game.engine.time.GameClock;
import game.entity.plant.Plant;
import game.entity.plant.ability.AttackAbility;
import game.entity.plant.ability.PlantAbility;
import game.entity.plant.ability.plantfood.DurationPlantFoodAbility;
import game.entity.plant.combat.PlantFoodProjectileLaunch;
import game.entity.plant.plantfood.PlantFoodAbilityType;
import game.entity.plant.plantfood.PlantFoodDefinition;
import game.entity.plant.plantfood.PlantFoodShot;
import game.entity.plant.upgrade.PlantUpgradeConfigLoader;
import game.entity.registry.PlantDefinitionLoader;
import game.entity.registry.PlantFoodDefinitionLoader;
import game.entity.registry.PlantFoodDefinitionRegistry;
import game.service.sun.SunSourceLocator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CactusAbilityWiringTest {
    @BeforeAll
    static void loadDefinitions() {
        PlantDefinitionLoader.load();
        PlantFoodDefinitionLoader.load();
        PlantUpgradeConfigLoader.get();
    }

    @Test
    void cactusKeepsNormalAttackAndGainsDurationPlantFood() {
        assertEquals(10, GameClock.TICKS_PER_SECOND);
        ArrayList<PlantAbility> abilities = AbilityFactory.getAbilities(PlantName.CACTUS);
        assertTrue(abilities.stream().anyMatch(AttackAbility.class::isInstance));
        assertTrue(abilities.stream().anyMatch(DurationPlantFoodAbility.class::isInstance));
        assertInstanceOf(DurationPlantFoodAbility.class, PlantFoodAbilityFactory.create(PlantName.CACTUS));
        assertTrue(PlantFoodDefinitionRegistry.isImplemented(PlantName.CACTUS));
        assertTrue(PlantFoodDefinitionRegistry.get(PlantName.CACTUS).keepsNormalCombat());
    }

    @Test
    void cactusPlantFoodUses150TickDurationAndPamOffset() {
        PlantFoodDefinition definition = PlantFoodDefinitionRegistry.get(PlantName.CACTUS);
        assertNotNull(definition);
        assertEquals(PlantFoodAbilityType.EMPOWER, definition.getAbilityType());
        assertEquals(150, definition.getDurationTicks());
        PlantFoodShot shot = definition.getEmpoweredAttackShot();
        assertNotNull(shot);
        assertEquals(5, shot.getDelayTicks());
        assertEquals(65f, shot.getOffsetX(), 0.0001f);
        assertEquals(55f, shot.getOffsetY(), 0.0001f);
        assertTrue(shot.isFromPamVisualCenter());
        assertTrue(shot.isApplyFacing());
        assertTrue(shot.getCapabilities().isPierceAll());
        assertTrue(shot.getCapabilities().isEnemyOnly());
        assertTrue(shot.getCapabilities().isPlantFoodVisual());
        assertFalse(shot.getCapabilities().isInstantKill());
        assertEquals("cactus_plantfood_hit", shot.getCapabilities().getHitEffectId());
        assertFalse(PlantFoodProjectileLaunch.shouldLaunchOnce(4, 5, false));
        assertTrue(PlantFoodProjectileLaunch.shouldLaunchOnce(5, 5, false));
        assertFalse(PlantFoodProjectileLaunch.shouldLaunchOnce(5, 5, true));

        Plant plant = PlantFactory.create(PlantName.CACTUS);
        plant.setPosition(new Position(100, 200));
        plant.setFacing(1);
        Position right = PlantFoodProjectileLaunch.spawnPosition(plant, shot);
        assertEquals(100 + 65 * SunSourceLocator.PAM_RENDER_SCALE, right.getX(), 0.0001);
        assertEquals(200 + 55 * SunSourceLocator.PAM_RENDER_SCALE, right.getY(), 0.0001);
        plant.setFacing(-1);
        Position left = PlantFoodProjectileLaunch.spawnPosition(plant, shot);
        assertEquals(100 - 65 * SunSourceLocator.PAM_RENDER_SCALE, left.getX(), 0.0001);
        assertEquals(200 + 55 * SunSourceLocator.PAM_RENDER_SCALE, left.getY(), 0.0001);
    }
}

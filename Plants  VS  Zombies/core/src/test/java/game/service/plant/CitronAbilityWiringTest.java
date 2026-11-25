package game.service.plant;

import app.enums.plant.PlantName;
import common.model.Position;
import game.engine.time.GameClock;
import game.entity.plant.Plant;
import game.entity.plant.ability.AttackAbility;
import game.entity.plant.ability.PlantAbility;
import game.entity.plant.ability.plantfood.RapidFirePlantFoodAbility;
import game.entity.plant.combat.PlantFoodProjectileLaunch;
import game.entity.plant.plantfood.PlantFoodAbilityType;
import game.entity.plant.plantfood.PlantFoodDefinition;
import game.entity.plant.plantfood.PlantFoodShot;
import game.entity.plant.upgrade.PlantUpgradeConfigLoader;
import game.entity.projectile.ProjectileFireTransform;
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

class CitronAbilityWiringTest {
    @BeforeAll
    static void loadDefinitions() {
        PlantDefinitionLoader.load();
        PlantFoodDefinitionLoader.load();
        PlantUpgradeConfigLoader.get();
    }

    @Test
    void citronKeepsNormalAttackAndGainsRapidFirePlantFood() {
        assertEquals(10, GameClock.TICKS_PER_SECOND);
        ArrayList<PlantAbility> abilities = AbilityFactory.getAbilities(PlantName.CITRON);
        assertTrue(abilities.stream().anyMatch(AttackAbility.class::isInstance));
        assertTrue(abilities.stream().anyMatch(RapidFirePlantFoodAbility.class::isInstance));
        assertInstanceOf(RapidFirePlantFoodAbility.class, PlantFoodAbilityFactory.create(PlantName.CITRON));
        assertTrue(ProjectileFireTransform.isExcluded(PlantName.CITRON));
    }

    @Test
    void citronPlantFoodUsesTickEightSingleShotAndPamOffset() {
        assertTrue(PlantFoodDefinitionRegistry.isImplemented(PlantName.CITRON));
        PlantFoodDefinition definition = PlantFoodDefinitionRegistry.get(PlantName.CITRON);
        assertNotNull(definition);
        assertEquals(PlantFoodAbilityType.RAPID_FIRE, definition.getAbilityType());
        assertEquals(8, definition.getFireStartDelayTicks());
        assertEquals(1, definition.getMaxShotsPerStream());
        PlantFoodShot shot = definition.getStreams().get(0);
        assertEquals(8, shot.getDelayTicks());
        assertEquals(60f, shot.getOffsetX(), 0.0001f);
        assertEquals(0f, shot.getOffsetY(), 0.0001f);
        assertTrue(shot.isFromPamVisualCenter());
        assertTrue(shot.isApplyFacing());
        assertTrue(shot.getCapabilities().isUnstoppable());
        assertTrue(shot.getCapabilities().isPierceAll());
        assertTrue(shot.getCapabilities().isInstantKill());
        assertTrue(shot.getCapabilities().isEnemyOnly());
        assertEquals("citron_plantfood_orb_hit", shot.getCapabilities().getHitEffectId());
        assertFalse(PlantFoodProjectileLaunch.shouldLaunchOnce(7, 8, false));
        assertTrue(PlantFoodProjectileLaunch.shouldLaunchOnce(8, 8, false));
        assertFalse(PlantFoodProjectileLaunch.shouldLaunchOnce(8, 8, true));

        Plant plant = PlantFactory.create(PlantName.CITRON);
        plant.setPosition(new Position(100, 200));
        plant.setFacing(1);
        Position right = PlantFoodProjectileLaunch.spawnPosition(plant, shot);
        assertEquals(100 + 60 * SunSourceLocator.PAM_RENDER_SCALE, right.getX(), 0.0001);
        assertEquals(200, right.getY(), 0.0001);
        plant.setFacing(-1);
        Position left = PlantFoodProjectileLaunch.spawnPosition(plant, shot);
        assertEquals(100 - 60 * SunSourceLocator.PAM_RENDER_SCALE, left.getX(), 0.0001);
        assertEquals(180d, PlantFoodProjectileLaunch.angleDegrees(plant, shot), 0.0001);
    }
}
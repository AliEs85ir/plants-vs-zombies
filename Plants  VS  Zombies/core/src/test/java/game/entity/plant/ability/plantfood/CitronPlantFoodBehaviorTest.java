package game.entity.plant.ability.plantfood;

import app.enums.effect.EffectType;
import app.enums.obstacle.ObstacleType;
import app.enums.plant.PlantName;
import common.model.Position;
import game.entity.effect.CharmEffect;
import game.entity.obstacle.Obstacle;
import game.entity.obstacle.ability.TakeDamageAbility;
import game.entity.plant.Plant;
import game.entity.plant.PlantHealth;
import game.entity.plant.upgrade.PlantUpgradeConfigLoader;
import game.entity.projectile.Projectile;
import game.entity.projectile.ProjectileFireTransform;
import game.entity.projectile.ShooterProjectile;
import game.entity.registry.PlantDefinitionLoader;
import game.entity.registry.PlantFoodDefinitionLoader;
import game.entity.registry.PlantFoodDefinitionRegistry;
import game.entity.zombie.Zombie;
import game.entity.zombie.strategy.takedamage.StandardTakingDamage;
import game.service.plant.PlantFactory;
import game.service.plant.PlantFoodAbilityFactory;
import game.service.plant.PlantFoodService;
import game.service.plant.PlantService;
import game.world.GameWorld;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CitronPlantFoodBehaviorTest {
    @BeforeAll
    static void loadDefinitions() {
        PlantDefinitionLoader.load();
        PlantFoodDefinitionLoader.load();
        PlantUpgradeConfigLoader.get();
    }

    @Test
    void pfSpawnsExactlyOneProjectileAtTickEightAndNotBefore() {
        GameWorld world = new GameWorld();
        PlantService plantService = new PlantService(world);
        Plant plant = citronAt(80, 40);
        RapidFirePlantFoodAbility ability = (RapidFirePlantFoodAbility) PlantFoodAbilityFactory.create(PlantName.CITRON);
        plant.setAbilities(new ArrayList<>(List.of(ability)));
        plant.getPlantFoodState().activate(0, 0);

        for (int tick = 0; tick <= 7; tick++) {
            ability.update(plant, plantService, tick);
            assertEquals(0, world.getProjectiles().size());
        }
        ability.update(plant, plantService, 8);
        assertEquals(1, world.getProjectiles().size());
        ability.update(plant, plantService, 8);
        ability.update(plant, plantService, 9);
        assertEquals(1, world.getProjectiles().size());

        Projectile projectile = world.getProjectiles().get(0);
        assertEquals(140, projectile.getPosition().getX(), 0.0001);
        assertEquals(40, projectile.getPosition().getY(), 0.0001);
        assertTrue(projectile.isUnstoppable());
        assertTrue(projectile.isInfinitePierce());
        assertTrue(projectile.isInstantKill());
        assertTrue(projectile.isEnemyOnly());
        assertTrue(projectile.isPlantFoodVisual());
        assertEquals("citron_plantfood_orb_hit", projectile.getHitEffectId());
    }

    @Test
    void deadCitronDoesNotLaunch() {
        GameWorld world = new GameWorld();
        PlantService plantService = new PlantService(world);
        Plant plant = citronAt(80, 40);
        RapidFirePlantFoodAbility ability = (RapidFirePlantFoodAbility) PlantFoodAbilityFactory.create(PlantName.CITRON);
        plant.getPlantFoodState().activate(0, 0);
        ability.update(plant, plantService, 0);
        plant.getHealth().die();
        ability.update(plant, plantService, 8);
        assertEquals(0, world.getProjectiles().size());
    }

    @Test
    void incompletePlantFoodIsCompletedNotReplaced() {
        Plant plant = citronAt(80, 40);
        plant.getPlantFoodState().activate(3, 0);
        assertFalse(PlantFoodService.canActivatePlantFoodEffect(plant));
        assertEquals(3, plant.getPlantFoodState().getStartTick());
    }

    @Test
    void instantKillPiercesAndDoesNotDuplicate() {
        GameWorld world = new GameWorld();
        Plant plant = citronAt(80, 40);
        ShooterProjectile projectile = orb(plant);
        Zombie first = zombie(world, 400, 40, 9999);
        Zombie second = zombie(world, 500, 40, 8888);

        projectile.handleHit(first, 0);
        assertTrue(first.isDead());
        assertTrue(projectile.isActive());
        assertEquals(400, projectile.getLastHitEffectWorld().getX(), 0.0001);
        assertEquals(40, projectile.getLastHitEffectWorld().getY(), 0.0001);
        assertNotEquals(80, projectile.getLastHitEffectWorld().getX(), 0.0001);

        projectile.handleHit(first, 1);
        assertEquals(1, projectile.getHitZombies().size());

        projectile.handleHit(second, 2);
        assertTrue(second.isDead());
        assertTrue(projectile.isActive());
        assertEquals(2, projectile.getHitZombies().size());
    }

    @Test
    void unstoppableDoesNotStopOnObstacle() {
        ShooterProjectile projectile = orb(citronAt(80, 40));
        Obstacle obstacle = new Obstacle(new Position(200, 40), ObstacleType.EGYPT_GRAVE);
        TakeDamageAbility health = new TakeDamageAbility(400);
        obstacle.addAbility(health);
        projectile.handleHit(obstacle, 0);
        assertTrue(projectile.isActive());
        assertTrue(projectile.getHitObstacles().contains(obstacle));
        projectile.handleHit(obstacle, 1);
        assertEquals(1, projectile.getHitObstacles().size());
    }

    @Test
    void friendlyPlantAndCharmedZombieAreUnharmed() {
        GameWorld world = new GameWorld();
        Plant ally = PlantFactory.create(PlantName.PEASHOOTER);
        ally.setHealth(new PlantHealth(100));
        ShooterProjectile projectile = orb(citronAt(80, 40));
        Zombie charmed = zombie(world, 300, 40, 50);
        charmed.addEffect(new CharmEffect(1));
        assertTrue(charmed.hasEffect(EffectType.CHARM));
        projectile.handleHit(charmed, 0);
        assertFalse(charmed.isDead());
        assertTrue(projectile.getHitZombies().isEmpty());
        assertEquals(100, ally.getHealth().getCurrentHealth());
    }

    @Test
    void plantFoodOrbStaysExcludedFromTorchwoodAndLeavesBoardViaDestroy() {
        ShooterProjectile projectile = orb(citronAt(80, 40));
        assertFalse(ProjectileFireTransform.isEligible(projectile));
        projectile.destroy();
        assertFalse(projectile.isActive());
    }

    private static Plant citronAt(double x, double y) {
        Plant plant = PlantFactory.create(PlantName.CITRON);
        plant.setPosition(new Position(x, y));
        plant.setFacing(1);
        return plant;
    }

    private static ShooterProjectile orb(Plant plant) {
        ShooterProjectile projectile = new ShooterProjectile(plant, 5, 1.5, 0);
        projectile.setSpawnWorld(new Position(140, 40));
        projectile.applyCapabilities(PlantFoodDefinitionRegistry.get(PlantName.CITRON).getStreams().get(0).getCapabilities());
        return projectile;
    }

    private static Zombie zombie(GameWorld world, double x, double y, double health) {
        return new Zombie(
            health,
            null,
            null,
            new StandardTakingDamage(),
            new Position(x, y),
            new ArrayList<>(),
            world,
            null,
            null
        );
    }
}
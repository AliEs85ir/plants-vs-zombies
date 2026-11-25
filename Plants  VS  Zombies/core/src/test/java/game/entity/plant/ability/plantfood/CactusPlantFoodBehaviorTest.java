package game.entity.plant.ability.plantfood;

import app.enums.effect.EffectType;
import app.enums.plant.PlantName;
import common.model.Position;
import game.engine.time.ScheduledTaskManager;
import game.entity.effect.CharmEffect;
import game.entity.plant.Plant;
import game.entity.plant.PlantHealth;
import game.entity.plant.combat.PlantFoodProjectileLaunch;
import game.entity.plant.plantfood.PlantFoodShot;
import game.entity.plant.upgrade.PlantUpgradeConfigLoader;
import game.entity.projectile.Projectile;
import game.entity.projectile.ProjectileCapabilities;
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
import game.service.projectile.ProjectileFactory;
import game.world.GameWorld;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CactusPlantFoodBehaviorTest {
    @BeforeAll
    static void loadDefinitions() {
        PlantDefinitionLoader.load();
        PlantFoodDefinitionLoader.load();
        PlantUpgradeConfigLoader.get();
    }

    @Test
    void pfEntersModeAndReturnsNormalAfter150Ticks() {
        Plant plant = cactusAt(80, 40);
        DurationPlantFoodAbility ability = (DurationPlantFoodAbility) PlantFoodAbilityFactory.create(PlantName.CACTUS);
        plant.setAbilities(new ArrayList<>(List.of(ability)));
        plant.getPlantFoodState().activate(0, 0);

        PlantService plantService = new PlantService(new GameWorld());
        for (int tick = 0; tick < 150; tick++) {
            ability.update(plant, plantService, tick);
            assertTrue(plant.getPlantFoodState().isActive(), "still active at " + tick);
        }
        ability.update(plant, plantService, 150);
        assertFalse(plant.getPlantFoodState().isActive());
    }

    @Test
    void attackAtTSpawnsPlantFoodProjectileAtTPlus5() {
        GameWorld world = new GameWorld();
        PlantService plantService = new PlantService(world);
        Plant plant = cactusAt(80, 40);
        plant.getPlantFoodState().activate(0, 0);
        PlantFoodShot shot = PlantFoodProjectileLaunch.empoweredAttackShot(plant);
        assertNotNull(shot);
        assertEquals(5, shot.getDelayTicks());

        ScheduledTaskManager tasks = new ScheduledTaskManager();
        long attackTick = 10;
        tasks.scheduleRelative(attackTick, shot.getDelayTicks(), () ->
            PlantFoodProjectileLaunch.spawnEmpoweredShot(plant, plantService, shot, plant.getAttackDamage(), 1.5)
        );
        tasks.executeTasks(attackTick + 4);
        assertEquals(0, world.getProjectiles().size());
        tasks.executeTasks(attackTick + 5);
        assertEquals(1, world.getProjectiles().size());

        Projectile projectile = world.getProjectiles().get(0);
        Position expected = PlantFoodProjectileLaunch.spawnPosition(plant, shot);
        assertEquals(expected.getX(), projectile.getPosition().getX(), 0.0001);
        assertEquals(expected.getY(), projectile.getPosition().getY(), 0.0001);
        assertTrue(projectile.isInfinitePierce());
        assertTrue(projectile.isEnemyOnly());
        assertTrue(projectile.isPlantFoodVisual());
        assertEquals("cactus_plantfood_hit", projectile.getHitEffectId());
        assertFalse(projectile.isInstantKill());
    }

    @Test
    void normalCactusAttackStillPresentWhenNotInPlantFood() {
        GameWorld world = new GameWorld();
        PlantService plantService = new PlantService(world);
        Plant plant = cactusAt(80, 40);
        assertNull(PlantFoodProjectileLaunch.empoweredAttackShot(plant));
        Position pos = new Position(plant.getPosition().getX() + 59, plant.getPosition().getY() + 59);
        ShooterProjectile projectile = ProjectileFactory.createShooterProjectile(plant, plant.getAttackDamage(), pos, 1.5, 0);
        projectile.setMaxTargets(3);
        plantService.addProjectile(projectile);
        assertFalse(projectile.isPlantFoodVisual());
        assertFalse(projectile.isInfinitePierce());
        assertEquals(3, projectile.getMaxTargets());
    }

    @Test
    void pierceThreeEnemiesAndProjectileSurvivesWithoutDuplicateHits() {
        GameWorld world = new GameWorld();
        ShooterProjectile projectile = pfSpike(cactusAt(80, 40));
        Zombie first = zombie(world, 300, 40, 40);
        Zombie second = zombie(world, 400, 40, 40);
        Zombie third = zombie(world, 500, 40, 40);

        projectile.handleHit(first, 0);
        assertEquals(300, projectile.getLastHitEffectWorld().getX(), 0.0001);
        assertEquals(40, projectile.getLastHitEffectWorld().getY(), 0.0001);
        assertNotEquals(80, projectile.getLastHitEffectWorld().getX(), 0.0001);
        projectile.handleHit(second, 1);
        projectile.handleHit(third, 2);
        assertEquals(3, projectile.getHitZombies().size());
        assertTrue(projectile.isActive());
        assertEquals("cactus_plantfood_hit", projectile.getHitEffectId());
        assertEquals(500, projectile.getLastHitEffectWorld().getX(), 0.0001);

        projectile.handleHit(first, 3);
        assertEquals(3, projectile.getHitZombies().size());
        assertTrue(projectile.isActive());
    }

    @Test
    void friendlyPlantAndCharmedZombieAreUnharmed() {
        GameWorld world = new GameWorld();
        Plant ally = PlantFactory.create(PlantName.PEASHOOTER);
        ally.setHealth(new PlantHealth(100));
        ShooterProjectile projectile = pfSpike(cactusAt(80, 40));
        Zombie charmed = zombie(world, 300, 40, 50);
        charmed.addEffect(new CharmEffect(1));
        assertTrue(charmed.hasEffect(EffectType.CHARM));
        projectile.handleHit(charmed, 0);
        assertFalse(charmed.isDead());
        assertTrue(projectile.getHitZombies().isEmpty());
        assertEquals(100, ally.getHealth().getCurrentHealth());
    }

    @Test
    void afterExpireNewShotsAreNormalAndOldPfShotsUnchanged() {
        GameWorld world = new GameWorld();
        PlantService plantService = new PlantService(world);
        Plant plant = cactusAt(80, 40);
        DurationPlantFoodAbility pf = (DurationPlantFoodAbility) PlantFoodAbilityFactory.create(PlantName.CACTUS);
        plant.setAbilities(new ArrayList<>(List.of(pf)));
        plant.getPlantFoodState().activate(0, 0);
        PlantFoodShot shot = PlantFoodProjectileLaunch.empoweredAttackShot(plant);
        Projectile oldShot = PlantFoodProjectileLaunch.spawnEmpoweredShot(
            plant, plantService, shot, plant.getAttackDamage(), 1.5
        );
        assertTrue(oldShot.isPlantFoodVisual());
        assertTrue(oldShot.isInfinitePierce());

        for (int tick = 0; tick <= 150; tick++) {
            pf.update(plant, plantService, tick);
        }
        assertFalse(plant.getPlantFoodState().isActive());
        assertTrue(oldShot.isPlantFoodVisual());
        assertTrue(oldShot.isInfinitePierce());
        assertEquals("cactus_plantfood_hit", oldShot.getHitEffectId());
        assertNull(PlantFoodProjectileLaunch.empoweredAttackShot(plant));

        Position pos = new Position(plant.getPosition().getX() + 59, plant.getPosition().getY() + 59);
        ShooterProjectile newest = ProjectileFactory.createShooterProjectile(plant, plant.getAttackDamage(), pos, 1.5, 0);
        newest.setMaxTargets(3);
        plantService.addProjectile(newest);
        assertFalse(newest.isPlantFoodVisual());
        assertFalse(newest.isInfinitePierce());
    }

    @Test
    void twoCactiAreIndependent() {
        Plant first = cactusAt(80, 40);
        Plant second = cactusAt(200, 40);
        DurationPlantFoodAbility firstAbility = (DurationPlantFoodAbility) PlantFoodAbilityFactory.create(PlantName.CACTUS);
        DurationPlantFoodAbility secondAbility = (DurationPlantFoodAbility) PlantFoodAbilityFactory.create(PlantName.CACTUS);
        first.setAbilities(new ArrayList<>(List.of(firstAbility)));
        second.setAbilities(new ArrayList<>(List.of(secondAbility)));
        PlantService plantService = new PlantService(new GameWorld());

        first.getPlantFoodState().activate(0, 0);
        firstAbility.update(first, plantService, 0);
        second.getPlantFoodState().activate(20, 0);
        secondAbility.update(second, plantService, 20);

        firstAbility.update(first, plantService, 150);
        assertFalse(first.getPlantFoodState().isActive());
        assertTrue(second.getPlantFoodState().isActive());
        secondAbility.update(second, plantService, 169);
        assertTrue(second.getPlantFoodState().isActive());
        secondAbility.update(second, plantService, 170);
        assertFalse(second.getPlantFoodState().isActive());
    }

    @Test
    void incompletePlantFoodIsCompletedNotReplaced() {
        Plant plant = cactusAt(80, 40);
        plant.getPlantFoodState().activate(4, 0);
        assertFalse(PlantFoodService.canActivatePlantFoodEffect(plant));
        assertEquals(4, plant.getPlantFoodState().getStartTick());
    }

    @Test
    void deadCactusDoesNotSpawnScheduledProjectile() {
        GameWorld world = new GameWorld();
        PlantService plantService = new PlantService(world);
        Plant plant = cactusAt(80, 40);
        plant.getPlantFoodState().activate(0, 0);
        PlantFoodShot shot = PlantFoodProjectileLaunch.empoweredAttackShot(plant);
        plant.getHealth().die();
        assertNull(PlantFoodProjectileLaunch.spawnEmpoweredShot(plant, plantService, shot, plant.getAttackDamage(), 1.5));
        assertEquals(0, world.getProjectiles().size());
    }

    @Test
    void empoweredShotLookupIsNullWhenNotInPlantFood() {
        Plant plant = cactusAt(80, 40);
        assertNull(PlantFoodProjectileLaunch.empoweredAttackShot(plant));
        plant.getPlantFoodState().activate(0, 0);
        assertNotNull(PlantFoodProjectileLaunch.empoweredAttackShot(plant));
    }

    private static Plant cactusAt(double x, double y) {
        Plant plant = PlantFactory.create(PlantName.CACTUS);
        plant.setPosition(new Position(x, y));
        plant.setFacing(1);
        return plant;
    }

    private static ShooterProjectile pfSpike(Plant plant) {
        ShooterProjectile projectile = new ShooterProjectile(plant, plant.getAttackDamage(), 1.5, 0);
        projectile.setSpawnWorld(new Position(145, 95));
        ProjectileCapabilities caps = PlantFoodDefinitionRegistry.get(PlantName.CACTUS)
            .getEmpoweredAttackShot()
            .getCapabilities();
        projectile.applyCapabilities(caps);
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

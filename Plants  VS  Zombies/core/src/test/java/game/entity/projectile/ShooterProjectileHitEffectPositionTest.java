package game.entity.projectile;

import app.enums.plant.PlantName;
import common.model.Position;
import game.entity.plant.Plant;
import game.entity.plant.upgrade.PlantUpgradeConfigLoader;
import game.entity.registry.PlantDefinitionLoader;
import game.entity.registry.PlantFoodDefinitionLoader;
import game.entity.registry.PlantFoodDefinitionRegistry;
import game.entity.zombie.Zombie;
import game.entity.zombie.strategy.takedamage.StandardTakingDamage;
import game.service.plant.PlantFactory;
import game.world.GameWorld;
import graphics.projectile.ProjectileRenderInstance;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ShooterProjectileHitEffectPositionTest {
    @BeforeAll
    static void loadDefinitions() {
        PlantDefinitionLoader.load();
        PlantFoodDefinitionLoader.load();
        PlantUpgradeConfigLoader.get();
    }

    @Test
    void hitEffectWorldIsZombieCenterNotPlantOrSpawn() {
        Plant plant = plantAt(PlantName.CITRON, 80, 40);
        Position plantPos = new Position(80, 40);
        ShooterProjectile projectile = new ShooterProjectile(plant, 5, 1.5, 0);
        projectile.setSpawnWorld(plantPos);
        projectile.applyCapabilities(
            PlantFoodDefinitionRegistry.get(PlantName.CITRON).getStreams().get(0).getCapabilities()
        );

        GameWorld world = new GameWorld();
        Zombie zombie = zombie(world, 400, 40, 9999);
        projectile.handleHit(zombie, 0);

        Position hit = projectile.getLastHitEffectWorld();
        assertNotNull(hit);
        assertEquals(zombie.getPosition().getX(), hit.getX(), 0.0001);
        assertEquals(zombie.getPosition().getY(), hit.getY(), 0.0001);
        assertNotEquals(plant.getPosition().getX(), hit.getX(), 0.0001);
        assertNotEquals(projectile.getSpawnWorld().getX(), hit.getX(), 0.0001);

        ProjectileRenderInstance instance = new ProjectileRenderInstance(projectile);
        instance.update(0f);
        Position rendered = instance.getRenderPosition();
        assertNotNull(rendered);
        assertEquals(zombie.getPosition().getX(), rendered.getX(), 0.0001);
        assertEquals(zombie.getPosition().getY(), rendered.getY(), 0.0001);
        assertNotEquals(plant.getPosition().getX(), rendered.getX(), 0.0001);
    }

    @Test
    void cactusHitEffectWorldIsZombieNotPlant() {
        Plant plant = plantAt(PlantName.CACTUS, 80, 40);
        ShooterProjectile projectile = new ShooterProjectile(plant, plant.getAttackDamage(), 1.5, 0);
        projectile.setSpawnWorld(new Position(plant.getPosition().getX(), plant.getPosition().getY()));
        projectile.applyCapabilities(
            PlantFoodDefinitionRegistry.get(PlantName.CACTUS).getEmpoweredAttackShot().getCapabilities()
        );
        Zombie zombie = zombie(new GameWorld(), 300, 40, 40);
        projectile.handleHit(zombie, 0);
        Position hit = projectile.getLastHitEffectWorld();
        assertEquals(300, hit.getX(), 0.0001);
        assertEquals(40, hit.getY(), 0.0001);
        assertNotEquals(plant.getPosition().getX(), hit.getX(), 0.0001);
    }

    private static Plant plantAt(PlantName name, double x, double y) {
        Plant plant = PlantFactory.create(name);
        plant.setPosition(new Position(x, y));
        plant.setFacing(1);
        return plant;
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
package game.entity.plant.ability.plantfood;

import app.enums.effect.EffectType;
import app.enums.plant.PlantName;
import common.model.Position;
import game.entity.plant.Plant;
import game.entity.plant.ability.AbstractPlantAbility;
import game.entity.plant.combat.StarfruitCombat;
import game.entity.plant.plantfood.PlantFoodFireLine;
import game.entity.plant.upgrade.PlantUpgradeConfigLoader;
import game.entity.projectile.Projectile;
import game.entity.projectile.ShooterProjectile;
import game.entity.registry.PlantDefinitionLoader;
import game.entity.registry.PlantFoodDefinitionLoader;
import game.entity.registry.PlantFoodDefinitionRegistry;
import game.entity.zombie.Zombie;
import game.entity.zombie.strategy.takedamage.StandardTakingDamage;
import game.service.plant.PlantFactory;
import game.service.plant.PlantFoodAbilityFactory;
import game.service.plant.PlantService;
import game.world.GameWorld;
import game.world.playground.Cell;
import game.world.playground.Grid;
import game.world.playground.MapConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FourShooterPlantFoodBehaviorTest {
    @BeforeAll
    static void loadDefinitions() {
        PlantDefinitionLoader.load();
        PlantFoodDefinitionLoader.load();
        PlantUpgradeConfigLoader.get();
    }

    @Test
    void firePeaLasts60TicksDamagesEveryTwoAndStops() {
        Lawn lawn = lawn();
        Plant plant = plantOn(lawn, PlantName.FIRE_PEASHOOTER, 0, 2);
        DurationPlantFoodAbility ability = (DurationPlantFoodAbility) PlantFoodAbilityFactory.create(PlantName.FIRE_PEASHOOTER);
        CountingAbility normal = new CountingAbility();
        plant.setAbilities(new ArrayList<>(List.of(ability, normal)));
        plant.getPlantFoodState().activate(0, 0);

        Cell fireCell = lawn.grid.getCell(0, 3);
        Zombie occupant = zombie(lawn.world, fireCell.getWorldX(), fireCell.getWorldY(), 4000);
        double health = occupant.getCurrentHealth();

        ability.update(plant, lawn.plants, 0);
        assertTrue(plant.getPlantFoodState().isActive());
        assertEquals(0, normal.updates);
        assertTrue(occupant.getCurrentHealth() < health);
        assertTrue(occupant.hasEffect(EffectType.FIRE));
        health = occupant.getCurrentHealth();

        ability.update(plant, lawn.plants, 1);
        assertEquals(health, occupant.getCurrentHealth(), 0.0001);

        ability.update(plant, lawn.plants, 2);
        assertTrue(occupant.getCurrentHealth() < health);

        occupant.setPosition(new Position(fireCell.getWorldX() + 800, fireCell.getWorldY()));
        health = occupant.getCurrentHealth();
        ability.update(plant, lawn.plants, 4);
        assertEquals(health, occupant.getCurrentHealth(), 0.0001);

        Zombie entering = zombie(lawn.world, fireCell.getWorldX(), fireCell.getWorldY(), 4000);
        ability.update(plant, lawn.plants, 6);
        assertTrue(entering.getCurrentHealth() < 4000);
        assertTrue(entering.hasEffect(EffectType.FIRE));

        for (int tick = 7; tick < 60; tick++) {
            ability.update(plant, lawn.plants, tick);
            assertTrue(plant.getPlantFoodState().isActive(), "active at " + tick);
        }
        double after = entering.getCurrentHealth();
        ability.update(plant, lawn.plants, 60);
        assertFalse(plant.getPlantFoodState().isActive());
        assertEquals(after, entering.getCurrentHealth(), 0.0001);

        tickPlant(plant, lawn.plants, 61);
        assertTrue(normal.updates >= 1);
    }

    @Test
    void fireLineGoesToBoundaryAndNotOffPlayground() {
        Lawn lawn = lawn();
        Plant plant = plantOn(lawn, PlantName.FIRE_PEASHOOTER, 1, 2);
        plant.setFacing(1);
        List<Cell> forward = PlantFoodFireLine.cellsInFront(lawn.grid, plant);
        assertEquals(6, forward.size());
        assertEquals(3, forward.get(0).getCol());
        assertEquals(8, forward.get(forward.size() - 1).getCol());

        Plant last = plantOn(lawn, PlantName.FIRE_PEASHOOTER, 1, 8);
        last.setFacing(1);
        assertTrue(PlantFoodFireLine.cellsInFront(lawn.grid, last).isEmpty());

        plant.setFacing(-1);
        List<Cell> back = PlantFoodFireLine.cellsInFront(lawn.grid, plant);
        assertEquals(2, back.size());
        assertEquals(1, back.get(0).getCol());
        assertEquals(0, back.get(1).getCol());
    }

    @Test
    void fireLineCleansUpOnDeathAndInstancesAreIndependent() {
        Lawn lawn = lawn();
        Plant first = plantOn(lawn, PlantName.FIRE_PEASHOOTER, 0, 1);
        Plant second = plantOn(lawn, PlantName.FIRE_PEASHOOTER, 2, 1);
        DurationPlantFoodAbility a1 = (DurationPlantFoodAbility) PlantFoodAbilityFactory.create(PlantName.FIRE_PEASHOOTER);
        DurationPlantFoodAbility a2 = (DurationPlantFoodAbility) PlantFoodAbilityFactory.create(PlantName.FIRE_PEASHOOTER);
        first.setAbilities(new ArrayList<>(List.of(a1)));
        second.setAbilities(new ArrayList<>(List.of(a2)));
        first.getPlantFoodState().activate(0, 0);
        second.getPlantFoodState().activate(0, 0);
        Zombie z1 = zombie(lawn.world, lawn.grid.getCell(0, 2).getWorldX(), lawn.grid.getCell(0, 2).getWorldY(), 4000);
        Zombie z2 = zombie(lawn.world, lawn.grid.getCell(2, 2).getWorldX(), lawn.grid.getCell(2, 2).getWorldY(), 4000);
        a1.update(first, lawn.plants, 0);
        a2.update(second, lawn.plants, 0);
        assertTrue(z1.getCurrentHealth() < 4000);
        assertTrue(z2.getCurrentHealth() < 4000);
        first.getHealth().die();
        double z1Health = z1.getCurrentHealth();
        a1.update(first, lawn.plants, 2);
        double z2Health = z2.getCurrentHealth();
        a2.update(second, lawn.plants, 2);
        assertEquals(z1Health, z1.getCurrentHealth(), 0.0001);
        assertTrue(z2.getCurrentHealth() < z2Health);
        assertTrue(second.getPlantFoodState().isActive());
    }

    @Test
    void starfruitFiresFiveDirectionsEachTickFor30AndDisablesNormal() {
        GameWorld world = new GameWorld();
        PlantService plantService = new PlantService(world);
        Plant plant = PlantFactory.create(PlantName.STARFRUIT);
        plant.setPosition(new Position(80, 40));
        plant.setFacing(1);
        RapidFirePlantFoodAbility ability = (RapidFirePlantFoodAbility) PlantFoodAbilityFactory.create(PlantName.STARFRUIT);
        CountingAbility normal = new CountingAbility();
        plant.setAbilities(new ArrayList<>(List.of(ability, normal)));
        plant.getPlantFoodState().activate(0, 0);

        tickPlant(plant, plantService, 0);
        assertEquals(StarfruitCombat.SHOTS.length, world.getProjectiles().size());
        assertEquals(0, normal.updates);

        for (int tick = 1; tick < 30; tick++) {
            ability.update(plant, plantService, tick);
        }
        assertEquals(30 * StarfruitCombat.SHOTS.length, world.getProjectiles().size());
        int before = world.getProjectiles().size();
        ability.update(plant, plantService, 30);
        assertEquals(before, world.getProjectiles().size());
        assertFalse(plant.getPlantFoodState().isActive());
        assertTrue(world.getProjectiles().get(0).isActive());
        tickPlant(plant, plantService, 31);
        assertTrue(normal.updates >= 1);
    }

    @Test
    void gooLaunchesOnceAtTick11WithCitronCapabilities() {
        GameWorld world = new GameWorld();
        PlantService plantService = new PlantService(world);
        Plant plant = PlantFactory.create(PlantName.GOO_PEASHOOTER);
        plant.setPosition(new Position(80, 40));
        plant.setFacing(1);
        RapidFirePlantFoodAbility ability = (RapidFirePlantFoodAbility) PlantFoodAbilityFactory.create(PlantName.GOO_PEASHOOTER);
        CountingAbility normal = new CountingAbility();
        plant.setAbilities(new ArrayList<>(List.of(ability, normal)));
        plant.getPlantFoodState().activate(0, 0);

        for (int tick = 0; tick <= 10; tick++) {
            ability.update(plant, plantService, tick);
            assertEquals(0, world.getProjectiles().size());
        }
        ability.update(plant, plantService, 11);
        assertEquals(1, world.getProjectiles().size());
        ability.update(plant, plantService, 11);
        ability.update(plant, plantService, 12);
        assertEquals(1, world.getProjectiles().size());
        Projectile projectile = world.getProjectiles().get(0);
        assertEquals(260, projectile.getPosition().getX(), 0.0001);
        assertEquals(60, projectile.getPosition().getY(), 0.0001);
        assertTrue(projectile.isUnstoppable());
        assertTrue(projectile.isInfinitePierce());
        assertTrue(projectile.isInstantKill());
        assertTrue(projectile.isEnemyOnly());
        assertTrue(projectile.isPlantFoodVisual());
        assertTrue(projectile.getHitEffectId() == null || projectile.getHitEffectId().isBlank());

        Zombie first = zombie(world, 400, 40, 9999);
        Zombie second = zombie(world, 500, 40, 8888);
        projectile.handleHit(first, 0);
        assertTrue(first.isDead());
        assertTrue(projectile.isActive());
        projectile.handleHit(second, 1);
        assertTrue(second.isDead());
        assertEquals(2, ((ShooterProjectile) projectile).getHitZombies().size());
        assertFalse(plant.getPlantFoodState().isActive());
        tickPlant(plant, plantService, 12);
        assertTrue(normal.updates >= 1);
        assertTrue(projectile.isUnstoppable());
    }

    @Test
    void megaGatlingShootsAtTPlus1ThenEvery3For40Ticks() {
        GameWorld world = new GameWorld();
        PlantService plantService = new PlantService(world);
        Plant plant = PlantFactory.create(PlantName.MEGA_GATLING_PEA);
        plant.setPosition(new Position(80, 40));
        plant.setFacing(1);
        RapidFirePlantFoodAbility ability = (RapidFirePlantFoodAbility) PlantFoodAbilityFactory.create(PlantName.MEGA_GATLING_PEA);
        CountingAbility normal = new CountingAbility();
        plant.setAbilities(new ArrayList<>(List.of(ability, normal)));
        plant.getPlantFoodState().activate(0, 0);

        ability.update(plant, plantService, 0);
        assertEquals(0, world.getProjectiles().size());
        ability.update(plant, plantService, 1);
        assertEquals(1, world.getProjectiles().size());
        assertEquals(180, world.getProjectiles().get(0).getPosition().getX(), 0.0001);
        assertEquals(65, world.getProjectiles().get(0).getPosition().getY(), 0.0001);
        ability.update(plant, plantService, 2);
        ability.update(plant, plantService, 3);
        assertEquals(1, world.getProjectiles().size());
        ability.update(plant, plantService, 4);
        assertEquals(2, world.getProjectiles().size());

        for (int tick = 5; tick < 40; tick++) {
            ability.update(plant, plantService, tick);
        }
        assertEquals(13, world.getProjectiles().size());
        Projectile old = world.getProjectiles().get(0);
        ability.update(plant, plantService, 40);
        assertEquals(13, world.getProjectiles().size());
        assertFalse(plant.getPlantFoodState().isActive());
        assertTrue(old.isActive());
        assertFalse(old.isPlantFoodVisual());
        tickPlant(plant, plantService, 41);
        assertTrue(normal.updates >= 1);
    }

    @Test
    void fourPlantsDoNotKeepNormalCombat() {
        assertFalse(PlantFoodDefinitionRegistry.get(PlantName.FIRE_PEASHOOTER).keepsNormalCombat());
        assertFalse(PlantFoodDefinitionRegistry.get(PlantName.STARFRUIT).keepsNormalCombat());
        assertFalse(PlantFoodDefinitionRegistry.get(PlantName.GOO_PEASHOOTER).keepsNormalCombat());
        assertFalse(PlantFoodDefinitionRegistry.get(PlantName.MEGA_GATLING_PEA).keepsNormalCombat());
        assertInstanceOf(DurationPlantFoodAbility.class, PlantFoodAbilityFactory.create(PlantName.FIRE_PEASHOOTER));
        assertInstanceOf(RapidFirePlantFoodAbility.class, PlantFoodAbilityFactory.create(PlantName.STARFRUIT));
        assertInstanceOf(RapidFirePlantFoodAbility.class, PlantFoodAbilityFactory.create(PlantName.GOO_PEASHOOTER));
        assertInstanceOf(RapidFirePlantFoodAbility.class, PlantFoodAbilityFactory.create(PlantName.MEGA_GATLING_PEA));
    }


    private static void tickPlant(Plant plant, PlantService plantService, long tick) {
        boolean plantFoodActive = plant.getPlantFoodState().isActive();
        boolean keepNormalCombat = false;
        if (plantFoodActive && plant.getDefinition() != null) {
            var definition = PlantFoodDefinitionRegistry.get(plant.getDefinition().getName());
            keepNormalCombat = definition != null && definition.keepsNormalCombat();
        }
        for (game.entity.plant.ability.PlantAbility ability : plant.getAbilities()) {
            if (ability == null) {
                continue;
            }
            if (ability.isPlantFoodAbility()) {
                if (plantFoodActive) {
                    ability.update(plant, plantService, tick);
                }
            } else if (!plantFoodActive || keepNormalCombat) {
                ability.update(plant, plantService, tick);
            }
        }
    }

    private static Lawn lawn() {
        GameWorld world = new GameWorld();
        Grid grid = new Grid(MapConfig.rectangular(5, 9, 80f, 80f));
        world.setGrid(grid);
        return new Lawn(world, new PlantService(world), grid);
    }

    private static Plant plantOn(Lawn lawn, PlantName name, int row, int col) {
        Plant plant = PlantFactory.create(name);
        plant.setFacing(1);
        Cell cell = lawn.grid.getCell(row, col);
        cell.addPlant(plant);
        plant.setPosition(new Position(cell.getWorldX(), cell.getWorldY()));
        return plant;
    }

    private static Zombie zombie(GameWorld world, double x, double y, double health) {
        Zombie zombie = new Zombie(
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
        world.getZombies().add(zombie);
        return zombie;
    }

    private static final class CountingAbility extends AbstractPlantAbility {
        private int updates;

        @Override
        protected void doUpdate(Plant plant, PlantService plantService, long currentTick) {
            updates++;
        }
    }

    private record Lawn(GameWorld world, PlantService plants, Grid grid) {
    }
}

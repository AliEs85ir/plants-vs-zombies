package game.entity.plant.ability;

import app.enums.obstacle.ObstacleType;
import app.enums.plant.PlantName;
import common.model.Position;
import game.engine.time.GameClock;
import game.entity.obstacle.Obstacle;
import game.entity.obstacle.ability.TakeDamageAbility;
import game.entity.plant.Plant;
import game.entity.registry.PlantDefinitionLoader;
import game.service.plant.PlantFactory;
import game.world.playground.Cell;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GraveBusterAbilityTest {
    @BeforeAll
    static void loadDefinitions() {
        PlantDefinitionLoader.load();
    }

    @Test
    void attackPlaysOnceThenAttack1LoopsAndDamagesPerTick() {
        Cell cell = cell();
        Obstacle grave = grave(200);
        cell.setObstacle(grave);

        Plant plant = buster(cell);
        GraveBusterAbility ability = abilityOf(plant);

        ability.update(plant, null, 0);
        assertTrue(ability.hasStartedAttack());
        assertFalse(ability.isAttack1Looping());
        assertSame(grave, ability.getTargetGrave());
        assertEquals(200, health(grave), 0.0001);

        ability.update(plant, null, GraveBusterAbility.ATTACK_DURATION_TICKS - 1);
        assertFalse(ability.isAttack1Looping());
        assertEquals(200, health(grave), 0.0001);

        ability.update(plant, null, GraveBusterAbility.ATTACK_DURATION_TICKS);
        assertTrue(ability.isAttack1Looping());
        assertEquals(150, health(grave), 0.0001);

        ability.update(plant, null, GraveBusterAbility.ATTACK_DURATION_TICKS);
        assertEquals(150, health(grave), 0.0001);

        ability.update(plant, null, GraveBusterAbility.ATTACK_DURATION_TICKS + 1);
        assertEquals(100, health(grave), 0.0001);
        assertFalse(ability.isFinished());
    }

    @Test
    void lethalDamageRemovesBusterSameTickWithoutExtraHit() {
        Cell cell = cell();
        Obstacle grave = grave(50);
        cell.setObstacle(grave);

        Plant plant = buster(cell);
        GraveBusterAbility ability = abilityOf(plant);

        ability.update(plant, null, 0);
        ability.update(plant, null, GraveBusterAbility.ATTACK_DURATION_TICKS);
        assertEquals(0, health(grave), 0.0001);
        assertTrue(GraveBusterAbility.isGraveDestroyed(grave));
        assertTrue(ability.isFinished());

        ability.update(plant, null, GraveBusterAbility.ATTACK_DURATION_TICKS + 1);
        assertEquals(0, health(grave), 0.0001);
    }

    @Test
    void otherSystemGraveDeathStopsAttackWithoutNpe() {
        Cell cell = cell();
        Obstacle grave = grave(400);
        cell.setObstacle(grave);

        Plant plant = buster(cell);
        GraveBusterAbility ability = abilityOf(plant);
        ability.update(plant, null, 0);
        ability.update(plant, null, GraveBusterAbility.ATTACK_DURATION_TICKS);
        assertEquals(350, health(grave), 0.0001);

        cell.clearObstacle();
        grave.setDead(true);
        ability.update(plant, null, GraveBusterAbility.ATTACK_DURATION_TICKS + 1);
        assertTrue(ability.isFinished());
        assertEquals(350, health(grave), 0.0001);
    }

    @Test
    void multipleBustersLockIndependentGravesAndDoNotRetarget() {
        Cell cellA = new Cell(0, 0, 0, 0, 80, 80);
        Cell cellB = new Cell(0, 1, 80, 0, 80, 80);
        Obstacle graveA = grave(200);
        Obstacle graveB = grave(200);
        cellA.setObstacle(graveA);
        cellB.setObstacle(graveB);

        Plant a = buster(cellA);
        Plant b = buster(cellB);
        GraveBusterAbility abilityA = abilityOf(a);
        GraveBusterAbility abilityB = abilityOf(b);

        abilityA.update(a, null, 0);
        abilityB.update(b, null, 0);
        assertSame(graveA, abilityA.getTargetGrave());
        assertSame(graveB, abilityB.getTargetGrave());
        assertNotSame(abilityA.getTargetGrave(), abilityB.getTargetGrave());

        cellA.setObstacle(graveB);
        abilityA.update(a, null, GraveBusterAbility.ATTACK_DURATION_TICKS);
        assertTrue(abilityA.isFinished());
        assertEquals(200, health(graveA), 0.0001);
        assertEquals(200, health(graveB), 0.0001);

        abilityB.update(b, null, GraveBusterAbility.ATTACK_DURATION_TICKS);
        assertEquals(150, health(graveB), 0.0001);
        assertFalse(abilityB.isFinished());
    }

    @Test
    void attackDurationUsesPamMetadataAndTenTicksPerSecond() {
        assertEquals(10, GameClock.TICKS_PER_SECOND);
        assertEquals(1.0f, GraveBusterAbility.ATTACK_CLIP_SECONDS);
        assertEquals(10, GraveBusterAbility.ATTACK_DURATION_TICKS);
        assertEquals(50, GraveBusterAbility.DAMAGE_PER_TICK);
    }

    private static Plant buster(Cell cell) {
        Plant plant = PlantFactory.create(PlantName.GRAVE_BUSTER);
        plant.setPlantedTick(0);
        plant.setCell(cell);
        GraveBusterAbility ability = new GraveBusterAbility();
        plant.setAbilities(new ArrayList<>(List.of(ability)));
        cell.addPlant(plant);
        return plant;
    }

    private static GraveBusterAbility abilityOf(Plant plant) {
        return (GraveBusterAbility) plant.getAbilities().get(0);
    }

    private static Cell cell() {
        return new Cell(1, 2, 0, 0, 80, 80);
    }

    private static Obstacle grave(double health) {
        Obstacle obstacle = new Obstacle(new Position(0, 0), ObstacleType.EGYPT_GRAVE);
        obstacle.addAbility(new TakeDamageAbility(health));
        return obstacle;
    }

    private static double health(Obstacle obstacle) {
        return ((TakeDamageAbility) obstacle.getAbilities().get(0)).getCurrentHealth();
    }
}

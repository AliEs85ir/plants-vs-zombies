package game.world.minigame.vasebreaker;

import app.enums.plant.PlantName;
import app.enums.obstacle.ObstacleType;
import game.world.minigame.vasebreaker.VaseBreakerMode;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class VaseBreakerLevelConfigTest {

    @Test
    void laterLevelsIncreaseGargantuarAndZombieChances() {
        VaseBreakerLevelConfig level1 = VaseBreakerLevelConfig.forLevel(1);
        VaseBreakerLevelConfig level2 = VaseBreakerLevelConfig.forLevel(2);
        VaseBreakerLevelConfig level3 = VaseBreakerLevelConfig.forLevel(3);

        assertTrue(level2.gargantuarVaseChance() > level1.gargantuarVaseChance());
        assertTrue(level3.gargantuarVaseChance() > level2.gargantuarVaseChance());
        assertTrue(level2.randomVaseZombieChance() > level1.randomVaseZombieChance());
        assertTrue(level3.randomVaseZombieChance() > level2.randomVaseZombieChance());
    }

    @Test
    void vaseTypeChancesCoverAFullRoll() {
        VaseBreakerLevelConfig config = VaseBreakerLevelConfig.forLevel(2);
        assertEquals(
            100,
            config.randomVaseChance() + config.plantVaseChance() + config.gargantuarVaseChance()
        );
        assertEquals(ObstacleType.RANDOM_VASE, config.rollVaseType(0));
        assertEquals(ObstacleType.PLANT_VASE, config.rollVaseType(config.randomVaseChance()));
        assertEquals(
            ObstacleType.GARGANTUAR_VASE,
            config.rollVaseType(config.randomVaseChance() + config.plantVaseChance())
        );
    }

    @Test
    void modeTracksThreeLevelProgression() {
        VaseBreakerMode level1 = new VaseBreakerMode(List.of(PlantName.PEASHOOTER), List.of("Imp1"), 1);
        VaseBreakerMode level2 = new VaseBreakerMode(List.of(PlantName.PEASHOOTER), List.of("Imp1"), 2);
        VaseBreakerMode level3 = new VaseBreakerMode(List.of(PlantName.PEASHOOTER), List.of("Imp1"), 3);

        assertEquals(1, level1.getLevel());
        assertEquals(2, level2.getLevel());
        assertEquals(3, level3.getLevel());
        assertFalse(level1.isFinalLevel());
        assertFalse(level2.isFinalLevel());
        assertTrue(level3.isFinalLevel());
    }
}

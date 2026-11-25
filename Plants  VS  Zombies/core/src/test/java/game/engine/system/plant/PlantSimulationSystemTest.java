package game.engine.system.plant;

import app.enums.plant.PlantName;
import game.entity.plant.Plant;
import game.entity.registry.PlantDefinitionLoader;
import game.service.plant.PlantFactory;
import game.world.GameWorld;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PlantSimulationSystemTest {
    @BeforeAll
    static void loadDefinitions() {
        PlantDefinitionLoader.load();
    }

    @Test
    void liveWorldPlantListThrowsWhenAddPlantDuringForeach() {
        GameWorld world = new GameWorld();
        world.addPlant(PlantFactory.create(PlantName.PEASHOOTER));
        world.addPlant(PlantFactory.create(PlantName.SUNFLOWER));

        assertThrows(ConcurrentModificationException.class, () -> {
            for (Plant ignored : world.getPlants()) {
                world.addPlant(PlantFactory.create(PlantName.WALL_NUT));
            }
        });
    }

    @Test
    void snapshotOfWorldPlantListAllowsAddPlantDuringForeach() {
        GameWorld world = new GameWorld();
        world.addPlant(PlantFactory.create(PlantName.PEASHOOTER));
        world.addPlant(PlantFactory.create(PlantName.SUNFLOWER));

        assertDoesNotThrow(() -> {
            for (Plant ignored : new ArrayList<>(world.getPlants())) {
                world.addPlant(PlantFactory.create(PlantName.WALL_NUT));
            }
        });
        assertEquals(4, world.getPlants().size());
    }
}
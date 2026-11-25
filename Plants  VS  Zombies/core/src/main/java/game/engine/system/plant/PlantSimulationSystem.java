package game.engine.system.plant;

import game.engine.time.TickSystem;
import game.entity.plant.Plant;
import game.entity.plant.PlantCooldown;
import game.service.plant.PlantService;
import game.world.GameWorld;

import java.util.ArrayList;

// >>> Handles all plant related time-based behaviors.
public class PlantSimulationSystem implements TickSystem
{
    // -> Properties
    private final GameWorld gameWorld;
    private final PlantService plantService;
    private final ArrayList<Plant> plants;
    private final ArrayList<PlantCooldown> cooldownPlants;

    // -> Constructor
    public PlantSimulationSystem(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
        this.plantService = new PlantService(this.gameWorld);
        this.plants = gameWorld.getPlants();
        this.cooldownPlants = gameWorld.getPlantCooldowns();
    }

    // -> Methods
    // Updates plant system.
    @Override
    public void update(long currentTick)
    {
        // Snapshot like ZombieSimulationSystem: plant.update may add plants
        // (ImitaterReplacement.complete -> GameWorld.addPlant) to this live list.
        for (Plant plant : new ArrayList<>(plants)) {
            plant.update(plantService, currentTick);
        }
        for (PlantCooldown element: cooldownPlants)
            element.update(currentTick);

        gameWorld.getPlants().removeIf(Plant::isDead);
    }
}

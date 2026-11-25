package game.world.minigame;

import app.enums.plant.PlantName;
import game.component.physic.PhysicService;
import game.engine.GameSimulationEngine;
import game.world.GameWorld;
import graphics.GraphicsEngine;

import java.util.List;

public interface Minigame {
    /**
     * Called once when the world is built (like setupMap/vases).
     */
    void onMinigameStart(GameWorld world, GraphicsEngine graphicsEngine, PhysicService physicService);

    /**
     * Called every tick. Use this for win/loss conditions specific to the minigame.
     */
    void onTick(GameWorld world, long currentTick);

    /**
     * Called when the engine starts. Use this to add/remove Simulation Systems.
     */
    void onEngineStart(GameSimulationEngine engine);

    void setWorld(GameWorld world);
    List<PlantName> getPlants();
    List<String> getAvailableZombies();
}

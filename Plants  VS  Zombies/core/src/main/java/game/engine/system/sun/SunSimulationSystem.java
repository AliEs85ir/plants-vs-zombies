package game.engine.system.sun;

import game.engine.time.TickSystem;
import game.service.sun.SunService;
import game.world.GameWorld;

// >>> Handles all sun related time-based behaviors.
public class SunSimulationSystem implements TickSystem
{
    // -> Properties
    private final GameWorld gameWorld;
    private final SunService sunService;

    // -> Constructor
    public SunSimulationSystem(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
        this.sunService = new SunService(this.gameWorld);
    }

    // -> Methods
    // Updates sun system.
    @Override
    public void update(long currentTick)
    {
        sunService.getSunSpawner().spawnSkySun(currentTick);
        sunService.getSunDropManager().update(currentTick);
    }
}

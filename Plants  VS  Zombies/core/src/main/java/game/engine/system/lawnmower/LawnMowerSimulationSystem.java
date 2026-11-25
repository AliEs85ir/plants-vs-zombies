package game.engine.system.lawnmower;

import game.engine.time.TickSystem;
import game.entity.lawnmower.LawnMower;
import game.entity.lawnmower.LawnmowerState;
import game.service.lawnmower.LawnMowerService;
import game.world.GameWorld;

import java.util.ArrayList;


public class LawnMowerSimulationSystem implements TickSystem
{
    private final GameWorld gameWorld;
    private final LawnMowerService lawnMowerService;
    private final ArrayList<LawnMower> lawnMowers;

    public LawnMowerSimulationSystem(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
        this.lawnMowers = gameWorld.getLawnMowers();
        this.lawnMowerService = new LawnMowerService(gameWorld , lawnMowers);

    }

    @Override
    public void update(long currentTick)
    {
        for (LawnMower lawnMower : lawnMowers) {
            if (lawnMower.getState() != LawnmowerState.DEAD) {
                lawnMower.update(lawnMowerService, currentTick);
            }
        }
    }
}

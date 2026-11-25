package game.engine.system.zombie;
import game.engine.time.TickSystem;
import game.entity.zombie.Zombie;
import game.service.Zombie.ZombieService;
import game.world.GameWorld;

import java.util.ArrayList;

// >>> Handles all zombie related time-based behaviors.
public class ZombieSimulationSystem implements TickSystem
{
    private final GameWorld world;
    private final ZombieService zombieService;
    private final ArrayList<Zombie> zombies;

    public ZombieSimulationSystem(GameWorld world) {
        this.world = world;
        this.zombieService = new ZombieService(world);
        zombies = world.getZombies();
    }

    // Updates zombie system.
    @Override
    public void update(long currentTick) {
        for (Zombie zombie : new ArrayList<>(zombies)) {
            zombie.update(zombieService, currentTick);
        }

        world.flushPendingZombies();
        world.getZombies().removeIf(zombie -> zombie.isDead() && !zombie.persistAfterDeath());
    }
}

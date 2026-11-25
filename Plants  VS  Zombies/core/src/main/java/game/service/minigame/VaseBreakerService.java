package game.service.minigame;

import app.model.App;
import game.service.Zombie.ZombieService;
import game.world.minigame.vasebreaker.Vase;
import game.world.minigame.vasebreaker.VaseBreakerWorld;
import game.world.playground.Cell;

public class VaseBreakerService {
    private final VaseBreakerWorld world;
    private final ZombieService zombieService;

    public VaseBreakerService(VaseBreakerWorld world) {
        this.world = world;
        this.zombieService = new ZombieService(world);
    }

    public void breakVase(Vase vase) {
        Cell vaseCell = world.getGrid().getCellAt(vase.getPosition());
        vaseCell.setObstacle(null);
        if(vase.getContainedPlantName() == null) {
            zombieService.spawnZombie(vase.getContainedZombieAlias(), vase.getPosition());
            world.addEvent("Zombie " + vase.getContainedZombieAlias()
                    + " got spawned in position " +
                    vase.getPosition().getX() + "," +
                    vase.getPosition().getY() + " due to vase break!");
        }
        else if(vase.getContainedZombieAlias() == null) {
            long currentTick = App.getCurrentGameEngine().getGameClock().getCurrentTick();
            world.addPlantToInventory(
                vase.getContainedPlantName(),
                currentTick,
                (float) vase.getPosition().getX(),
                (float) vase.getPosition().getY()
            );
            world.addEvent("Plant " + vase.getContainedPlantName().getLabel() + " spawned due to vase break and added to inventory!");
        }
    }
}

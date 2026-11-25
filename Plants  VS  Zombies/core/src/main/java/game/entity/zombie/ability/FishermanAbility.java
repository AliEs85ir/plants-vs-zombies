package game.entity.zombie.ability;


import app.enums.obstacle.ObstacleType;
import app.model.App;
import game.entity.obstacle.ObstacleFactory;
import game.entity.obstacle.ability.HookAbility;
import game.entity.plant.Plant;
import game.entity.zombie.Zombie;
import game.service.Zombie.ZombieService;
import game.world.playground.Cell;
import game.world.playground.Grid;
import graphics.general.event.ZombieVisualEventType;

import java.util.ArrayList;
import java.util.List;

public class FishermanAbility extends AbstractZombieAbility{
    private final float timeBetweenCasts = 100;
    private long lastCastTick = 0;
    private boolean intro;


    @Override
    public void update(Zombie zombie, ZombieService zombieService, long currentTick) {
        if(!intro) {
            zombie.pushEvent(ZombieVisualEventType.INTRO);
            intro = true;
            return;
        }
        if(!canCast(currentTick)) {
            return;
        }
        lastCastTick = currentTick;
        Plant target = getPlant(zombie);
        if(target != null && !target.isDead()) {
            zombie.pushEvent(ZombieVisualEventType.THROW_HOOK);
            zombie.pushEvent(ZombieVisualEventType.HOOKING);
            App.getCurrentGameEngine().getTaskManager().scheduleRelative(
                currentTick,
                13,
                () -> hook(zombie, target)
            );
        }




    }

    private void hook(Zombie zombie, Plant target) {
        ObstacleFactory.getInstance().createRuntimeObstacle(
            ObstacleType.HOOK,
            20,
            20,
            (float) zombie.getPosition().getX(),
            (float) zombie.getPosition().getY() - 100,
            List.of(new HookAbility(target, zombie))
        );
    }

    private static Plant getPlant(Zombie zombie) {
        Grid grid = zombie.getWorld().getGrid();
        int lane = grid.getEntityLane(zombie);
        System.out.println(lane);
        List<Cell> cells = grid.getRowCells(lane);
        Plant target = null;
        int bestCol = -1;
        for(Plant plant : zombie.getWorld().getPlants()) {
            Cell cellAt = grid.getCellAt(plant.getPosition());
            if(cells.contains(cellAt)) {
                if(cellAt.getCol() > bestCol) {
                    bestCol = cellAt.getCol();
                    target = plant;
                }
            }
        }
        return target;
    }

    @Override
    public void onDeath(Zombie zombie) {

    }

    private boolean canCast(long currentTick) {
        return lastCastTick < 0 || (currentTick - lastCastTick) > timeBetweenCasts;
    }
}

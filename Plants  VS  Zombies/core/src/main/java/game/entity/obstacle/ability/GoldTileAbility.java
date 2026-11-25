package game.entity.obstacle.ability;

import game.entity.obstacle.Obstacle;
import game.service.obstacle.ObstacleService;
import game.world.playground.Cell;
import graphics.general.event.ObstacleVisualEventType;

public class GoldTileAbility implements ObstacleAbility{
    private Cell myCell;

    public GoldTileAbility(Cell myCell) {
        this.myCell = myCell;
    }

    @Override
    public void update(Obstacle obstacle, ObstacleService obstacleService, long currentTick) {
        if(myCell.hasPlant()) {
            obstacle.pushEvent(ObstacleVisualEventType.PLANT_ON_GOLD_TILE);
        }
    }

    @Override
    public void onDeath(Obstacle obstacle) {

    }
}

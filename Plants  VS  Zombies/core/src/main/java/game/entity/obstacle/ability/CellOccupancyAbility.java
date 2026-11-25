package game.entity.obstacle.ability;

import game.entity.obstacle.Obstacle;
import game.service.obstacle.ObstacleService;
import game.world.playground.Cell;

public class CellOccupancyAbility implements ObstacleAbility {
    private final Cell cell;

    public CellOccupancyAbility(Cell cell) {
        this.cell = cell;
    }

    @Override
    public void update(Obstacle obstacle, ObstacleService obstacleService, long currentTick) {
    }

    @Override
    public void onDeath(Obstacle obstacle) {
        if (cell != null && cell.getObstacle() == obstacle) {
            cell.clearObstacle();
        }
    }
}

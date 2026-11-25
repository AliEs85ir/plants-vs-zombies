package game.entity.obstacle.ability;

import game.entity.obstacle.Obstacle;
import game.service.obstacle.ObstacleService;

public interface ObstacleAbility {
    void update(
        Obstacle obstacle,
        ObstacleService obstacleService,
        long currentTick);

    void onDeath(Obstacle obstacle);

}

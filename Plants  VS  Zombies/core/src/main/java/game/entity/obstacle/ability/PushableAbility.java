package game.entity.obstacle.ability;

import game.entity.obstacle.Obstacle;
import game.service.obstacle.ObstacleService;

public class PushableAbility implements ObstacleAbility{

    @Override
    public void update(Obstacle obstacle, ObstacleService obstacleService, long currentTick) {
        if(obstacle.getTargetPlant() == null) {
            return;
        }
        obstacle.getTargetPlant().markDead(obstacleService.getWorld());
    }

    @Override
    public void onDeath(Obstacle obstacle) {

    }
}

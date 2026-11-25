package game.entity.obstacle.ability;

import game.entity.obstacle.Obstacle;
import game.entity.plant.Plant;
import game.service.obstacle.ObstacleService;

public class SnowballAbility implements ObstacleAbility{

    @Override
    public void update(Obstacle obstacle, ObstacleService obstacleService, long currentTick) {
        obstacle.getBody().setLinearVelocity(-5, 0);
        if(!obstacleService.getWorld().getGrid().isInsideMap(obstacle.getPosition())) {
            obstacle.die();
        }
        if(obstacle.getTargetPlant() == null) {
            return;
        }
        obstacle.stopMovement();
        Plant plant = obstacle.getTargetPlant();
        plant.takeDamage(40, null, currentTick);
        plant.addFreezeLevel();
        obstacle.die();
    }

    @Override
    public void onDeath(Obstacle obstacle) {

    }
}

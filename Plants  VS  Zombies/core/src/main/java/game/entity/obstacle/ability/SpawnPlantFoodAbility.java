package game.entity.obstacle.ability;

import app.model.App;
import game.entity.obstacle.Obstacle;
import game.service.drop.DropSpawner;
import game.service.obstacle.ObstacleService;
import game.world.GameWorld;

public class SpawnPlantFoodAbility implements ObstacleAbility {

    @Override
    public void update(Obstacle obstacle, ObstacleService obstacleService, long currentTick) {
    }

    @Override
    public void onDeath(Obstacle obstacle) {
        if (obstacle == null || obstacle.getPosition() == null) {
            return;
        }
        if (App.getCurrentGameEngine() == null) {
            return;
        }
        GameWorld world = App.getCurrentGameEngine().getGameWorld();
        if (world == null) {
            return;
        }
        DropSpawner.spawnPlantFood(obstacle.getPosition(), world);
    }
}

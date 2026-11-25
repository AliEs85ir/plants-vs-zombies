package game.entity.obstacle.ability;

import app.model.App;
import game.entity.obstacle.Obstacle;
import game.service.obstacle.ObstacleService;
import game.service.sun.SunSpawner;
import game.world.GameWorld;

public class SpawnSunAbility implements ObstacleAbility {
    private static final int SUN_AMOUNT = 50;

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
        new SunSpawner(world).spawnObstacleSun(obstacle, SUN_AMOUNT);
    }
}

package game.service.obstacle;

import app.model.App;
import game.entity.obstacle.Obstacle;
import game.world.GameWorld;
import graphics.obstacle.ObstacleRenderInstance;

public class ObstacleService {
    private GameWorld world;

    public ObstacleService(GameWorld world) {
        this.world = world;
    }

    public GameWorld getWorld() {
        return world;
    }

    public void applyRotation(Obstacle obstacle) {
        ObstacleRenderInstance instance = App.getRealTimeEngine()
            .getGraphicsEngine().getObstacleInstance(obstacle);
        if(instance != null) {
            instance.setRotationFunction(obstacle1 -> {
                if(obstacle1.getBody() != null) {
                    return (float) Math.toDegrees(obstacle1.getBody().getAngle());
                }
                return 0;
            });
        }
    }
}

package graphics.obstacle;

import game.entity.obstacle.Obstacle;
import graphics.general.event.ObstacleVisualEventType;
import graphics.general.instance.RenderInstance;

public class ObstacleRenderInstance extends RenderInstance<Obstacle, ObstacleVisualState, ObstacleVisualEventType, ObstacleAnimationVariant> {

    public ObstacleRenderInstance(Obstacle obstacle) {
        super(obstacle, ObstacleVisualState.IDLE);
    }

    @Override
    protected float generateRandomVariationInterval() {
        return  3 + (float) (Math.random() * 4.0f);
    }

    public Obstacle getObstacle() { return getModel(); }

}


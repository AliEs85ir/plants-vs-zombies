package graphics.obstacle;

import app.model.App;
import game.entity.obstacle.Obstacle;
import graphics.general.animation.condition.AnimationCondition;
import graphics.general.event.ObstacleVisualEventType;
import graphics.general.event.VisualEvent;

public class ObstacleConditions {
    public static AnimationCondition<Obstacle> event(ObstacleVisualEventType eventType) {
        return obstacle -> {
            VisualEvent<ObstacleVisualEventType> event = App.getRealTimeEngine()
                .getGraphicsEngine()
                .getObstacleInstance(obstacle)
                .getCurrentEvent();
            return event != null && event.getType() == eventType;
        };
    }
}

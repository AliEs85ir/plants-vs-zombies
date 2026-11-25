package game.entity.obstacle.ability;

import game.engine.time.GameClock;
import game.entity.obstacle.Obstacle;
import game.service.obstacle.ObstacleService;
import graphics.general.event.ObstacleVisualEventType;

public class TimerdLifeAbility implements ObstacleAbility{
    public float timer;
    private boolean initialized;

    public TimerdLifeAbility(float timer) {
        this.timer = timer;
        this.initialized = false;
    }

    @Override
    public void update(Obstacle obstacle, ObstacleService obstacleService, long currentTick) {
        if(!initialized) {
            obstacle.pushEvent(ObstacleVisualEventType.SCORCH_START);
            obstacle.pushEvent(ObstacleVisualEventType.SCORCH_LOOP);
            initialized = true;
        }
        timer -= GameClock.SECONDS_PER_TICK;
        if(timer < 0) {
            obstacle.pushEvent(ObstacleVisualEventType.DEATH);
            obstacle.die();
        }
    }

    @Override
    public void onDeath(Obstacle obstacle) {

    }
}

package game.entity.obstacle.ability;

import game.entity.obstacle.Obstacle;
import game.entity.zombie.Zombie;
import game.service.obstacle.ObstacleService;
import graphics.general.event.ObstacleVisualEventType;

import java.util.List;

public class BrainAbility implements ObstacleAbility{

    @Override
    public void update(Obstacle obstacle, ObstacleService obstacleService, long currentTick) {
        if (obstacle == null || obstacle.isDead()) {
            return;
        }
        List<Zombie> targets = obstacle.getTargetZombies();
        if (targets == null || targets.isEmpty()) {
            return;
        }
        boolean hit = false;
        for (Zombie zombie : targets) {
            if (zombie != null && !zombie.isDead()) {
                hit = true;
                break;
            }
        }
        if (!hit) {
            return;
        }
        obstacle.pushEvent(ObstacleVisualEventType.DEATH);
        obstacle.die();
    }

    @Override
    public void onDeath(Obstacle obstacle) {

    }
}

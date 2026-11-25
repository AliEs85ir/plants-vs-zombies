package game.entity.obstacle.ability;

import app.model.App;
import app.view.game.PlayScreen;
import game.entity.obstacle.Obstacle;
import game.entity.zombie.Zombie;
import game.service.obstacle.ObstacleService;
import graphics.obstacle.ObstacleRenderInstance;

import java.util.List;

public class TallnutAbility implements ObstacleAbility{
    private boolean isInitialized = false;

    @Override
    public void update(Obstacle obstacle, ObstacleService obstacleService, long currentTick) {
        obstacle.getBody().setLinearVelocity(2,0);
        if (!isInitialized) {
            obstacle.getBody().setAngularVelocity(-4f);
            obstacleService.applyRotation(obstacle);
            isInitialized = true;
        }

        if(!obstacleService.getWorld().getGrid().isInsideMap(obstacle.getPosition())) {
            obstacle.die();
            return;
        }

        List<Zombie> targets = obstacle.getTargetZombies();
        if(targets.isEmpty()) {
            return;
        }

        for (Zombie z : targets) {
            if (!z.isDead()) {
                z.die();
            }
        }
        PlayScreen.shake(0.6f, 16f);
        game.audio.Sfx.bowlingHit();

        obstacle.clearTargetZombies();
    }

    @Override
    public void onDeath(Obstacle obstacle) {

    }

}


package game.entity.obstacle.ability;

import app.model.App;
import app.view.game.PlayScreen;
import game.entity.DamageContext;
import game.entity.obstacle.Obstacle;
import game.entity.zombie.Zombie;
import game.service.obstacle.ObstacleService;
import game.world.playground.Grid;
import game.world.playground.MapConfig;
import graphics.obstacle.ObstacleRenderInstance;

import java.util.List;
import java.util.Random;

public class WalnutAbility implements ObstacleAbility{

    private int hitCount = 0;
    private float speedX = 2f;
    private float speedY = 0f;
    private boolean isInitialized = false;
    private final Random random = new Random();


    @Override
    public void update(Obstacle obstacle, ObstacleService obstacleService, long currentTick) {
        if (!isInitialized) {
            obstacle.getBody().setLinearVelocity(speedX, speedY);
            obstacle.getBody().setAngularVelocity(-6f);
            obstacleService.applyRotation(obstacle);
            isInitialized = true;
        }

        Grid grid = obstacleService.getWorld().getGrid();
        MapConfig config = grid.getConfig();

        if (!grid.isInsideMap(obstacle.getPosition())) {
            obstacle.die();
            return;
        }

        handleHitAndBounce(obstacle, currentTick);

        handleBorderHit(obstacle, config);
    }

    private void handleBorderHit(Obstacle obstacle, MapConfig config) {
        float yPos = (float) obstacle.getPosition().getY();
        float topBorder = config.getPlaygroundY() + config.getPlaygroundHeight();
        float bottomBorder = config.getPlaygroundY();

        boolean bouncedOffBorder = false;

        if (yPos >= topBorder && speedY > 0) {
            speedY = -speedY;
            bouncedOffBorder = true;
        }
        else if (yPos <= bottomBorder && speedY < 0) {
            speedY = -speedY;
            bouncedOffBorder = true;
        }
        if (bouncedOffBorder) {
            obstacle.getBody().setLinearVelocity(speedX, speedY);
        }
    }

    private void handleHitAndBounce(Obstacle obstacle, long currentTick) {
        List<Zombie> targets = obstacle.getTargetZombies();
        if (targets != null && !targets.isEmpty()) {

            Zombie hitZombie = null;
            for (Zombie z : targets) {
                if (!z.isDead()) {
                    hitZombie = z;
                    break;
                }
            }

            if (hitZombie != null) {
                DamageContext context = new DamageContext(obstacle, 300, false);
                hitZombie.takeDamage(context, currentTick);
                if (hitCount == 0) {
                    speedY = random.nextBoolean() ? speedX : -speedX;
                } else {
                    speedY = -speedY;
                }

                hitCount++;
                obstacle.getBody().setLinearVelocity(speedX, speedY);
                game.audio.Sfx.bowlingHit();
            }
            PlayScreen.shake(0.6f, 16f);
            obstacle.clearTargetZombies();
        }
    }

    @Override
    public void onDeath(Obstacle obstacle) {
    }
}

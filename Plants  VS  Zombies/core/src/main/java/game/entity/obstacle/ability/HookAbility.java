package game.entity.obstacle.ability;

import com.badlogic.gdx.math.Vector2;
import common.model.Position;
import game.component.physic.PhysicService;
import game.entity.obstacle.Obstacle;
import game.entity.plant.Plant;
import game.entity.plant.ability.FishermanAttackAbility;
import game.entity.zombie.Zombie;
import game.service.obstacle.ObstacleService;
import graphics.general.event.ObstacleVisualEventType;
import graphics.general.event.ZombieVisualEventType;

public class HookAbility implements ObstacleAbility{
    private Plant targetPlant;
    private enum State {FLYING, CATCHING}
    private State state;
    private boolean initialized = false;
    private float startX, startY;
    private float targetX, targetY;
    private float velocityX, velocityY;
    private int elapsedTicks = 0;
    private int totalFlightTicks = 0;
    private final float gravityPx = -20f;
    private Zombie fisherman;

    public HookAbility(Plant targetPlant, Zombie zombie) {
        this.targetPlant = targetPlant;
        this.state = State.FLYING;
        this.fisherman = zombie;

    }

    @Override
    public void update(Obstacle obstacle, ObstacleService obstacleService, long currentTick) {
        switch (state) {
            case CATCHING -> {
                fisherman.pushEvent(ZombieVisualEventType.REEL);
                if(!targetPlant.isDead()) {
                    targetPlant.addAbility(new FishermanAttackAbility(
                        obstacleService.getWorld().getGrid(), targetPlant
                    ));
                }
                obstacle.die();
            }
            case FLYING -> {
                obstacle.pushEvent(ObstacleVisualEventType.FLY);
                if (!initialized) {
                    calculateTrajectory(obstacle);
                    initialized = true;
                }

                elapsedTicks++;
                float t = elapsedTicks;
                float newX = startX + velocityX * t;
                float newY = startY + velocityY * t + 0.5f * gravityPx * t * t;

                boolean hasLanded = elapsedTicks >= totalFlightTicks;

                if (hasLanded) {
                    newX = targetX;
                    newY = targetY;
                    state = State.CATCHING;
                }
                obstacle.setPosition(new Position(newX, newY));
                float b2X = newX / PhysicService.PIXEL_PER_METER;
                float b2Y = newY / PhysicService.PIXEL_PER_METER;
                obstacle.getBody().setTransform(
                    new Vector2(b2X, b2Y),
                    obstacle.getBody().getAngle()
                );
            }
        }
    }

    private void calculateTrajectory(Obstacle obstacle) {
        this.startX = (float) obstacle.getPosition().getX();
        this.startY = (float) obstacle.getPosition().getY();

        this.targetX = (float) targetPlant.getPosition().getX();
        this.targetY = (float) targetPlant.getPosition().getY();

        float arcHeightPx = 350f;

        this.velocityY = (float) Math.sqrt(2 * Math.abs(gravityPx) * arcHeightPx);

        float timeToPeak = this.velocityY / Math.abs(gravityPx);

        float descentHeight = Math.max(0, arcHeightPx + (startY - targetY));

        float timeToDescend = (float) Math.sqrt(2 * descentHeight / Math.abs(gravityPx));

        this.totalFlightTicks = (int) Math.ceil(timeToPeak + timeToDescend);
        if (this.totalFlightTicks <= 0) {
            this.totalFlightTicks = 1;
        }

        this.velocityX = (targetX - startX) / totalFlightTicks;
    }

    @Override
    public void onDeath(Obstacle obstacle) {

    }
}

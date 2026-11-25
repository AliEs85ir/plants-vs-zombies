package game.entity.obstacle.ability;

import app.model.App;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Filter;
import common.model.Position;
import game.component.contacts.ContactBits;
import game.component.physic.PhysicService;
import game.entity.obstacle.Obstacle;
import game.entity.plant.Plant;
import game.service.obstacle.ObstacleService;
import graphics.general.event.ObstacleVisualEventType;

public class OctopusAbility implements ObstacleAbility{
    private final Plant targetPlant;
    private enum State {FLYING, CATCHING, IDLE}
    private State state;
    private double currentHealth;
    private boolean dead;
    private boolean applied;

    private boolean initialized = false;
    private float startX, startY;
    private float targetX, targetY;
    private float velocityX, velocityY;
    private int elapsedTicks = 0;
    private int totalFlightTicks = 0;
    private final float gravityPx = -20f;

    public OctopusAbility(Plant targetPlant, double currentHealth) {
        this.dead = false;
        this.applied = false;
        this.currentHealth = currentHealth;
        this.targetPlant = targetPlant;
        this.state = State.FLYING;
    }

    @Override
    public void update(Obstacle obstacle, ObstacleService obstacleService, long currentTick) {
        if(dead) {
            if (!applied) {
                obstacle.pushEvent(ObstacleVisualEventType.DEATH);
                obstacle.die();
                applied = true;
            }
            return;
        }

        switch (state) {
            case IDLE -> {
                obstacle.pushEvent(ObstacleVisualEventType.IDLE);
            }
            case CATCHING -> {
                obstacle.pushEvent(ObstacleVisualEventType.LAND);
                applyCatch();
                state = State.IDLE;
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

    private void applyCatch() {
        targetPlant.setCanAct(false);
        App.getRealTimeEngine().getGraphicsEngine().unregisterPlant(targetPlant);
        Filter filter = targetPlant.getBody().getFixtureList().first().getFilterData();
        filter.maskBits = ContactBits.OBSTACLE;
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
        targetPlant.setCanAct(true);
        App.getRealTimeEngine().getGraphicsEngine().registerPlant(targetPlant);
        Filter filter = targetPlant.getBody().getFixtureList().first().getFilterData();
        filter.maskBits = ContactBits.OBSTACLE | ContactBits.ZOMBIE;
    }

    public void takeDamage(int amount, Obstacle obstacle) {
        currentHealth -= amount;
        if(currentHealth <= 0) {
            dead = true;
        }
    }

    public Plant getTargetPlant() {
        return targetPlant;
    }
}

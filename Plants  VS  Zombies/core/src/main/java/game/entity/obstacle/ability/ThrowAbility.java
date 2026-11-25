package game.entity.obstacle.ability;

import com.badlogic.gdx.math.Vector2;
import common.model.Position;
import game.component.physic.PhysicService;
import game.entity.obstacle.Obstacle;
import game.service.obstacle.ObstacleService;
import graphics.general.event.ObstacleVisualEventType;

public class ThrowAbility implements ObstacleAbility{
    private final Vector2 start;
    private final Vector2 velocity;
    private final float gravity;
    private final float groundY;
    private int elapsedTicks;
    private boolean landed;

    public ThrowAbility(Vector2 start, Vector2 velocity, float gravity, float groundY) {
        this.start = start;
        this.velocity = velocity;
        this.gravity = gravity;
        this.groundY = groundY;
    }

    @Override
    public void update(Obstacle obstacle, ObstacleService obstacleService, long currentTick) {
        if (landed) return;

        obstacle.pushEvent(ObstacleVisualEventType.FLY);

        elapsedTicks++;
        float t = elapsedTicks;
        float newX = start.x + velocity.x * t;
        float newY = start.y + velocity.y * t + 0.5f * gravity * t * t;

        boolean hasLanded = newY <= groundY;
        if (hasLanded) {
            newY = groundY;
            landed = true;
            obstacle.pushEvent(ObstacleVisualEventType.LAND);
        }

        float ppm = PhysicService.PIXEL_PER_METER;
        obstacle.getBody().setTransform(newX / ppm, newY / ppm, 0);

        obstacle.getBody().setLinearVelocity(
            (landed ? 0 : velocity.x) / ppm,
            (landed ? 0 : velocity.y + gravity * t) / ppm
        );

        obstacle.setPosition(new Position(newX, newY));
    }

    @Override
    public void onDeath(Obstacle obstacle) {

    }
}

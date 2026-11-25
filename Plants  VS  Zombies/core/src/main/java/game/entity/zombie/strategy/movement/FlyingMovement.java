package game.entity.zombie.strategy.movement;

import com.badlogic.gdx.math.Vector2;
import common.model.Position;
import game.component.physic.PhysicService;
import game.entity.zombie.Zombie;
import game.service.Zombie.ZombieService;
import graphics.general.event.ZombieVisualEventType;

public class FlyingMovement implements MovementStrategy{
    private final Vector2 start;
    private final Vector2 velocity;
    private final float gravity;
    private final float groundY;
    private int elapsedTicks;
    private boolean landed;

    public FlyingMovement(Vector2 start, Vector2 velocity, float gravity, float groundY) {
        this.start = start;
        this.velocity = velocity;
        this.gravity = gravity;
        this.groundY = groundY;
    }

    @Override
    public boolean move(Zombie zombie, ZombieService zombieService, long currentTick) {
        if (landed) return false;

        zombie.pushEvent(ZombieVisualEventType.FLY);

        elapsedTicks++;
        float t = elapsedTicks;
        float newX = start.x + velocity.x * t;
        float newY = start.y + velocity.y * t + 0.5f * gravity * t * t;

        boolean hasLanded = newY <= groundY;
        if (hasLanded) {
            newY = groundY;
            landed = true;
            zombie.pushEvent(ZombieVisualEventType.LAND);
        }

        float ppm = PhysicService.PIXEL_PER_METER;
        zombie.getBody().setTransform(newX / ppm, newY / ppm, 0);

        zombie.getBody().setLinearVelocity(
            (landed ? 0 : velocity.x) / ppm,
            (landed ? 0 : velocity.y + gravity * t) / ppm
        );

        zombie.setPosition(new Position(newX, newY));

        return !landed;
    }

    public boolean hasLanded() {
        return landed;
    }
}

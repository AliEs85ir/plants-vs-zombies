package graphics.general.effects.anchers;

import com.badlogic.gdx.math.Vector2;

public class KinematicAnchor implements EffectAnchor{
    private static final float GRAVITY = 900f;

    private final Vector2 position;
    private final Vector2 velocity;
    private final float rotationSpeed;
    private final float groundY;

    private float rotation;
    private boolean landed;

    public KinematicAnchor(Vector2 startPosition,
                           Vector2 initialVelocity,
                           float rotationSpeed,
                           float groundY) {
        this.position = startPosition.cpy();
        this.velocity = initialVelocity.cpy();
        this.rotationSpeed = rotationSpeed;
        this.groundY = groundY;
    }

    @Override
    public void update(float delta) {
        if (landed) {
            return;
        }

        velocity.y -= GRAVITY * delta;
        position.mulAdd(velocity, delta);
        rotation += rotationSpeed * delta;

        if (position.y <= groundY) {
            position.y = groundY;
            velocity.setZero();
            landed = true;
        }
    }

    @Override
    public Vector2 resolvePosition() {
        return position;
    }

    @Override
    public float resolveRotation() {
        return rotation;
    }

    public boolean hasLanded() {
        return landed;
    }
}

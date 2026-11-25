package graphics.general.effects.anchers;

import com.badlogic.gdx.math.Vector2;

public interface EffectAnchor {
    /** Position in world coordinates for the current frame. */
    Vector2 resolvePosition();
    default void update(float delta) { }

    /** Optional rotation in degrees. Defaults to 0. */
    default float resolveRotation() {
        return 0f;
    }
}

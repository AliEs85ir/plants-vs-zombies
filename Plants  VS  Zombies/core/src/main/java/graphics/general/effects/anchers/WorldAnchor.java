package graphics.general.effects.anchers;

import com.badlogic.gdx.math.Vector2;
import common.model.Position;

public final class WorldAnchor implements EffectAnchor {

    private final Vector2 position;

    public WorldAnchor(Vector2 position) {
        this.position = position == null ? new Vector2() : position.cpy();
    }

    public WorldAnchor(Position pos) {
        this((float) pos.getX(), (float) pos.getY());
    }

    public WorldAnchor(float x, float y) {
        this(new Vector2(x, y));
    }

    @Override
    public Vector2 resolvePosition() {
        return position.cpy();
    }

    @Override
    public String toString() {
        return "WorldAnchor" + position;
    }
}

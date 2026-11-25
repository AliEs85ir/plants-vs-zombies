package graphics.drop;

import game.entity.drop.Drop;

public class DropRenderInstance {
    private final Drop drop;
    private float stateTime;

    public DropRenderInstance(Drop drop) {
        this.drop = drop;
    }

    public void update(float delta) {
        stateTime += delta;
        drop.update(delta);
    }

    public Drop getDrop() {
        return drop;
    }

    public float getStateTime() {
        return stateTime;
    }
}

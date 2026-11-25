package game.component.physic;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import game.component.contacts.WorldContactListener;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

public class PhysicService {
    public static final float PIXEL_PER_METER = 100;
    private final World b2world;
    private final float timeStep = 1 / 60f;
    private final Set<Body> pendingDestroy = java.util.Collections.newSetFromMap(new IdentityHashMap<>());

    public PhysicService() {
        this.b2world = new World(new Vector2(0, 0), true);
        b2world.setContactListener(new WorldContactListener());
    }

    public World getB2dWorld() {
        return b2world;
    }

    public Body createBody(BodyDef bodyDef) {
        return b2world.createBody(bodyDef);
    }

    public void destroyBody(Body body) {
        if (body == null) {
            return;
        }
        if (b2world.isLocked()) {
            pendingDestroy.add(body);
            return;
        }
        destroyNow(body);
    }

    public void updateWorld(float delta) {
        b2world.step(timeStep, 6, 2);
        flushPendingDestroys();
    }

    private void flushPendingDestroys() {
        if (pendingDestroy.isEmpty()) {
            return;
        }
        List<Body> toDestroy = new ArrayList<>(pendingDestroy);
        pendingDestroy.clear();
        for (Body body : toDestroy) {
            destroyNow(body);
        }
    }

    private void destroyNow(Body body) {
        if (body == null || body.getWorld() == null) {
            return;
        }
        b2world.destroyBody(body);
    }
}

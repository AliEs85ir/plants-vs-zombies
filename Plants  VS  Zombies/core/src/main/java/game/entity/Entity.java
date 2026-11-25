package game.entity;

import app.model.App;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import common.model.Position;
import game.component.physic.PhysicService;
import game.world.playground.Grid;

import java.util.List;

public abstract class Entity {
    protected Body body;
    protected int laneMask;

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }


    public Vector2 getPhysicsPosition() {
        if (body != null) {
            return body.getPosition();
        }
        return Vector2.Zero;
    }

    public void destroyBody(PhysicService physicsService) {
        if (body != null) {
            physicsService.destroyBody(body);
            body = null;
        }
    }
    public int getLaneMask() {
        return laneMask;
    }

    public void  setLaneMask(int laneMask) {
        this.laneMask = laneMask;
    }
    public abstract Position getPosition();

    public void updateLaneMask() {
        Grid grid = App.getCurrentGameEngine().getGameWorld().getGrid();
        laneMask = 1 << grid.getEntityLane(this);
    }

    public boolean lineMatches(Entity other) {
        return (laneMask & other.laneMask) != 0;
    }
}

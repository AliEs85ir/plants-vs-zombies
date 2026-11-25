package game.entity.sun;

import app.enums.sun.SunSourceType;
import app.enums.sun.SunState;
import app.enums.sun.SunType;
import common.model.Position;
import game.component.physic.PhysicService;
import game.entity.Entity;

public abstract class Sun extends Entity {
    protected SunType type;
    protected SunState state;
    protected SunSourceType sourceType;
    protected Position position;
    protected int value;

    public SunType getType() {
        return type;
    }

    public SunState getState() {
        return state;
    }

    public void setState(SunState state) {
        this.state = state;
    }

    @Override
    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public int getValue() {
        return value;
    }

    public SunSourceType getSourceType() {
        return sourceType;
    }

    public abstract boolean isCollectable();

}

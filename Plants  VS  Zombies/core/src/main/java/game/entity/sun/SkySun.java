package game.entity.sun;

import app.enums.sun.SunSourceType;
import app.enums.sun.SunState;
import app.enums.sun.SunType;
import common.model.Position;
import game.component.physic.PhysicService;

public class SkySun extends Sun {
    private final long spawnTick;
    private long lastMoveTick;
    private final float fallSpeed = 6f;
    private final long landingTick;

    public SkySun(SunType type, Position position, int value, long tick) {
        this.type = type;
        this.position = position;
        this.value = value;
        this.spawnTick = tick;
        this.lastMoveTick = tick;
        this.state = SunState.DROPPING;
        this.sourceType = SunSourceType.SKY;
        this.landingTick = (long) ((position.getY() - 2646) / fallSpeed);
    }

    public long getSpawnTick() {
        return spawnTick;
    }

    public long getLastMoveTick() {
        return lastMoveTick;
    }

    public void setLastMoveTick(long tick) {
        lastMoveTick = tick;
    }

    public void move() {
        position.setY(position.getY() - fallSpeed);
        syncBody();
    }

    public void syncBody() {
        if (body != null) {
            body.setTransform(
                (float) position.getX() / PhysicService.PIXEL_PER_METER,
                (float) position.getY() / PhysicService.PIXEL_PER_METER,
                body.getAngle()
            );
        }
    }

    public void land() {
        if (state == SunState.ON_GROUND)
            return;

        state = SunState.ON_GROUND;

        if (type == SunType.RADIOACTIVE) {
            type = SunType.NORMAL;
            value = 25;
        }
    }

    @Override
    public boolean isCollectable() {
        return state != SunState.COLLECTED;
    }
}

package game.entity.drop;

import app.enums.drop.DropType;
import common.model.Position;
import game.service.drop.DropSettings;

public class Drop {
    private final DropType type;
    private final float spawnX;
    private final float spawnY;
    private final float driftX;
    private final Position position;
    private float age;
    private boolean collected;
    private boolean expired;

    public Drop(DropType type, Position origin, float driftX) {
        this.type = type;
        this.spawnX = origin == null ? 0f : (float) origin.getX();
        this.spawnY = origin == null ? 0f : (float) origin.getY() + DropSettings.SPAWN_Y_OFFSET;
        this.driftX = driftX;
        this.position = new Position(spawnX, spawnY);
        this.age = 0f;
    }

    public void update(float delta) {
        if (collected || expired) {
            return;
        }
        age += delta;
        if (age >= DropSettings.LIFETIME_SECONDS) {
            expired = true;
            return;
        }
        if (age < DropSettings.LAUNCH_SECONDS) {
            float progress = age / DropSettings.LAUNCH_SECONDS;
            float height = 4f * progress * (1f - progress) * DropSettings.LAUNCH_HEIGHT;
            position.setX(spawnX + driftX * progress);
            position.setY(spawnY + height);
            return;
        }
        float bobTime = age - DropSettings.LAUNCH_SECONDS;
        position.setX(spawnX + driftX);
        position.setY(spawnY + (float) Math.sin(bobTime * DropSettings.BOB_SPEED) * DropSettings.BOB_AMPLITUDE);
    }

    public DropType getType() {
        return type;
    }

    public Position getPosition() {
        return position;
    }

    public boolean isCollectable() {
        return !collected && !expired;
    }

    public boolean isFinished() {
        return collected || expired;
    }

    public void markCollected() {
        collected = true;
    }

    public boolean isCollected() {
        return collected;
    }

    public boolean isExpired() {
        return expired;
    }

    public float getAge() {
        return age;
    }
}

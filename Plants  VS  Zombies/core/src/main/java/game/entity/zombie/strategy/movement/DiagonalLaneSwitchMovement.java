package game.entity.zombie.strategy.movement;

import com.badlogic.gdx.math.Vector2;
import common.model.Position;
import game.component.physic.PhysicService;
import game.entity.zombie.Zombie;
import game.service.Zombie.ZombieService;
import graphics.general.event.ZombieVisualEventType;

public final class DiagonalLaneSwitchMovement implements MovementStrategy {
    private final float startX;
    private final float startY;
    private final float targetX;
    private final float targetY;
    private final int targetRow;
    private final int durationTicks;
    private final MovementStrategy restoreStrategy;
    private int elapsedTicks;

    public DiagonalLaneSwitchMovement(
        float startX,
        float startY,
        float targetX,
        float targetY,
        int targetRow,
        int durationTicks,
        MovementStrategy restoreStrategy
    ) {
        this.startX = startX;
        this.startY = startY;
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetRow = targetRow;
        this.durationTicks = Math.max(1, durationTicks);
        this.restoreStrategy = restoreStrategy;
    }

    public static int computeDurationTicks(double distancePixels, double speedPixelsPerTick) {
        return Math.max(6, (int) Math.ceil(distancePixels / Math.max(speedPixelsPerTick, 0.01d)));
    }

    public int getTargetRow() {
        return targetRow;
    }

    public MovementStrategy getRestoreStrategy() {
        return restoreStrategy;
    }

    @Override
    public boolean move(Zombie zombie, ZombieService zombieService, long currentTick) {
        elapsedTicks++;
        float progress = Math.min(1f, (float) elapsedTicks / durationTicks);
        float newX = startX + (targetX - startX) * progress;
        float newY = startY + (targetY - startY) * progress;
        applyPosition(zombie, newX, newY);
        zombie.pushEvent(ZombieVisualEventType.WALK);

        if (elapsedTicks < durationTicks) {
            return true;
        }

        applyPosition(zombie, targetX, targetY);
        zombie.setLaneMask(1 << targetRow);
        zombie.setMovementStrategy(restoreStrategy != null ? restoreStrategy : new NormalMovement());
        if (zombie.getState() != Zombie.ZombieState.USING_ABILITY
            && zombie.getState() != Zombie.ZombieState.DYING) {
            zombie.setState(Zombie.ZombieState.WALKING);
        }
        return false;
    }

    private static void applyPosition(Zombie zombie, float x, float y) {
        zombie.setPosition(new Position(x, y));
        if (zombie.getBody() == null) {
            return;
        }
        float ppm = PhysicService.PIXEL_PER_METER;
        zombie.getBody().setTransform(x / ppm, y / ppm, zombie.getBody().getAngle());
        zombie.getBody().setLinearVelocity(0f, 0f);
    }
}

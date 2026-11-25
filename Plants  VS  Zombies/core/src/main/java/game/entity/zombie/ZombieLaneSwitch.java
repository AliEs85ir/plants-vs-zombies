package game.entity.zombie;

import com.badlogic.gdx.math.Vector2;
import common.model.Position;
import game.component.physic.PhysicService;
import game.engine.time.GameClock;
import game.entity.zombie.strategy.movement.DiagonalLaneSwitchMovement;
import game.entity.zombie.strategy.movement.MovementStrategy;
import game.entity.zombie.strategy.movement.NormalMovement;
import game.world.playground.Grid;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class ZombieLaneSwitch {
    public static final float LANE_PULL_SPEED_MULTIPLIER = 2.5f;
    public static final float LANE_ARRIVAL_TOLERANCE_PIXELS = 2f;

    private ZombieLaneSwitch() {
    }

    public static List<Integer> validAdjacentRows(int currentRow, int rowCount) {
        List<Integer> rows = new ArrayList<>(2);
        if (rowCount <= 0 || currentRow < 0 || currentRow >= rowCount) {
            return rows;
        }
        int above = currentRow - 1;
        int below = currentRow + 1;
        if (above >= 0) {
            rows.add(above);
        }
        if (below < rowCount) {
            rows.add(below);
        }
        return rows;
    }

    public static boolean isDivertInProgress(Zombie zombie) {
        return zombie != null && zombie.getMovementStrategy() instanceof DiagonalLaneSwitchMovement;
    }

    public static boolean divertToAdjacentLane(Zombie zombie, Grid grid) {
        if (zombie == null || grid == null || zombie.getPosition() == null || isDivertInProgress(zombie)) {
            return false;
        }
        int currentRow = grid.getEntityLane(zombie);
        List<Integer> valid = validAdjacentRows(currentRow, grid.getRowCount());
        if (valid.isEmpty()) {
            return false;
        }
        int targetRow = valid.size() == 1
            ? valid.get(0)
            : valid.get(ThreadLocalRandom.current().nextInt(valid.size()));
        return beginDiagonalDivert(zombie, grid, targetRow);
    }

    public static boolean beginDiagonalDivert(Zombie zombie, Grid grid, int targetRow) {
        if (zombie == null || grid == null || zombie.getPosition() == null || isDivertInProgress(zombie)) {
            return false;
        }
        Vector2 rowCenter = grid.getRowCenter(targetRow);
        if (rowCenter == null) {
            return false;
        }

        float startX = (float) zombie.getPosition().getX();
        float startY = (float) zombie.getPosition().getY();
        Position target = computeDiagonalTarget(startX, rowCenter.y, grid.getConfig().getCellWidth());
        double distance = Math.hypot(target.getX() - startX, target.getY() - startY);
        double speedPixelsPerTick = zombie.getSpeed()
            * PhysicService.PIXEL_PER_METER
            * GameClock.SECONDS_PER_TICK;
        int durationTicks = DiagonalLaneSwitchMovement.computeDurationTicks(distance, speedPixelsPerTick);

        MovementStrategy restoreStrategy = zombie.getMovementStrategy();
        if (restoreStrategy instanceof DiagonalLaneSwitchMovement) {
            restoreStrategy = new NormalMovement();
        }

        zombie.setTargetPlant(null);
        if (zombie.getState() != Zombie.ZombieState.USING_ABILITY
            && zombie.getState() != Zombie.ZombieState.DYING) {
            zombie.setState(Zombie.ZombieState.WALKING);
        }
        zombie.setMovementStrategy(new DiagonalLaneSwitchMovement(
            startX,
            startY,
            (float) target.getX(),
            (float) target.getY(),
            targetRow,
            durationTicks,
            restoreStrategy
        ));
        return true;
    }

    public static Position computeDiagonalTarget(float startX, float targetRowCenterY, float cellWidth) {
        float targetX = startX - cellWidth;
        return new Position(targetX, targetRowCenterY);
    }

    public static double forwardPixelsPerTick(Zombie zombie) {
        if (zombie == null) {
            return 0.01d;
        }
        return Math.max(
            zombie.getSpeed() * PhysicService.PIXEL_PER_METER * GameClock.SECONDS_PER_TICK,
            0.01d
        );
    }

    public static int computeLanePullDurationTicks(double distanceY, double lateralPixelsPerTick) {
        return Math.max(1, (int) Math.ceil(Math.abs(distanceY) / Math.max(lateralPixelsPerTick, 0.01d)));
    }

    public static float computeLanePullTargetX(float startX, int durationTicks, double forwardPixelsPerTick) {
        return startX - (float) (Math.max(1, durationTicks) * Math.max(forwardPixelsPerTick, 0.01d));
    }

    public static boolean beginLanePull(
        Zombie zombie,
        Grid grid,
        int targetRow,
        float lateralSpeedMultiplier,
        boolean retarget
    ) {
        if (zombie == null || grid == null || zombie.getPosition() == null) {
            return false;
        }
        Vector2 rowCenter = grid.getRowCenter(targetRow);
        if (rowCenter == null) {
            return false;
        }

        DiagonalLaneSwitchMovement current = currentDivert(zombie);
        if (current != null) {
            if (!retarget || current.getTargetRow() == targetRow) {
                return false;
            }
        }

        float startX = (float) zombie.getPosition().getX();
        float startY = (float) zombie.getPosition().getY();
        float targetY = rowCenter.y;
        if (Math.abs(targetY - startY) <= LANE_ARRIVAL_TOLERANCE_PIXELS) {
            return false;
        }

        double forwardPxPerTick = forwardPixelsPerTick(zombie);
        double lateralPxPerTick = forwardPxPerTick * Math.max(1f, lateralSpeedMultiplier);
        int durationTicks = computeLanePullDurationTicks(targetY - startY, lateralPxPerTick);
        float targetX = computeLanePullTargetX(startX, durationTicks, forwardPxPerTick);

        MovementStrategy restoreStrategy = current != null
            ? current.getRestoreStrategy()
            : zombie.getMovementStrategy();
        if (restoreStrategy instanceof DiagonalLaneSwitchMovement) {
            restoreStrategy = new NormalMovement();
        }

        zombie.setTargetPlant(null);
        if (zombie.getState() != Zombie.ZombieState.USING_ABILITY
            && zombie.getState() != Zombie.ZombieState.DYING) {
            zombie.setState(Zombie.ZombieState.WALKING);
        }
        zombie.setMovementStrategy(new DiagonalLaneSwitchMovement(
            startX,
            startY,
            targetX,
            targetY,
            targetRow,
            durationTicks,
            restoreStrategy
        ));
        return true;
    }

    private static DiagonalLaneSwitchMovement currentDivert(Zombie zombie) {
        if (zombie == null || !(zombie.getMovementStrategy() instanceof DiagonalLaneSwitchMovement divert)) {
            return null;
        }
        return divert;
    }
}

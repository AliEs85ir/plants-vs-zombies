package game.entity.plant.ability;

import game.entity.plant.Plant;
import game.entity.zombie.Zombie;
import game.world.playground.Grid;

public final class FieldOfView {
    public static final double DEFAULT_CONE_HALF_ANGLE = 42.0;

    private final double angleDegrees;
    private final double halfAngleDegrees;
    private final double maxRange;
    private final int laneOffset;
    private final boolean laneBased;
    private final boolean forward;

    private FieldOfView(
        double angleDegrees,
        double halfAngleDegrees,
        double maxRange,
        int laneOffset,
        boolean laneBased,
        boolean forward
    ) {
        this.angleDegrees = angleDegrees;
        this.halfAngleDegrees = halfAngleDegrees;
        this.maxRange = maxRange;
        this.laneOffset = laneOffset;
        this.laneBased = laneBased;
        this.forward = forward;
    }

    public static FieldOfView forwardLane() {
        return forwardLane(0, -1);
    }

    public static FieldOfView forwardLane(int laneOffset) {
        return forwardLane(laneOffset, -1);
    }

    public static FieldOfView forwardLane(int laneOffset, double maxRange) {
        return new FieldOfView(0, 90, maxRange, laneOffset, true, true);
    }

    public static FieldOfView backwardLane() {
        return backwardLane(0, -1);
    }

    public static FieldOfView backwardLane(int laneOffset) {
        return backwardLane(laneOffset, -1);
    }

    public static FieldOfView backwardLane(int laneOffset, double maxRange) {
        return new FieldOfView(180, 90, maxRange, laneOffset, true, false);
    }

    public static FieldOfView cone(double angleDegrees) {
        return cone(angleDegrees, DEFAULT_CONE_HALF_ANGLE, -1);
    }

    public static FieldOfView cone(double angleDegrees, double halfAngleDegrees) {
        return cone(angleDegrees, halfAngleDegrees, -1);
    }

    public static FieldOfView cone(double angleDegrees, double halfAngleDegrees, double maxRange) {
        return new FieldOfView(angleDegrees, halfAngleDegrees, maxRange, 0, false, true);
    }

    public boolean contains(Plant plant, Zombie zombie, Grid grid) {
        if (plant == null || zombie == null || zombie.isDead() || grid == null) {
            return false;
        }
        if (plant.getPosition() == null || zombie.getPosition() == null) {
            return false;
        }
        if (laneBased) {
            return containsLane(plant, zombie, grid);
        }
        return containsCone(plant, zombie, grid);
    }

    public boolean isLaneBased() {
        return laneBased;
    }

    public boolean isForward() {
        return forward;
    }

    public int getLaneOffset() {
        return laneOffset;
    }

    public double getAngleDegrees() {
        return angleDegrees;
    }

    public double getMaxRange() {
        return maxRange;
    }

    private boolean containsLane(Plant plant, Zombie zombie, Grid grid) {
        int plantLane = grid.getEntityLane(plant);
        int targetLane = plantLane + laneOffset;
        if (targetLane < 0 || targetLane >= grid.getRowCount()) {
            return false;
        }
//        if (grid.getEntityLane(zombie) != targetLane) {
//            return false;
//        }
        int mask = (1 << targetLane);
        if ((zombie.getLaneMask() & mask) == 0) {
            return false;
        }

        double plantX = plant.getPosition().getX();
        double zombieX = zombie.getPosition().getX();
        double lead = grid.getConfig().getCellWidth();
        double range = resolveRange(plant, grid);

        if (forward) {
            return zombieX >= plantX - lead && zombieX <= plantX + range;
        }
        return zombieX <= plantX + lead && zombieX >= plantX - range;
    }

    private boolean containsCone(Plant plant, Zombie zombie, Grid grid) {
        double dx = zombie.getPosition().getX() - plant.getPosition().getX();
        double dy = zombie.getPosition().getY() - plant.getPosition().getY();
        double distance = Math.hypot(dx, dy);
        double range = resolveRange(plant, grid);
        if (distance > range) {
            return false;
        }

        double closeRange = grid.getConfig().getCellWidth();
        if (distance <= closeRange) {
            return true;
        }

        double zombieAngle = Math.toDegrees(Math.atan2(dy, dx));
        return angleDifference(zombieAngle, angleDegrees) <= halfAngleDegrees;
    }

    private double resolveRange(Plant plant, Grid grid) {
        if (maxRange >= 0) {
            return maxRange + grid.getConfig().getCellWidth();
        }
        if (!laneBased) {
            return Math.hypot(grid.getPlaygroundWidth(), grid.getPlaygroundHeight())
                + grid.getConfig().getCellWidth();
        }
        double plantX = plant.getPosition().getX();
        double playgroundLeft = grid.getConfig().getPlaygroundX();
        double playgroundRight = playgroundLeft + grid.getPlaygroundWidth();
        double lead = grid.getConfig().getCellWidth();
        if (forward) {
            return playgroundRight - plantX + lead;
        }
        return plantX - playgroundLeft + lead;
    }

    private static double angleDifference(double first, double second) {
        double diff = Math.abs(normalizeAngle(first) - normalizeAngle(second));
        if (diff > 180) {
            diff = 360 - diff;
        }
        return diff;
    }

    private static double normalizeAngle(double angle) {
        double normalized = angle % 360;
        if (normalized < 0) {
            normalized += 360;
        }
        return normalized;
    }
}

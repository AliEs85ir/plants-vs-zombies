package game.entity.plant.ability;

import common.model.Position;
import game.component.physic.PhysicService;
import game.entity.plant.Plant;
import game.entity.zombie.Zombie;
import game.world.GameWorld;
import game.world.playground.Cell;
import game.world.playground.Grid;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class PlantTargeting {
    public static final int ALL_LAWN_RADIUS = -1;

    private PlantTargeting() {
    }

    public static boolean hasVisibleZombie(GameWorld world, Plant plant, FieldOfView fov) {
        return selectTarget(world, plant, fov) != null;
    }

    public static Zombie selectTarget(GameWorld world, Plant plant, FieldOfView fov) {
        List<Zombie> visible = findVisibleZombies(world, plant, fov);
        if (visible.isEmpty()) {
            return null;
        }

        Zombie selected = null;
        double bestScore = Double.MAX_VALUE;
        for (Zombie zombie : visible) {
            double score = scoreTarget(plant, zombie, fov);
            if (selected == null || score < bestScore) {
                selected = zombie;
                bestScore = score;
            }
        }
        return selected;
    }

    public static List<Zombie> findVisibleZombies(GameWorld world, Plant plant, FieldOfView fov) {
        List<Zombie> result = new ArrayList<>();
        if (world == null || plant == null || fov == null || world.getZombies() == null) {
            return result;
        }
        Grid grid = world.getGrid();
        for (Zombie zombie : world.getZombies()) {
            if (fov.contains(plant, zombie, grid)) {
                result.add(zombie);
            }
        }
        return result;
    }

    public static Cell cellOf(GameWorld world, Plant plant) {
        if (plant == null) {
            return null;
        }
        if (plant.getCell() != null) {
            return plant.getCell();
        }
        if (world == null || world.getGrid() == null || plant.getPosition() == null) {
            return null;
        }
        return world.getGrid().getCellAt(plant.getPosition());
    }

    public static Cell cellOf(GameWorld world, Zombie zombie) {
        if (world == null || world.getGrid() == null || zombie == null || zombie.getPosition() == null) {
            return null;
        }
        return world.getGrid().getCellAt(zombie.getPosition());
    }

    public static boolean occupiesCell(GameWorld world, Zombie zombie, Cell cell) {
        if (zombie == null || cell == null || !isEligible(zombie)) {
            return false;
        }
        if (zombie.getPosition() != null
            && cell.contains((float) zombie.getPosition().getX(), (float) zombie.getPosition().getY())) {
            return true;
        }
        Cell zombieCell = cellOf(world, zombie);
        if (zombieCell == cell) {
            return true;
        }
        Plant target = zombie.getTargetPlant();
        return target != null && !target.isDead() && target.getCell() == cell;
    }

    public static List<Zombie> zombiesInCell(GameWorld world, Cell cell) {
        List<Zombie> result = new ArrayList<>();
        if (world == null || cell == null || world.getZombies() == null) {
            return result;
        }
        for (Zombie zombie : world.getZombies()) {
            if (occupiesCell(world, zombie, cell)) {
                result.add(zombie);
            }
        }
        return result;
    }

    public static Zombie firstTriggeringZombie(GameWorld world, Plant plant) {
        Cell cell = cellOf(world, plant);
        if (cell == null || world == null || world.getZombies() == null) {
            return null;
        }
        List<Zombie> triggering = new ArrayList<>();
        for (Zombie zombie : world.getZombies()) {
            if (!isEligible(zombie)) {
                continue;
            }
            if (zombie.getTargetPlant() == plant || occupiesCell(world, zombie, cell)) {
                triggering.add(zombie);
            }
        }
        return firstByArrival(triggering);
    }

    public static List<Zombie> zombiesInChebyshevArea(GameWorld world, int centerRow, int centerCol, int radius) {
        return zombiesInChebyshevArea(world, centerRow, centerCol, radius, 0f);
    }

    public static List<Zombie> zombiesInChebyshevArea(
        GameWorld world,
        int centerRow,
        int centerCol,
        int radius,
        float areaPercent
    ) {
        List<Zombie> result = new ArrayList<>();
        if (world == null || world.getZombies() == null || world.getGrid() == null) {
            return result;
        }
        if (radius < 0) {
            for (Zombie zombie : world.getZombies()) {
                if (isEligible(zombie)) {
                    result.add(zombie);
                }
            }
            return result;
        }
        Grid grid = world.getGrid();
        float scale = 1f + areaPercent;
        float cellW = grid.getConfig().getCellWidth();
        float cellH = grid.getConfig().getCellHeight();
        Cell center = grid.getCell(centerRow, centerCol);
        float cx = center != null
            ? center.getWorldX()
            : grid.getConfig().getPlaygroundX() + (centerCol + 0.5f) * cellW;
        float cy = center != null
            ? center.getWorldY()
            : grid.getConfig().getPlaygroundY() + (centerRow + 0.5f) * cellH;
        float halfW = (radius + 0.5f) * cellW * scale;
        float halfH = (radius + 0.5f) * cellH * scale;
        for (Zombie zombie : world.getZombies()) {
            if (!isEligible(zombie) || zombie.getPosition() == null) {
                continue;
            }
            float zx = (float) zombie.getPosition().getX();
            float zy = (float) zombie.getPosition().getY();
            if (Math.abs(zx - cx) <= halfW && Math.abs(zy - cy) <= halfH) {
                result.add(zombie);
                continue;
            }
            Cell zombieCell = cellOf(world, zombie);
            if (zombieCell != null && inChebyshev(zombieCell, centerRow, centerCol, radius)) {
                result.add(zombie);
                continue;
            }
            Plant target = zombie.getTargetPlant();
            if (target != null && !target.isDead() && target.getCell() != null
                && inChebyshev(target.getCell(), centerRow, centerCol, radius)) {
                result.add(zombie);
            }
        }
        return result;
    }

    public static List<Zombie> zombiesInCellRadius(GameWorld world, Cell origin, int radius) {
        if (radius < 0) {
            return zombiesInChebyshevArea(world, 0, 0, ALL_LAWN_RADIUS);
        }
        List<Zombie> result = new ArrayList<>();
        if (world == null || origin == null || world.getGrid() == null) {
            return result;
        }
        Grid grid = world.getGrid();
        Set<Zombie> unique = new LinkedHashSet<>();
        for (int row = origin.getRow() - radius; row <= origin.getRow() + radius; row++) {
            for (int col = origin.getCol() - radius; col <= origin.getCol() + radius; col++) {
                unique.addAll(zombiesInCell(world, grid.getCell(row, col)));
            }
        }
        result.addAll(unique);
        return result;
    }

    public static List<Zombie> zombiesInOccupiedChebyshev(
        GameWorld world,
        Cell origin,
        int radius,
        float areaPercent
    ) {
        Set<Zombie> unique = new LinkedHashSet<>();
        unique.addAll(zombiesInCellRadius(world, origin, radius));
        if (origin != null) {
            unique.addAll(zombiesInChebyshevArea(
                world,
                origin.getRow(),
                origin.getCol(),
                radius,
                areaPercent
            ));
        }
        return new ArrayList<>(unique);
    }

    private static boolean inChebyshev(Cell cell, int centerRow, int centerCol, int radius) {
        return Math.abs(cell.getRow() - centerRow) <= radius
            && Math.abs(cell.getCol() - centerCol) <= radius;
    }

    public static List<Zombie> zombiesInRow(GameWorld world, int row) {
        List<Zombie> result = new ArrayList<>();
        if (world == null) {
            return result;
        }
        List<Zombie> zombies = world.getZombiesInRow(row);
        if (zombies == null) {
            return result;
        }
        for (Zombie zombie : zombies) {
            if (isEligible(zombie)) {
                result.add(zombie);
            }
        }
        return result;
    }

    public static List<Zombie> zombiesForwardInLane(GameWorld world, Plant plant) {
        List<Zombie> result = new ArrayList<>();
        if (world == null || plant == null || plant.getPosition() == null || world.getGrid() == null) {
            return result;
        }
        Cell cell = cellOf(world, plant);
        int row = cell != null ? cell.getRow() : world.getGrid().getEntityLane(plant);
        double plantX = plant.getPosition().getX();
        boolean facingLeft = plant.facesLeft();
        for (Zombie zombie : zombiesInRow(world, row)) {
            if (zombie.getPosition() == null) {
                continue;
            }
            if (zombie.getTargetPlant() == plant) {
                result.add(zombie);
                continue;
            }
            double zombieX = zombie.getPosition().getX();
            boolean inFront = facingLeft ? zombieX <= plantX : zombieX >= plantX;
            if (inFront) {
                result.add(zombie);
            }
        }
        return result;
    }

    public static Zombie firstZombieInCell(GameWorld world, Cell cell) {
        List<Zombie> zombies = zombiesInCell(world, cell);
        return firstByArrival(zombies);
    }

    public static Cell frontCell(GameWorld world, Plant plant) {
        Cell origin = cellOf(world, plant);
        if (origin == null || world == null || world.getGrid() == null) {
            return null;
        }
        int offset = plant != null && plant.facesLeft() ? -1 : 1;
        return world.getGrid().getCell(origin.getRow(), origin.getCol() + offset);
    }

    public static Zombie firstZombieInFrontCell(GameWorld world, Plant plant) {
        return firstZombieInCell(world, frontCell(world, plant));
    }

    public static Zombie firstZombieInHorizontalRange(GameWorld world, Plant plant) {
        Cell origin = cellOf(world, plant);
        if (origin == null || world == null || world.getGrid() == null) {
            return null;
        }
        Grid grid = world.getGrid();
        Cell left = grid.getCell(origin.getRow(), origin.getCol() - 1);
        Cell right = grid.getCell(origin.getRow(), origin.getCol() + 1);
        Zombie own = firstZombieInCell(world, origin);
        if (own != null) {
            return own;
        }
        Zombie ahead = firstZombieInCell(world, right);
        if (ahead != null) {
            return ahead;
        }
        return firstZombieInCell(world, left);
    }

    public static List<Zombie> zombiesEating(GameWorld world, Plant plant) {
        List<Zombie> result = new ArrayList<>();
        if (world == null || plant == null || world.getZombies() == null) {
            return result;
        }
        for (Zombie zombie : world.getZombies()) {
            if (isEating(zombie, plant)) {
                result.add(zombie);
            }
        }
        return result;
    }

    public static boolean isEating(Zombie zombie, Plant plant) {
        return isEligible(zombie)
            && plant != null
            && plant.getHealth() != null
            && !plant.isDead()
            && zombie.getState() == Zombie.ZombieState.EATING
            && zombie.getTargetPlant() == plant;
    }

    public static boolean isEligible(Zombie zombie) {
        return zombie != null && !zombie.isDead();
    }

    private static Zombie firstByArrival(List<Zombie> zombies) {
        Zombie selected = null;
        for (Zombie zombie : zombies) {
            if (!isEligible(zombie)) {
                continue;
            }
            if (selected == null || zombie.getPosition().getX() < selected.getPosition().getX()) {
                selected = zombie;
            }
        }
        return selected;
    }

    public static Position predictLanding(Position start, Zombie zombie, double projectileSpeedPx, double leadScale) {
        if (start == null) {
            return new Position(0, 0);
        }
        if (zombie == null || zombie.getPosition() == null) {
            return new Position(start.getX(), start.getY());
        }
        Position now = zombie.getPosition();
        if (zombie.isDead()) {
            return new Position(now.getX(), now.getY());
        }
        double vx = 0;
        double vy = 0;
        if (zombie.getBody() != null) {
            var velocity = zombie.getBody().getLinearVelocity();
            vx = velocity.x * PhysicService.PIXEL_PER_METER;
            vy = velocity.y * PhysicService.PIXEL_PER_METER;
        }
        double predX = now.getX();
        double predY = now.getY();
        double speed = Math.max(1, projectileSpeedPx);
        double scale = Math.max(0, leadScale);
        for (int i = 0; i < 3; i++) {
            double travel = Math.hypot(predX - start.getX(), predY - start.getY());
            double flightSeconds = travel / speed;
            predX = now.getX() + vx * flightSeconds * scale;
            predY = now.getY() + vy * flightSeconds * scale;
        }
        return new Position(predX, predY);
    }

    private static double scoreTarget(Plant plant, Zombie zombie, FieldOfView fov) {
        if (fov.isLaneBased()) {
            double zombieX = zombie.getPosition().getX();
            return fov.isForward() ? zombieX : -zombieX;
        }
        double dx = zombie.getPosition().getX() - plant.getPosition().getX();
        double dy = zombie.getPosition().getY() - plant.getPosition().getY();
        return dx * dx + dy * dy;
    }
}

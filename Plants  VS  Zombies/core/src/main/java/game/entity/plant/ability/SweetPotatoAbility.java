package game.entity.plant.ability;

import game.entity.plant.Plant;
import game.entity.zombie.Zombie;
import game.entity.zombie.ZombieLaneSwitch;
import game.service.plant.PlantService;
import game.world.GameWorld;
import game.world.playground.Cell;
import game.world.playground.Grid;

public class SweetPotatoAbility extends AbstractPlantAbility {
    public static final int ATTRACT_ROW_RADIUS = 1;
    public static final int ATTRACT_COL_BEHIND = 1;
    public static final int ATTRACT_COL_AHEAD = 2;

    @Override
    protected void doUpdate(Plant plant, PlantService plantService, long currentTick) {
        if (plant == null || !plant.isAlive() || plantService == null) {
            return;
        }
        GameWorld world = plantService.getGameWorld();
        if (world == null || world.getGrid() == null || world.getZombies() == null) {
            return;
        }
        Cell origin = PlantTargeting.cellOf(world, plant);
        if (origin == null) {
            return;
        }
        Grid grid = world.getGrid();
        int targetRow = origin.getRow();
        for (Zombie zombie : world.getZombies()) {
            if (!PlantTargeting.isEligible(zombie) || ZombieLaneSwitch.isDivertInProgress(zombie)) {
                continue;
            }
            if (grid.getEntityLane(zombie) == targetRow) {
                continue;
            }
            if (!coversZombie(world, origin, zombie)) {
                continue;
            }
            if (!isClosestAttractor(world, plant, origin, zombie)) {
                continue;
            }
            ZombieLaneSwitch.beginLanePull(
                zombie,
                grid,
                targetRow,
                ZombieLaneSwitch.LANE_PULL_SPEED_MULTIPLIER,
                false
            );
        }
    }

    public static boolean inAttractRange(int plantRow, int plantCol, int zombieRow, int zombieCol) {
        int rowDelta = zombieRow - plantRow;
        int colDelta = zombieCol - plantCol;
        return rowDelta >= -ATTRACT_ROW_RADIUS
            && rowDelta <= ATTRACT_ROW_RADIUS
            && colDelta >= -ATTRACT_COL_BEHIND
            && colDelta <= ATTRACT_COL_AHEAD;
    }

    public static int attractCellCount() {
        return (ATTRACT_ROW_RADIUS * 2 + 1) * (ATTRACT_COL_BEHIND + 1 + ATTRACT_COL_AHEAD);
    }

    static boolean coversZombie(GameWorld world, Cell origin, Zombie zombie) {
        if (origin == null || zombie == null) {
            return false;
        }
        Cell zombieCell = PlantTargeting.cellOf(world, zombie);
        if (zombieCell != null
            && inAttractRange(origin.getRow(), origin.getCol(), zombieCell.getRow(), zombieCell.getCol())) {
            return true;
        }
        Plant target = zombie.getTargetPlant();
        if (target == null || target.isDead() || target.getCell() == null) {
            return false;
        }
        Cell targetCell = target.getCell();
        return inAttractRange(origin.getRow(), origin.getCol(), targetCell.getRow(), targetCell.getCol());
    }

    private static boolean isClosestAttractor(GameWorld world, Plant plant, Cell origin, Zombie zombie) {
        if (world.getPlants() == null) {
            return true;
        }
        Cell zombieCell = PlantTargeting.cellOf(world, zombie);
        int bestDistance = distance(origin, zombieCell);
        int bestRow = origin.getRow();
        int bestCol = origin.getCol();
        Plant best = plant;
        for (Plant other : world.getPlants()) {
            if (other == null || other == plant || !other.isAlive() || !hasSweetPotatoAbility(other)) {
                continue;
            }
            Cell otherOrigin = PlantTargeting.cellOf(world, other);
            if (otherOrigin == null || !coversZombie(world, otherOrigin, zombie)) {
                continue;
            }
            int distance = distance(otherOrigin, zombieCell);
            if (isCloser(otherOrigin, distance, bestRow, bestCol, bestDistance)) {
                best = other;
                bestDistance = distance;
                bestRow = otherOrigin.getRow();
                bestCol = otherOrigin.getCol();
            }
        }
        return best == plant;
    }

    private static boolean hasSweetPotatoAbility(Plant plant) {
        if (plant.getAbilities() == null) {
            return false;
        }
        for (PlantAbility ability : plant.getAbilities()) {
            if (ability instanceof SweetPotatoAbility) {
                return true;
            }
        }
        return false;
    }

    private static int distance(Cell origin, Cell zombieCell) {
        if (origin == null || zombieCell == null) {
            return Integer.MAX_VALUE;
        }
        return Math.max(
            Math.abs(zombieCell.getRow() - origin.getRow()),
            Math.abs(zombieCell.getCol() - origin.getCol())
        );
    }

    private static boolean isCloser(Cell candidate, int candidateDistance, int bestRow, int bestCol, int bestDistance) {
        if (candidateDistance < bestDistance) {
            return true;
        }
        if (candidateDistance > bestDistance) {
            return false;
        }
        if (candidate.getRow() != bestRow) {
            return candidate.getRow() < bestRow;
        }
        return candidate.getCol() < bestCol;
    }
}

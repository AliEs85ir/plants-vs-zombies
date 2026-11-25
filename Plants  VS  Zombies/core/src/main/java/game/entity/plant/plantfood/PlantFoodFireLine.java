package game.entity.plant.plantfood;

import common.model.Position;
import game.entity.plant.Plant;
import game.world.playground.Cell;
import game.world.playground.Grid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Playable cells in front of a plant until the playground boundary (not a fixed column count).
 * Off-playground cells are omitted; after the last playable cell the line is empty.
 */
public final class PlantFoodFireLine {
    private PlantFoodFireLine() {
    }

    public static List<Cell> cellsInFront(Grid grid, Plant plant) {
        if (grid == null || plant == null || plant.getCell() == null) {
            return List.of();
        }
        Cell origin = plant.getCell();
        int row = origin.getRow();
        int col = origin.getCol();
        int step = plant.facesLeft() ? -1 : 1;
        List<Cell> cells = new ArrayList<>();
        for (int c = col + step; ; c += step) {
            Cell cell = grid.getCell(row, c);
            if (cell == null) {
                break;
            }
            Position center = new Position(cell.getWorldX(), cell.getWorldY());
            if (grid.getConfig() != null
                && !grid.isInsidePlayGround(center)
                && !grid.isInsideGrid(center)) {
                break;
            }
            cells.add(cell);
        }
        return Collections.unmodifiableList(cells);
    }
}

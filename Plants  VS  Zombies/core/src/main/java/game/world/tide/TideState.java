package game.world.tide;

import game.world.playground.Cell;
import game.world.playground.Grid;

import java.util.ArrayList;
import java.util.List;

/**
 * Gameplay source of truth for water. Visual overlays read the visual water X
 * separately; they never write back into this state.
 */
public final class TideState {
    private TideLevel currentLevel;
    private TideLevel targetLevel;
    private WaterLine waterLine;
    private final TideLine tideLine;
    private boolean transitioning;
    private float visualWaterX;

    public TideState(TideLine tideLine, TideLevel initialLevel, WaterLine waterLine, float visualWaterX) {
        this.tideLine = tideLine;
        this.currentLevel = initialLevel;
        this.targetLevel = initialLevel;
        this.waterLine = waterLine;
        this.visualWaterX = visualWaterX;
        this.transitioning = false;
    }

    public TideLine getTideLine() {
        return tideLine;
    }

    public TideLevel getCurrentLevel() {
        return currentLevel;
    }

    public TideLevel getTargetLevel() {
        return targetLevel;
    }

    public WaterLine getWaterLine() {
        return waterLine;
    }

    public boolean isWater(int column) {
        return waterLine.isWater(column);
    }

    public boolean isLand(int column) {
        return !isWater(column);
    }

    public boolean isWater(Cell cell) {
        return cell != null && isWater(cell.getCol());
    }

    public boolean isTransitioning() {
        return transitioning;
    }

    public float getVisualWaterX() {
        return visualWaterX;
    }

    public void setVisualWaterX(float visualWaterX) {
        this.visualWaterX = visualWaterX;
    }

    public void beginTransition(TideLevel target, WaterLine nextWaterLine) {
        this.targetLevel = target;
        this.waterLine = nextWaterLine;
        this.transitioning = true;
    }

    public void completeTransition() {
        this.currentLevel = targetLevel;
        this.transitioning = false;
    }

    public List<Cell> collectWaterCells(Grid grid) {
        return collectCells(grid, true);
    }

    public List<Cell> collectLandCells(Grid grid) {
        return collectCells(grid, false);
    }

    private List<Cell> collectCells(Grid grid, boolean water) {
        List<Cell> result = new ArrayList<>();
        if (grid == null) {
            return result;
        }
        for (int row = 0; row < grid.getRowCount(); row++) {
            for (int col = 0; col < grid.getColCount(); col++) {
                if (isWater(col) == water) {
                    result.add(grid.getCell(row, col));
                }
            }
        }
        return result;
    }
}

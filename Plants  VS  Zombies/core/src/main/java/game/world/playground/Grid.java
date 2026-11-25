package game.world.playground;

import com.badlogic.gdx.math.Vector2;
import common.model.Position;
import game.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class Grid {
    private final MapConfig config;
    private final Cell[][] cells;

    public Grid(MapConfig config) {
        this.config = config;
        this.cells = new Cell[config.getRows()][config.getCols()];
        initializeCells();
    }

    private void initializeCells() {
        for (int row = 0; row < config.getRows(); row++) {
            for (int col = 0; col < config.getCols(); col++) {
                float x = config.getPlaygroundX() + col * config.getCellWidth();
                float y = config.getPlaygroundY() + row * config.getCellHeight();

                cells[row][col] = new Cell(
                    row,
                    col,
                    x,
                    y,
                    config.getCellWidth(),
                    config.getCellHeight()
                );
            }
        }
    }

    public Cell getCell(int row, int col) {
        if (row < 0 || row >= config.getRows() || col < 0 || col >= config.getCols()) {
            return null;
        }

        return cells[row][col];
    }

    public Cell getCellAt(float worldX, float worldY) {
        if (
            worldX < config.getPlaygroundX()
                || worldX >= config.getPlaygroundX() + config.getPlaygroundWidth()
                || worldY < config.getPlaygroundY()
                || worldY >= config.getPlaygroundY() + config.getPlaygroundHeight()
        ) {
            return null;
        }

        int col = (int) (
            (worldX - config.getPlaygroundX())
                / config.getCellWidth()
        );

        int row = (int) (
            (worldY - config.getPlaygroundY())
                / config.getCellHeight()
        );

        return getCell(row, col);
    }

    public Cell getCellAt(Position position) {
        if (position == null) {
            return null;
        }
        return getCellAt((float) position.getX(), (float) position.getY());
    }

    public Vector2 getCellCenter(int row, int col) {
        Cell cell = getCell(row, col);

        if (cell == null) {
            return null;
        }

        return cell.getWorldPosition();
    }
    public List<Cell> getColumnCells(int col) {
        List<Cell> result = new ArrayList<>();

        if (col < 0 || col >= config.getCols()) {
            return result;
        }

        for (int row = 0; row < config.getRows(); row++) {
            result.add(cells[row][col]);
        }

        return result;
    }

    public int getRowCount() {
        return config.getRows();
    }

    public int getColCount() {
        return config.getCols();
    }

    public MapConfig getConfig() {
        return config;
    }

    public Cell[] getAllCells() {
        Cell[] result = new Cell[config.getRows() * config.getCols()];

        int index = 0;

        for (int row = 0; row < config.getRows(); row++) {
            for (int col = 0; col < config.getCols(); col++) {
                result[index++] = cells[row][col];
            }
        }
        System.out.println(
            "WORLD: x=" + config.getPlaygroundX()
                + " y=" + config.getPlaygroundY()
                + " | PLAYGROUND: x=" + config.getPlaygroundX()
                + " y=" + config.getPlaygroundY()
                + " width=" + config.getPlaygroundWidth()
                + " height=" + config.getPlaygroundHeight()
        );
        return result;
    }

    public boolean isInsideGrid(Position position) {
        return getCellAt(position) != null;
    }

    public float getPlaygroundWidth() {
        return config.getPlaygroundWidth();
    }

    public float getPlaygroundHeight() {
        return config.getPlaygroundHeight();
    }

    public boolean isInsideMap(Position position) {
        return config.isInsideMap(position);
    }

    public int getEntityLane(Entity entity) {
        Position position = entity.getPosition();
        if (position == null) {
            return -1;
        }

        float worldY = (float) position.getY();

        float cellHeight = config.getCellHeight();
        float playgroundY = config.getPlaygroundY();

        int row = (int) Math.floor((worldY - playgroundY) / cellHeight);

        return Math.max(0, Math.min(row, config.getRows() - 1));
    }

    public int getEntityLane(Position position) {
        if (position == null) {
            return -1;
        }

        float worldY = (float) position.getY();

        float cellHeight = config.getCellHeight();
        float playgroundY = config.getPlaygroundY();

        int row = (int) Math.floor((worldY - playgroundY) / cellHeight);

        return Math.max(0, Math.min(row, config.getRows() - 1));
    }

    public int getEntityLane(float x, float y) {
        return getEntityLane(new Position(x, y));
    }

    public boolean isInsidePlayGround(Position position) {
        return config.isInsidePlayGround(position);
    }

    public List<Cell> getLastColumns(int columnCount) {
        List<Cell> result = new ArrayList<>();
        int startCol = Math.max(0, config.getCols() - columnCount);

        for (int row = 0; row < config.getRows(); row++) {
            for (int col = startCol; col < config.getCols(); col++) {
                result.add(cells[row][col]);
            }
        }

        return result;
    }
    public List<Cell> getFirstColumns(int columnCount) {
        List<Cell> result = new ArrayList<>();
        int startCol = 0;

        for (int row = 0; row < config.getRows(); row++) {
            for (int col = startCol; col < columnCount; col++) {
                result.add(cells[row][col]);
            }
        }

        return result;
    }

    public List<Cell> getRowCells(int row) {
        List<Cell> result = new ArrayList<>();

        if (row < 0 || row >= config.getRows()) {
            return result;
        }

        for (int col = 0; col < config.getCols(); col++) {
            result.add(cells[row][col]);
        }

        return result;
    }

    public Vector2 getRowCenter(int row) {
        if (row < 0 || row >= config.getRows()) {
            return null;
        }

        float centerX = config.getPlaygroundX() + config.getPlaygroundWidth() / 2f;
        float centerY = config.getPlaygroundY() + (row + 0.5f) * config.getCellHeight();

        return new Vector2(centerX, centerY);
    }
}

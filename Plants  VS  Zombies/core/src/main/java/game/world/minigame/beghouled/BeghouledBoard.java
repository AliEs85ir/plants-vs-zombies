package game.world.minigame.beghouled;

import app.enums.plant.PlantName;

import java.util.Arrays;
import java.util.Objects;

/**
 * Snapshot of the Beghouled grid. No LibGDX or Box2D.
 * Row 0 is the visually lower lawn row (grid y = playgroundY + row * cellHeight).
 */
public final class BeghouledBoard {
    private final PlantName[][] tiles;
    private final boolean[][] crater;

    public BeghouledBoard(int rows, int cols) {
        if (rows < 1 || cols < 1) {
            throw new IllegalArgumentException("Board must be at least 1x1");
        }
        this.tiles = new PlantName[rows][cols];
        this.crater = new boolean[rows][cols];
    }

    public static BeghouledBoard copyOf(BeghouledBoard other) {
        BeghouledBoard copy = new BeghouledBoard(other.rows(), other.cols());
        for (int row = 0; row < other.rows(); row++) {
            for (int col = 0; col < other.cols(); col++) {
                copy.tiles[row][col] = other.tiles[row][col];
                copy.crater[row][col] = other.crater[row][col];
            }
        }
        return copy;
    }

    public int rows() {
        return tiles.length;
    }

    public int cols() {
        return tiles[0].length;
    }

    public boolean inBounds(int row, int col) {
        return row >= 0 && row < rows() && col >= 0 && col < cols();
    }

    public PlantName get(int row, int col) {
        return inBounds(row, col) ? tiles[row][col] : null;
    }

    public void set(int row, int col, PlantName plant) {
        if (!inBounds(row, col) || crater[row][col]) {
            return;
        }
        tiles[row][col] = plant;
    }

    public boolean isCrater(int row, int col) {
        return inBounds(row, col) && crater[row][col];
    }

    public void setCrater(int row, int col, boolean blocked) {
        if (!inBounds(row, col)) {
            return;
        }
        crater[row][col] = blocked;
        if (blocked) {
            tiles[row][col] = null;
        }
    }

    public boolean isEmpty(int row, int col) {
        return inBounds(row, col) && !crater[row][col] && tiles[row][col] == null;
    }

    public boolean isPlayable(int row, int col) {
        return inBounds(row, col) && !crater[row][col];
    }

    public void swap(int r1, int col1, int r2, int col2) {
        if (!inBounds(r1, col1) || !inBounds(r2, col2) || crater[r1][col1] || crater[r2][col2]) {
            return;
        }
        PlantName first = tiles[r1][col1];
        tiles[r1][col1] = tiles[r2][col2];
        tiles[r2][col2] = first;
    }

    public boolean adjacent(int r1, int col1, int r2, int col2) {
        if (!inBounds(r1, col1) || !inBounds(r2, col2)) {
            return false;
        }
        return Math.abs(r1 - r2) + Math.abs(col1 - col2) == 1;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof BeghouledBoard other)) {
            return false;
        }
        return Arrays.deepEquals(tiles, other.tiles) && Arrays.deepEquals(crater, other.crater);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.deepHashCode(tiles), Arrays.deepHashCode(crater));
    }
}

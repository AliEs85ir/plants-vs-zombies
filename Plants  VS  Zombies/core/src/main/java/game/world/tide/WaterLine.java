package game.world.tide;

/**
 * Current land/water boundary. Water occupies every column at or to the right
 * of {@code startColumn} (0-based). This value moves with tide; {@link TideLine}
 * never does.
 */
public final class WaterLine {
    private final int startColumn;

    public WaterLine(int startColumn) {
        this.startColumn = Math.max(0, startColumn);
    }

    public int getStartColumn() {
        return startColumn;
    }

    public boolean isWater(int column) {
        return column >= startColumn;
    }

    public WaterLine withStartColumn(int column) {
        return new WaterLine(column);
    }
}

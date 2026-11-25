package game.world.tide;

/**
 * Fixed maximum inland reach of water. Independent of the current {@link WaterLine}.
 *
 * Columns are 0-based. {@code firstFloodableColumn = 2} means columns 0 and 1
 * (1-based columns 1 and 2) are always land, and the visual tide line sits on
 * the boundary between column 1 and column 2.
 */
public final class TideLine {
    private final int firstFloodableColumn;

    public TideLine(int firstFloodableColumn) {
        if (firstFloodableColumn < 0) {
            throw new IllegalArgumentException("firstFloodableColumn must be >= 0");
        }
        this.firstFloodableColumn = firstFloodableColumn;
    }

    /**
     * Tide line after {@code alwaysLandColumnCount} land columns on the left.
     * A value of 2 keeps 1-based columns 1 and 2 dry forever.
     */
    public static TideLine afterAlwaysLandColumns(int alwaysLandColumnCount) {
        return new TideLine(alwaysLandColumnCount);
    }

    public int getFirstFloodableColumn() {
        return firstFloodableColumn;
    }

    public boolean canFlood(int column) {
        return column >= firstFloodableColumn;
    }

    public int clampWaterStartColumn(int requestedStartColumn, int columnCount) {
        int maxStart = Math.max(firstFloodableColumn, columnCount);
        int start = Math.max(firstFloodableColumn, requestedStartColumn);
        return Math.min(start, maxStart);
    }
}

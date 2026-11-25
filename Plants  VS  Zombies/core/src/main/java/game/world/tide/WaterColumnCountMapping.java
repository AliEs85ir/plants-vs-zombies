package game.world.tide;

/**
 * Tide N means N columns of water, counted from the right edge of the lawn.
 * Tide 6 on a 9-column grid covers columns 4-9; Tide 3 covers columns 7-9 (low tide).
 * {@link TideLine} still prevents water from entering always-land columns.
 */
public final class WaterColumnCountMapping implements TideLevelMapping {
    @Override
    public int toWaterStartColumn(TideLevel tideLevel, int columnCount, TideLine tideLine) {
        int waterColumns = Math.max(0, tideLevel.getValue());
        int requested = columnCount - waterColumns;
        return tideLine.clampWaterStartColumn(requested, columnCount);
    }
}

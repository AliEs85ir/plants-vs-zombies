package game.world.tide;

/**
 * Converts a configured {@link TideLevel} into a gameplay water-start column.
 */
public interface TideLevelMapping {
    /**
     * @return 0-based first water column, already clamped by {@link TideLine}
     */
    int toWaterStartColumn(TideLevel tideLevel, int columnCount, TideLine tideLine);
}

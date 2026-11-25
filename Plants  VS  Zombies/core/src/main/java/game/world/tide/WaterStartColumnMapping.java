package game.world.tide;

/**
 * Tide N means the water line is the 1-based column N
 * (water occupies column N and every column to its right).
 */
public final class WaterStartColumnMapping implements TideLevelMapping {
    @Override
    public int toWaterStartColumn(TideLevel tideLevel, int columnCount, TideLine tideLine) {
        int requested = Math.max(0, tideLevel.getValue() - 1);
        return tideLine.clampWaterStartColumn(requested, columnCount);
    }
}

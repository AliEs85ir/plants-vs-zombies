package game.world.tide;

/**
 * Named tide intensity from level data. Converted to a {@link WaterLine}
 * through {@link TideLevelMapping} so wave tables never hard-code columns.
 */
public final class TideLevel {
    private final int value;

    public TideLevel(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Tide level must be >= 0");
        }
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof TideLevel tideLevel && tideLevel.value == value;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }

    @Override
    public String toString() {
        return "TideLevel(" + value + ")";
    }
}

package game.world.playground;

public record GridConfig(int rowCount, int columnCount, double cellWidth, double cellHeight) {

    public static GridConfig defaultConfig() {
        return new GridConfig(5, 9, 1f, 1f);
    }

    public double getGridWidth() {
        return columnCount * cellWidth;
    }
}

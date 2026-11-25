package game.world.minigame.beghouled;

import app.enums.plant.PlantName;

import java.util.List;

public final class BeghouledMatch {
    private final PlantName type;
    private final List<BeghouledCell> cells;

    public BeghouledMatch(PlantName type, List<BeghouledCell> cells) {
        this.type = type;
        this.cells = List.copyOf(cells);
    }

    public PlantName type() {
        return type;
    }

    public List<BeghouledCell> cells() {
        return cells;
    }

    public int length() {
        return cells.size();
    }
}

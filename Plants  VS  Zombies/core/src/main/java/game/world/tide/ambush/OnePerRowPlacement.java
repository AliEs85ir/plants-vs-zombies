package game.world.tide.ambush;

import game.world.playground.Cell;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Places one spawn in each lawn row, preferring newly exposed cells.
 */
public final class OnePerRowPlacement implements AmbushPlacement {
    @Override
    public List<Cell> choose(List<Cell> newlyExposedLand, int count) {
        if (newlyExposedLand == null || newlyExposedLand.isEmpty()) {
            return List.of();
        }
        Map<Integer, Cell> byRow = new LinkedHashMap<>();
        for (Cell cell : newlyExposedLand) {
            byRow.putIfAbsent(cell.getRow(), cell);
        }
        List<Cell> chosen = new ArrayList<>(byRow.values());
        if (count > 0 && chosen.size() > count) {
            return List.copyOf(chosen.subList(0, count));
        }
        return List.copyOf(chosen);
    }
}

package game.service.quest;

import app.enums.plant.PlantCategory;
import app.enums.plant.PlantName;
import app.enums.plant.PlantTag;
import game.entity.plant.Plant;
import game.entity.registry.PlantRegistry;
import game.world.GameMap;
import game.world.playground.Cell;
import game.world.playground.Grid;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


public class GameObserver {
    private final GameMap map;
    private int lostPlantsCount;
    private int sunProducerCount;
    private final EnumSet<PlantCategory> familiesPlaced = EnumSet.noneOf(PlantCategory.class);
    private final Set<Integer> rowsPlanted = new HashSet<>();
    private final Set<Integer> columnsPlanted = new HashSet<>();
    private final EnumSet<PlantCategory> familiesThatKilled = EnumSet.noneOf(PlantCategory.class);

    private boolean usedNonMushroom;

    public GameObserver(GameMap map) {
        this.map = map;
    }

    public void onPlantPlaced(PlantName name, int row, int col) {
        PlantCategory category = categoryOf(name);
        if (category != null) {
            familiesPlaced.add(category);
            if (category == PlantCategory.SUN_PRODUCER) {
                sunProducerCount++;
            }
        }
        if (!isMushroom(name)) {
            usedNonMushroom = true;
        }
        rowsPlanted.add(row);
        columnsPlanted.add(col);
    }

    public void onZombieKilled(PlantCategory killerFamily) {
        if (killerFamily != null) {
            familiesThatKilled.add(killerFamily);
        }
    }

    public void addLostPlantsCount(int amount) {
        if (amount > 0) {
            lostPlantsCount += amount;
        }
    }

    public boolean isSymmetric() {
        if (map == null || map.getGrid() == null) {
            return false;
        }
        Grid grid = map.getGrid();
        int rowCount = grid.getRowCount();
        int colCount = grid.getColCount();
        for (int row = 0; row < rowCount / 2; row++) {
            int oppositeRow = rowCount - 1 - row;
            for (int col = 0; col < colCount; col++) {
                if (!arePlantsEqual(grid.getCell(row, col), grid.getCell(oppositeRow, col))) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isAsymmetricExceptMiddle() {
        if (map == null || map.getGrid() == null) {
            return false;
        }
        Grid grid = map.getGrid();
        int rowCount = grid.getRowCount();
        int colCount = grid.getColCount();
        for (int row = 0; row < rowCount / 2; row++) {
            int oppositeRow = rowCount - 1 - row;
            for (int col = 0; col < colCount; col++) {
                if (arePlantsEqual(grid.getCell(row, col), grid.getCell(oppositeRow, col))) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isRowEmpty(int row) {
        if (map == null || map.getGrid() == null) {
            return true;
        }
        Grid grid = map.getGrid();
        if (row < 0 || row >= grid.getRowCount()) {
            return true;
        }
        for (int col = 0; col < grid.getColCount(); col++) {
            Cell cell = grid.getCell(row, col);
            if (cell != null && cell.getPlant() != null) {
                return false;
            }
        }
        return true;
    }

    public boolean isColumnEmpty(int col) {
        if (map == null || map.getGrid() == null) {
            return true;
        }
        Grid grid = map.getGrid();
        if (col < 0 || col >= grid.getColCount()) {
            return true;
        }
        for (int row = 0; row < grid.getRowCount(); row++) {
            Cell cell = grid.getCell(row, col);
            if (cell != null && cell.getPlant() != null) {
                return false;
            }
        }
        return true;
    }

    public boolean isCrossEmpty(int n) {
        return isRowEmpty(n) && isColumnEmpty(n);
    }

    public boolean allPlantsAreMushrooms() {
        if (usedNonMushroom) {
            return false;
        }
        if (map == null || map.getPlants() == null || map.getPlants().isEmpty()) {
            return !familiesPlaced.isEmpty();
        }
        for (Plant plant : map.getPlants()) {
            if (plant == null || plant.getDefinition() == null) {
                continue;
            }
            if (plant.getDefinition().getTags() == null
                || !plant.getDefinition().getTags().hasTag(PlantTag.SHROOM)) {
                return false;
            }
        }
        return true;
    }

    public boolean usedOnlyMushrooms() {
        return allPlantsAreMushrooms();
    }

    public int getLostPlantsCount() {
        return lostPlantsCount;
    }

    public int getSunProducerCount() {
        return sunProducerCount;
    }

    public Set<PlantCategory> familiesPlaced() {
        return Collections.unmodifiableSet(familiesPlaced);
    }

    public Set<PlantCategory> familiesThatKilled() {
        return Collections.unmodifiableSet(familiesThatKilled);
    }

    public Set<Integer> rowsPlanted() {
        return Collections.unmodifiableSet(rowsPlanted);
    }

    public Set<Integer> columnsPlanted() {
        return Collections.unmodifiableSet(columnsPlanted);
    }

    private static PlantCategory categoryOf(PlantName name) {
        if (name == null || PlantRegistry.get(name) == null) {
            return null;
        }
        return PlantRegistry.get(name).getCategory();
    }

    private static boolean isMushroom(PlantName name) {
        if (name == null || PlantRegistry.get(name) == null || PlantRegistry.get(name).getTags() == null) {
            return false;
        }
        return PlantRegistry.get(name).getTags().hasTag(PlantTag.SHROOM);
    }

    private static boolean arePlantsEqual(Cell cell1, Cell cell2) {
        return Objects.equals(plantName(cell1), plantName(cell2));
    }

    private static String plantName(Cell cell) {
        if (cell == null || cell.getPlant() == null) {
            return null;
        }
        return cell.getPlant().getName();
    }
}

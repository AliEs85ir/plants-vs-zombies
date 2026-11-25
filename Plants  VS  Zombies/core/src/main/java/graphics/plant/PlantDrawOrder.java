package graphics.plant;

import common.model.Position;
import game.entity.plant.Plant;
import game.entity.plant.PlantStackLayer;
import game.world.playground.Cell;

import java.util.Comparator;
import java.util.List;

/**
 * Draw order for stacked plants: earlier plants are underneath, except
 * platform (lily pad) stays at the bottom and shield (pumpkin) stays on top.
 * That matches {@link game.service.plant.PlantPlacementPolicy#insertionIndex}.
 */
public final class PlantDrawOrder {

    public static final Comparator<PlantRenderInstance> COMPARATOR =
        PlantDrawOrder::compare;

    private PlantDrawOrder() {
    }

    public static void sort(List<PlantRenderInstance> instances) {
        instances.sort(COMPARATOR);
    }

    private static int compare(PlantRenderInstance left, PlantRenderInstance right) {
        Plant plantA = left.getPlant();
        Plant plantB = right.getPlant();

        Position posA = left.getRenderPosition();
        Position posB = right.getRenderPosition();
        if (posA != null && posB != null) {
            int column = Double.compare(posA.getX(), posB.getX());
            if (column != 0) {
                return column;
            }
        }

        int stack = Integer.compare(stackIndex(plantA), stackIndex(plantB));
        if (stack != 0) {
            return stack;
        }

        return Long.compare(plantA.getPlantedTick(), plantB.getPlantedTick());
    }

    private static int stackIndex(Plant plant) {
        Cell cell = plant.getCell();
        if (cell != null) {
            int index = cell.getPlants().indexOf(plant);
            if (index >= 0) {
                return index;
            }
        }
        PlantStackLayer layer = plant.getDefinition() != null
            ? plant.getDefinition().getStackLayer()
            : PlantStackLayer.CORE;
        return switch (layer) {
            case PLATFORM -> Integer.MIN_VALUE;
            case SHIELD -> Integer.MAX_VALUE;
            case CORE, REPEATABLE -> 0;
        };
    }
}

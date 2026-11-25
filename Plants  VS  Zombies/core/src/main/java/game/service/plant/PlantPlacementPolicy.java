package game.service.plant;

import app.enums.obstacle.ObstacleType;
import app.enums.plant.PlantTag;
import game.entity.obstacle.Obstacle;
import game.entity.plant.Plant;
import game.entity.plant.PlantDefinition;
import game.entity.plant.PlantStackLayer;
import game.world.playground.Cell;
import game.world.playground.celltypes.IceCellType;
import game.world.playground.celltypes.WaterCellType;


public final class PlantPlacementPolicy {
    private PlantPlacementPolicy() {
    }

    public static boolean canPlace(Cell cell, PlantDefinition definition) {
        if (cell == null || definition == null) {
            System.err.println("CANT PLANT BECAUSE CELL OR DEFINITION IS NULL");
            return false;
        }
        if (!isObstacleCompatible(cell, definition)) {
            System.err.println("CANT PLANT BECAUSE CELL HAS OBSTACLE");
            return false;
        }
        if (cell.getCellType() != null
            && !cell.getCellType().canPlant()
            && !isWaterCell(cell)
            && !(isIceTerrainPlant(definition) && isIceCell(cell))) {
            return false;
        }
        if (!isTerrainCompatible(cell, definition)) {
            return false;
        }
        return isStackingAllowed(cell, definition);
    }

    public static boolean isTerrainCompatible(Cell cell, PlantDefinition definition) {
        boolean waterCell = isWaterCell(cell);
        boolean waterPlant = isWaterPlant(definition);
        boolean landOnly = isLandOnlyPlant(definition);
        boolean hasPlatform = hasLayer(cell, PlantStackLayer.PLATFORM);
        boolean iceCell = isIceCell(cell);
        boolean iceTerrainPlant = isIceTerrainPlant(definition);

        if (iceTerrainPlant && !iceCell) {
            return false;
        }
        if (waterPlant && !waterCell) {
            return false;
        }
        if (waterCell && !waterPlant && !hasPlatform) {
            return false;
        }
        if (!waterCell && waterPlant) {
            return false;
        }
        if (landOnly && waterCell && !hasPlatform) {
            return false;
        }
        if (definition.getStackLayer() == PlantStackLayer.SHIELD && waterCell) {
            return false;
        }
        return true;
    }

    public static boolean isPlantStillLegal(Cell cell, Plant plant) {
        if (plant == null || plant.getDefinition() == null) {
            return false;
        }
        PlantDefinition definition = plant.getDefinition();
        if (cell.hasObstacle() && !isGraveBuster(definition)) {
            return false;
        }
        boolean waterCell = isWaterCell(cell);
        boolean waterPlant = isWaterPlant(definition);
        boolean hasOtherPlatform = hasOtherPlantWithLayer(cell, plant, PlantStackLayer.PLATFORM);

        if (waterPlant && !waterCell) {
            return false;
        }
        if (waterCell && !waterPlant && !hasOtherPlatform) {
            return false;
        }
        if (definition.getStackLayer() == PlantStackLayer.SHIELD && waterCell) {
            return false;
        }
        return true;
    }

    public static boolean isObstacleCompatible(Cell cell, PlantDefinition definition) {
        if (isGraveBuster(definition)) {
            return isValidGraveBusterPlacement(cell);
        }
        return cell.getObstacle() == null;
    }

    public static boolean isValidGraveBusterPlacement(Cell cell) {
        return cell != null && isGrave(cell.getObstacle());
    }

    public static boolean isStackingAllowed(Cell cell, PlantDefinition definition) {
        PlantStackLayer layer = definition.getStackLayer();
        int maxCopies = Math.max(1, definition.getMaxPerCell());

        return switch (layer) {
            case PLATFORM ->
                !hasLayer(cell, PlantStackLayer.PLATFORM)
                    && coreCount(cell) == 0
                    && !hasLayer(cell, PlantStackLayer.SHIELD);
            case SHIELD ->
                !isWaterCell(cell)
                    && !hasLayer(cell, PlantStackLayer.SHIELD)
                    && coreCount(cell) > 0;
            case REPEATABLE ->
                !hasOtherCoreName(cell, definition)
                    && countSameName(cell, definition) < maxCopies;
            case CORE -> coreCount(cell) == 0;
        };
    }

    public static int insertionIndex(Cell cell, PlantDefinition definition) {
        PlantStackLayer layer = definition.getStackLayer();
        var plants = cell.getPlants();
        if (layer == PlantStackLayer.PLATFORM) {
            return 0;
        }
        if (layer == PlantStackLayer.SHIELD) {
            return plants.size();
        }
        int index = 0;
        for (int i = 0; i < plants.size(); i++) {
            PlantStackLayer existing = plants.get(i).getDefinition().getStackLayer();
            if (existing == PlantStackLayer.PLATFORM) {
                index = i + 1;
            } else if (existing == PlantStackLayer.SHIELD) {
                return i;
            } else {
                index = i + 1;
            }
        }
        return index;
    }

    public static boolean isWaterCell(Cell cell) {
        return cell.getCellType() instanceof WaterCellType;
    }

    public static boolean isWaterPlant(PlantDefinition definition) {
        return definition.hasTag(PlantTag.WATER);
    }

    public static boolean isLandOnlyPlant(PlantDefinition definition) {
        return !isWaterPlant(definition);
    }

    public static boolean isIceCell(Cell cell) {
        return cell != null && cell.getCellType() instanceof IceCellType;
    }

    public static boolean isIceTerrainPlant(PlantDefinition definition) {
        return definition != null && definition.hasTag(PlantTag.ICE_TERRAIN);
    }

    public static boolean isGraveBuster(PlantDefinition definition) {
        return definition != null && definition.hasTag(PlantTag.GRAVE);
    }

    public static boolean isGrave(Obstacle obstacle) {
        if (obstacle == null || obstacle.getType() == null) {
            return false;
        }
        return switch (obstacle.getType()) {
            case EGYPT_GRAVE, TUTORIAL_GRAVE, SUN_GRAVE, PLANTFOOD_GRAVE -> true;
            default -> false;
        };
    }

    public static boolean hasLayer(Cell cell, PlantStackLayer layer) {
        for (Plant plant : cell.getPlants()) {
            if (plant.getDefinition().getStackLayer() == layer) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasOtherPlantWithLayer(Cell cell, Plant self, PlantStackLayer layer) {
        for (Plant plant : cell.getPlants()) {
            if (plant != self && plant.getDefinition().getStackLayer() == layer) {
                return true;
            }
        }
        return false;
    }

    public static int coreCount(Cell cell) {
        int count = 0;
        for (Plant plant : cell.getPlants()) {
            PlantStackLayer layer = plant.getDefinition().getStackLayer();
            if (layer == PlantStackLayer.CORE || layer == PlantStackLayer.REPEATABLE) {
                count++;
            }
        }
        return count;
    }

    private static boolean hasOtherCoreName(Cell cell, PlantDefinition definition) {
        for (Plant plant : cell.getPlants()) {
            PlantStackLayer layer = plant.getDefinition().getStackLayer();
            if ((layer == PlantStackLayer.CORE || layer == PlantStackLayer.REPEATABLE)
                && plant.getDefinition().getName() != definition.getName()) {
                return true;
            }
        }
        return false;
    }

    private static int countSameName(Cell cell, PlantDefinition definition) {
        int count = 0;
        for (Plant plant : cell.getPlants()) {
            if (plant.getDefinition().getName() == definition.getName()) {
                count++;
            }
        }
        return count;
    }
}

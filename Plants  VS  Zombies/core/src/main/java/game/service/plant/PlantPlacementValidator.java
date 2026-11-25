package game.service.plant;

import app.controller.game.EventResult;
import app.enums.plant.PlantName;
import game.entity.plant.PlantCooldown;
import game.entity.plant.PlantDefinition;
import game.entity.registry.PlantRegistry;
import game.service.special_levels.ConveyorBeltService;
import game.world.GameWorld;
import game.world.minigame.vasebreaker.VaseBreakerWorld;
import game.world.playground.Cell;

public class PlantPlacementValidator {
    private final GameWorld world;

    public PlantPlacementValidator(GameWorld world) {
        this.world = world;
    }

    public EventResult validate(PlantName plantName, Cell targetCell) {
        if (plantName == null) {
            return EventResult.PLANT_PLACEMENT_PLANT_NOT_SELECTED;
        }

        if (targetCell == null) {
            return EventResult.INVALID_CELL;
        }

        PlantDefinition definition = PlantRegistry.get(plantName);

        if (definition == null) {
            return EventResult.INVALID_PLANT;
        }

        if (world.isConveyorActive()) {
            ConveyorBeltService conveyor = world.getConveyorBeltService();
            if (conveyor == null || !conveyor.hasPlant(plantName)) {
                return EventResult.PLANT_PLACEMENT_NOT_ON_CONVEYOR;
            }
        } else if (world.isVaseBreakerActive()) {
            if (!(world instanceof VaseBreakerWorld vaseWorld)
                || !vaseWorld.hasPlantInInventory(plantName)) {
                return EventResult.NOT_IN_VASE_INVENTORY;
            }
        } else {
            if (!isCooldownAvailable(plantName)) {
                return EventResult.PLANT_PLACEMENT_COOLDOWN;
            }

            if (!hasEnoughSun(plantName)) {
                return EventResult.NOT_ENOUGH_SUN;
            }
        }

        if (!targetCell.isPlantable(definition)) {
            if (PlantPlacementPolicy.isGraveBuster(definition)
                && !PlantPlacementPolicy.isValidGraveBusterPlacement(targetCell)) {
                return EventResult.GRAVE_PLANT_REQUIRES_GRAVE;
            }
            return EventResult.PLANT_PLACEMENT_CELL_OCCUPIED;
        }

        return EventResult.PLANT_PLACEMENT_SUCCESS;
    }

    public boolean canPlant(PlantName plantName, Cell targetCell) {
        return validate(plantName, targetCell).isSuccess();
    }

    private boolean isCooldownAvailable(PlantName plantName) {
        PlantCooldown cooldown = world.getCoolDownPlant(plantName);
        return cooldown != null && cooldown.isAvailable();
    }

    private boolean hasEnoughSun(PlantName plantName) {
        return world.getSunAmount() >= PlantUpgradeService.getEffectiveStats(plantName).sunCost();
    }
}

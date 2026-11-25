package app.controller.game.interaction.hover;

import app.controller.game.EventResult;
import app.enums.plant.PlantName;
import game.service.plant.ImitaterReplacement;
import game.service.plant.PlantPlacementService;
import game.world.playground.Cell;

public final class PlantingHoverHandler extends CellHoverHandler {
    private final PlantPlacementService placementService;
    private PlantName plantName;

    public PlantingHoverHandler(PlantPlacementService placementService) {
        this.placementService = placementService;
    }

    public void setPlantName(PlantName plantName) {
        this.plantName = plantName;
    }

    @Override
    public boolean isActive() {
        return plantName != null && placementService != null;
    }

    @Override
    protected boolean isValid(Cell cell) {
        if (ImitaterReplacement.canBegin(plantName, cell)) {
            return true;
        }
        return placementService.getValidator().canPlant(plantName, cell);
    }

    @Override
    protected String message(Cell cell, boolean valid) {
        if (ImitaterReplacement.canBegin(plantName, cell)) {
            return EventResult.IMITATER_REPLACEMENT_SUCCESS.getMessage();
        }
        EventResult result = placementService.getValidator().validate(plantName, cell);
        return result != null ? result.getMessage() : "";
    }

    @Override
    public EventResult execute(Cell cell) {
        if (ImitaterReplacement.canBegin(plantName, cell)) {
            return ImitaterReplacement.begin(plantName, cell);
        }
        return placementService.placePlant(plantName, cell);
    }
}

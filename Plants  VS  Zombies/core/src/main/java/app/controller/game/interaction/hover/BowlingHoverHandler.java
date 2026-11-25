package app.controller.game.interaction.hover;

import app.controller.game.EventResult;
import app.enums.plant.PlantName;
import game.world.minigame.wallnut_bowling.placement.BowlingPlacementService;
import game.world.playground.Cell;

public final class BowlingHoverHandler extends CellHoverHandler{
    private final BowlingPlacementService placementService;
    private PlantName plantName;
    public BowlingHoverHandler(BowlingPlacementService placementService) {
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
        return placementService.getValidator().canPlant(plantName, cell);
    }

    @Override
    protected String message(Cell cell, boolean valid) {
        EventResult result = placementService.getValidator().validate(plantName, cell);
        return result != null ? result.getMessage() : "";
    }

    @Override
    public EventResult execute(Cell cell) {
        return placementService.placeBowling(plantName, cell);
    }
}

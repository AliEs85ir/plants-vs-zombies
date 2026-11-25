package app.controller.game.interaction.hover;

import app.controller.game.EventResult;
import app.view.hud.config.HudLayoutConfig;
import com.badlogic.gdx.graphics.Color;
import game.service.plant.PlantFoodService;
import game.world.playground.Cell;

public final class PlantFoodHoverHandler extends CellHoverHandler {
    private final PlantFoodService plantFoodService;

    public PlantFoodHoverHandler(PlantFoodService plantFoodService) {
        this.plantFoodService = plantFoodService;
    }

    @Override
    public boolean isActive() {
        return plantFoodService != null;
    }

    @Override
    protected boolean isValid(Cell cell) {
        EventResult result = plantFoodService.validateTarget(cell);
        return result != null && result.isSuccess();
    }

    @Override
    protected String message(Cell cell, boolean valid) {
        EventResult result = plantFoodService.validateTarget(cell);
        return result != null ? result.getMessage() : "";
    }

    @Override
    protected Color validColor() {
        return HudLayoutConfig.GRID_HOVER.validColor;
    }

    @Override
    protected Color invalidColor() {
        return HudLayoutConfig.GRID_HOVER.invalidColor;
    }

    @Override
    public EventResult execute(Cell cell) {
        EventResult validation = plantFoodService.validateTarget(cell);
        if (validation == null || !validation.isSuccess()) {
            return validation == null ? EventResult.PLANT_FOOD_OUT_OF_BOUNDS : validation;
        }
        boolean activated = plantFoodService.activatePlantFood(cell.getCurrentPlant());
        return activated ? EventResult.PLANT_FOOD_SUCCESS : EventResult.PLANT_FOOD_NOT_IMPLEMENTED;
    }
}

package app.controller.game.interaction.hover;

import app.controller.game.EventResult;
import game.service.plant.ShovelService;
import game.world.playground.Cell;

public final class ShovelHoverHandler extends CellHoverHandler {
    private final ShovelService shovelService;

    public ShovelHoverHandler(ShovelService shovelService) {
        this.shovelService = shovelService;
    }

    @Override
    public boolean isActive() {
        return shovelService != null;
    }

    @Override
    protected boolean isValid(Cell cell) {
        return shovelService.canShovel(cell);
    }

    @Override
    protected String message(Cell cell, boolean valid) {
        EventResult result = shovelService.validate(cell);
        return result != null ? result.getMessage() : "";
    }

    @Override
    public EventResult execute(Cell cell) {
        return shovelService.shovel(cell);
    }
}

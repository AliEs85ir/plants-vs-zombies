package app.controller.game.interaction.hover;

import app.controller.game.EventResult;
import game.service.Zombie.ZombiePlacementService;
import game.world.playground.Cell;

public final class ZombiePlacementHoverHandler extends CellHoverHandler {
    private final ZombiePlacementService placementService;
    private String alias;

    public ZombiePlacementHoverHandler(ZombiePlacementService placementService) {
        this.placementService = placementService;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Override
    public boolean isActive() {
        return alias != null && placementService != null;
    }

    @Override
    protected boolean isValid(Cell cell) {
        return placementService.getValidator().canPlaceZombie(alias, cell);
    }

    @Override
    protected String message(Cell cell, boolean valid) {
        EventResult result = placementService.getValidator().validate(alias, cell);
        return result != null ? result.getMessage() : "";
    }

    @Override
    public EventResult execute(Cell cell) {
        return placementService.placeZombie(alias, cell);
    }
}

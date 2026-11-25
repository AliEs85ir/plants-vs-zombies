package app.controller.game.interaction;

import app.controller.game.EventResult;
import app.enums.cursor.CursorType;
import app.enums.plant.PlantName;
import game.world.playground.Cell;

public final class InteractionController {
    private InteractionType type = InteractionType.NONE;
    private HoverHandler hoverHandler = IdleHoverHandler.INSTANCE;
    private PlantName selectedPlant;
    private String selectedZombie;

    public void beginPlanting(PlantName plantName, HoverHandler handler) {
        selectedPlant = plantName;
        selectedZombie = null;
        type = InteractionType.PLANTING;
        hoverHandler = handler != null ? handler : IdleHoverHandler.INSTANCE;
    }

    public void beginBowling(PlantName plantName, HoverHandler handler) {
        selectedPlant = plantName;
        selectedZombie = null;
        type = InteractionType.BOWLING;
        hoverHandler = handler != null ? handler : IdleHoverHandler.INSTANCE;
    }


    public void beginZombiePlacement(String alias, HoverHandler handler) {
        selectedZombie = alias;
        selectedPlant = null;
        type = InteractionType.ZOMBIE_PLACEMENT;
        hoverHandler = handler != null ? handler : IdleHoverHandler.INSTANCE;
    }

    public void beginShovel(HoverHandler handler) {
        selectedPlant = null;
        selectedZombie = null;
        type = InteractionType.SHOVEL;
        hoverHandler = handler != null ? handler : IdleHoverHandler.INSTANCE;
    }

    public void beginPlantFood(HoverHandler handler) {
        selectedPlant = null;
        selectedZombie = null;
        type = InteractionType.PLANT_FOOD;
        hoverHandler = handler != null ? handler : IdleHoverHandler.INSTANCE;
    }

    public void beginTarget(HoverHandler handler) {
        selectedPlant = null;
        selectedZombie = null;
        type = InteractionType.TARGET;
        hoverHandler = handler != null ? handler : IdleHoverHandler.INSTANCE;
    }

    public void beginUpgrade(PlantName plantName) {
        selectedPlant = plantName;
        selectedZombie = null;
        type = InteractionType.UPGRADE;
        hoverHandler = IdleHoverHandler.INSTANCE;
    }

    public void clear() {
        selectedPlant = null;
        selectedZombie = null;
        type = InteractionType.NONE;
        hoverHandler = IdleHoverHandler.INSTANCE;
    }

    public InteractionType type() {
        return type;
    }

    public boolean is(InteractionType interactionType) {
        return type == interactionType;
    }

    public PlantName selectedPlant() {
        return selectedPlant;
    }

    public String selectedZombie() {
        return selectedZombie;
    }

    public HoverFeedback evaluate(Cell cell) {
        return hoverHandler.evaluate(cell);
    }

    public EventResult execute(Cell cell) {
        return hoverHandler.execute(cell);
    }

    public boolean hasCellHover() {
        return hoverHandler.isActive();
    }

    public boolean endsAfterSuccess() {
        return hoverHandler.endsAfterSuccess();
    }

    public boolean endsAfterFailure() {
        return hoverHandler.endsAfterFailure();
    }

    public boolean blocksSeedSelection() {
        return type == InteractionType.SHOVEL || type == InteractionType.PLANT_FOOD;
    }

    public boolean blocksUnrelatedWorldActions() {
        return type == InteractionType.SHOVEL || type == InteractionType.PLANT_FOOD;
    }

    public CursorType resolveCursor(boolean pressed, boolean hoveringInteractive) {
        return switch (type) {
            case SHOVEL -> CursorType.SHOVEL;
            case PLANTING, ZOMBIE_PLACEMENT, BOWLING -> CursorType.PLANTING;
            case TARGET -> CursorType.TARGET;
            case PLANT_FOOD -> CursorType.PLANT_FOOD;
            case SWAP, UPGRADE -> CursorType.PLANTING;
            case NONE -> {
                if (pressed) {
                    yield CursorType.PRESSED;
                }
                if (hoveringInteractive) {
                    yield CursorType.HOVER;
                }
                yield CursorType.DEFAULT;
            }
        };
    }
}

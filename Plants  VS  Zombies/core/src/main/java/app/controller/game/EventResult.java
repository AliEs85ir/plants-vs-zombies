package app.controller.game;

public enum EventResult {
    PLANT_PLACEMENT_SUCCESS(true, "Plant planted successfully."),
    PLANT_PLACEMENT_PLANT_NOT_SELECTED(false, "No plant has been selected."),
    INVALID_CELL(false, "The selected cell is invalid."),
    INVALID_PLANT(false, "The selected plant is invalid."),
    PLANT_PLACEMENT_COOLDOWN(false, "This plant is still on cooldown."),
    NOT_ENOUGH_SUN(false, "You do not have enough sun."),
    PLANT_PLACEMENT_CELL_OCCUPIED(false, "This cell cannot contain this plant."),
    GRAVE_PLANT_REQUIRES_GRAVE(false, "Grave Buster can only be planted on graves."),
    IMITATER_REPLACEMENT_SUCCESS(true, "Imitater is transforming."),
    PLANT_PLACEMENT_NOT_ON_CONVEYOR(false, "This plant is not on the conveyor belt."),

    ZOMBIE_PLACEMENT_SUCCESS(true, "Zombie placed successfully."),
    ZOMBIE_PLACEMENT_ZOMBIE_NOT_SELECTED(false, "No zombie has been selected."),
    INVALID_ZOMBIE(false, "The selected zombie is invalid."),
    ZOMBIE_PLACEMENT_COOLDOWN(false, "This zombie is still on cooldown."),
    ZOMBIE_PLACEMENT_NOT_ENOUGH_SUN(false, "You do not have enough sun to place this zombie."),
    ZOMBIE_PLACEMENT_CELL_OCCUPIED(false, "This cell cannot contain this zombie."),
    ZOMBIE_PLACEMENT_NOT_ON_CONVEYOR(false, "This zombie is not on the conveyor belt."),
    NOT_IN_VASE_INVENTORY(false, "plant is not in your vase inventory"),

    SHOVEL_SUCCESS(true, "Plant removed."),
    SHOVEL_NO_TARGET(false, "There is no plant to remove."),
    SHOVEL_NOT_ALLOWED(false, "This plant cannot be removed."),

    BOWLING_PLACEMENT_SUCCESS(true, "Bowling placed successfully"),
    BOWLING_PLACEMENT_RULE(false, "Bowling rile is null in this world"),
    BOWLING_PLACEMENT_REDLINE(false, "You can only place your plants before flower line"),

    PLANT_FOOD_SUCCESS(true, ""),
    PLANT_FOOD_OUT_OF_BOUNDS(false, "You cannot use Plant Food here."),
    PLANT_FOOD_NO_PLANT(false, "There is no plant here."),
    PLANT_FOOD_NOT_SUPPORTED(false, "This plant cannot use Plant Food."),
    PLANT_FOOD_NOT_IMPLEMENTED(false, "Plant Food for this plant is not implemented yet."),
    PLANT_FOOD_EMPTY(false, "You don't have any Plant Food."),

    BEGHOULED_INVALID_SWAP(false, "That swap does not make a match."),
    BEGHOULED_NOT_ADJACENT(false, "You can only swap adjacent plants."),
    BEGHOULED_BOARD_BUSY(false, "Wait for the board to settle."),
    BEGHOULED_CRATER(false, "Craters cannot be swapped."),
    BEGHOULED_UPGRADE_INVALID(false, "That plant cannot use this upgrade."),
    BEGHOULED_NO_PLANT(false, "There is no plant here."),
    ;

    private final boolean success;
    private final String message;

    EventResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}

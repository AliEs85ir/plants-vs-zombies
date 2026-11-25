package game.service.special_levels;

public interface ConveyorBeltListener {

    default void onItemArrived(ConveyorBeltItem item) {}

    default void onItemRemoved(ConveyorBeltItem item) {}

    default void onSelectionChanged(ConveyorBeltItem item) {}
}

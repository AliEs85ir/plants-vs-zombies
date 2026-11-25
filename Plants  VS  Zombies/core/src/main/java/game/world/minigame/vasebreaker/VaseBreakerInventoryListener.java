package game.world.minigame.vasebreaker;

public interface VaseBreakerInventoryListener {

    default void onItemArrived(VaseBreakerInventoryPlant item) {}

    default void onItemRemoved(VaseBreakerInventoryPlant item) {}

    default void onSelectionChanged(VaseBreakerInventoryPlant item) {}
}

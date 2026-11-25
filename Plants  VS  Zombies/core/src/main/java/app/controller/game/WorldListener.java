package app.controller.game;

import app.enums.plant.PlantName;

public interface WorldListener {
    default void onPlantCardSelected(PlantName plantName){};
    default void onPlantCardDeselected(PlantName plantName){};
    default void onZombieCardDeselected(String alias){};
    default void onZombieCardSelected(String alias){};
    default void onPlantCardPlaceAt(float worldX, float worldY) {}
    default void onShovelToggled() {}
    default void onPlantFoodBankClicked() {}
}

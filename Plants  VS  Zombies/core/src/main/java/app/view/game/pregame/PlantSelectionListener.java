package app.view.game.pregame;

import app.enums.plant.PlantName;

import java.util.List;

public interface PlantSelectionListener {

    void onSelectionChanged(List<PlantName> selected);

    default void onSelectionRejected(PlantName plant, String reason) {}
}

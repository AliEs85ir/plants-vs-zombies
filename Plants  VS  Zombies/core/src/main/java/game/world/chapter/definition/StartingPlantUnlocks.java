package game.world.chapter.definition;

import app.enums.plant.PlantName;

import java.util.List;

public final class StartingPlantUnlocks {
    private StartingPlantUnlocks() {
    }

    public static List<PlantName> initialPlants() {
        return List.of(
            PlantName.SUNFLOWER,
            PlantName.PEASHOOTER,
            PlantName.WALL_NUT,
            PlantName.POTATO_MINE,
            PlantName.CHERRY_BOMB
        );
    }
}

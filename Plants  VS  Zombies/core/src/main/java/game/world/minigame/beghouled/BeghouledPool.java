package game.world.minigame.beghouled;

import app.enums.plant.PlantName;

import java.util.List;

public final class BeghouledPool {
    public static final List<PlantName> BOARD_TYPES = List.of(
        PlantName.PEASHOOTER,
        PlantName.SUNFLOWER,
        PlantName.WALL_NUT,
        PlantName.SNOW_PEA,
        PlantName.REPEATER
    );

    private BeghouledPool() {}
}

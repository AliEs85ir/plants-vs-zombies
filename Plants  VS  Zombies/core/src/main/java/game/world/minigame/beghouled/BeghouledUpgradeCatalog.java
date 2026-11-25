package game.world.minigame.beghouled;

import app.enums.plant.PlantName;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class BeghouledUpgradeCatalog {
    public record Upgrade(PlantName from, PlantName to, int cost) {}

    private static final List<Upgrade> LEVEL_ONE = List.of(
        new Upgrade(PlantName.SUNFLOWER, PlantName.TWIN_SUNFLOWER, 125),
        new Upgrade(PlantName.PEASHOOTER, PlantName.REPEATER, 200)
    );

    private static final List<Upgrade> LEVEL_TWO = List.of(
        new Upgrade(PlantName.SUNFLOWER, PlantName.TWIN_SUNFLOWER, 125),
        new Upgrade(PlantName.PEASHOOTER, PlantName.REPEATER, 200),
        new Upgrade(PlantName.WALL_NUT, PlantName.TALL_NUT, 250)
    );

    private static final List<Upgrade> LEVEL_THREE = List.of(
        new Upgrade(PlantName.SUNFLOWER, PlantName.TWIN_SUNFLOWER, 125),
        new Upgrade(PlantName.PEASHOOTER, PlantName.REPEATER, 200),
        new Upgrade(PlantName.REPEATER, PlantName.MEGA_GATLING_PEA, 400),
        new Upgrade(PlantName.WALL_NUT, PlantName.TALL_NUT, 125)
    );

    private BeghouledUpgradeCatalog() {}

    public static List<Upgrade> upgradesForLevel(int level) {
        return switch (BeghouledLevelConfig.clamp(level)) {
            case 2 -> LEVEL_TWO;
            case 3 -> LEVEL_THREE;
            default -> LEVEL_ONE;
        };
    }

    public static List<PlantName> cardPlantsForLevel(int level) {
        return upgradesForLevel(level).stream().map(Upgrade::to).distinct().toList();
    }

    public static List<PlantName> cardPlants() {
        return cardPlantsForLevel(1);
    }

    public static Optional<Upgrade> find(PlantName from, PlantName to) {
        return findForLevel(1, from, to);
    }

    public static Optional<Upgrade> findForLevel(int level, PlantName from, PlantName to) {
        if (from == null || to == null) {
            return Optional.empty();
        }
        for (Upgrade upgrade : upgradesForLevel(level)) {
            if (upgrade.from() == from && upgrade.to() == to) {
                return Optional.of(upgrade);
            }
        }
        return Optional.empty();
    }

    public static List<PlantName> availablePlantsForLevel(int level) {
        ArrayList<PlantName> plants = new ArrayList<>(BeghouledPool.BOARD_TYPES);
        for (PlantName name : cardPlantsForLevel(level)) {
            if (!plants.contains(name)) {
                plants.add(name);
            }
        }
        for (Upgrade upgrade : upgradesForLevel(level)) {
            if (!plants.contains(upgrade.from())) {
                plants.add(upgrade.from());
            }
        }
        return List.copyOf(plants);
    }
}

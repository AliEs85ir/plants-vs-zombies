package game.entity.plant.upgrade;

import java.util.Collections;
import java.util.List;

public final class PlantUpgradePreview {
    private final int currentLevel;
    private final int nextLevel;
    private final int maxLevel;
    private final boolean atMaxLevel;
    private final EffectivePlantStats currentStats;
    private final EffectivePlantStats nextStats;
    private final List<PlantStatPreview> stats;

    public PlantUpgradePreview(
        int currentLevel,
        int nextLevel,
        int maxLevel,
        boolean atMaxLevel,
        EffectivePlantStats currentStats,
        EffectivePlantStats nextStats,
        List<PlantStatPreview> stats
    ) {
        this.currentLevel = currentLevel;
        this.nextLevel = nextLevel;
        this.maxLevel = maxLevel;
        this.atMaxLevel = atMaxLevel;
        this.currentStats = currentStats;
        this.nextStats = nextStats;
        this.stats = stats == null ? List.of() : Collections.unmodifiableList(stats);
    }

    public int currentLevel() {
        return currentLevel;
    }

    public int nextLevel() {
        return nextLevel;
    }

    public int maxLevel() {
        return maxLevel;
    }

    public boolean atMaxLevel() {
        return atMaxLevel;
    }

    public EffectivePlantStats currentStats() {
        return currentStats;
    }

    public EffectivePlantStats nextStats() {
        return nextStats;
    }

    public List<PlantStatPreview> stats() {
        return stats;
    }

    public PlantStatPreview stat(String id) {
        if (id == null) {
            return null;
        }
        for (PlantStatPreview stat : stats) {
            if (id.equals(stat.id())) {
                return stat;
            }
        }
        return null;
    }
}

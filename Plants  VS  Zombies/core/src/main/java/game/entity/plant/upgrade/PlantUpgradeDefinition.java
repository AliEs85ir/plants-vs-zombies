package game.entity.plant.upgrade;

import java.util.ArrayList;
import java.util.List;

public final class PlantUpgradeDefinition {
    public int maxLevel = 1;
    public List<PlantLevelStep> levels;

    public List<PlantLevelStep> levelsOrEmpty() {
        return levels == null ? List.of() : levels;
    }

    public PlantLevelStep stepForLevel(int level) {
        if (levels == null) {
            return null;
        }
        for (PlantLevelStep step : levels) {
            if (step != null && step.level == level) {
                return step;
            }
        }
        return null;
    }

    void sanitize() {
        if (levels == null) {
            levels = new ArrayList<>();
        }
        for (PlantLevelStep step : levels) {
            if (step != null) {
                step.sanitize();
            }
        }
    }
}

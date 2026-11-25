package game.entity.sun;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Optional arc/launch presentation for plant-produced suns.
 * Missing or empty source lists mean Direct spawn (no toss).
 */
public final class SunLaunchSpec {
    private final int durationTicks;
    private final double arcHeight;
    private final List<SunSpawnPoint> sources;
    private final List<SunSpawnPoint> plantFoodSources;

    public SunLaunchSpec(
        int durationTicks,
        double arcHeight,
        List<SunSpawnPoint> sources,
        List<SunSpawnPoint> plantFoodSources
    ) {
        this.durationTicks = Math.max(1, durationTicks);
        this.arcHeight = Math.max(0d, arcHeight);
        this.sources = freeze(sources);
        this.plantFoodSources = freeze(plantFoodSources);
    }

    private static List<SunSpawnPoint> freeze(List<SunSpawnPoint> points) {
        if (points == null || points.isEmpty()) {
            return List.of();
        }
        return Collections.unmodifiableList(new ArrayList<>(points));
    }

    public int getDurationTicks() {
        return durationTicks;
    }

    public double getArcHeight() {
        return arcHeight;
    }

    public List<SunSpawnPoint> getSources() {
        return sources;
    }

    public List<SunSpawnPoint> getPlantFoodSources() {
        return plantFoodSources;
    }

    public List<SunSpawnPoint> sourcesFor(boolean plantFood) {
        if (plantFood && !plantFoodSources.isEmpty()) {
            return plantFoodSources;
        }
        return sources;
    }

    public boolean hasLaunchSources(boolean plantFood) {
        return !sourcesFor(plantFood).isEmpty();
    }
}

package game.world.chapter.definition;

import app.enums.SeasonType;
import app.enums.plant.PlantName;
import game.world.chapter.ChapterId;

import java.util.List;
import java.util.Map;

/**
 * Per-stage allowed + recommended plant pools.
 * Data source of truth: {@code assets/chapters/stage_plant_pools.json}
 * (loaded by {@link ChapterPlantCatalogLoader}).
 */
public final class ChapterPlantCatalog {
    private ChapterPlantCatalog() {
    }

    public static List<PlantName> plantsFor(ChapterId id) {
        ChapterPlantCatalogLoader.StagePlantPool pool = pool(id);
        return pool == null ? List.of() : pool.allowedPlants();
    }

    public static List<PlantName> recommendedFor(ChapterId id) {
        ChapterPlantCatalogLoader.StagePlantPool pool = pool(id);
        return pool == null ? List.of() : pool.recommendedPlants();
    }

    public static List<PlantName> plantsFor(SeasonType season, int stage) {
        return plantsFor(new ChapterId(season, stage));
    }

    public static List<PlantName> recommendedFor(SeasonType season, int stage) {
        return recommendedFor(new ChapterId(season, stage));
    }

    public static Map<ChapterId, ChapterPlantCatalogLoader.StagePlantPool> all() {
        return ChapterPlantCatalogLoader.get();
    }

    private static ChapterPlantCatalogLoader.StagePlantPool pool(ChapterId id) {
        if (id == null) {
            return null;
        }
        return ChapterPlantCatalogLoader.get().get(id);
    }
}

package game.world.chapter.definition;

import app.enums.SeasonType;
import app.enums.plant.PlantName;
import game.world.chapter.ChapterId;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Plants unlocked when a stage becomes available.
 * SoT = recommended list of that stage from {@link ChapterPlantCatalog}
 * ({@code assets/chapters/stage_plant_pools.json} → {@code recommended}).
 * Completing stage N unlocks recommended of stage N+1 via {@code ProgressionService}.
 */
public final class ChapterUnlockCatalog {
    private ChapterUnlockCatalog() {
    }

    public static List<PlantName> unlockPlantsFor(ChapterId id) {
        return ChapterPlantCatalog.recommendedFor(id);
    }

    public static List<PlantName> unlockPlantsFor(SeasonType season, int stage) {
        if (season == null || season == SeasonType.MINIGAME) {
            return List.of();
        }
        return unlockPlantsFor(new ChapterId(season, stage));
    }

    public static Map<ChapterId, List<PlantName>> all() {
        Map<ChapterId, List<PlantName>> out = new LinkedHashMap<>();
        for (Map.Entry<ChapterId, ChapterPlantCatalogLoader.StagePlantPool> e
            : ChapterPlantCatalog.all().entrySet()) {
            out.put(e.getKey(), e.getValue().recommendedPlants());
        }
        return Collections.unmodifiableMap(out);
    }
}

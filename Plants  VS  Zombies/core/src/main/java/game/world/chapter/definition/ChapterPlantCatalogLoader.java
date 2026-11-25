package game.world.chapter.definition;

import app.enums.SeasonType;
import app.enums.plant.PlantName;
import app.service.ConfigFiles;
import app.view.loading.InternalFiles;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import game.world.chapter.ChapterId;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Loads per-stage allowed + recommended plant pools from
 * {@code assets/chapters/stage_plant_pools.json}.
 * Edit that JSON to change stage plant chooser pools / recommended unlocks.
 */
public final class ChapterPlantCatalogLoader {
    public static final String CONFIG_PATH = "chapters/stage_plant_pools.json";

    private static final Gson GSON = new GsonBuilder().create();
    private static Map<ChapterId, StagePlantPool> cached;

    private ChapterPlantCatalogLoader() {
    }

    public static Map<ChapterId, StagePlantPool> get() {
        if (cached == null) {
            cached = load();
        }
        return cached;
    }

    public static void clearCache() {
        cached = null;
    }

    public static void overridePools(Map<ChapterId, StagePlantPool> pools) {
        if (pools == null) {
            cached = Map.of();
            return;
        }
        Map<ChapterId, StagePlantPool> copy = new LinkedHashMap<>();
        for (Map.Entry<ChapterId, StagePlantPool> e : pools.entrySet()) {
            if (e.getKey() != null && e.getValue() != null) {
                copy.put(e.getKey(), e.getValue().sanitized());
            }
        }
        cached = Collections.unmodifiableMap(copy);
    }

    public static Map<ChapterId, StagePlantPool> load() {
        Map<ChapterId, StagePlantPool> fromGdx = loadWithGdx();
        if (fromGdx != null && !fromGdx.isEmpty()) {
            return fromGdx;
        }
        Map<ChapterId, StagePlantPool> fromFile = loadWithFile();
        if (fromFile != null && !fromFile.isEmpty()) {
            return fromFile;
        }
        return Map.of();
    }

    public static Map<ChapterId, StagePlantPool> parse(String json) {
        if (json == null || json.isBlank()) {
            return Map.of();
        }
        try (Reader reader = new StringReader(json)) {
            Root root = GSON.fromJson(reader, Root.class);
            return sanitizeRoot(root);
        } catch (Exception e) {
            return Map.of();
        }
    }

    private static Map<ChapterId, StagePlantPool> loadWithGdx() {
        try {
            if (Gdx.files == null) {
                return null;
            }
            FileHandle file = InternalFiles.resolve(CONFIG_PATH);
            if (file == null || !file.exists()) {
                return null;
            }
            return parse(file.readString("UTF-8"));
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static Map<ChapterId, StagePlantPool> loadWithFile() {
        File file = ConfigFiles.resolve(CONFIG_PATH);
        if (file == null || !file.isFile()) {
            return null;
        }
        try (FileReader reader = new FileReader(file)) {
            Root root = GSON.fromJson(reader, Root.class);
            return sanitizeRoot(root);
        } catch (Exception e) {
            return Map.of();
        }
    }

    private static Map<ChapterId, StagePlantPool> sanitizeRoot(Root root) {
        if (root == null || root.stages == null || root.stages.isEmpty()) {
            return Map.of();
        }
        Map<ChapterId, StagePlantPool> out = new LinkedHashMap<>();
        for (Map.Entry<String, StagePlantPool> e : root.stages.entrySet()) {
            ChapterId id = parseChapterKey(e.getKey());
            if (id == null || e.getValue() == null) {
                continue;
            }
            out.put(id, e.getValue().sanitized());
        }
        return Collections.unmodifiableMap(out);
    }

    static ChapterId parseChapterKey(String key) {
        if (key == null || key.isBlank()) {
            return null;
        }
        String normalized = key.trim().toUpperCase().replace(' ', '_');
        int dash = normalized.lastIndexOf('-');
        if (dash <= 0 || dash >= normalized.length() - 1) {
            return null;
        }
        try {
            SeasonType season = SeasonType.valueOf(normalized.substring(0, dash));
            int stage = Integer.parseInt(normalized.substring(dash + 1));
            if (season == SeasonType.MINIGAME || stage < 1) {
                return null;
            }
            return new ChapterId(season, stage);
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    public static final class Root {
        @SerializedName("_comment")
        public String comment;
        public Map<String, StagePlantPool> stages;
    }

    public static final class StagePlantPool {
        public List<String> allowed;
        public List<String> recommended;

        public StagePlantPool() {
        }

        public StagePlantPool(List<PlantName> allowedPlants, List<PlantName> recommendedPlants) {
            this.allowed = toIds(allowedPlants);
            this.recommended = toIds(recommendedPlants);
        }

        public StagePlantPool sanitized() {
            List<PlantName> allowedPlants = parseNames(allowed);
            Set<PlantName> allowedSet = new LinkedHashSet<>(allowedPlants);
            List<PlantName> recommendedPlants = new ArrayList<>();
            for (PlantName plant : parseNames(recommended)) {
                if (allowedSet.contains(plant) && !recommendedPlants.contains(plant)) {
                    recommendedPlants.add(plant);
                }
            }
            StagePlantPool cleaned = new StagePlantPool();
            cleaned.allowed = toIds(allowedPlants);
            cleaned.recommended = toIds(recommendedPlants);
            return cleaned;
        }

        public List<PlantName> allowedPlants() {
            return parseNames(allowed);
        }

        public List<PlantName> recommendedPlants() {
            return parseNames(recommended);
        }

        private static List<PlantName> parseNames(List<String> raw) {
            if (raw == null || raw.isEmpty()) {
                return List.of();
            }
            List<PlantName> out = new ArrayList<>();
            Set<PlantName> seen = new LinkedHashSet<>();
            for (String token : raw) {
                PlantName plant = resolvePlant(token);
                if (plant != null && seen.add(plant)) {
                    out.add(plant);
                }
            }
            return List.copyOf(out);
        }

        private static PlantName resolvePlant(String token) {
            if (token == null || token.isBlank()) {
                return null;
            }
            String trimmed = token.trim();
            try {
                return PlantName.valueOf(trimmed.toUpperCase().replace('-', '_').replace(' ', '_'));
            } catch (IllegalArgumentException ignored) {
            }
            PlantName byLabel = PlantName.fromLabel(trimmed);
            if (byLabel != null) {
                return byLabel;
            }
            if ("CATTAIL-MINT".equalsIgnoreCase(trimmed) || "CATTAIL_MINT".equalsIgnoreCase(trimmed)) {
                return PlantName.CAT_TAIL_MINT;
            }
            return null;
        }

        private static List<String> toIds(List<PlantName> plants) {
            if (plants == null || plants.isEmpty()) {
                return List.of();
            }
            List<String> out = new ArrayList<>(plants.size());
            for (PlantName plant : plants) {
                if (plant != null) {
                    out.add(plant.name());
                }
            }
            return List.copyOf(out);
        }
    }
}

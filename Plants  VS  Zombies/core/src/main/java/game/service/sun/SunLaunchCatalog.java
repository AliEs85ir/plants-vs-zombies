package game.service.sun;

import app.enums.plant.PlantName;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import game.engine.time.GameClock;
import game.entity.plant.Plant;
import game.entity.sun.SunLaunchSpec;
import game.entity.sun.SunSpawnPoint;

import java.io.Reader;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Data-driven launch specs keyed by plant name. Values come from plant JSON
 * ({@code sunLaunch}); built-in defaults match that JSON so tests work without Gdx.
 */
public final class SunLaunchCatalog {
    /** ~0.7s at {@link GameClock#TICKS_PER_SECOND} (10) = 7 ticks. */
    public static final int DEFAULT_DURATION_TICKS = Math.max(1, Math.round(0.70f * GameClock.TICKS_PER_SECOND));
    /** Arc height in project/world pixels (SunToss parabola peak). */
    public static final double DEFAULT_ARC_HEIGHT = 30d;

    private static final Map<PlantName, SunLaunchSpec> SPECS = new EnumMap<>(PlantName.class);
    private static boolean assetsAttempted;

    static {
        registerBuiltIns();
    }

    private SunLaunchCatalog() {
    }

    public static void register(PlantName name, SunLaunchSpec spec) {
        if (name != null && spec != null) {
            SPECS.put(name, spec);
        }
    }

    public static SunLaunchSpec get(PlantName name) {
        ensureAssetsLoaded();
        if (name == null) {
            return null;
        }
        return SPECS.get(name);
    }

    public static SunLaunchSpec get(Plant plant) {
        if (plant == null || plant.getDefinition() == null) {
            return null;
        }
        return get(plant.getDefinition().getName());
    }

    public static boolean isPlantFoodActive(Plant plant) {
        return plant != null
            && plant.getPlantFoodState() != null
            && plant.getPlantFoodState().isActive();
    }

    public static void parseInto(PlantName name, JsonObject root) {
        if (name == null || root == null || !root.has("sunLaunch") || !root.get("sunLaunch").isJsonObject()) {
            return;
        }
        JsonObject launch = root.getAsJsonObject("sunLaunch");
        int duration = launch.has("durationTicks")
            ? launch.get("durationTicks").getAsInt()
            : DEFAULT_DURATION_TICKS;
        double arcHeight = launch.has("arcHeight")
            ? launch.get("arcHeight").getAsDouble()
            : DEFAULT_ARC_HEIGHT;
        List<SunSpawnPoint> sources = readPoints(launch.get("sources"));
        List<SunSpawnPoint> plantFoodSources = readPoints(launch.get("plantFoodSources"));
        register(name, new SunLaunchSpec(duration, arcHeight, sources, plantFoodSources));
    }

    public static void parseInto(Reader reader) {
        JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
        PlantName name = PlantName.valueOf(root.get("plant").getAsString());
        parseInto(name, root);
    }

    private static List<SunSpawnPoint> readPoints(JsonElement element) {
        List<SunSpawnPoint> points = new ArrayList<>();
        if (element == null || !element.isJsonArray()) {
            return points;
        }
        JsonArray array = element.getAsJsonArray();
        for (JsonElement item : array) {
            if (!item.isJsonObject()) {
                continue;
            }
            JsonObject obj = item.getAsJsonObject();
            double x = obj.has("x") ? obj.get("x").getAsDouble() : 0d;
            double y = obj.has("y") ? obj.get("y").getAsDouble() : 0d;
            points.add(new SunSpawnPoint(x, y));
        }
        return points;
    }

    private static void registerBuiltIns() {
        register(
            PlantName.SUNFLOWER,
            new SunLaunchSpec(
                DEFAULT_DURATION_TICKS,
                DEFAULT_ARC_HEIGHT,
                List.of(new SunSpawnPoint(0, 40)),
                List.of(new SunSpawnPoint(0, 40))
            )
        );
        register(
            PlantName.TWIN_SUNFLOWER,
            new SunLaunchSpec(
                DEFAULT_DURATION_TICKS,
                DEFAULT_ARC_HEIGHT,
                List.of(new SunSpawnPoint(40, 40), new SunSpawnPoint(-25, 30)),
                List.of(new SunSpawnPoint(40, 40), new SunSpawnPoint(-25, 30))
            )
        );
        register(
            PlantName.SUN_SHROOM,
            new SunLaunchSpec(
                DEFAULT_DURATION_TICKS,
                DEFAULT_ARC_HEIGHT,
                List.of(new SunSpawnPoint(0, 30)),
                List.of(new SunSpawnPoint(0, 30))
            )
        );
        register(
            PlantName.PRIMAL_SUNFLOWER,
            new SunLaunchSpec(
                DEFAULT_DURATION_TICKS,
                DEFAULT_ARC_HEIGHT,
                List.of(new SunSpawnPoint(0, 60)),
                List.of(new SunSpawnPoint(65, 0))
            )
        );
        register(
            PlantName.GOLD_BLOOM,
            new SunLaunchSpec(
                DEFAULT_DURATION_TICKS,
                DEFAULT_ARC_HEIGHT,
                List.of(new SunSpawnPoint(0, 240)),
                List.of()
            )
        );
    }

    private static void ensureAssetsLoaded() {
        if (assetsAttempted) {
            return;
        }
        assetsAttempted = true;
        try {
            if (Gdx.files == null) {
                return;
            }
            loadAsset(PlantName.SUNFLOWER, "plants/sunflower.json");
            loadAsset(PlantName.TWIN_SUNFLOWER, "plants/twin_sunflower.json");
            loadAsset(PlantName.SUN_SHROOM, "plants/sun_shroom.json");
            loadAsset(PlantName.PRIMAL_SUNFLOWER, "plants/primal_sunflower.json");
            loadAsset(PlantName.GOLD_BLOOM, "plants/gold_bloom.json");
        } catch (Throwable ignored) {
            // Tests and headless callers keep built-in specs.
        }
    }

    private static void loadAsset(PlantName name, String path) {
        FileHandle file = Gdx.files.internal(path);
        if (file == null || !file.exists()) {
            return;
        }
        try (Reader reader = file.reader()) {
            parseInto(name, JsonParser.parseReader(reader).getAsJsonObject());
        } catch (Exception ignored) {
        }
    }
}

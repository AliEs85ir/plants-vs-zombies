package graphics.general.animation.loader;

import app.enums.plant.PlantName;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import game.entity.plant.Plant;
import graphics.general.animation.PamPathResolver;
import graphics.general.animation.condition.AnimationCondition;
import graphics.plant.PlantAnimationProfile;
import graphics.plant.PlantAnimationTag;
import graphics.plant.PlantAnimationVariant;
import graphics.plant.PlantConditions;
import graphics.plant.PlantVisualState;

import java.io.Reader;
import java.util.List;

public final class PlantAnimationProfileLoader {

    private static final String DEFAULT_PATH = "plants/peashooter.json";
    public static final String DEFAULT_PAM_PATH = "768/INITIAL/PLANT/PEASHOOTER/PEASHOOTER.PAM";

    private PlantAnimationProfileLoader() {
    }

    public static PlantAnimationProfile load(String path) {
        return load(path, null);
    }

    public static PlantAnimationProfile loadFor(PlantName plantName) {
        String underscored = "plants/" + plantName.name().toLowerCase() + ".json";
        String compact = "plants/" + plantName.name().toLowerCase().replace("_", "") + ".json";
        PlantAnimationProfile profile = tryParse(underscored, plantName);
        if (profile != null) {
            return profile;
        }
        if (!compact.equals(underscored)) {
            profile = tryParse(compact, plantName);
            if (profile != null) {
                return profile;
            }
        }
        return loadDefault(plantName);
    }

    public static PlantAnimationProfile load(String path, PlantName expectedName) {
        PlantAnimationProfile profile = tryParse(path, expectedName);
        if (profile != null) {
            return profile;
        }
        return loadDefault(expectedName);
    }

    public static PlantAnimationProfile fallbackFor(PlantName expectedName) {
        return loadDefault(expectedName);
    }

    private static PlantAnimationProfile loadDefault(PlantName expectedName) {
        PlantName key = expectedName != null ? expectedName : PlantName.PEASHOOTER;
        PlantAnimationProfile profile = tryParse(DEFAULT_PATH, key);
        if (profile != null) {
            Gdx.app.error(
                "PlantAnimationProfileLoader",
                "Using default plant animation profile for " + key
            );
            return profile;
        }
        return createBuiltInFallback(key);
    }

    private static PlantAnimationProfile tryParse(String path, PlantName expectedName) {
        try {
            FileHandle file = Gdx.files.internal(path);
            if (!file.exists()) {
                return null;
            }

            JsonObject root;
            try (Reader reader = file.reader()) {
                root = JsonParser.parseReader(reader).getAsJsonObject();
            }

            PlantName plantName = expectedName != null
                ? expectedName
                : PlantName.valueOf(root.get("plant").getAsString());

            String pamPath = root.has("pamPath") ? root.get("pamPath").getAsString() : "";
            if (pamPath == null || pamPath.isBlank()) {
                Gdx.app.error(
                    "PlantAnimationProfileLoader",
                    "Missing pamPath in " + path + ", using peashooter animation"
                );
                if (DEFAULT_PATH.equals(path)) {
                    return createBuiltInFallback(plantName);
                }
                return loadDefault(plantName);
            }
            pamPath = resolvePamPath(path, pamPath);
            PlantAnimationProfile profile = new PlantAnimationProfile(plantName, pamPath);

            JsonObject states = root.getAsJsonObject("states");
            for (String stateName : states.keySet()) {
                PlantVisualState state = PlantVisualState.valueOf(stateName.toUpperCase());

                states.getAsJsonArray(stateName).forEach(element -> {
                    JsonObject obj = element.getAsJsonObject();

                    String clip = obj.get("clip").getAsString();
                    boolean loop = obj.has("loop") && obj.get("loop").getAsBoolean();
                    int priority = obj.has("priority") ? obj.get("priority").getAsInt() : 0;
                    PlantAnimationTag tag = obj.has("tag")
                        ? PlantAnimationTag.valueOf(obj.get("tag").getAsString().toUpperCase())
                        : PlantAnimationTag.DEFAULT;

                    AnimationCondition<Plant> condition = null;
                    if (obj.has("condition") && !obj.get("condition").isJsonNull()) {
                        condition = parseCondition(obj.getAsJsonObject("condition"));
                    }

                    PlantAnimationVariant variant = condition != null
                        ? new PlantAnimationVariant(clip, loop, priority, tag, condition)
                        : new PlantAnimationVariant(clip, loop, priority, tag);

                    profile.addVariant(state, variant);
                });
            }

            return profile;
        } catch (Exception e) {
            Gdx.app.error(
                "PlantAnimationProfileLoader",
                "Failed to load plant animation JSON: " + path,
                e
            );
            return null;
        }
    }

    private static PlantAnimationProfile createBuiltInFallback(PlantName plantName) {
        PlantAnimationProfile profile = new PlantAnimationProfile(
            plantName,
            DEFAULT_PAM_PATH
        );
        profile.addVariant(
            PlantVisualState.IDLE,
            new PlantAnimationVariant("idle", true, 0, PlantAnimationTag.DEFAULT)
        );
        profile.addVariant(
            PlantVisualState.ATTACK,
            new PlantAnimationVariant("attack", false, 0, PlantAnimationTag.DEFAULT)
        );
        profile.addVariant(
            PlantVisualState.PLANT_FOOD,
            new PlantAnimationVariant("plantfood", false, 0, PlantAnimationTag.PLANT_FOOD_STAGE)
        );
        return profile;
    }

    public static String idleClipOf(PlantAnimationProfile profile) {
        if (profile == null) {
            return "idle";
        }
        List<PlantAnimationVariant> idles = profile.getVariants(PlantVisualState.IDLE);
        if (idles.isEmpty() || idles.get(0).getClipName() == null || idles.get(0).getClipName().isBlank()) {
            return "idle";
        }
        return idles.get(0).getClipName();
    }

    private static String resolvePamPath(String profilePath, String pamPath) {
        PamPathResolver resolver = PamPathResolver.get();
        String resolved = resolver.resolve(pamPath);
        if (resolved != null && !resolved.equals(pamPath)) {
            Gdx.app.log(
                "PlantAnimationProfileLoader",
                "Resolved plant PAM " + pamPath + " -> " + resolved + " for " + profilePath
            );
        }
        if (resolved != null && !resolver.exists(resolved)) {
            Gdx.app.error(
                "PlantAnimationProfileLoader",
                "Plant PAM not found: " + pamPath + " in " + profilePath
            );
        }
        return resolved != null ? resolved : pamPath;
    }

    private static AnimationCondition<Plant> parseCondition(JsonObject conditionObj) {
        String type = conditionObj.get("type").getAsString().toLowerCase();

        return switch (type) {
            case "stage" -> {
                int stage = conditionObj.get("stage").getAsInt();
                yield PlantConditions.stage(stage);
            }
            case "health_segment" -> {
                int segment = conditionObj.get("segment").getAsInt();
                int count = conditionObj.get("count").getAsInt();
                yield PlantConditions.healthSegment(segment, count);
            }
            case "phase" -> {
                String phase = conditionObj.get("phase").getAsString();
                yield PlantConditions.phase(phase);
            }
            case "facing" -> {
                String facing = conditionObj.get("facing").getAsString().toLowerCase();
                yield "left".equals(facing) ? PlantConditions.facingLeft() : PlantConditions.facingRight();
            }
            case "plant_food" -> {
                boolean active = !conditionObj.has("active") || conditionObj.get("active").getAsBoolean();
                yield active ? PlantConditions.plantFoodActive() : PlantConditions.plantFoodInactive();
            }
            default -> throw new IllegalArgumentException(
                "Unknown plant animation condition: " + type
            );
        };
    }
}

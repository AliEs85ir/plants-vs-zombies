package graphics.projectile;

import app.enums.plant.PlantName;
import game.entity.projectile.Projectile;
import graphics.general.animation.condition.AnimationCondition;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

public final class ProjectileAnimationProfileLoader {

    private static final String DEFAULT_PATH = "projectile/peashooter.json";

    private ProjectileAnimationProfileLoader() {}

    public static ProjectileAnimationProfile load(String resourcePath) {
        return load(resourcePath, null);
    }

    public static ProjectileAnimationProfile loadFor(PlantName plantName) {
        String underscored = "projectile/" + plantName.name().toLowerCase() + ".json";
        String compact = "projectile/" + plantName.name().toLowerCase().replace("_", "") + ".json";
        ProjectileAnimationProfile profile = tryParse(underscored, plantName);
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

    public static ProjectileAnimationProfile load(String resourcePath, PlantName expectedName) {
        ProjectileAnimationProfile profile = tryParse(resourcePath, expectedName);
        if (profile != null) {
            return profile;
        }
        return loadDefault(expectedName);
    }

    private static ProjectileAnimationProfile loadDefault(PlantName expectedName) {
        PlantName key = expectedName != null ? expectedName : PlantName.PEASHOOTER;
        ProjectileAnimationProfile profile = tryParse(DEFAULT_PATH, key);
        if (profile != null) {
            Gdx.app.error(
                "ProjectileAnimationProfileLoader",
                "Using default projectile animation profile for " + key
            );
            return profile;
        }
        return createBuiltInFallback(key);
    }

    private static ProjectileAnimationProfile tryParse(String resourcePath, PlantName expectedName) {
        try {
            FileHandle file = Gdx.files.internal(resourcePath);
            if (!file.exists()) {
                return null;
            }

            JsonValue root = new JsonReader().parse(file);
            PlantName plantName = expectedName != null
                ? expectedName
                : PlantName.valueOf(root.getString("plant"));
            String pamPath = root.getString("pamPath");
            ProjectileAnimationProfile profile = new ProjectileAnimationProfile(plantName, pamPath);

            JsonValue states = root.get("states");
            for (JsonValue stateEntry : states) {
                ProjectileVisualState state = ProjectileVisualState.valueOf(stateEntry.name());
                for (JsonValue variantJson : stateEntry) {
                    String clip = variantJson.getString("clip");
                    boolean loop = variantJson.getBoolean("loop");
                    int priority = variantJson.getInt("priority", 0);
                    ProjectileAnimationTag tag = ProjectileAnimationTag.valueOf(
                        variantJson.getString("tag", "DEFAULT").toUpperCase());
                    String variantPam = variantJson.getString("pamPath", null);
                    AnimationCondition<Projectile> condition = conditionFor(tag);
                    profile.addVariant(
                        state,
                        new ProjectileAnimationVariant(clip, loop, priority, tag, condition, variantPam)
                    );
                }
            }
            return profile;
        } catch (Exception e) {
            Gdx.app.error(
                "ProjectileAnimationProfileLoader",
                "Failed to load projectile animation JSON: " + resourcePath,
                e
            );
            return null;
        }
    }

    private static AnimationCondition<Projectile> conditionFor(ProjectileAnimationTag tag) {
        if (tag == ProjectileAnimationTag.PLANT_FOOD) {
            return Projectile::isPlantFoodVisual;
        }
        if (tag == ProjectileAnimationTag.DEFAULT) {
            return projectile -> projectile == null || !projectile.isPlantFoodVisual();
        }
        return null;
    }

    private static ProjectileAnimationProfile createBuiltInFallback(PlantName plantName) {
        ProjectileAnimationProfile profile = new ProjectileAnimationProfile(
            plantName,
            "768/INITIAL/EFFECTS/PVINE_PROJECTILE/PVINE_PROJECTILE.PAM"
        );
        profile.addVariant(
            ProjectileVisualState.FLYING,
            new ProjectileAnimationVariant("animation", false, 0, ProjectileAnimationTag.DEFAULT)
        );
        profile.addVariant(
            ProjectileVisualState.IMPACT,
            new ProjectileAnimationVariant(
                "animation3",
                false,
                0,
                ProjectileAnimationTag.DEFAULT,
                null,
                "768/FULL/EFFECTS/T_SPLAT_PEA_ROCK/T_SPLAT_PEA_ROCK.PAM"
            )
        );
        return profile;
    }
}

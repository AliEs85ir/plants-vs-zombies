package graphics.general.animation.loader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import game.entity.zombie.Zombie;
import graphics.general.animation.condition.AnimationCondition;
import graphics.general.effects.EffectCue;
import graphics.general.event.ZombieVisualEventType;
import graphics.zombie.*;
import graphics.zombie.visibility.VisibilityConfig;
import graphics.zombie.visibility.VisibilityConfigLoader;

public final class ZombieAnimationProfileLoader {

    private ZombieAnimationProfileLoader() {
    }

    public static ZombieAnimationProfile load(String path) {
        FileHandle file = Gdx.files.internal(path);

        if (!file.exists()) {
            throw new IllegalArgumentException(
                "Resource not found: " + path
            );
        }

        JsonObject root = JsonParser
            .parseReader(file.reader())
            .getAsJsonObject();

        String alias = root.get("zombie").getAsString();
        String pamPath = root.get("pamPath").getAsString();

        ZombieAnimationProfile profile =
            new ZombieAnimationProfile(alias, pamPath);

        if (root.has("visibilityConfig") && !root.get("visibilityConfig").isJsonNull()) {
            JsonObject visibilityJson = root.getAsJsonObject("visibilityConfig");
            VisibilityConfig visibilityConfig = VisibilityConfigLoader.parse(visibilityJson);

            profile.setVisibilityConfig(visibilityConfig);
        }

        JsonObject states = root.getAsJsonObject("states");

        for (String stateName : states.keySet()) {
            ZombieVisualState state =
                ZombieVisualState.valueOf(stateName.toUpperCase());

            states.getAsJsonArray(stateName).forEach(jsonElement -> {
                JsonObject object = jsonElement.getAsJsonObject();

                String clip = object.get("clip").getAsString();

                boolean loop = object.has("loop")
                    && object.get("loop").getAsBoolean();

                int priority = object.has("priority")
                    ? object.get("priority").getAsInt()
                    : 0;

                ZombieAnimationTag tag = object.has("tag")
                    ? ZombieAnimationTag.valueOf(
                    object.get("tag")
                        .getAsString()
                        .toUpperCase()
                )
                    : ZombieAnimationTag.DEFAULT;

                AnimationCondition<Zombie> condition = null;

                if (object.has("condition")
                    && !object.get("condition").isJsonNull()) {
                    condition = parseCondition(
                        object.getAsJsonObject("condition")
                    );
                }

                ZombieAnimationVariant variant;

                if (condition != null) {
                    variant = new ZombieAnimationVariant(
                        clip,
                        loop,
                        priority,
                        tag,
                        condition
                    );
                } else {
                    variant = new ZombieAnimationVariant(
                        clip,
                        loop,
                        priority,
                        tag
                    );
                }

                for (EffectCue cue : EffectCueParser.parse(object)) {
                    variant.addEffectCue(cue);
                }

                profile.addVariant(state, variant);
            });
        }

        return profile;
    }

    private static AnimationCondition<Zombie> parseCondition(
        JsonObject conditionObj
    ) {
        String type = conditionObj.get("type").getAsString();

        switch (type) {
            case "allStar": {
                String eventType =
                    conditionObj.get("eventType").getAsString();

                return ZombieConditions.allStar(
                    ZombieVisualEventType.valueOf(
                        eventType.toUpperCase()
                    )
                );
            }

            case "newspaper": {
                boolean eventType =
                    conditionObj.get("eventType").getAsBoolean();

                return ZombieConditions.newspaper(eventType);
            }

            default:
                throw new IllegalArgumentException(
                    "Unknown condition type: " + type
                );
        }
    }

    public static ZombieAnimationProfile loadSpecific(String alias) {
        switch (alias) {
            case "Gargantuar1" -> {
                return load("gargantuar/gargantuar1.json");
            }
            case "ZombieNewspaper" -> {
                return load("newspaper.json");
            }
            default -> {
                return null;
            }
            // TODO -> ADD OTHERS.
        }
    }
}

package graphics.general.animation.loader;

import app.enums.obstacle.ObstacleType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import game.entity.obstacle.Obstacle;
import graphics.general.animation.condition.AnimationCondition;
import graphics.general.effects.EffectCue;
import graphics.general.event.ObstacleVisualEventType;
import graphics.obstacle.*;

public class ObstacleAnimationProfileLoader {
    private ObstacleAnimationProfileLoader() {
    }

    public static ObstacleAnimationProfile load(String path) {
        FileHandle file = Gdx.files.internal(path);

        if (!file.exists()) {
            throw new IllegalArgumentException(
                "Resource not found: " + path
            );
        }

        JsonObject root = JsonParser
            .parseReader(file.reader())
            .getAsJsonObject();

        ObstacleType type = ObstacleType.valueOf(root.get("obstacle").getAsString());
        String pamPath = root.get("pamPath").getAsString();

        ObstacleAnimationProfile profile =
            new ObstacleAnimationProfile(type, pamPath);

        JsonObject states = root.getAsJsonObject("states");

        for (String stateName : states.keySet()) {
            ObstacleVisualState state = ObstacleVisualState.valueOf(stateName.toUpperCase());

            states.getAsJsonArray(stateName).forEach(jsonElement -> {
                JsonObject object = jsonElement.getAsJsonObject();

                String clip = object.get("clip").getAsString();

                boolean loop = object.has("loop")
                    && object.get("loop").getAsBoolean();

                int priority = object.has("priority")
                    ? object.get("priority").getAsInt()
                    : 0;

                ObstacleAnimationTag tag = object.has("tag")
                    ? ObstacleAnimationTag.valueOf(
                    object.get("tag")
                        .getAsString()
                        .toUpperCase()
                )
                    : ObstacleAnimationTag.DEFAULT;

                AnimationCondition<Obstacle> condition = null;

                if (object.has("condition")
                    && !object.get("condition").isJsonNull()) {
                    condition = parseCondition(
                        object.getAsJsonObject("condition")
                    );
                }

                ObstacleAnimationVariant variant;

                if (condition != null) {
                    variant = new ObstacleAnimationVariant(
                        clip,
                        loop,
                        priority,
                        tag,
                        condition
                    );
                } else {
                    variant = new ObstacleAnimationVariant(
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

    private static AnimationCondition<Obstacle> parseCondition(
        JsonObject conditionObj
    ) {
        String type = conditionObj.get("type").getAsString();

        switch (type) {
            case "event" : {
                String eventType = conditionObj.get("eventType").getAsString();
                return ObstacleConditions.event(ObstacleVisualEventType.valueOf(eventType.toUpperCase()));
            }

            default:
                throw new IllegalArgumentException(
                    "Unknown condition type: " + type
                );
        }
    }
}

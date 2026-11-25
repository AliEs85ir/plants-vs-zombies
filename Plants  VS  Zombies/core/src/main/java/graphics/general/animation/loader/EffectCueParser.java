package graphics.general.animation.loader;

import com.badlogic.gdx.math.Vector2;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import game.entity.zombie.Zombie;
import graphics.general.animation.condition.AnimationCondition;
import graphics.general.effects.EffectAnchorType;
import graphics.general.effects.EffectConditions;
import graphics.general.effects.EffectCue;
import graphics.general.effects.EffectTrigger;
import graphics.general.event.ZombieVisualEventType;
import graphics.zombie.ZombieConditions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class    EffectCueParser {
    private EffectCueParser() {
    }

    public static List<EffectCue> parse(JsonObject clipObj) {
        List<EffectCue> cues = new ArrayList<>();

        if (clipObj == null
            || !clipObj.has("effects")
            || clipObj.get("effects").isJsonNull()) {
            return cues;
        }

        JsonArray effectsArray = clipObj.getAsJsonArray("effects");

        for (JsonElement element : effectsArray) {
            JsonObject effectObject = element.getAsJsonObject();

            if (!effectObject.has("id")) {
                throw new IllegalArgumentException(
                    "Effect cue is missing required field: id"
                );
            }

            if (!effectObject.has("trigger")) {
                throw new IllegalArgumentException(
                    "Effect cue is missing required field: trigger"
                );
            }

            String effectId =
                effectObject.get("id").getAsString();

            EffectTrigger trigger =
                EffectTrigger.valueOf(
                    effectObject.get("trigger")
                        .getAsString()
                        .toUpperCase()
                );

            EffectAnchorType anchorType =
                effectObject.has("anchor")
                    ? EffectAnchorType.valueOf(
                    effectObject.get("anchor")
                        .getAsString()
                        .toUpperCase()
                )
                    : EffectAnchorType.SELF;

            Vector2 offset = parseVec(
                effectObject.has("offset")
                    ? effectObject.get("offset")
                    : null,
                new Vector2()
            );

            float delay = effectObject.has("delay")
                ? effectObject.get("delay").getAsFloat()
                : 0f;

            float normalizedTime = effectObject.has("time")
                ? effectObject.get("time").getAsFloat()
                : 0f;

            boolean perLoop = effectObject.has("perLoop")
                && effectObject.get("perLoop").getAsBoolean();

            boolean once = effectObject.has("once")
                && effectObject.get("once").getAsBoolean();

            AnimationCondition<Object> condition = null;
            if (effectObject.has("condition")) {
                JsonObject condObj = effectObject.getAsJsonObject("condition");
                condition = parseEffectCondition(condObj);
            }

            Map<String, Boolean> visibilityMap = new HashMap<>();
            if (effectObject.has("visibility")) {
                JsonObject visObj = effectObject.getAsJsonObject("visibility");
                for (String layerName : visObj.keySet()) {
                    visibilityMap.put(layerName, visObj.get(layerName).getAsBoolean());
                }
            }

            Vector2 velocity = parseVec(effectObject.has("velocity") ? effectObject.get("velocity") : null, new Vector2());

            cues.add(new EffectCue(
                effectId, trigger, anchorType, offset, delay, normalizedTime, perLoop, once,
                condition, visibilityMap, velocity
            ));
        }

        return cues;
    }

    private static Vector2 parseVec(
        JsonElement element,
        Vector2 fallback
    ) {
        if (element == null
            || element.isJsonNull()
            || !element.isJsonObject()) {
            return fallback;
        }

        JsonObject object = element.getAsJsonObject();

        float x = object.has("x")
            ? object.get("x").getAsFloat()
            : 0f;

        float y = object.has("y")
            ? object.get("y").getAsFloat()
            : 0f;

        return new Vector2(x, y);
    }

    private static AnimationCondition<Object> parseEffectCondition(
        JsonObject conditionObj
    ) {
        String type = conditionObj.get("type").getAsString();

        switch (type) {
            case "healthBelow" : {
                float eventType =
                    conditionObj.get("eventType").getAsFloat();

                return EffectConditions.healthBelow(eventType);
            }
            case "eventBased" : {
                String eventType =
                    conditionObj.get("eventType").getAsString();

                return EffectConditions.fromEvent(
                        eventType.toUpperCase()
                    );
            }

            default:
                throw new IllegalArgumentException(
                    "Unknown condition type: " + type
                );
        }
    }
}

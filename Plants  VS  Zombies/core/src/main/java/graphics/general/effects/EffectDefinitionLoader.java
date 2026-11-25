package graphics.general.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import graphics.general.animation.condition.AnimationCondition;
import graphics.general.effects.strategy.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EffectDefinitionLoader {
    private EffectDefinitionLoader() {
    }

    public static void loadInto(String path, EffectRegistry registry) {
        FileHandle file = Gdx.files.internal(path);

        if (!file.exists()) {
            throw new IllegalArgumentException("Resource not found: " + path);
        }

        JsonObject root = JsonParser
            .parseReader(file.reader())
            .getAsJsonObject();

        JsonArray effects = root.getAsJsonArray("effects");

        if (effects == null) {
            return;
        }

        for (JsonElement element : effects) {
            JsonObject effectObject = element.getAsJsonObject();
            EffectDefinition<?> definition = parseDefinition(effectObject);
            registry.register(definition);
        }
    }

    private static EffectDefinition<?> parseDefinition(JsonObject object) {
        String id = object.get("id").getAsString();
        String pamPath = object.get("pamPath").getAsString();

        EffectLayer layer = object.has("layer")
            ? EffectLayer.valueOf(
            object.get("layer").getAsString().toUpperCase()
        )
            : EffectLayer.DEFAULT;

        boolean followTarget = object.has("followTarget")
            && object.get("followTarget").getAsBoolean();

        String selectionName = object.has("selection")
            ? object.get("selection").getAsString().toUpperCase()
            : "FIRST";

        float groundOffset = object.has("groundOffset")
            ? object.get("groundOffset").getAsFloat()
            : 20f;

        float despawnDelay = object.has("despawnDelay")
            ? object.get("despawnDelay").getAsFloat()
            : 2f;

        float fadeDuration = object.has("fadeDuration")
            ? object.get("fadeDuration").getAsFloat()
            : 1f;

        List<EffectVariant<Object, EffectTag>> variants =
            parseVariants(object.getAsJsonArray("variants"));

        EffectSelectionStrategy<Object> strategy =
            createStrategy(selectionName);

        return new EffectDefinition<>(
            id,
            pamPath,
            variants,
            strategy,
            layer,
            followTarget,
            groundOffset,
            despawnDelay,
            fadeDuration
        );
    }

    private static List<EffectVariant<Object, EffectTag>> parseVariants(
        JsonArray variantsArray
    ) {
        List<EffectVariant<Object, EffectTag>> variants = new ArrayList<>();

        if (variantsArray == null) {
            return variants;
        }

        for (JsonElement element : variantsArray) {
            JsonObject variantObject = element.getAsJsonObject();

            String clip = variantObject.get("clip").getAsString();

            boolean loop = variantObject.has("loop")
                && variantObject.get("loop").getAsBoolean();

            int priority = variantObject.has("priority")
                ? variantObject.get("priority").getAsInt()
                : 0;

            EffectTag tag = variantObject.has("tag")
                ? EffectTag.valueOf(variantObject.get("tag").getAsString().toUpperCase())
                : EffectTag.DEFAULT;

            AnimationCondition<Object> condition = null;
            if (variantObject.has("condition")) {
                JsonObject condObj = variantObject.getAsJsonObject("condition");
                String conditionType = condObj.get("type").getAsString();
                String eventType = condObj.has("eventType") ? condObj.get("eventType").getAsString() : null;

                if ("zombieEvent".equals(conditionType)) {
                    condition = EffectConditions.fromEvent(eventType);
                }
            }

            Map<String, Boolean> visibilityMap = new HashMap<>();
            if (variantObject.has("visibility") && variantObject.get("visibility").isJsonObject()) {
                JsonObject visObj = variantObject.getAsJsonObject("visibility");
                for (String layerName : visObj.keySet()) {
                    visibilityMap.put(layerName, visObj.get(layerName).getAsBoolean());
                }
            }

            String pamPath = variantObject.has("pamPath") ? variantObject.get("pamPath").getAsString() : null;

            variants.add(
                new EffectVariant<>(
                    clip,
                    loop,
                    priority,
                    tag,
                    condition,
                    pamPath,
                    visibilityMap
                )
            );
        }
        return variants;
    }

    private static EffectSelectionStrategy<Object> createStrategy(String selection) {
        return switch (selection) {
            case "RANDOM" -> new RandomEffectSelectionStrategy<>();
            case "SEQUENCE" -> new SequenceEffectSelectionStrategy<>();
            case "CONDITIONAL" -> new ConditionEffectSelectionStrategy<>();
            case "FIRST" -> new FirstEffectSelectionStrategy<>();
            default -> new FirstEffectSelectionStrategy<>();
        };
    }
}

package graphics.zombie.visibility;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import graphics.general.event.ZombieVisualEventType;
import graphics.zombie.ZombieVisualState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VisibilityConfigLoader {
    public static VisibilityConfig parse(JsonObject root) {
        if (root == null || root.isJsonNull()) {
            return null;
        }

        VisibilityConfig config = new VisibilityConfig();

        // 1. Parse Events
        if (root.has("events")) {
            JsonObject eventsObj = root.getAsJsonObject("events");
            Map<ZombieVisualEventType, Map<String, Boolean>> eventMap = new HashMap<>();

            for (String eventKey : eventsObj.keySet()) {
                ZombieVisualEventType eventType = ZombieVisualEventType.valueOf(eventKey.toUpperCase());
                eventMap.put(eventType, parseLayerMap(eventsObj.getAsJsonObject(eventKey)));
            }
            config.setEventVisibility(eventMap);
        }

        // 2. Parse Conditions
        if (root.has("conditions")) {
            JsonArray conditionsArray = root.getAsJsonArray("conditions");
            List<VisibilityConfig.ConditionVisibility> conditionsList = new ArrayList<>();

            for (JsonElement elem : conditionsArray) {
                JsonObject condObj = elem.getAsJsonObject();
                VisibilityConfig.ConditionVisibility condition = new VisibilityConfig.ConditionVisibility();

                condition.setType(condObj.get("type").getAsString());

                // Map the JSON value field based on condition type to the generic Object 'value' field
                if (condObj.has("value")) {
                    condition.setValue(condObj.get("value").getAsFloat());
                } else if (condObj.has("effectType")) {
                    condition.setValue(condObj.get("effectType").getAsString());
                } else if (condObj.has("armorAlias")) {
                    condition.setValue(condObj.get("armorAlias").getAsString());
                }

                if (condObj.has("layers")) {
                    condition.setLayers(parseLayerMap(condObj.getAsJsonObject("layers")));
                }

                if (condObj.has("phase")) {
                    condition.setPhase(condObj.get("phase").getAsString());
                }

                conditionsList.add(condition);
            }
            config.setConditionVisibilities(conditionsList);
        }

        // 3. Parse States
        if (root.has("states")) {
            JsonObject statesObj = root.getAsJsonObject("states");
            Map<ZombieVisualState, Map<String, Boolean>> stateMap = new HashMap<>();

            for (String stateKey : statesObj.keySet()) {
                ZombieVisualState stateType = ZombieVisualState.valueOf(stateKey.toUpperCase());
                stateMap.put(stateType, parseLayerMap(statesObj.getAsJsonObject(stateKey)));
            }
            config.setStateVisibility(stateMap);
        }

        return config;
    }

    private static Map<String, Boolean> parseLayerMap(JsonObject layersObj) {
        Map<String, Boolean> map = new HashMap<>();
        for (String layerKey : layersObj.keySet()) {
            map.put(layerKey, layersObj.get(layerKey).getAsBoolean());
        }
        return map;
    }
}

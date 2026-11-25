package graphics.general.instance;

import java.util.HashMap;
import java.util.Map;

public class VisibilityController {

    private final Map<String, Boolean> layerVisibilityMap = new HashMap<>();
    private final Map<String, Boolean> persistentOverrides = new HashMap<>();

    public void setLayer(String layerName, boolean visible) {
        layerVisibilityMap.put(layerName, visible);
    }

    public void setVisible(String layerName) {
        persistentOverrides.put(layerName, true);
        layerVisibilityMap.put(layerName, true);
    }

    public void setInvisible(String layerName) {
        persistentOverrides.put(layerName, false);
        layerVisibilityMap.put(layerName, false);
    }

    public void clearOverride(String layerName) {
        persistentOverrides.remove(layerName);
        layerVisibilityMap.remove(layerName);
    }

    public boolean isLayerVisible(String layerName) {
        if (persistentOverrides.containsKey(layerName)) {
            return persistentOverrides.get(layerName);
        }
        return layerVisibilityMap.getOrDefault(layerName, true);
    }

    public Map<String, Boolean> getLayerVisibilityMap() {
        if (persistentOverrides.isEmpty()) {
            return layerVisibilityMap;
        }
        Map<String, Boolean> merged = new HashMap<>(layerVisibilityMap);
        merged.putAll(persistentOverrides);
        return merged;
    }

    public void applyPersistentOverrides() {
        layerVisibilityMap.putAll(persistentOverrides);
    }

    public void resetAll() {
        layerVisibilityMap.clear();
    }
}

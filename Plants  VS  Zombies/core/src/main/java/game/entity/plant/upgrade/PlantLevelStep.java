package game.entity.plant.upgrade;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class PlantLevelStep {
    public int level;
    public int requiredSeedPackets;
    public int requiredCoins;
    public Map<String, Object> modifiers;

    public Map<String, Object> modifiersOrEmpty() {
        return modifiers == null ? Collections.emptyMap() : modifiers;
    }

    void sanitize() {
        if (modifiers == null) {
            modifiers = new LinkedHashMap<>();
        }
    }
}

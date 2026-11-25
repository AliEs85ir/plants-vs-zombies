package game.service.Zombie;

import game.entity.plant.PlantRole;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class ZombieInfo {

    private final String name;
    private final String description;
    private final String rarity;
    private final Map<String, String> extras;

    public ZombieInfo(String name, String description) {
        this(name, description, "", null);
    }

    public ZombieInfo(String name, String description, String rarity,
                      Map<String, String> extras) {
        this.name = name;
        this.description = (description != null) ? description : "";
        this.rarity = (rarity != null) ? rarity : "";
        this.extras = (extras != null)
            ? Collections.unmodifiableMap(new LinkedHashMap<>(extras))
            : Collections.emptyMap();
    }


    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getRarity() {
        return rarity;
    }

    public Map<String, String> getExtras() {
        return extras;
    }

    public String getExtra(String key) {
        return extras.get(key);
    }

    public boolean hasExtra(String key) {
        return extras.containsKey(key);
    }
}

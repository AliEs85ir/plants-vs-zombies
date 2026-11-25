package game.entity.plant;

import app.enums.plant.PlantName;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class PlantInfo {

    private final PlantName name;
    private final String description;
    private final String rarity;
    private final PlantRole role;
    private final Map<String, String> extras;

    public PlantInfo(PlantName name, String description) {
        this(name, description, null, null, null);
    }

    public PlantInfo(PlantName name, String description, String rarity,
                     PlantRole role, Map<String, String> extras) {
        this.name = name;
        this.description = (description != null) ? description : "";
        this.rarity = (rarity != null) ? rarity : "";
        this.role = (role != null) ? role : PlantRole.OFFENSE;
        this.extras = (extras != null)
            ? Collections.unmodifiableMap(new LinkedHashMap<>(extras))
            : Collections.emptyMap();
    }

    public PlantRole getRole() {
        return role;
    }

    public PlantName getName() {
        return name;
    }

    public String getDisplayName() {
        return name.getLabel();
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

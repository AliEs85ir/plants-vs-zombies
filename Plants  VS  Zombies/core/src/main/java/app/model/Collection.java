package app.model;

import app.enums.plant.PlantName;
import game.entity.plant.PlantDefinition;
import game.entity.registry.PlantRegistry;
import game.entity.registry.ZombieRegistry;
import game.entity.zombie.Zombie;
import game.entity.zombie.data.properties.Property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Collection {
    private List<String> seenZombieAliases;
    private List<PlantName> availablePlants;
    private static final List<PlantDefinition> ALL_PLANTS = PlantRegistry.getPlantDefinitions();

    public Collection() {
        this.seenZombieAliases = new ArrayList<>();
        this.availablePlants = new ArrayList<>();
    }

    public List<Zombie> getSeenZombies() {
        return Collections.emptyList();
    }

    public List<Property> getSeenZombieProperties() {
        List<Property> result = new ArrayList<>();
        for (String alias : getSeenZombieAliases()) {
            Property property = ZombieRegistry.getZombie(alias);
            if (property != null) {
                result.add(property);
            }
        }
        return result;
    }

    public List<String> getSeenZombieAliases() {
        if (seenZombieAliases == null) {
            seenZombieAliases = new ArrayList<>();
        }
        return seenZombieAliases;
    }

    public void syncFromAliases(Set<String> aliases) {
        Set<String> merged = new LinkedHashSet<>(getSeenZombieAliases());
        if (aliases != null) {
            for (String alias : aliases) {
                if (alias != null && !alias.isBlank()) {
                    merged.add(alias);
                }
            }
        }
        seenZombieAliases = new ArrayList<>(merged);
    }

    public void addSeenZombieAlias(String alias) {
        if (alias == null || alias.isBlank()) {
            return;
        }
        List<String> aliases = getSeenZombieAliases();
        if (!aliases.contains(alias)) {
            aliases.add(alias);
        }
    }

    public List<PlantName> getAvailablePlants() {
        if (availablePlants == null) {
            availablePlants = new ArrayList<>();
        }
        return availablePlants;
    }

    public static List<PlantDefinition> getAllPlants() {
        return ALL_PLANTS != null ? ALL_PLANTS : Collections.emptyList();
    }
}

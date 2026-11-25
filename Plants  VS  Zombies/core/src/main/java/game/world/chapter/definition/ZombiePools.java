package game.world.chapter.definition;

import app.enums.SeasonType;
import game.entity.registry.ZombieRegistry;
import game.entity.zombie.data.properties.Property;
import game.world.chapter.ChapterId;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class ZombiePools {
    private ZombiePools() {
    }

    public static List<Property> forChapter(ChapterId id) {
        return resolve(ChapterZombieCatalog.zombiesFor(id));
    }

    public static List<Property> forChapter(SeasonType season, int stage) {
        return forChapter(new ChapterId(season, stage));
    }

    public static List<Property> resolve(List<String> aliases) {
        List<Property> zombies = new ArrayList<>();
        if (aliases != null) {
            for (String alias : aliases) {
                addIfPresent(zombies, alias);
            }
        }
        return zombies;
    }

    public static List<Property> resolve(String... aliases) {
        return resolve(aliases == null ? List.of() : List.of(aliases));
    }

    public static List<Property> catalog() {
        List<Property> zombies = new ArrayList<>();
        for (Property property : ZombieRegistry.getAllZombies()) {
            String alias = property.getAlias();
            if (alias == null || alias.isBlank() || alias.startsWith("ZombieBoss")) {
                continue;
            }
            zombies.add(property);
        }
        zombies.sort(Comparator.comparing(Property::getAlias, String.CASE_INSENSITIVE_ORDER));
        return zombies;
    }

    private static void addIfPresent(List<Property> zombies, String alias) {
        if (alias == null || alias.isBlank()) {
            return;
        }
        Property property = ZombieRegistry.getZombie(alias);
        if (property != null) {
            zombies.add(property);
        }
    }
}

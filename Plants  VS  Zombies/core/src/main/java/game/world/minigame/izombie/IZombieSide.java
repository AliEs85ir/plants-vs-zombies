package game.world.minigame.izombie;

import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

public enum IZombieSide {
    PLANTS,
    ZOMBIES;

    public boolean isPlants() {
        return this == PLANTS;
    }

    public boolean isZombies() {
        return this == ZOMBIES;
    }

    public IZombieSide opponent() {
        return isPlants() ? ZOMBIES : PLANTS;
    }

    public static IZombieSide random() {
        return ThreadLocalRandom.current().nextBoolean() ? PLANTS : ZOMBIES;
    }

    public static IZombieSide parse(String raw) {
        if (raw == null || raw.isBlank()) {
            return ZOMBIES;
        }
        try {
            return valueOf(raw.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            return ZOMBIES;
        }
    }
}

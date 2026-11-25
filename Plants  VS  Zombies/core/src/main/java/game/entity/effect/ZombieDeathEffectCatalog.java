package game.entity.effect;

public final class ZombieDeathEffectCatalog {
    public static final String DEFAULT_ASH_EFFECT_ID = "zombie_ash";

    private ZombieDeathEffectCatalog() {
    }

    public static String ashEffectId(String zombieName) {
        if (zombieName == null || zombieName.isBlank()) {
            return DEFAULT_ASH_EFFECT_ID;
        }
        return switch (zombieName.trim()) {
            default -> DEFAULT_ASH_EFFECT_ID;
        };
    }
}

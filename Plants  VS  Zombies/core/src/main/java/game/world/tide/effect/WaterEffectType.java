package game.world.tide.effect;

/**
 * Catalog of Big Wave Beach water effects. Gameplay systems emit these
 * through {@link WaterEffectEmitter}; clip choice stays with the renderer.
 */
public enum WaterEffectType {
    GARGANTUAR_RIPPLE(
        "768/FULL/BACKGROUNDS/WATER_GARGANTUAR_RIPPLE/WATER_GARGANTUAR_RIPPLE.PAM",
        "ripple",
        "ripple_exit"
    ),
    WATER_BREAKER(
        "768/FULL/EFFECTS/WATER_BREAKER/WATER_BREAKER.PAM",
        "water_breaker_right",
        "water_breaker_left"
    ),
    WATER_FOAM(
        "768/FULL/EFFECTS/WATER_FOAM/WATER_FOAM.PAM",
        "water_foam_right",
        "water_foam_left"
    ),
    WATER_SPLASH(
        "768/FULL/EFFECTS/WATER_SPLASH/WATER_SPLASH.PAM",
        "water_splash_01",
        "water_splash_02"
    );

    private final String pamPath;
    private final String[] clips;

    WaterEffectType(String pamPath, String... clips) {
        this.pamPath = pamPath;
        this.clips = clips;
    }

    public String getPamPath() {
        return pamPath;
    }

    public String[] getClips() {
        return clips;
    }
}

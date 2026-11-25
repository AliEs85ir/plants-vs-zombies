package game.world.tide.effect;

/**
 * Default emitter until a full PAM effect pipeline is wired for water.
 * Tide systems can still call it without branching on "are effects ready".
 */
public final class NoOpWaterEffectEmitter implements WaterEffectEmitter {
    public static final NoOpWaterEffectEmitter INSTANCE = new NoOpWaterEffectEmitter();

    private NoOpWaterEffectEmitter() {
    }

    @Override
    public void emit(WaterEffectType type, String clip, float worldX, float worldY) {
    }
}

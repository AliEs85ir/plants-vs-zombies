package game.world.tide.effect;

/**
 * Extension point for splash, foam, breaker and ripple playback.
 * Tide gameplay never talks to PAM directly.
 */
public interface WaterEffectEmitter {
    void emit(WaterEffectType type, String clip, float worldX, float worldY);

    default void emit(WaterEffectType type, float worldX, float worldY) {
        String[] clips = type.getClips();
        if (clips.length == 0) {
            return;
        }
        emit(type, clips[0], worldX, worldY);
    }
}

package game.audio;

public final class MusicTransition {
    private final float fadeSeconds;

    private MusicTransition(float fadeSeconds) {
        this.fadeSeconds = Math.max(0f, fadeSeconds);
    }

    public static MusicTransition immediate() {
        return new MusicTransition(0f);
    }

    public static MusicTransition fade(float seconds) {
        return new MusicTransition(seconds);
    }

    public float fadeSeconds() {
        return fadeSeconds;
    }

    public boolean isImmediate() {
        return fadeSeconds <= 0f;
    }
}

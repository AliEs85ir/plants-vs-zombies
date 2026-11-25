package game.audio;

public final class AudioDefinition {
    public static final int INFINITE = -1;

    private final String path;
    private final AudioCategory category;
    private final float defaultVolume;
    private final float defaultPitch;
    private final boolean overlay;
    private final boolean looping;
    private final int repeatCount;
    private final float loopStart;
    private final float loopEnd;
    private final boolean duckMusic;
    private final float duckAmount;
    private final float duckFadeSeconds;
    private final int maxConcurrent;
    private final int priority;
    private final boolean streaming;
    private final boolean eagerLoad;

    private AudioDefinition(Builder builder) {
        this.path = builder.path;
        this.category = builder.category;
        this.defaultVolume = clamp(builder.defaultVolume);
        this.defaultPitch = builder.defaultPitch <= 0f ? 1f : builder.defaultPitch;
        this.overlay = builder.overlay;
        this.looping = builder.looping;
        this.repeatCount = builder.repeatCount;
        this.loopStart = builder.loopStart;
        this.loopEnd = builder.loopEnd;
        this.duckMusic = builder.duckMusic;
        this.duckAmount = clamp(builder.duckAmount);
        this.duckFadeSeconds = Math.max(0f, builder.duckFadeSeconds);
        this.maxConcurrent = Math.max(1, builder.maxConcurrent);
        this.priority = builder.priority;
        this.streaming = builder.streaming;
        this.eagerLoad = builder.eagerLoad;
    }

    public static Builder music(String path) {
        return new Builder(path, AudioCategory.MUSIC).looping().streaming();
    }

    public static Builder sfx(String path) {
        return new Builder(path, AudioCategory.SFX).overlay().repeat(1).maxConcurrent(8);
    }

    public static Builder voice(String path) {
        return new Builder(path, AudioCategory.VOICE).overlay().repeat(1).streaming().duck(0.4f, 0.35f);
    }

    public static Builder ui(String path) {
        return new Builder(path, AudioCategory.UI).overlay().repeat(1).maxConcurrent(4);
    }

    public String path() {
        return path;
    }

    public AudioCategory category() {
        return category;
    }

    public float defaultVolume() {
        return defaultVolume;
    }

    public float defaultPitch() {
        return defaultPitch;
    }

    public boolean overlay() {
        return overlay;
    }

    public boolean looping() {
        return looping;
    }

    public int repeatCount() {
        return repeatCount;
    }

    public float loopStart() {
        return loopStart;
    }

    public float loopEnd() {
        return loopEnd;
    }

    public boolean hasLoopRange() {
        return !Float.isNaN(loopEnd);
    }

    public boolean duckMusic() {
        return duckMusic;
    }

    public float duckAmount() {
        return duckAmount;
    }

    public float duckFadeSeconds() {
        return duckFadeSeconds;
    }

    public int maxConcurrent() {
        return maxConcurrent;
    }

    public int priority() {
        return priority;
    }

    public boolean streaming() {
        return streaming;
    }

    public boolean eagerLoad() {
        return eagerLoad;
    }

    private static float clamp(float value) {
        if (value < 0f) {
            return 0f;
        }
        if (value > 1f) {
            return 1f;
        }
        return value;
    }

    public static final class Builder {
        private final String path;
        private final AudioCategory category;
        private float defaultVolume = 1f;
        private float defaultPitch = 1f;
        private boolean overlay;
        private boolean looping;
        private int repeatCount = 1;
        private float loopStart = 0f;
        private float loopEnd = Float.NaN;
        private boolean duckMusic;
        private float duckAmount = 1f;
        private float duckFadeSeconds = 0.35f;
        private int maxConcurrent = 8;
        private int priority;
        private boolean streaming;
        private boolean eagerLoad = true;

        private Builder(String path, AudioCategory category) {
            this.path = path;
            this.category = category;
        }

        public Builder volume(float volume) {
            this.defaultVolume = volume;
            return this;
        }

        public Builder pitch(float pitch) {
            this.defaultPitch = pitch;
            return this;
        }

        public Builder overlay() {
            this.overlay = true;
            return this;
        }

        public Builder exclusive() {
            this.overlay = false;
            return this;
        }

        public Builder looping() {
            this.looping = true;
            this.repeatCount = INFINITE;
            return this;
        }

        public Builder loopRange(float startSeconds, float endSeconds) {
            this.looping = true;
            this.repeatCount = INFINITE;
            this.loopStart = Math.max(0f, startSeconds);
            this.loopEnd = Math.max(this.loopStart, endSeconds);
            return this;
        }

        public Builder repeat(int count) {
            this.repeatCount = count < 0 ? INFINITE : Math.max(1, count);
            this.looping = this.repeatCount == INFINITE;
            return this;
        }

        public Builder duck(float amount, float fadeSeconds) {
            this.duckMusic = true;
            this.duckAmount = amount;
            this.duckFadeSeconds = fadeSeconds;
            return this;
        }

        public Builder maxConcurrent(int count) {
            this.maxConcurrent = count;
            return this;
        }

        public Builder priority(int priority) {
            this.priority = priority;
            return this;
        }

        public Builder streaming() {
            this.streaming = true;
            return this;
        }

        public Builder lazy() {
            this.eagerLoad = false;
            return this;
        }

        public AudioDefinition build() {
            return new AudioDefinition(this);
        }
    }
}

package game.audio;

import com.badlogic.gdx.Gdx;

public final class AudioManager {
    private static final AudioSettings SETTINGS = new AudioSettings();
    private static MusicController music;
    private static SfxController sfx;
    private static boolean initialized;

    private AudioManager() {
    }

    public static void init() {
        if (initialized || Gdx.audio == null) {
            return;
        }
        music = new MusicController(SETTINGS);
        sfx = new SfxController(SETTINGS);
        initialized = true;
        for (AudioId id : AudioId.values()) {
            if (!id.definition().streaming() && id.definition().eagerLoad()) {
                sfx.preload(id);
            }
        }
    }

    public static void update(float delta) {
        if (!initialized) {
            return;
        }
        music.update(delta);
        sfx.applyVolumes();
    }

    public static void dispose() {
        if (!initialized) {
            return;
        }
        music.dispose();
        sfx.dispose();
        music = null;
        sfx = null;
        initialized = false;
    }

    public static void play(AudioId id) {
        play(id, MusicTransition.immediate());
    }

    public static void play(AudioId id, MusicTransition transition) {
        if (id == null || !ensureInit()) {
            return;
        }
        AudioDefinition definition = id.definition();
        if (definition.streaming()) {
            music.play(id, transition);
            return;
        }
        if (definition.looping() && definition.repeatCount() == AudioDefinition.INFINITE) {
            sfx.loop(id);
            return;
        }
        sfx.play(id);
    }

    public static void playLoop(AudioId id) {
        if (id == null || !ensureInit()) {
            return;
        }
        if (id.definition().streaming()) {
            music.play(id, MusicTransition.immediate());
            return;
        }
        sfx.loop(id);
    }

    public static void stop(AudioId id) {
        stop(id, 0f);
    }

    public static void stop(AudioId id, float fadeSeconds) {
        if (id == null || !ensureInit()) {
            return;
        }
        if (id.definition().streaming()) {
            music.stop(id, fadeSeconds);
            return;
        }
        sfx.stop(id);
    }

    public static void stopMusic() {
        stopMusic(0f);
    }

    public static void stopMusic(float fadeSeconds) {
        if (!ensureInit()) {
            return;
        }
        music.stopCategory(AudioCategory.MUSIC, fadeSeconds);
    }

    public static void stopGameplayAudio() {
        if (!ensureInit()) {
            return;
        }
        music.stopAll(0f);
    }

    public static void stopAll() {
        if (!ensureInit()) {
            return;
        }
        music.stopAll(0f);
        sfx.stopAll();
    }

    public static void duckMusic(float amount, float fadeSeconds) {
        if (!ensureInit()) {
            return;
        }
        music.duck(amount, fadeSeconds);
    }

    public static void restoreMusic(float fadeSeconds) {
        if (!ensureInit()) {
            return;
        }
        music.restore(fadeSeconds);
    }

    public static AudioSettings settings() {
        return SETTINGS;
    }

    public static void setMasterVolume(float volume) {
        SETTINGS.setMasterVolume(volume);
    }

    public static void setMusicVolume(float volume) {
        SETTINGS.setMusicVolume(volume);
    }

    public static void setSfxVolume(float volume) {
        SETTINGS.setSfxVolume(volume);
    }

    public static void setVoiceVolume(float volume) {
        SETTINGS.setVoiceVolume(volume);
    }

    public static void setUiVolume(float volume) {
        SETTINGS.setUiVolume(volume);
    }

    public static void mute() {
        SETTINGS.mute();
    }

    public static void unmute() {
        SETTINGS.unmute();
    }

    public static void setMuted(boolean muted) {
        SETTINGS.setMuted(muted);
    }

    public static void setCategoryMuted(AudioCategory category, boolean muted) {
        SETTINGS.setCategoryMuted(category, muted);
    }

    public static boolean isCategoryMuted(AudioCategory category) {
        return SETTINGS.isCategoryMuted(category);
    }

    public static void setCategoryVolume(AudioCategory category, float volume) {
        SETTINGS.setCategoryVolume(category, volume);
    }

    public static void resetToDefaults() {
        SETTINGS.resetToDefaults();
    }

    private static boolean ensureInit() {
        if (!initialized) {
            init();
        }
        return initialized && music != null && sfx != null;
    }
}

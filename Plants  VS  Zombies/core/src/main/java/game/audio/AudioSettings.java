package game.audio;

import java.util.EnumSet;

public final class AudioSettings {
    public static final float DEFAULT_VOLUME = 0.70f;

    private float masterVolume = DEFAULT_VOLUME;
    private float musicVolume = DEFAULT_VOLUME;
    private float sfxVolume = DEFAULT_VOLUME;
    private float voiceVolume = DEFAULT_VOLUME;
    private float uiVolume = DEFAULT_VOLUME;
    private boolean muted;
    private final EnumSet<AudioCategory> mutedCategories = EnumSet.noneOf(AudioCategory.class);

    public float masterVolume() {
        return masterVolume;
    }

    public void setMasterVolume(float volume) {
        this.masterVolume = clamp(volume);
    }

    public float musicVolume() {
        return musicVolume;
    }

    public void setMusicVolume(float volume) {
        this.musicVolume = clamp(volume);
    }

    public float sfxVolume() {
        return sfxVolume;
    }

    public void setSfxVolume(float volume) {
        this.sfxVolume = clamp(volume);
    }

    public float voiceVolume() {
        return voiceVolume;
    }

    public void setVoiceVolume(float volume) {
        this.voiceVolume = clamp(volume);
    }

    public float uiVolume() {
        return uiVolume;
    }

    public void setUiVolume(float volume) {
        this.uiVolume = clamp(volume);
    }

    public boolean muted() {
        return muted;
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    public void mute() {
        this.muted = true;
    }

    public void unmute() {
        this.muted = false;
    }

    public boolean isCategoryMuted(AudioCategory category) {
        return category != null && mutedCategories.contains(category);
    }

    public void setCategoryMuted(AudioCategory category, boolean categoryMuted) {
        if (category == null) {
            return;
        }
        if (categoryMuted) {
            mutedCategories.add(category);
        } else {
            mutedCategories.remove(category);
        }
    }

    public void setCategoryVolume(AudioCategory category, float volume) {
        if (category == null) {
            return;
        }
        switch (category) {
            case MUSIC -> setMusicVolume(volume);
            case SFX -> setSfxVolume(volume);
            case VOICE -> setVoiceVolume(volume);
            case UI -> setUiVolume(volume);
        }
    }

    public void resetToDefaults() {
        masterVolume = DEFAULT_VOLUME;
        musicVolume = DEFAULT_VOLUME;
        sfxVolume = DEFAULT_VOLUME;
        voiceVolume = DEFAULT_VOLUME;
        uiVolume = DEFAULT_VOLUME;
        muted = false;
        mutedCategories.clear();
    }

    public float categoryVolume(AudioCategory category) {
        if (category == null) {
            return 1f;
        }
        return switch (category) {
            case MUSIC -> musicVolume;
            case SFX -> sfxVolume;
            case VOICE -> voiceVolume;
            case UI -> uiVolume;
        };
    }

    public float resolve(AudioCategory category, float assetVolume, float modifier) {
        if (muted || isCategoryMuted(category)) {
            return 0f;
        }
        return clamp(masterVolume)
            * clamp(categoryVolume(category))
            * clamp(assetVolume)
            * clamp(modifier);
    }

    static float clamp(float value) {
        if (value < 0f) {
            return 0f;
        }
        if (value > 1f) {
            return 1f;
        }
        return value;
    }
}

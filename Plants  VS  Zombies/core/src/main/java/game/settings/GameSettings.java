package game.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import game.audio.AudioCategory;
import game.audio.AudioManager;
import game.audio.AudioSettings;

public final class GameSettings {
    private static final String PREFS_NAME = "pvz-game-settings";
    private static final GameplaySettings GAMEPLAY = new GameplaySettings();
    private static boolean loaded;

    private GameSettings() {
    }

    public static void load() {
        if (Gdx.app == null) {
            return;
        }
        Preferences preferences = Gdx.app.getPreferences(PREFS_NAME);
        AudioSettings audio = AudioManager.settings();
        audio.setMasterVolume(preferences.getFloat("audio.master", AudioSettings.DEFAULT_VOLUME));
        audio.setMusicVolume(preferences.getFloat("audio.music", AudioSettings.DEFAULT_VOLUME));
        audio.setSfxVolume(preferences.getFloat("audio.sfx", AudioSettings.DEFAULT_VOLUME));
        audio.setVoiceVolume(preferences.getFloat("audio.voice", AudioSettings.DEFAULT_VOLUME));
        audio.setUiVolume(preferences.getFloat("audio.ui", AudioSettings.DEFAULT_VOLUME));
        audio.setMuted(preferences.getBoolean("audio.masterMuted", false));
        for (AudioCategory category : AudioCategory.values()) {
            audio.setCategoryMuted(
                category,
                preferences.getBoolean("audio.mute." + category.name(), false)
            );
        }
        GAMEPLAY.setNotificationsEnabled(preferences.getBoolean("gameplay.notifications", true));
        GAMEPLAY.setScreenShakeEnabled(preferences.getBoolean("gameplay.screenShake", true));
        GAMEPLAY.setDebugMode(preferences.getBoolean("gameplay.debug", true));
        GAMEPLAY.setGridOverlayEnabled(preferences.getBoolean("gameplay.gridOverlay", false));
        GAMEPLAY.setGameSpeed(preferences.getInteger(
            "gameplay.gameSpeed",
            GameplaySettings.DEFAULT_GAME_SPEED
        ));
        loaded = true;
        if (!preferences.contains("audio.master")) {
            save();
        }
    }

    public static void save() {
        if (Gdx.app == null) {
            return;
        }
        Preferences preferences = Gdx.app.getPreferences(PREFS_NAME);
        AudioSettings audio = AudioManager.settings();
        preferences.putFloat("audio.master", audio.masterVolume());
        preferences.putFloat("audio.music", audio.musicVolume());
        preferences.putFloat("audio.sfx", audio.sfxVolume());
        preferences.putFloat("audio.voice", audio.voiceVolume());
        preferences.putFloat("audio.ui", audio.uiVolume());
        preferences.putBoolean("audio.masterMuted", audio.muted());
        for (AudioCategory category : AudioCategory.values()) {
            preferences.putBoolean("audio.mute." + category.name(), audio.isCategoryMuted(category));
        }
        preferences.putBoolean("gameplay.notifications", GAMEPLAY.notificationsEnabled());
        preferences.putBoolean("gameplay.screenShake", GAMEPLAY.screenShakeEnabled());
        preferences.putBoolean("gameplay.debug", GAMEPLAY.debugMode());
        preferences.putBoolean("gameplay.gridOverlay", GAMEPLAY.gridOverlayEnabled());
        preferences.putInteger("gameplay.gameSpeed", GAMEPLAY.gameSpeed());
        preferences.flush();
    }

    public static GameplaySettings gameplay() {
        ensureLoaded();
        return GAMEPLAY;
    }

    public static AudioSettings audio() {
        ensureLoaded();
        return AudioManager.settings();
    }

    private static void ensureLoaded() {
        if (!loaded) {
            load();
        }
    }
}

package game.audio;

public final class MenuMusic {
    private MenuMusic() {
    }

    public static void ensurePlaying() {
        ensurePlaying(AudioId.MENU_MUSIC);
    }

    public static void ensurePlaying(AudioId id) {
        AudioId track = id != null ? id : AudioId.MENU_MUSIC;
        AudioManager.stopGameplayAudio();
        AudioManager.play(track, MusicTransition.fade(0.35f));
    }
}

package game.audio;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AudioIdTest {

    @Test
    void waveStingersStreamAndDoNotLoop() {
        assertTrue(AudioId.EGYPT_FIRST_WAVE.definition().streaming());
        assertFalse(AudioId.EGYPT_FIRST_WAVE.definition().looping());
        assertTrue(AudioId.BEACH_MIDWAVE_B.definition().streaming());
        assertTrue(AudioId.MINIGAME_WAVES.definition().streaming());
    }

    @Test
    void chooseSeedsAndLevelThemesLoop() {
        assertTrue(AudioId.EGYPT_CHOOSE_SEEDS.definition().looping());
        assertTrue(AudioId.EGYPT_LEVEL.definition().looping());
        assertTrue(AudioId.MENU_MUSIC.definition().looping());
        assertEquals("audio/music/title.mp3", AudioId.MENU_MUSIC.definition().path());
    }

    @Test
    void victoryAndGameOverDoNotLoop() {
        assertFalse(AudioId.EGYPT_VICTORY.definition().looping());
        assertFalse(AudioId.EGYPT_GAME_OVER.definition().looping());
        assertFalse(AudioId.MINIGAME_VICTORY.definition().looping());
    }
}

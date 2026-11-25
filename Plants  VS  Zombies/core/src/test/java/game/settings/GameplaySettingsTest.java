package game.settings;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameplaySettingsTest {
    @Test
    void defaultsMatchPauseMenuExpectations() {
        GameplaySettings settings = new GameplaySettings();
        assertFalse(settings.gridOverlayEnabled());
        assertEquals(GameplaySettings.DEFAULT_GAME_SPEED, settings.gameSpeed());
        assertEquals(1f, settings.gameSpeedMultiplier());
    }

    @Test
    void gameSpeedClampsToOneThroughThree() {
        GameplaySettings settings = new GameplaySettings();
        settings.setGameSpeed(0);
        assertEquals(1, settings.gameSpeed());
        settings.setGameSpeed(99);
        assertEquals(3, settings.gameSpeed());
        settings.setGameSpeed(2);
        assertEquals(2, settings.gameSpeed());
        assertEquals(2f, settings.gameSpeedMultiplier());
    }

    @Test
    void gridOverlayTogglePersistsInMemory() {
        GameplaySettings settings = new GameplaySettings();
        settings.setGridOverlayEnabled(true);
        assertTrue(settings.gridOverlayEnabled());
        settings.setGridOverlayEnabled(false);
        assertFalse(settings.gridOverlayEnabled());
    }
}

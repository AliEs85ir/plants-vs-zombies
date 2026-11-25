package app.view.mainmenu.settings;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class SettingsMenuConfigTest {
    @Test
    void sanitizeFillsMissingSectionOrderAndKeepsDifficultyRange() {
        SettingsMenuConfig config = new SettingsMenuConfig().sanitize();
        assertEquals(3, config.layout.orderedSections().size());
        assertEquals("difficulty", config.layout.orderedSections().get(0));
        assertEquals("audio", config.layout.orderedSections().get(1));
        assertEquals("debug", config.layout.orderedSections().get(2));
        assertEquals(1f, config.difficulty.sliderMinValue, 0.001f);
        assertEquals(5f, config.difficulty.sliderMaxValue, 0.001f);
        assertEquals(1f, config.difficulty.sliderStep, 0.001f);
        assertEquals(2, config.audio.columns);
        assertEquals("Mute", config.audio.muteLabel);
        assertEquals("purple", config.resetAudio.buttonStyle);
        assertEquals("Reset Audio Settings", config.resetAudio.buttonLabel);
        assertEquals("left", config.difficulty.titleAlignment);
        assertEquals(22f, config.layout.sectionSpacing, 0.001f);
        assertEquals(true, config.layout.separator.enabled);
        assertEquals(35f, config.audio.muteSize, 0.001f);
        assertEquals(38f, config.debug.checkboxSize, 0.001f);
        assertEquals(4f, config.audio.titleSliderGap, 0.001f);
        assertEquals(210f, config.audio.sliderWidth, 0.001f);
        assertEquals(1.09f, config.audio.titleFontRowSize, 0.001f);
    }

    @Test
    void blankValuesAreReplacedWithNamedDefaults() {
        SettingsMenuConfig config = new SettingsMenuConfig();
        config.difficulty.titleText = " ";
        config.audio.musicTitle = "";
        config.debug.checkboxLabel = null;
        config.resetAudio.buttonLabel = "  ";
        config.layout.sectionOrder.clear();
        config.sanitize();
        assertEquals("Game Difficulty", config.difficulty.titleText);
        assertEquals("Music", config.audio.musicTitle);
        assertEquals("Enable debug overlay", config.debug.checkboxLabel);
        assertEquals("Reset Audio Settings", config.resetAudio.buttonLabel);
        assertEquals("Level 3", config.difficulty.formattedValue(3));
        assertFalse(config.layout.orderedSections().contains("resetAudio"));
    }
}

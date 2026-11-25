package app.view.mainmenu.settings;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Align;
import game.audio.AudioCategory;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SettingsMenuConfig {
    public LayoutConfig layout = new LayoutConfig();
    public DifficultyConfig difficulty = new DifficultyConfig();
    public AudioConfig audio = new AudioConfig();
    public DebugConfig debug = new DebugConfig();
    public ResetAudioConfig resetAudio = new ResetAudioConfig();

    public SettingsMenuConfig sanitize() {
        if (layout == null) {
            layout = new LayoutConfig();
        }
        if (difficulty == null) {
            difficulty = new DifficultyConfig();
        }
        if (audio == null) {
            audio = new AudioConfig();
        }
        if (debug == null) {
            debug = new DebugConfig();
        }
        if (resetAudio == null) {
            resetAudio = new ResetAudioConfig();
        }
        layout.sanitize();
        difficulty.sanitize();
        audio.sanitize();
        debug.sanitize();
        resetAudio.sanitize();
        return this;
    }

    public static class LayoutConfig {
        public float contentWidth = 920f;
        public String contentAlignment = "top";
        public float paddingLeft = 36f;
        public float paddingRight = 36f;
        public float paddingTop = 8f;
        public float paddingBottom = 16f;
        public float offsetX;
        public float offsetY;
        public float sectionSpacing = 22f;
        public SeparatorConfig separator = new SeparatorConfig();
        public ArrayList<String> sectionOrder = new ArrayList<>();

        public void sanitize() {
            if (contentWidth <= 0f) {
                contentWidth = 920f;
            }
            if (contentAlignment == null || contentAlignment.isBlank()) {
                contentAlignment = "top";
            }
            if (sectionSpacing < 0f) {
                sectionSpacing = 22f;
            }
            if (separator == null) {
                separator = new SeparatorConfig();
            }
            separator.sanitize();
            if (sectionOrder == null) {
                sectionOrder = new ArrayList<>();
            }
            sectionOrder.removeIf(value -> value == null || value.isBlank());
            if (sectionOrder.isEmpty()) {
                sectionOrder.add("difficulty");
                sectionOrder.add("audio");
                sectionOrder.add("debug");
            }
        }

        public List<String> orderedSections() {
            return List.copyOf(sectionOrder);
        }
    }

    public static class SeparatorConfig {
        public boolean enabled = true;
        public float height = 1f;
        public String color = "FFFFFF40";
        public float topPadding = 14f;
        public float bottomPadding = 14f;

        public void sanitize() {
            if (height < 0f) {
                height = 1f;
            }
            if (color == null || color.isBlank()) {
                color = "FFFFFF40";
            }
            if (topPadding < 0f) {
                topPadding = 14f;
            }
            if (bottomPadding < 0f) {
                bottomPadding = 14f;
            }
        }
    }

    public static class DifficultyConfig {
        public String titleText = "Game Difficulty";
        public String titleFont = "FBUSV8C5EI_1_outline";
        public String titleFontFallback = "FBUSV8C5EI_1";
        public float titleFontSize = 0.87f;
        public String titleColor = "F4E7B0";
        public String titleAlignment = "left";
        public float titleOffsetX;
        public float titleOffsetY;
        public float titleSliderGap = 8f;
        public float sliderWidth = 420f;
        public float sliderHeight = 28f;
        public float sliderMinValue = 1f;
        public float sliderMaxValue = 5f;
        public float sliderStep = 1f;
        public float sliderDefaultValue = 3f;
        public String sliderStyle = "default-horizontal";
        public boolean sliderUseFlatStyle = true;
        public String sliderTrackEmptyColor = "1414148C";
        public String sliderTrackFilledColor = "E6B83D";
        public String sliderKnobColor = "FFF4D1";
        public String sliderColor = "FFFFFF";
        public float sliderTrackHeight = 8f;
        public float sliderKnobSize = 18f;
        public float sliderOffsetX;
        public float sliderOffsetY;
        public boolean valueLabelVisible = true;
        public String valueLabelFont = "BRIANNETOD";
        public String valueLabelFontFallback = "BRIANNETOD";
        public float valueLabelFontSize = 0.90f;
        public String valueLabelColor = "F2E7C4";
        public String valueLabelFormat = "Level {value}";
        public float valueLabelGap = 10f;
        public String hoverColor = "FFF4C0";

        public void sanitize() {
            titleText = text(titleText, "Game Difficulty");
            titleFont = text(titleFont, "FBUSV8C5EI_1_outline");
            titleFontFallback = text(titleFontFallback, "FBUSV8C5EI_1");
            if (titleFontSize <= 0f) {
                titleFontSize = 0.87f;
            }
            titleColor = text(titleColor, "F4E7B0");
            titleAlignment = text(titleAlignment, "left");
            if (titleSliderGap < 0f) {
                titleSliderGap = 8f;
            }
            if (sliderWidth <= 0f) {
                sliderWidth = 420f;
            }
            if (sliderHeight <= 0f) {
                sliderHeight = 28f;
            }
            if (sliderMaxValue <= sliderMinValue) {
                sliderMinValue = 1f;
                sliderMaxValue = 5f;
            }
            if (sliderStep <= 0f) {
                sliderStep = 1f;
            }
            sliderDefaultValue = clamp(sliderDefaultValue, sliderMinValue, sliderMaxValue);
            sliderStyle = text(sliderStyle, "default-horizontal");
            sliderTrackEmptyColor = text(sliderTrackEmptyColor, "1414148C");
            sliderTrackFilledColor = text(sliderTrackFilledColor, "E6B83D");
            sliderKnobColor = text(sliderKnobColor, "FFF4D1");
            sliderColor = text(sliderColor, "FFFFFF");
            if (sliderTrackHeight <= 0f) {
                sliderTrackHeight = 8f;
            }
            if (sliderKnobSize <= 0f) {
                sliderKnobSize = 18f;
            }
            valueLabelFont = text(valueLabelFont, "BRIANNETOD");
            valueLabelFontFallback = text(valueLabelFontFallback, "BRIANNETOD");
            if (valueLabelFontSize <= 0f) {
                valueLabelFontSize = 0.90f;
            }
            valueLabelColor = text(valueLabelColor, "F2E7C4");
            valueLabelFormat = text(valueLabelFormat, "Level {value}");
            hoverColor = text(hoverColor, "FFF4C0");
        }

        public String formattedValue(int value) {
            return valueLabelFormat.replace("{value}", String.valueOf(value));
        }
    }

    public static class AudioConfig {
        public String titleText = "Audio Settings";
        public String titleFont = "FBUSV8C5EI_1_outline";
        public String titleFontFallback = "FBUSV8C5EI_1";
        public float titleFontSize = 0.87f;
        public String titleColor = "F4E7B0";
        public String titleAlignment = "left";
        public float titleOffsetX;
        public float titleOffsetY;
        public float titleBottomPadding = 8f;
        public String musicTitle = "Music";
        public String sfxTitle = "SFX";
        public String voiceTitle = "Voice";
        public String uiTitle = "UI";
        public int columns = 2;
        public float columnSpacing = 28f;
        public float rowHeight = 40f;
        public float rowSpacing = 10f;
        public float titleWidth = 96f;
        public String titleFontRow = "BRIANNETOD";
        public String titleFontRowFallback = "BRIANNETOD";
        public float titleFontRowSize = 1.09f;
        public String titleColorRow = "F2E7C4";
        public String titleAlignmentRow = "left";
        public float titleSliderGap = 4f;
        public float sliderWidth = 210f;
        public float sliderHeight = 24f;
        public float sliderMinValue;
        public float sliderMaxValue = 1f;
        public float sliderStep = 0.01f;
        public String sliderStyle = "default-horizontal";
        public boolean sliderUseFlatStyle = true;
        public String sliderTrackEmptyColor = "1414148C";
        public String sliderTrackFilledColor = "E6B83D";
        public String sliderKnobColor = "FFF4D1";
        public String sliderColor = "FFFFFF";
        public float sliderTrackHeight = 7f;
        public float sliderKnobSize = 16f;
        public float sliderMuteGap = 10f;
        public float muteSize = 35f;
        public String muteLabel = "Mute";
        public String muteLabelFont = "BRIANNETOD";
        public float muteLabelFontSize = 0.78f;
        public String muteLabelColor = "E4DCC4";
        public String muteStyle = "default";
        public String muteHoverColor = "FFF4C0";
        public String hoverColor = "FFF4C0";
        public float resetTopPadding = 12f;

        public void sanitize() {
            titleText = text(titleText, "Audio Settings");
            titleFont = text(titleFont, "FBUSV8C5EI_1_outline");
            titleFontFallback = text(titleFontFallback, "FBUSV8C5EI_1");
            if (titleFontSize <= 0f) {
                titleFontSize = 0.87f;
            }
            titleColor = text(titleColor, "F4E7B0");
            titleAlignment = text(titleAlignment, "left");
            if (titleBottomPadding < 0f) {
                titleBottomPadding = 8f;
            }
            musicTitle = text(musicTitle, "Music");
            sfxTitle = text(sfxTitle, "SFX");
            voiceTitle = text(voiceTitle, "Voice");
            uiTitle = text(uiTitle, "UI");
            if (columns <= 0) {
                columns = 2;
            }
            if (columnSpacing < 0f) {
                columnSpacing = 28f;
            }
            if (rowHeight <= 0f) {
                rowHeight = 40f;
            }
            if (rowSpacing < 0f) {
                rowSpacing = 10f;
            }
            if (titleWidth <= 0f) {
                titleWidth = 96f;
            }
            titleFontRow = text(titleFontRow, "BRIANNETOD");
            titleFontRowFallback = text(titleFontRowFallback, "BRIANNETOD");
            if (titleFontRowSize <= 0f) {
                titleFontRowSize = 1.09f;
            }
            titleColorRow = text(titleColorRow, "F2E7C4");
            titleAlignmentRow = text(titleAlignmentRow, "left");
            if (titleSliderGap < 0f) {
                titleSliderGap = 4f;
            }
            if (sliderWidth <= 0f) {
                sliderWidth = 210f;
            }
            if (sliderHeight <= 0f) {
                sliderHeight = 24f;
            }
            if (sliderMaxValue <= sliderMinValue) {
                sliderMinValue = 0f;
                sliderMaxValue = 1f;
            }
            if (sliderStep <= 0f) {
                sliderStep = 0.01f;
            }
            sliderStyle = text(sliderStyle, "default-horizontal");
            sliderTrackEmptyColor = text(sliderTrackEmptyColor, "1414148C");
            sliderTrackFilledColor = text(sliderTrackFilledColor, "E6B83D");
            sliderKnobColor = text(sliderKnobColor, "FFF4D1");
            sliderColor = text(sliderColor, "FFFFFF");
            if (sliderTrackHeight <= 0f) {
                sliderTrackHeight = 7f;
            }
            if (sliderKnobSize <= 0f) {
                sliderKnobSize = 16f;
            }
            if (sliderMuteGap < 0f) {
                sliderMuteGap = 10f;
            }
            if (muteSize <= 0f) {
                muteSize = 35f;
            }
            muteLabel = text(muteLabel, "Mute");
            muteLabelFont = text(muteLabelFont, "BRIANNETOD");
            if (muteLabelFontSize <= 0f) {
                muteLabelFontSize = 0.78f;
            }
            muteLabelColor = text(muteLabelColor, "E4DCC4");
            muteStyle = text(muteStyle, "default");
            muteHoverColor = text(muteHoverColor, "FFF4C0");
            hoverColor = text(hoverColor, "FFF4C0");
            if (resetTopPadding < 0f) {
                resetTopPadding = 12f;
            }
        }

        public String titleOf(AudioCategory category) {
            if (category == null) {
                return "";
            }
            return switch (category) {
                case MUSIC -> musicTitle;
                case SFX -> sfxTitle;
                case VOICE -> voiceTitle;
                case UI -> uiTitle;
            };
        }
    }

    public static class DebugConfig {
        public String titleText = "Debug Mode";
        public String titleFont = "FBUSV8C5EI_1_outline";
        public String titleFontFallback = "FBUSV8C5EI_1";
        public float titleFontSize = 0.87f;
        public String titleColor = "F4E7B0";
        public String titleAlignment = "left";
        public float titleOffsetX;
        public float titleOffsetY;
        public float titleCheckboxGap = 12f;
        public float checkboxSize = 38f;
        public String checkboxLabel = "Enable debug overlay";
        public String checkboxLabelFont = "BRIANNETOD";
        public float checkboxLabelFontSize = 0.86f;
        public String checkboxLabelColor = "F2E7C4";
        public String checkboxStyle = "default";
        public String checkboxHoverColor = "FFF4C0";

        public void sanitize() {
            titleText = text(titleText, "Debug Mode");
            titleFont = text(titleFont, "FBUSV8C5EI_1_outline");
            titleFontFallback = text(titleFontFallback, "FBUSV8C5EI_1");
            if (titleFontSize <= 0f) {
                titleFontSize = 0.87f;
            }
            titleColor = text(titleColor, "F4E7B0");
            titleAlignment = text(titleAlignment, "left");
            if (titleCheckboxGap < 0f) {
                titleCheckboxGap = 10f;
            }
            if (checkboxSize <= 0f) {
                checkboxSize = 38f;
            }
            checkboxLabel = text(checkboxLabel, "Enable debug overlay");
            checkboxLabelFont = text(checkboxLabelFont, "BRIANNETOD");
            if (checkboxLabelFontSize <= 0f) {
                checkboxLabelFontSize = 0.86f;
            }
            checkboxLabelColor = text(checkboxLabelColor, "F2E7C4");
            checkboxStyle = text(checkboxStyle, "default");
            checkboxHoverColor = text(checkboxHoverColor, "FFF4C0");
        }
    }

    public static class ResetAudioConfig {
        public String buttonLabel = "Reset Audio Settings";
        public String buttonStyle = "purple";
        public float buttonWidth = 224f;
        public float buttonHeight = 32f;
        public float buttonMinWidth = 144f;
        public float buttonPadX = 8f;
        public float buttonPadY = 2f;
        public float buttonLabelScale = 0.72f;
        public float buttonHoverScale = 1.05f;
        public float buttonHoverDuration = 0.10f;
        public String alignment = "left";
        public float offsetX;
        public float offsetY;

        public void sanitize() {
            buttonLabel = text(buttonLabel, "Reset Audio Settings");
            buttonStyle = text(buttonStyle, "purple");
            if (buttonWidth <= 0f) {
                buttonWidth = 224f;
            }
            if (buttonHeight <= 0f) {
                buttonHeight = 32f;
            }
            if (buttonMinWidth <= 0f) {
                buttonMinWidth = 144f;
            }
            if (buttonLabelScale <= 0f) {
                buttonLabelScale = 0.72f;
            }
            if (buttonHoverScale <= 0f) {
                buttonHoverScale = 1.05f;
            }
            if (buttonHoverDuration < 0f) {
                buttonHoverDuration = 0.10f;
            }
            alignment = text(alignment, "left");
        }
    }

    public static Color color(String hex, Color fallback) {
        if (hex == null || hex.isBlank()) {
            return fallback.cpy();
        }
        String value = hex.trim();
        if (value.startsWith("#")) {
            value = value.substring(1);
        }
        try {
            return Color.valueOf(value);
        } catch (Exception exception) {
            return fallback.cpy();
        }
    }

    public static int align(String value, int fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return switch (value.trim().toLowerCase(Locale.ROOT)) {
            case "left", "left_top", "topleft" -> Align.left | Align.top;
            case "right", "right_top", "topright" -> Align.right | Align.top;
            case "center", "top" -> Align.top | Align.center;
            case "bottom" -> Align.bottom | Align.center;
            case "bottom_left", "bottomleft" -> Align.bottom | Align.left;
            case "bottom_right", "bottomright" -> Align.bottom | Align.right;
            default -> fallback;
        };
    }

    public static int textAlign(String value) {
        if (value == null || value.isBlank()) {
            return Align.left;
        }
        return switch (value.trim().toLowerCase(Locale.ROOT)) {
            case "center" -> Align.center;
            case "right" -> Align.right;
            default -> Align.left;
        };
    }

    private static String text(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}

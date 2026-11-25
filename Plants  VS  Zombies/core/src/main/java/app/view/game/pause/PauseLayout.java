package app.view.game.pause;

import app.view.ui.PanelStyle;
import app.view.ui.UiTheme;
import app.view.ui.button.ButtonTheme;
import app.view.ui.text.TextAlign;
import app.view.ui.text.TextStyle;
import com.badlogic.gdx.graphics.Color;

public class PauseLayout {
    public final Spacing spacing = new Spacing();
    public final Size size = new Size();
    public final Overlay overlay = new Overlay();
    public final Animation animation = new Animation();
    public final Header header = new Header();
    public final Stats stats = new Stats();
    public final Audio audio = new Audio();
    public final Toggles toggles = new Toggles();
    public final Buttons buttons = new Buttons();
    public final Backgrounds backgrounds = new Backgrounds();
    public final Text text = new Text();
    public final Icons icons = new Icons();

    public static PauseLayout defaults() {
        return new PauseLayout();
    }

    public static class Spacing {
        public float contentPadding = 16f;
        public float innerPadding = 8f;
        public float tightPadding = 4f;
        public float sectionSpacing = 8f;
        public float headerSpacing = 8f;
        public float bottomPadding = 8f;
        public float separatorHeight = 3f;
        public float separatorGap = 4f;
    }

    public static class Size {
        public float width = 1180f;
        public float height = 0f;
        public float scale = 1f;
        public float headerHeight = 48f;
        public float footerHeight = 42f;
    }

    public static class Overlay {
        public float opacity = 0.78f;
        public Color color = new Color(0f, 0f, 0f, 1f);
    }

    public static class Animation {
        public float fadeIn = 0.18f;
        public float fadeOut = 0.12f;
        public float startScale = 0.96f;
        public float scaleDuration = 0.14f;
    }

    public static class Header {
        public String title = "PAUSED";
        public float height = 48f;
        public float sectionHeaderHeight = 22f;
        public float sectionIconSize = 18f;
        public float sectionIconGap = 6f;
        public float separatorGap = 3f;
        public float paddingBottom = 6f;
    }

    public static class Stats {
        public float fieldHeight = 28f;
        public float fieldSpacing = 8f;
        public float iconSize = 16f;
        public float iconTextGap = 4f;
        public float labelValueGap = 4f;
        public float chipPadX = 8f;
        public String plantsIcon = "IMAGE_UI_SEASONS_UNCOMPRESSED_PVZ2_SEASONS_UIASSET_ICON_SUN";
        public String waveIcon = "IMAGE_UI_PERKS_RIFT_ICON_TIMEBONUS";
        public String zombiesIcon = "IMAGE_NOTES_ZOMBIENOTE_DARK";
        public String plantsUsedIcon = "IMAGE_UI_HUD_INGAME_NOTIFICATION_ICON";
        public String mowersIcon = "IMAGE_UI_HUD_INGAME_NOTIFICATION_ICON";
    }

    public static class Audio {
        public int columns = 2;
        public float sliderWidth = 220f;
        public float sliderHeight = 16f;
        public float trackHeight = 6f;
        public float knobSize = 14f;
        public float rowHeight = 28f;
        public float rowSpacing = 8f;
        public float columnSpacing = 28f;
        public float labelSliderGap = 8f;
        public float checkboxSize = 22f;
        public float muteGap = 6f;
        public float labelWidth = 72f;
        public boolean flatSlider = true;
        public Color hoverColor = new Color(1.12f, 1.10f, 0.95f, 1f);
        public Color trackEmptyColor = new Color(0.08f, 0.08f, 0.08f, 0.55f);
        public Color trackFilledColor = new Color(0.90f, 0.72f, 0.24f, 1f);
        public Color knobColor = new Color(1f, 0.96f, 0.82f, 1f);
        public String sliderStyle = "default-horizontal";
        public String checkboxStyle = "default";
        public String trackEmpty = null;
        public String trackFilled = null;
        public String knob = null;
        public String knobOver = null;
        public String checkboxOn = null;
        public String checkboxOff = null;
        public String checkboxOnOver = null;
        public String checkboxOffOver = null;
        public String muteLabel = "Mute";
        public String resetLabel = "Reset Default";
        public Color sliderColor = new Color(1f, 1f, 1f, 1f);
    }

    public static class Toggles {
        public float checkboxSize = 22f;
        public float rowHeight = 28f;
        public float itemSpacing = 28f;
        public float labelGap = 6f;
        public float speedRowGap = 10f;
        public float speedLabelGap = 8f;
        public float speedSliderWidth = 180f;
        public float speedSliderHeight = 16f;
        public float speedValueGap = 8f;
        public Color hoverColor = new Color(1.12f, 1.10f, 0.95f, 1f);
        public String checkboxStyle = "default";
        public String checkboxOn = null;
        public String checkboxOff = null;
        public String checkboxOnOver = null;
        public String checkboxOffOver = null;
    }

    public static class Buttons {
        public String continueStyle = UiTheme.BTN_GREEN;
        public String saveExitStyle = UiTheme.BTN_BROWN;
        public String restartStyle = UiTheme.BTN_PURPLE;
        public String resetStyle = UiTheme.BTN_GREEN_SMALL;
        public final ButtonTheme actions = new ButtonTheme();
        public final ButtonTheme reset = new ButtonTheme();

        public Buttons() {
            actions.width = 168f;
            actions.height = 42f;
            actions.minWidth = 120f;
            actions.gap = 12f;
            actions.padX = 6f;
            actions.padY = 1f;
            actions.labelScale = 0.78f;
            actions.hoverScale = 1.05f;
            actions.hoverDuration = 0.10f;

            reset.width = 140f;
            reset.height = 32f;
            reset.minWidth = 100f;
            reset.gap = 0f;
            reset.padX = 6f;
            reset.padY = 1f;
            reset.labelScale = 0.74f;
        }
    }

    public static class Backgrounds {
        public final PanelStyle popupRoot = PanelStyle.of(UiTheme.BG_DIALOG);
        public final PanelStyle header = PanelStyle.of(UiTheme.BG_RIBBON, UiTheme.BG_TAB);
        public final PanelStyle section = PanelStyle.of(UiTheme.BG_GUARANTEE, UiTheme.BG_TAB);
        public final PanelStyle statField = PanelStyle.of(UiTheme.BG_REWARD, UiTheme.BG_TAB);
        public final PanelStyle separator = PanelStyle.of(UiTheme.BG_COST, UiTheme.BG_TAB);
        public final PanelStyle divider = PanelStyle.of(UiTheme.BG_COST, UiTheme.BG_TAB);
    }

    public static class Icons {
        public String header = "IMAGE_UI_PAUSEMENU_STAR_GOAL_BORDER";
        public String status = "IMAGE_UI_SEASONS_UNCOMPRESSED_PVZ2_SEASONS_UIASSET_ICON_SUN";
        public String audio = "IMAGE_UI_HUD_INGAME_NOTIFICATION_ICON";
        public String gameplay = "IMAGE_UI_HUD_INGAME_NOTIFICATION_ICON";
    }

    public static class Text {
        public final TextStyle header = TextStyle.of(UiTheme.LBL_BIG_OUTLINE)
            .align(TextAlign.CENTER).color(UiTheme.ACCENT);
        public final TextStyle sectionTitle = TextStyle.of(UiTheme.LBL_MEDIUM_OUTLINE)
            .align(TextAlign.LEFT).color(UiTheme.ACCENT).scale(0.84f);
        public final TextStyle statLabel = TextStyle.of(UiTheme.LBL_SECONDARY)
            .align(TextAlign.LEFT).color(UiTheme.TEXT_SECONDARY).scale(0.72f);
        public final TextStyle statValue = TextStyle.of(UiTheme.LBL_MEDIUM_OUTLINE)
            .align(TextAlign.LEFT).color(UiTheme.TEXT_STAT).scale(0.80f);
        public final TextStyle channel = TextStyle.of(UiTheme.LBL_DEFAULT)
            .align(TextAlign.LEFT).color(UiTheme.TEXT_PRIMARY).scale(0.86f);
        public final TextStyle mute = TextStyle.of(UiTheme.LBL_SECONDARY)
            .align(TextAlign.LEFT).color(UiTheme.TEXT_SECONDARY).scale(0.72f);
        public final TextStyle toggle = TextStyle.of(UiTheme.LBL_DEFAULT)
            .align(TextAlign.LEFT).color(UiTheme.TEXT_PRIMARY).scale(0.90f);
        public final TextStyle error = TextStyle.of(UiTheme.LBL_SECONDARY)
            .align(TextAlign.CENTER).color(UiTheme.TEXT_ERROR).wrap(true).scale(0.82f);
        public Color infoColor = UiTheme.TEXT_STAT;
    }

    public PauseLayout withSize(float width, float height) {
        size.width = width;
        size.height = height;
        return this;
    }

    public PauseLayout withScale(float scale) {
        size.scale = scale <= 0f ? 1f : scale;
        return this;
    }

    public PauseLayout withOverlayOpacity(float opacity) {
        overlay.opacity = Math.max(0f, Math.min(1f, opacity));
        return this;
    }
}


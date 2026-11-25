package app.view.game.result;

import app.view.game.pause.PauseLayout;
import app.view.ui.UiTheme;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Align;

public final class ResultLayout {
    public final PauseLayout pause = PauseLayout.defaults();
    public final Stats stats = new Stats();
    public ResultPopupConfig.Variant texts = new ResultPopupConfig.Variant();
    public float panelWidth = 960f;
    public float panelHeight = 0f;
    public float cardScale = 0.50f;
    public int plantColumns = 6;
    public int zombieColumns = 5;
    public float dropdownBodyPad = 8f;
    public float dropdownGap = 8f;
    public float dropdownHeaderHeight = 28f;
    public float dropdownContentMaxHeight = 200f;
    public float cardPad = 4f;
    public float rewardCardScale = 0.425f;
    public float captionPadTop = 2f;
    public float questRowGap = 6f;
    public float rewardChipPadX = 10f;
    public float rewardChipPadY = 6f;
    public float rewardCardGap = 8f;
    public String dropdownCountFormat = "{title}  ({count})";
    public String dropdownExpandedMark = "  ▴";
    public String dropdownCollapsedMark = "  ▾";

    public static ResultLayout win() {
        return ResultPopupConfigLoader.get().toLayout(true);
    }

    public static ResultLayout lose() {
        return ResultPopupConfigLoader.get().toLayout(false);
    }

    public static final class Stats {
        public float iconBox = 28f;
        public float chipHeight = 54f;
        public float chipSpacing = 10f;
        public float chipPadX = 10f;
        public float chipPadY = 6f;
        public float iconTextGap = 8f;
        public float labelValueGap = 2f;
        public float labelScale = 0.70f;
        public float valueScale = 0.88f;
        public int labelAlign = Align.left;
        public int valueAlign = Align.left;
        public Color labelColor = UiTheme.TEXT_SECONDARY.cpy();
        public Color valueColor = UiTheme.TEXT_STAT.cpy();
        public String zombiesIcon = "IMAGE_NOTES_ZOMBIENOTE_DARK";
        public String plantsIcon = "IMAGE_UI_HUD_INGAME_NOTIFICATION_ICON";
        public String sunsIcon = "IMAGE_UI_SEASONS_UNCOMPRESSED_PVZ2_SEASONS_UIASSET_ICON_SUN";
        public String timeIcon = "IMAGE_UI_PERKS_RIFT_ICON_TIMEBONUS";
        public String zombiesLabel = "Zombies Killed";
        public String plantsLabel = "Plants Planted";
        public String sunsLabel = "Suns Collected";
        public String timeLabel = "Play Time";
    }
}

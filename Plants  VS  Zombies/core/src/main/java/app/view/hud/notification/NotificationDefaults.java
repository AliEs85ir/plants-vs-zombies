package app.view.hud.notification;

import app.enums.asset.GameIcon;
import app.view.ui.UiColor;
import game.component.AssetManager;

public final class NotificationDefaults {
    public static final GameIcon ICON = GameIcon.IMAGE_NOTES_ZOMBIENOTE_DARK;
    public static final float ICON_SCALE = 0.8f;
    public static final IconPlacement ICON_PLACEMENT = IconPlacement.LEFT;
    public static final String FONT = AssetManager.DEFAULT_FONT;
    public static final TextSize TEXT_SIZE = TextSize.MEDIUM;
    public static final int LINE_COUNT = 1;
    public static final NotificationSize SIZE = NotificationSize.MEDIUM;
    public static final NotificationAnchor ANCHOR = NotificationAnchor.BOTTOM_CENTER;
    public static final NotificationPriority PRIORITY = NotificationPriority.NORMAL;
    public static final UiColor COLOR = UiColor.DARK_RED;
    public static final float PAD_LEFT = 118f;
    public static final float PAD_BOTTOM = 280f;
    public static final float PAD_RIGHT = 28f;
    public static final float PAD_TOP = 72f;
    public static final float STACK_SPACING = 12f;
    public static final int MAX_VISIBLE = 4;
    public static final float LAYOUT_MOVE_DURATION = 0.28f;
    public static final float INNER_PADDING = 12f;
    public static final float ICON_TEXT_SPACING = 10f;
    public static final DurationPolicy DURATION_POLICY = new WordCountDurationPolicy();

    private NotificationDefaults() {}
}

package app.view.mainmenu.title;

import app.view.mainmenu.config.MainMenuConfig;
import com.badlogic.gdx.utils.Align;

public final class TabTitleLayout {
    private TabTitleLayout() {
    }

    public static final class Placement {
        public float x;
        public float y;
        public float width;
        public float height;
    }

    public static Placement place(
        float areaWidth,
        float areaHeight,
        MainMenuConfig.TabTitleLayoutConfig layout,
        MainMenuConfig.TabTitleTabConfig tab
    ) {
        Placement placement = new Placement();
        if (layout == null) {
            return placement;
        }
        float scale = layout.titleScale;
        if (tab != null && tab.titleScale > 0f) {
            scale = tab.titleScale;
        }
        float width = layout.titleWidth;
        float height = layout.titleHeight;
        if (tab != null) {
            if (tab.titleWidth > 0f) {
                width = tab.titleWidth;
            }
            if (tab.titleHeight > 0f) {
                height = tab.titleHeight;
            }
        }
        placement.width = Math.max(1f, width * scale);
        placement.height = Math.max(1f, height * scale);
        float offsetX = layout.titleOffsetX + (tab == null ? 0f : tab.titleOffsetX);
        float offsetY = layout.titleOffsetY + (tab == null ? 0f : tab.titleOffsetY);
        int align = align(layout.titleAlignment);
        if ((align & Align.right) != 0) {
            placement.x = areaWidth - placement.width;
        } else if ((align & Align.left) != 0) {
            placement.x = 0f;
        } else {
            placement.x = (areaWidth - placement.width) * 0.5f;
        }
        placement.x += offsetX;
        placement.y = areaHeight - layout.titleTopPadding - placement.height + offsetY;
        return placement;
    }

    private static int align(String value) {
        if (value == null || value.isBlank()) {
            return Align.top | Align.center;
        }
        return switch (value.trim().toLowerCase()) {
            case "left", "left_top", "topleft" -> Align.top | Align.left;
            case "right", "right_top", "topright" -> Align.top | Align.right;
            default -> Align.top | Align.center;
        };
    }
}

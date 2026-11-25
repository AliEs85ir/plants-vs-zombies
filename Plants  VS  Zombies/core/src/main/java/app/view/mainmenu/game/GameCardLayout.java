package app.view.mainmenu.game;

import app.view.mainmenu.config.MainMenuConfig;

public final class GameCardLayout {
    private GameCardLayout() {
    }

    public static final class Placement {
        public float x;
        public float y;
        public float width;
        public float height;
    }

    public static Placement[] place(
        int count,
        float areaWidth,
        float areaHeight,
        MainMenuConfig.GameMenuLayoutConfig layout
    ) {
        int n = Math.max(0, count);
        Placement[] out = new Placement[n];
        if (n == 0 || layout == null) {
            return out;
        }
        float width = layout.drawWidth();
        float height = layout.drawHeight();
        float stepY = layout.stepY();
        float x = originX(areaWidth, width, layout) + layout.horizontalOffset;
        float firstY = layout.firstCardY + layout.verticalOffset;
        if (areaHeight > 0f) {
            firstY = Math.max(0f, Math.min(firstY, Math.max(0f, areaHeight - height)));
        }
        for (int i = 0; i < n; i++) {
            Placement placement = new Placement();
            placement.width = width;
            placement.height = height;
            placement.x = x;
            placement.y = firstY - i * stepY;
            out[i] = placement;
        }
        return out;
    }

    private static float originX(float areaWidth, float cardWidth, MainMenuConfig.GameMenuLayoutConfig layout) {
        int align = layout.align();
        if (align == 0) {
            return layout.firstCardX;
        }
        if (align == 2) {
            return areaWidth - cardWidth + layout.firstCardX;
        }
        return (areaWidth - cardWidth) * 0.5f + layout.firstCardX;
    }
}

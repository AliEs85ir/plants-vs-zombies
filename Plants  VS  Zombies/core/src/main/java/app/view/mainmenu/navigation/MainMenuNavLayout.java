package app.view.mainmenu.navigation;

import app.view.mainmenu.config.MainMenuConfig;

public final class MainMenuNavLayout {
    private MainMenuNavLayout() {
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
        MainMenuConfig.NavigationLayoutConfig layout
    ) {
        int n = Math.max(0, count);
        Placement[] out = new Placement[n];
        if (n == 0 || layout == null) {
            return out;
        }
        float startX = resolveX(layout.startX, areaWidth);
        float endX = resolveX(layout.endX, areaWidth);
        if (endX < startX) {
            float swap = startX;
            startX = endX;
            endX = swap;
        }
        float width = layout.drawWidth();
        float height = layout.drawHeight();
        float spacing = layout.iconSpacing;
        if (spacing <= 0f && n > 1) {
            spacing = ((endX - startX) - n * width) / (n - 1);
        }
        float clusterWidth = n * width + Math.max(0, n - 1) * spacing;
        float originX = originX(startX, endX, clusterWidth, layout.align()) + layout.horizontalOffset;
        float y = layout.bottomOffset + layout.verticalOffset;
        if (areaHeight > 0f) {
            y = Math.max(0f, Math.min(y, Math.max(0f, areaHeight - height)));
        }
        for (int i = 0; i < n; i++) {
            Placement placement = new Placement();
            placement.width = width;
            placement.height = height;
            placement.x = originX + i * (width + spacing);
            placement.y = y;
            out[i] = placement;
        }
        return out;
    }

    private static float resolveX(float value, float areaWidth) {
        if (areaWidth > 0f && value >= 0f && value <= 1f) {
            return value * areaWidth;
        }
        return value;
    }

    private static float originX(float startX, float endX, float clusterWidth, int align) {
        float range = endX - startX;
        if (align == 0) {
            return startX;
        }
        if (align == 2) {
            return endX - clusterWidth;
        }
        return startX + (range - clusterWidth) * 0.5f;
    }
}

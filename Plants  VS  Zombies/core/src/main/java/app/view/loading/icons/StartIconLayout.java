package app.view.loading.icons;

import app.view.loading.icons.config.StartIconsConfig;

public final class StartIconLayout {
    private StartIconLayout() {
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
        StartIconsConfig.LayoutConfig layout
    ) {
        int n = Math.max(0, count);
        Placement[] out = new Placement[n];
        if (n == 0 || layout == null) {
            return out;
        }
        int columns = Math.max(1, layout.columns);
        int rows = (n + columns - 1) / columns;
        float width = layout.drawWidth();
        float height = layout.drawHeight();
        float rowGap = layout.spacingY + layout.sectionGap;
        float clusterWidth = columns * width + Math.max(0, columns - 1) * layout.spacingX;
        float clusterHeight = rows * height + Math.max(0, rows - 1) * rowGap;
        float startX = startX(areaWidth, clusterWidth, layout);
        float startY = startY(areaHeight, clusterHeight, layout);
        for (int i = 0; i < n; i++) {
            int column = i % columns;
            int rowFromTop = i / columns;
            Placement placement = new Placement();
            placement.width = width;
            placement.height = height;
            placement.x = startX + column * (width + layout.spacingX);
            placement.y = startY + (rows - 1 - rowFromTop) * (height + rowGap);
            out[i] = placement;
        }
        return out;
    }

    private static float startX(float areaWidth, float clusterWidth, StartIconsConfig.LayoutConfig layout) {
        float left = layout.paddingLeft + layout.offsetX;
        float right = areaWidth - layout.paddingRight - clusterWidth + layout.offsetX;
        int align = layout.align();
        if (align == 0) {
            return left;
        }
        if (align == 2) {
            return right;
        }
        float innerLeft = layout.paddingLeft;
        float innerWidth = areaWidth - layout.paddingLeft - layout.paddingRight;
        return innerLeft + (innerWidth - clusterWidth) * 0.5f + layout.offsetX;
    }

    private static float startY(float areaHeight, float clusterHeight, StartIconsConfig.LayoutConfig layout) {
        if (layout.bottomAnchor()) {
            return layout.paddingBottom + layout.offsetY;
        }
        return areaHeight - layout.paddingTop - clusterHeight + layout.offsetY;
    }
}

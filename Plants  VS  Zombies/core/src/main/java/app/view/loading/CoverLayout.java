package app.view.loading;

public final class CoverLayout {
    private CoverLayout() {
    }

    public static void compute(
        float viewportWidth,
        float viewportHeight,
        float imageWidth,
        float imageHeight,
        float offsetX,
        float offsetY,
        float[] outXywh
    ) {
        float viewW = Math.max(1f, viewportWidth);
        float viewH = Math.max(1f, viewportHeight);
        float imageW = Math.max(1f, imageWidth);
        float imageH = Math.max(1f, imageHeight);
        float scale = Math.max(viewW / imageW, viewH / imageH);
        float drawW = imageW * scale;
        float drawH = imageH * scale;
        outXywh[0] = (viewW - drawW) * 0.5f + offsetX;
        outXywh[1] = (viewH - drawH) * 0.5f + offsetY;
        outXywh[2] = drawW;
        outXywh[3] = drawH;
    }
}

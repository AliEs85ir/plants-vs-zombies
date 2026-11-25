package app.view.loading;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CoverLayoutTest {
    @Test
    void coversSixteenByNineWithoutDistortion() {
        float[] out = new float[4];
        CoverLayout.compute(1280f, 720f, 1920f, 1080f, 0f, 0f, out);
        assertEquals(0f, out[0], 0.01f);
        assertEquals(0f, out[1], 0.01f);
        assertEquals(1280f, out[2], 0.01f);
        assertEquals(720f, out[3], 0.01f);
    }

    @Test
    void cropsWiderViewportWhileKeepingAspect() {
        float[] out = new float[4];
        CoverLayout.compute(1366f, 768f, 1920f, 1080f, 0f, 0f, out);
        float scale = out[2] / 1920f;
        assertEquals(scale, out[3] / 1080f, 0.0001f);
        assertTrue(out[2] >= 1366f - 0.5f);
        assertTrue(out[3] >= 768f - 0.5f);
    }

    @Test
    void appliesConfiguredOffsets() {
        float[] out = new float[4];
        CoverLayout.compute(1280f, 720f, 1920f, 1080f, 12f, -8f, out);
        assertEquals(12f, out[0], 0.01f);
        assertEquals(-8f, out[1], 0.01f);
    }
}

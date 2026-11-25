package app.view.loading;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoadingProgressModelTest {
    @Test
    void neverReachesCompleteBeforeMarkedComplete() {
        LoadingProgressModel model = new LoadingProgressModel();
        model.setActual(1f);
        float displayed = 0f;
        for (int i = 0; i < 120; i++) {
            displayed = model.update(0.016f, 20f, 0.99f);
        }
        assertTrue(displayed <= 0.99f);
        assertFalse(model.isComplete());
    }

    @Test
    void reachesOneOnlyAfterComplete() {
        LoadingProgressModel model = new LoadingProgressModel();
        model.setActual(0.5f);
        model.update(1f, 20f, 0.99f);
        model.markComplete();
        float displayed = 0f;
        for (int i = 0; i < 120; i++) {
            displayed = model.update(0.016f, 20f, 0.99f);
        }
        assertEquals(1f, displayed, 0.001f);
        assertTrue(model.isComplete());
    }
}

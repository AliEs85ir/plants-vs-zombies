package graphics.plant;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlantDamageFlashControllerTest {

    @Test
    void firstDamageEntersStateAndFlashesImmediately() {
        PlantDamageFlashController controller = new PlantDamageFlashController();

        controller.notifyDamaged();

        assertTrue(controller.isDamaged());
        assertTrue(controller.isActive());
        assertEquals(PlantDamageFlashConfig.ALPHA, controller.getOverlayAlpha(), 0.001f);
    }

    @Test
    void damageDuringFlashDoesNotRestartOrResetCadence() {
        PlantDamageFlashController controller = new PlantDamageFlashController();
        controller.notifyDamaged();

        controller.update(PlantDamageFlashConfig.DURATION * 0.5f);
        assertTrue(controller.isActive());

        controller.notifyDamaged();
        controller.update(PlantDamageFlashConfig.DURATION * 0.5f);

        assertTrue(controller.isDamaged());
        assertFalse(controller.isActive());
        assertEquals(0f, controller.getOverlayAlpha(), 0.001f);
    }

    @Test
    void rapidHitsDoNotCreateExtraFlashes() {
        PlantDamageFlashController controller = new PlantDamageFlashController();
        controller.notifyDamaged();

        float step = 0.03f;
        int flashesStarted = 1;
        boolean wasActive = true;
        float elapsed = 0f;
        while (elapsed + step < PlantDamageFlashConfig.INTERVAL) {
            controller.notifyDamaged();
            controller.update(step);
            elapsed += step;
            if (!wasActive && controller.isActive()) {
                flashesStarted++;
            }
            wasActive = controller.isActive();
        }

        assertEquals(1, flashesStarted);
        assertTrue(controller.isDamaged());
        assertFalse(controller.isActive());
    }

    @Test
    void nextFlashStartsOnIntervalWhileStillDamaged() {
        PlantDamageFlashController controller = new PlantDamageFlashController();
        controller.notifyDamaged();

        controller.update(PlantDamageFlashConfig.DURATION);
        assertFalse(controller.isActive());

        controller.notifyDamaged();
        controller.update(PlantDamageFlashConfig.INTERVAL - PlantDamageFlashConfig.DURATION - 0.01f);
        assertFalse(controller.isActive());

        controller.notifyDamaged();
        controller.update(0.02f);

        assertTrue(controller.isActive());
        assertEquals(PlantDamageFlashConfig.ALPHA, controller.getOverlayAlpha(), 0.001f);
    }

    @Test
    void stateEndsAfterTimeoutWithoutDamageAndStopsCadence() {
        PlantDamageFlashController controller = new PlantDamageFlashController();
        controller.notifyDamaged();

        controller.update(PlantDamageFlashConfig.STATE_TIMEOUT);

        assertFalse(controller.isDamaged());
        assertFalse(controller.isActive());
        assertEquals(0f, controller.getOverlayAlpha(), 0.001f);

        controller.update(PlantDamageFlashConfig.INTERVAL);
        assertFalse(controller.isActive());
    }

    @Test
    void newDamageAfterTimeoutStartsAFreshFlash() {
        PlantDamageFlashController controller = new PlantDamageFlashController();
        controller.notifyDamaged();
        controller.update(PlantDamageFlashConfig.STATE_TIMEOUT);

        controller.notifyDamaged();

        assertTrue(controller.isDamaged());
        assertTrue(controller.isActive());
    }
}

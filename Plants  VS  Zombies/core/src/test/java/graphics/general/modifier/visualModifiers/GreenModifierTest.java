package graphics.general.modifier.visualModifiers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GreenModifierTest {

    @Test
    void glowKeepsRunningUntilStopped() {
        GreenModifier modifier = new GreenModifier();
        modifier.update(100f);
        assertFalse(modifier.isFinished());
        modifier.stop();
        assertTrue(modifier.isFinished());
    }
}

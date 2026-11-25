package app.view.greenhouse.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GreenhouseConfigTest {

    @Test
    void overlayButtonsDefaultToShrunkRaisedLayout() {
        GreenhouseConfig config = new GreenhouseConfig().sanitize();
        assertEquals(95f, config.timer.backgroundWidth, 0.01f);
        assertEquals(24f, config.timer.backgroundHeight, 0.01f);
        assertEquals(8f, config.timer.backgroundOffsetY, 0.01f);
        assertEquals(95f, config.actionButton.backgroundWidth, 0.01f);
        assertEquals(24f, config.actionButton.backgroundHeight, 0.01f);
        assertEquals(4f, config.actionButton.relativeToTimerOffsetY, 0.01f);
        assertEquals("bundle_reward_multiplier", config.messages.rewardLabelStyle);
        assertEquals(0f, config.messages.anchorY, 0.01f);
        assertEquals(40f, config.messages.bottomOffset, 0.01f);
    }

    @Test
    void plantVisualScaleRemainsConfigurable() {
        GreenhousePlantVisualsConfig visuals = new GreenhousePlantVisualsConfig().sanitize();
        GreenhousePlantVisualsConfig.PlantVisualConfig def = visuals.defaultPlant;
        assertTrue(def.scale > 0f);
        assertEquals(20f, def.offsetY, 0.01f);
    }
}

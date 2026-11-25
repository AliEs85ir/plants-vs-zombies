package app.view.loading.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoadingScreenConfigTest {
    @Test
    void defaultOverlayUsesRequestedPamClip() {
        LoadingScreenConfig config = new LoadingScreenConfig().sanitize();
        assertEquals("768/INITIAL/EFFECTS/LOAD_ICON_BACK/LOAD_ICON_BACK.PAM", config.inGameOverlay.pamPath);
        assertEquals("animation", config.inGameOverlay.clip);
        assertTrue(config.inGameOverlay.loop);
    }

    @Test
    void negativeOffsetsAreKept() {
        LoadingScreenConfig.SpacingConfig spacing = new LoadingScreenConfig.SpacingConfig();
        spacing.logoToProgressBarOffset = -20f;
        spacing.progressBarToEntryButtonOffset = -4f;
        spacing.entryButtonToTextOffset = 0f;
        spacing.sanitize();
        assertEquals(-20f, spacing.logoToProgressBarOffset);
        assertEquals(-4f, spacing.progressBarToEntryButtonOffset);
        assertEquals(0f, spacing.entryButtonToTextOffset);
    }

    @Test
    void creditsWidthIsIndependentOfProgressBar() {
        LoadingScreenConfig config = new LoadingScreenConfig().sanitize();
        assertEquals(900f, config.credits.maxWidth);
        assertFalse(config.credits.wrap);
        LoadingScreenConfig.CreditsConfig credits = new LoadingScreenConfig.CreditsConfig();
        credits.maxWidth = 0f;
        credits.sanitize();
        assertEquals(900f, credits.maxWidth);
    }

    @Test
    void progressWeightsAreNormalized() {
        LoadingScreenConfig.ProgressConfig progress = new LoadingScreenConfig.ProgressConfig();
        progress.coreAssetsWeight = 2f;
        progress.skinUiWeight = 2f;
        progress.gameDataWeight = 2f;
        progress.audioSettingsWeight = 1f;
        progress.sessionWeight = 1f;
        progress.cursorsWeight = 1f;
        progress.overlayPamWeight = 1f;
        progress.finalizationWeight = 0f;
        progress.sanitize();
        float total = progress.coreAssetsWeight + progress.skinUiWeight + progress.gameDataWeight
            + progress.audioSettingsWeight + progress.sessionWeight + progress.cursorsWeight
            + progress.overlayPamWeight + progress.finalizationWeight;
        assertEquals(1f, total, 0.0001f);
    }
}

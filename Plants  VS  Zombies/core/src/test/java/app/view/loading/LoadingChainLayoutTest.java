package app.view.loading;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoadingChainLayoutTest {
    @Test
    void logoStaysPutWhenProgressOffsetChanges() {
        LoadingChainLayout.Result first = new LoadingChainLayout.Result();
        LoadingChainLayout.Result second = new LoadingChainLayout.Result();
        compute(30f, first);
        compute(5f, second);
        assertEquals(first.logoX, second.logoX, 0.001f);
        assertEquals(first.logoY, second.logoY, 0.001f);
        assertEquals(first.groupHeight, second.groupHeight, 0.001f);
        assertEquals(first.progressY + 25f, second.progressY, 0.001f);
        assertEquals(first.buttonY + 25f, second.buttonY, 0.001f);
        assertEquals(first.textY + 25f, second.textY, 0.001f);
    }

    @Test
    void buttonFollowsProgressBarAndTextFollowsButton() {
        LoadingChainLayout.Result result = new LoadingChainLayout.Result();
        compute(30f, result);
        assertEquals(result.logoY - 30f, result.progressY + result.progressHeight, 0.001f);
        assertEquals(result.progressY - 20f, result.buttonY + result.buttonHeight, 0.001f);
        assertEquals(result.buttonY - 15f, result.textY + result.textHeight, 0.001f);
    }

    @Test
    void negativeOffsetOverlapsLogoWithoutRelocatingIt() {
        LoadingChainLayout.Result result = new LoadingChainLayout.Result();
        compute(-20f, result);
        assertEquals(0f, result.logoY, 0.001f);
        assertTrue(result.progressY + result.progressHeight > result.logoY);
        assertTrue(result.progressY + result.progressHeight < result.logoY + result.logoHeight);
    }

    @Test
    void buttonOffsetDoesNotMoveLogoOrProgressBar() {
        LoadingChainLayout.Result first = new LoadingChainLayout.Result();
        LoadingChainLayout.Result second = new LoadingChainLayout.Result();
        compute(30f, 20f, 15f, 50f, true, first);
        compute(30f, 8f, 15f, 50f, true, second);
        assertEquals(first.logoY, second.logoY, 0.001f);
        assertEquals(first.progressY, second.progressY, 0.001f);
        assertEquals(first.buttonY + 12f, second.buttonY, 0.001f);
        assertEquals(first.textY + 12f, second.textY, 0.001f);
    }

    @Test
    void textOffsetDoesNotMoveLogoProgressOrButton() {
        LoadingChainLayout.Result first = new LoadingChainLayout.Result();
        LoadingChainLayout.Result second = new LoadingChainLayout.Result();
        compute(30f, 20f, 15f, 50f, true, first);
        compute(30f, 20f, 5f, 50f, true, second);
        assertEquals(first.logoY, second.logoY, 0.001f);
        assertEquals(first.progressY, second.progressY, 0.001f);
        assertEquals(first.buttonY, second.buttonY, 0.001f);
        assertEquals(first.textY + 10f, second.textY, 0.001f);
    }

    @Test
    void textAttachesToProgressBarWhenButtonIsHidden() {
        LoadingChainLayout.Result result = new LoadingChainLayout.Result();
        compute(30f, 20f, 15f, 50f, false, result);
        assertEquals(result.progressY - 20f, result.textY + result.textHeight, 0.001f);
    }

    @Test
    void progressBarUsesActualLogoBoundsNotAFixedHeight() {
        LoadingChainLayout.Result shortLogo = new LoadingChainLayout.Result();
        LoadingChainLayout.Result tallLogo = new LoadingChainLayout.Result();
        compute(30f, 20f, 15f, 50f, true, shortLogo);
        compute(30f, 20f, 15f, 80f, true, tallLogo);
        assertEquals(50f, shortLogo.logoHeight, 0.001f);
        assertEquals(80f, tallLogo.logoHeight, 0.001f);
        assertEquals(shortLogo.logoY - 30f, shortLogo.progressY + shortLogo.progressHeight, 0.001f);
        assertEquals(tallLogo.logoY - 30f, tallLogo.progressY + tallLogo.progressHeight, 0.001f);
        assertEquals(shortLogo.groupHeight, shortLogo.logoHeight, 0.001f);
        assertEquals(tallLogo.groupHeight, tallLogo.logoHeight, 0.001f);
    }

    private static void compute(float logoToProgressBarOffset, LoadingChainLayout.Result result) {
        compute(logoToProgressBarOffset, 20f, 15f, 50f, true, result);
    }

    private static void compute(
        float logoToProgressBarOffset,
        float progressBarToEntryButtonOffset,
        float entryButtonToTextOffset,
        float logoHeight,
        boolean buttonVisible,
        LoadingChainLayout.Result result
    ) {
        LoadingChainLayout.compute(
            true, 100f, logoHeight, 0f,
            80f, 10f, 0f, logoToProgressBarOffset,
            buttonVisible, 90f, 40f, 0f, progressBarToEntryButtonOffset,
            80f, 24f, 0f, entryButtonToTextOffset,
            false, 0f, 0f, 0f,
            result
        );
    }
}

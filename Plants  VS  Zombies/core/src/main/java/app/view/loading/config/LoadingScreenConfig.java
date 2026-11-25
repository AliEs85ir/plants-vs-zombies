package app.view.loading.config;

import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LoadingScreenConfig {
    public BackgroundConfig background = new BackgroundConfig();
    public LogoConfig logo = new LogoConfig();
    public ProgressBarConfig progressBar = new ProgressBarConfig();
    public SpacingConfig spacing = new SpacingConfig();
    public GroupConfig group = new GroupConfig();
    public FloatAnimationConfig floatAnimation = new FloatAnimationConfig();
    public FloatingAnimationConfig floatingAnimation = new FloatingAnimationConfig();
    public CreditsConfig credits = new CreditsConfig();
    public EntryButtonConfig entryButton = new EntryButtonConfig();
    public TransitionConfig transition = new TransitionConfig();
    public ProgressConfig progress = new ProgressConfig();
    public ErrorConfig error = new ErrorConfig();
    public InGameOverlayConfig inGameOverlay = new InGameOverlayConfig();

    public LoadingScreenConfig sanitize() {
        if (background == null) {
            background = new BackgroundConfig();
        }
        if (logo == null) {
            logo = new LogoConfig();
        }
        if (progressBar == null) {
            progressBar = new ProgressBarConfig();
        }
        if (spacing == null) {
            spacing = new SpacingConfig();
        }
        if (group == null) {
            group = new GroupConfig();
        }
        if (floatAnimation == null) {
            floatAnimation = new FloatAnimationConfig();
        }
        if (floatingAnimation == null) {
            floatingAnimation = new FloatingAnimationConfig();
        }
        if (credits == null) {
            credits = new CreditsConfig();
        }
        if (entryButton == null) {
            entryButton = new EntryButtonConfig();
        }
        if (transition == null) {
            transition = new TransitionConfig();
        }
        if (progress == null) {
            progress = new ProgressConfig();
        }
        if (error == null) {
            error = new ErrorConfig();
        }
        if (inGameOverlay == null) {
            inGameOverlay = new InGameOverlayConfig();
        }
        background.sanitize();
        logo.sanitize();
        progressBar.sanitize();
        spacing.sanitize();
        credits.sanitize();
        entryButton.sanitize();
        transition.sanitize();
        progress.sanitize();
        error.sanitize();
        inGameOverlay.sanitize();
        floatingAnimation.adopt(floatAnimation);
        return this;
    }

    public static class BackgroundConfig {
        public String path = "menu/bg/start.png";
        public float sourceWidth = 1920f;
        public float sourceHeight = 1080f;
        public String scaleMode = "cover";
        public float offsetX;
        public float offsetY;

        public void sanitize() {
            if (path == null || path.isBlank()) {
                path = "menu/bg/start.png";
            }
            if (sourceWidth <= 0f) {
                sourceWidth = 1920f;
            }
            if (sourceHeight <= 0f) {
                sourceHeight = 1080f;
            }
            if (scaleMode == null || scaleMode.isBlank()) {
                scaleMode = "cover";
            }
        }

        public boolean cover() {
            return "cover".equalsIgnoreCase(scaleMode);
        }
    }

    public static class LogoConfig {
        public String path = "logo/logo.png";
        public float width = 420f;
        public float height = 220f;
        public float scale = 1f;
        public float offsetX;
        public float offsetY;

        public void sanitize() {
            if (path == null || path.isBlank()) {
                path = "logo/logo.png";
            }
            if (width <= 0f) {
                width = 420f;
            }
            if (height <= 0f) {
                height = 220f;
            }
            if (scale <= 0f) {
                scale = 1f;
            }
        }

        public float drawWidth() {
            return width * scale;
        }

        public float drawHeight() {
            return height * scale;
        }
    }

    public static class ProgressBarConfig {
        public String style = "progressbar_default-horizontal";
        public float width = 520f;
        public float height = 28f;
        public float scale = 1f;
        public float offsetX;
        public float fallbackTrackR = 0.05f;
        public float fallbackTrackG = 0.05f;
        public float fallbackTrackB = 0.05f;
        public float fallbackTrackA = 0.72f;
        public float fallbackFillR = 0.90f;
        public float fallbackFillG = 0.72f;
        public float fallbackFillB = 0.24f;
        public float fallbackFillA = 1f;

        public void sanitize() {
            if (style == null || style.isBlank()) {
                style = "progressbar_default-horizontal";
            }
            if (width <= 0f) {
                width = 520f;
            }
            if (height <= 0f) {
                height = 28f;
            }
            if (scale <= 0f) {
                scale = 1f;
            }
        }

        public float drawWidth() {
            return width * scale;
        }

        public float drawHeight() {
            return height * scale;
        }
    }

    public static class SpacingConfig {
        public float logoToProgressBarOffset = 100f;
        public float progressBarToEntryButtonOffset = 22f;
        public float entryButtonToTextOffset = 36f;

        public void sanitize() {
        }
    }

    public static class GroupConfig {
        public float positionX;
        public float positionY = 36f;

        public float resolvedX() {
            return positionX;
        }

        public float resolvedY() {
            return positionY;
        }
    }

    public static class FloatAnimationConfig {
        public boolean enabled = true;
        public float amplitude = 8f;
        public float speed = 1.35f;
    }

    public static class FloatingAnimationConfig {
        public boolean enabled = true;
        public float amplitude = 8f;
        public float speed = 1.35f;

        public void adopt(FloatAnimationConfig legacy) {
            if (legacy == null) {
                return;
            }
            if (enabled && amplitude == 8f && speed == 1.35f) {
                enabled = legacy.enabled;
                amplitude = legacy.amplitude;
                speed = legacy.speed;
            }
        }
    }

    public static class CreditsConfig {
        public List<String> lines = defaultCredits();
        public String font = "BRIANNETOD";
        public float fontScale = 0.82f;
        public String alignment = "center";
        public float lineHeight = 28f;
        public float offsetX;
        public float offsetY;
        public float maxWidth = 900f;
        public boolean wrap = false;

        public void sanitize() {
            if (lines == null || lines.isEmpty()) {
                lines = defaultCredits();
            }
            if (font == null || font.isBlank()) {
                font = "BRIANNETOD";
            }
            if (fontScale <= 0f) {
                fontScale = 0.82f;
            }
            if (alignment == null || alignment.isBlank()) {
                alignment = "center";
            }
            if (lineHeight <= 0f) {
                lineHeight = 28f;
            }
            if (maxWidth <= 0f) {
                maxWidth = 900f;
            }
        }

        public float drawWidth() {
            return maxWidth;
        }

        public String joinedText() {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < lines.size(); i++) {
                if (i > 0) {
                    builder.append('\n');
                }
                builder.append(lines.get(i));
            }
            return builder.toString();
        }

        public int gdxAlign() {
            String value = alignment.toLowerCase(Locale.ROOT);
            if ("left".equals(value)) {
                return Align.left;
            }
            if ("right".equals(value)) {
                return Align.right;
            }
            return Align.center;
        }

        private static List<String> defaultCredits() {
            List<String> values = new ArrayList<>();
            values.add("Made by the Sunflower Team");
            values.add(">> Ali Esmaeili | Mohammadreza Ajdari | Amirreza Najarian");
            return values;
        }
    }

    public static class EntryButtonConfig {
        public String loginPath = "menu/button/login.png";
        public String signUpPath = "menu/button/sign_up.png";
        public float width = 280f;
        public float height = 92f;
        public float scale = 1f;
        public float hoverScale = 1.03f;
        public float pressedScale = 0.97f;
        public float offsetX;
        public float offsetY;
        public float revealDuration = 0.45f;
        public float revealOffsetY = 12f;
        public float revealStartScale = 0.86f;

        public void sanitize() {
            if (loginPath == null || loginPath.isBlank()) {
                loginPath = "menu/button/login.png";
            }
            if (signUpPath == null || signUpPath.isBlank()) {
                signUpPath = "menu/button/sign_up.png";
            }
            if (width <= 0f) {
                width = 280f;
            }
            if (height <= 0f) {
                height = 92f;
            }
            if (scale <= 0f) {
                scale = 1f;
            }
            if (hoverScale <= 0f) {
                hoverScale = 1.03f;
            }
            if (pressedScale <= 0f) {
                pressedScale = 0.97f;
            }
            if (revealDuration < 0f) {
                revealDuration = 0.45f;
            }
            if (revealStartScale <= 0f) {
                revealStartScale = 0.86f;
            }
        }

        public float drawWidth() {
            return width * scale;
        }

        public float drawHeight() {
            return height * scale;
        }
    }

    public static class TransitionConfig {
        public boolean enabled = true;
        public float completionHoldSeconds = 0.35f;
        public float entryButtonRevealDuration = 0.45f;
        public float entryButtonRevealOffsetY = 12f;
        public float creditsCompletionOffsetY = 28f;
        public float fadeDuration = 0.35f;

        public void sanitize() {
            completionHoldSeconds = Math.max(0f, completionHoldSeconds);
            entryButtonRevealDuration = Math.max(0f, entryButtonRevealDuration);
            fadeDuration = Math.max(0f, fadeDuration);
        }
    }

    public static class ProgressConfig {
        public float lerpSpeed = 3.2f;
        public float incompleteCap = 0.99f;
        public float coreAssetsWeight = 0.28f;
        public float skinUiWeight = 0.17f;
        public float gameDataWeight = 0.25f;
        public float audioSettingsWeight = 0.10f;
        public float sessionWeight = 0.06f;
        public float cursorsWeight = 0.04f;
        public float overlayPamWeight = 0.07f;
        public float finalizationWeight = 0.03f;

        public void sanitize() {
            if (lerpSpeed <= 0f) {
                lerpSpeed = 3.2f;
            }
            if (incompleteCap <= 0f || incompleteCap >= 1f) {
                incompleteCap = 0.99f;
            }
            float total = coreAssetsWeight + skinUiWeight + gameDataWeight + audioSettingsWeight
                + sessionWeight + cursorsWeight + overlayPamWeight + finalizationWeight;
            if (total <= 0f) {
                coreAssetsWeight = 0.28f;
                skinUiWeight = 0.17f;
                gameDataWeight = 0.25f;
                audioSettingsWeight = 0.10f;
                sessionWeight = 0.06f;
                cursorsWeight = 0.04f;
                overlayPamWeight = 0.07f;
                finalizationWeight = 0.03f;
                return;
            }
            coreAssetsWeight /= total;
            skinUiWeight /= total;
            gameDataWeight /= total;
            audioSettingsWeight /= total;
            sessionWeight /= total;
            cursorsWeight /= total;
            overlayPamWeight /= total;
            finalizationWeight /= total;
        }
    }

    public static class ErrorConfig {
        public String title = "Startup could not finish";
        public String font = "BRIANNETOD";
        public float fontScale = 0.9f;
        public float padTop = 16f;

        public void sanitize() {
            if (title == null || title.isBlank()) {
                title = "Startup could not finish";
            }
            if (font == null || font.isBlank()) {
                font = "BRIANNETOD";
            }
            if (fontScale <= 0f) {
                fontScale = 0.9f;
            }
        }
    }

    public static class InGameOverlayConfig {
        public String pamPath = "768/INITIAL/EFFECTS/LOAD_ICON_BACK/LOAD_ICON_BACK.PAM";
        public String clip = "animation";
        public boolean loop = true;
        public float width = 180f;
        public float height = 180f;
        public float scale = 1f;
        public float offsetX;
        public float offsetY;

        public void sanitize() {
            if (pamPath == null || pamPath.isBlank()) {
                pamPath = "768/INITIAL/EFFECTS/LOAD_ICON_BACK/LOAD_ICON_BACK.PAM";
            }
            if (clip == null || clip.isBlank()) {
                clip = "animation";
            }
            if (width <= 0f) {
                width = 180f;
            }
            if (height <= 0f) {
                height = 180f;
            }
            if (scale <= 0f) {
                scale = 1f;
            }
        }
    }
}

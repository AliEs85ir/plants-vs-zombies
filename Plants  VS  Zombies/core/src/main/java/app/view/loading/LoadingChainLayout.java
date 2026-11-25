package app.view.loading;

public final class LoadingChainLayout {
    private LoadingChainLayout() {
    }

    public static final class Result {
        public float groupWidth;
        public float groupHeight;
        public float logoX;
        public float logoY;
        public float logoWidth;
        public float logoHeight;
        public float progressX;
        public float progressY;
        public float progressWidth;
        public float progressHeight;
        public float textX;
        public float textY;
        public float textWidth;
        public float textHeight;
        public float buttonX;
        public float buttonY;
        public float buttonWidth;
        public float buttonHeight;
        public float errorX;
        public float errorY;
        public float errorWidth;
        public float errorHeight;
        public boolean logoVisible;
        public boolean buttonVisible;
        public boolean errorVisible;
    }

    public static void compute(
        boolean logoVisible,
        float logoWidth,
        float logoHeight,
        float logoOffsetX,
        float progressWidth,
        float progressHeight,
        float progressOffsetX,
        float logoToProgressBarOffset,
        boolean buttonVisible,
        float buttonWidth,
        float buttonHeight,
        float buttonOffsetX,
        float progressBarToEntryButtonOffset,
        float textWidth,
        float textHeight,
        float textOffsetX,
        float entryButtonToTextOffset,
        boolean errorVisible,
        float errorWidth,
        float errorHeight,
        float errorOffsetY,
        Result out
    ) {
        float logoW = logoVisible ? Math.max(0f, logoWidth) : 0f;
        float logoH = logoVisible ? Math.max(0f, logoHeight) : 0f;
        float barW = Math.max(0f, progressWidth);
        float barH = Math.max(0f, progressHeight);
        float textW = Math.max(0f, textWidth);
        float textH = Math.max(0f, textHeight);
        float buttonW = buttonVisible ? Math.max(0f, buttonWidth) : 0f;
        float buttonH = buttonVisible ? Math.max(0f, buttonHeight) : 0f;
        float errorW = errorVisible ? Math.max(0f, errorWidth) : 0f;
        float errorH = errorVisible ? Math.max(0f, errorHeight) : 0f;

        float width = Math.max(logoW, Math.max(barW, textW));
        if (buttonVisible) {
            width = Math.max(width, buttonW);
        }
        if (errorVisible) {
            width = Math.max(width, errorW);
        }

        out.groupWidth = Math.max(1f, width);
        out.logoVisible = logoVisible;
        out.buttonVisible = buttonVisible;
        out.errorVisible = errorVisible;
        out.logoWidth = logoW;
        out.logoHeight = logoH;
        out.progressWidth = barW;
        out.progressHeight = barH;
        out.textWidth = textW;
        out.textHeight = textH;
        out.buttonWidth = buttonW;
        out.buttonHeight = buttonH;
        out.errorWidth = errorW;
        out.errorHeight = errorH;

        if (logoVisible) {
            out.groupHeight = Math.max(1f, logoH);
            out.logoX = (out.groupWidth - logoW) * 0.5f + logoOffsetX;
            out.logoY = 0f;
            out.progressY = out.logoY - logoToProgressBarOffset - barH;
        } else {
            out.groupHeight = Math.max(1f, barH);
            out.logoX = 0f;
            out.logoY = 0f;
            out.progressY = 0f;
        }
        out.progressX = (out.groupWidth - barW) * 0.5f + progressOffsetX;
        out.buttonX = (out.groupWidth - buttonW) * 0.5f + buttonOffsetX;
        if (buttonVisible) {
            out.buttonY = out.progressY - progressBarToEntryButtonOffset - buttonH;
            out.textY = out.buttonY - entryButtonToTextOffset - textH;
        } else {
            out.buttonY = 0f;
            out.textY = out.progressY - progressBarToEntryButtonOffset - textH;
        }
        out.textX = (out.groupWidth - textW) * 0.5f + textOffsetX;
        out.errorX = (out.groupWidth - errorW) * 0.5f;
        out.errorY = out.textY - errorOffsetY - errorH;
    }
}

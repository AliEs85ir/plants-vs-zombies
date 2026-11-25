package app.view.game.result;

import graphics.general.effects.EffectLayer;

public final class ResultEffectConfig {
    public Placement win = new Placement();
    public Placement lose = new Placement();

    public ResultEffectConfig sanitize() {
        if (win == null) {
            win = new Placement();
        }
        if (lose == null) {
            lose = new Placement();
        }
        win.sanitize("won");
        lose.sanitize("lose");
        return this;
    }

    public Placement placement(boolean isWin) {
        return isWin ? win : lose;
    }

    public static final class Placement {
        public String effectId;
        public float playgroundXFactor = 0.5f;
        public float playgroundYFactor = 0.65f;
        public float worldOffsetX = 0f;
        public float worldOffsetY = 0f;
        public float fallbackX = 640f;
        public float fallbackY = 400f;
        public float scale = 1f;
        public String layer = "UI";
        public float musicFadeSeconds = 0.25f;

        private void sanitize(String defaultEffectId) {
            if (effectId == null || effectId.isBlank()) {
                effectId = defaultEffectId;
            }
            if (scale <= 0f) {
                scale = 1f;
            }
            if (musicFadeSeconds < 0f) {
                musicFadeSeconds = 0.25f;
            }
            if (layer == null || layer.isBlank()) {
                layer = "UI";
            }
        }

        public EffectLayer resolvedLayer() {
            try {
                return EffectLayer.valueOf(layer.trim().toUpperCase());
            } catch (Exception ignored) {
                return EffectLayer.UI;
            }
        }
    }
}

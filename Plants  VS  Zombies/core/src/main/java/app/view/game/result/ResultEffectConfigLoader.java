package app.view.game.result;

public final class ResultEffectConfigLoader {
    public static final String CONFIG_PATH = "ui/result_effects.json";

    private ResultEffectConfigLoader() {
    }

    public static ResultEffectConfig get() {
        return ResultJson.loadCached(CONFIG_PATH, ResultEffectConfig.class, ResultEffectConfig::new)
            .sanitize();
    }
}

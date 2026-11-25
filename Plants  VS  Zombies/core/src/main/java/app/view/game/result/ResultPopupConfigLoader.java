package app.view.game.result;

public final class ResultPopupConfigLoader {
    public static final String CONFIG_PATH = "ui/result_popup.json";

    private ResultPopupConfigLoader() {
    }

    public static ResultPopupConfig get() {
        return ResultJson.loadCached(CONFIG_PATH, ResultPopupConfig.class, ResultPopupConfig::new)
            .sanitize();
    }
}

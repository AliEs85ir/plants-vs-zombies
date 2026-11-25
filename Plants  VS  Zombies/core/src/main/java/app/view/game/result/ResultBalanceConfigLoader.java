package app.view.game.result;

import game.world.levelConditions.result.ResultBalanceConfig;

public final class ResultBalanceConfigLoader {
    public static final String CONFIG_PATH = "ui/result_balance.json";

    private ResultBalanceConfigLoader() {
    }

    public static ResultBalanceConfig get() {
        return ResultJson.loadCached(CONFIG_PATH, ResultBalanceConfig.class, ResultBalanceConfig::new)
            .sanitize();
    }
}

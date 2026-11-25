package game.world.levelConditions.result;

public final class ResultBalanceConfig {
    public int loseSkipDiamondCost = 1000;
    public float popupDelaySeconds = 2.0f;
    public String buyWinLabelFormat = "Buy Win ({cost})";
    public String notEnoughDiamonds = "Not enough Diamonds.";
    public String notEnoughDiamondsFormat = "Not enough Diamonds ({cost} required).";
    public String convertFailed = "Could not convert this loss.";

    public ResultBalanceConfig sanitize() {
        if (loseSkipDiamondCost < 0) {
            loseSkipDiamondCost = 1000;
        }
        if (popupDelaySeconds < 0f) {
            popupDelaySeconds = 2.0f;
        }
        buyWinLabelFormat = text(buyWinLabelFormat, "Buy Win ({cost})");
        notEnoughDiamonds = text(notEnoughDiamonds, "Not enough Diamonds.");
        notEnoughDiamondsFormat = text(notEnoughDiamondsFormat, "Not enough Diamonds ({cost} required).");
        convertFailed = text(convertFailed, "Could not convert this loss.");
        return this;
    }

    public String buyWinLabel() {
        return buyWinLabelFormat.replace("{cost}", String.valueOf(loseSkipDiamondCost));
    }

    public String notEnoughDiamondsWithCost() {
        return notEnoughDiamondsFormat.replace("{cost}", String.valueOf(loseSkipDiamondCost));
    }

    private static String text(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}

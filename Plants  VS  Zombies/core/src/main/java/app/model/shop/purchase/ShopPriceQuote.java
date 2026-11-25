package app.model.shop.purchase;

import app.model.shop.enums.CurrencyType;

public final class ShopPriceQuote {
    private final int totalAmount;
    private final CurrencyType currencyType;
    private final int unitAmount;

    public ShopPriceQuote(int unitAmount, int totalAmount, CurrencyType currencyType) {
        this.unitAmount = unitAmount;
        this.totalAmount = totalAmount;
        this.currencyType = currencyType;
    }

    public int getUnitAmount() {
        return unitAmount;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public CurrencyType getCurrencyType() {
        return currencyType;
    }
}

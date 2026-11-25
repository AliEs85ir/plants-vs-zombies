package app.model.shop.items;

import app.model.shop.Price;
import app.model.shop.ShopItem;
import app.model.shop.enums.CurrencyType;
import app.model.user.User;

public class CurrencyExchangeItem extends ShopItem {

    public CurrencyExchangeItem() {
        super(
            "Currency Exchange",
            new Price(5, CurrencyType.GEM)
        );
    }

    @Override
    public void apply(
        User user,
        int count
    ) {
        user.addCoins(500 * count);
    }

    @Override
    public String getDisplayName() {
        return "Currency Exchange";
    }
}

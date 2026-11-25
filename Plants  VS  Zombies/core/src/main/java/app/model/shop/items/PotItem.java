package app.model.shop.items;

import app.model.shop.Price;
import app.model.shop.ShopItem;
import app.model.shop.enums.CurrencyType;
import app.model.user.User;

public class PotItem extends ShopItem {

    public PotItem() {
        super(
            "Pot",
            new Price(2000, CurrencyType.COIN)
        );
    }


    @Override
    public boolean canApply(
        User user,
        int count
    ) {
        return super.canApply(user, count)
            && user.getGreenhouse().isPotAvailable(count);
    }

    @Override
    public String getDisplayName() {
        return "Pot";
    }

    @Override
    public void apply(
        User user,
        int count
    ) {
        for (int i = 0; i < count; i++) {
            user.getGreenhouse().addPot();
        }
    }
}

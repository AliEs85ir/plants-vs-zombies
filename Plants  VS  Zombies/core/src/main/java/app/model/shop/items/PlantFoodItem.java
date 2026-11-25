package app.model.shop.items;

import app.model.shop.Price;
import app.model.shop.ShopItem;
import app.model.shop.enums.CurrencyType;
import app.model.user.User;

public class PlantFoodItem extends ShopItem {

    public PlantFoodItem() {
        super(
            "Plant Food",
            new Price(3, CurrencyType.GEM)
        );
    }

    @Override
    public boolean canApply(
        User user,
        int count
    ) {
        return super.canApply(user, count)
            && !user.getFoodInventory().willBeFull(count);
    }

    @Override
    public void apply(
        User user,
        int count
    ) {
        for (int i = 0; i < count; i++) {
            user.getFoodInventory().addPlantFood();
        }
    }

    @Override
    public String getDisplayName() {
        return "Plant Food";
    }
}

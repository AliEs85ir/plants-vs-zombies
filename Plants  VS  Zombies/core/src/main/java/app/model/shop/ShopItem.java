package app.model.shop;

import app.model.user.User;

public abstract class ShopItem {

    private final String name;
    private final Price price;

    protected ShopItem(
        String name,
        Price price
    ) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public Price getPrice() {
        return price;
    }

    public boolean canApply(
        User user,
        int count
    ) {
        return count > 0;
    }

    public abstract void apply(
        User user,
        int count
    );

    public abstract String getDisplayName();
}

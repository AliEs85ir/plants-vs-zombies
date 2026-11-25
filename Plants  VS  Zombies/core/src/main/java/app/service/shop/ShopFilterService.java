package app.service.shop;

import app.model.shop.enums.CurrencyType;
import app.model.shop.enums.ShopProductAvailability;
import app.model.shop.purchase.ShopPriceQuote;
import app.model.shop.purchase.ShopPurchaseContext;
import app.model.user.User;
import app.view.shop.config.ShopCatalogConfig;

import java.util.ArrayList;
import java.util.List;

public final class ShopFilterService {
    private ShopFilterService() {
    }

    public static List<ShopCatalogConfig.ProductConfig> filterProducts(
        User user,
        ShopFilterState state,
        List<ShopCatalogConfig.ProductConfig> source
    ) {
        ArrayList<ShopCatalogConfig.ProductConfig> filtered = new ArrayList<>();
        ShopPurchaseContext context = new ShopPurchaseContext(1);
        for (ShopCatalogConfig.ProductConfig product : source) {
            if (!matchesCategory(product, state)) {
                continue;
            }
            if (!matchesCurrency(product, state, context)) {
                continue;
            }
            if (!matchesPrice(product, state, context)) {
                continue;
            }
            if (!matchesAvailability(user, product, context)) {
                continue;
            }
            filtered.add(product);
        }
        return filtered;
    }

    private static boolean matchesCategory(ShopCatalogConfig.ProductConfig product, ShopFilterState state) {
        if (state.getCategoryId() == null || state.getCategoryId().isBlank()) {
            return true;
        }
        return state.getCategoryId().equals(product.categoryId);
    }

    private static boolean matchesCurrency(
        ShopCatalogConfig.ProductConfig product,
        ShopFilterState state,
        ShopPurchaseContext context
    ) {
        CurrencyType filter = state.getCurrencyFilter();
        if (filter == null) {
            return true;
        }
        ShopPriceQuote quote = ShopProductEffects.calculatePrice(product, context);
        return quote.getCurrencyType() == filter;
    }

    private static boolean matchesPrice(
        ShopCatalogConfig.ProductConfig product,
        ShopFilterState state,
        ShopPurchaseContext context
    ) {
        ShopPriceQuote quote = ShopProductEffects.calculatePrice(product, context);
        int price = quote.getTotalAmount();
        return price >= state.getAppliedMinPrice() && price <= state.getAppliedMaxPrice();
    }

    private static boolean matchesAvailability(
        User user,
        ShopCatalogConfig.ProductConfig product,
        ShopPurchaseContext context
    ) {
        ShopProductAvailability availability = ShopProductEffects.availability(user, product, context);
        return availability != ShopProductAvailability.UNAVAILABLE
            && availability != ShopProductAvailability.LOCKED
            && availability != ShopProductAvailability.SOLD_OUT;
    }
}

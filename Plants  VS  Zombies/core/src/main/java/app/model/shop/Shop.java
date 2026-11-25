package app.model.shop;

import app.model.App;
import app.model.shop.purchase.ShopPriceQuote;
import app.model.shop.purchase.ShopPurchaseContext;
import app.model.user.User;
import app.service.AppService;
import app.service.shop.ShopCatalog;
import app.service.shop.ShopProductEffects;
import app.view.shop.config.ShopCatalogConfig;
import app.view.shop.config.ShopMessages;
import common.Result;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Shop {

    private transient DailyOffer dailyOffer;

    public Shop() {
    }

    public Result buyProduct(
        User user,
        ShopCatalogConfig.ProductConfig product,
        ShopPurchaseContext context
    ) {
        if (user == null) {
            return Result.failure(ShopMessages.get("userUnavailable"));
        }
        if (product == null) {
            return Result.failure(ShopMessages.get("productNotFound"));
        }
        if (context == null || context.getQuantity() <= 0) {
            return Result.failure(ShopMessages.get("invalidQuantity"));
        }

        if (!ShopProductEffects.canApply(user, product, context, true)) {
            return Result.failure(ShopProductEffects.validationMessage(user, product, context));
        }

        ShopPriceQuote quote = ShopProductEffects.calculatePrice(product, context);
        if (quote.getCurrencyType() == app.model.shop.enums.CurrencyType.REAL_MONEY) {
            return Result.failure(ShopMessages.get("realMoneyUnavailable"));
        }

        if (!ShopProductEffects.canPay(user, quote)) {
            return Result.failure(ShopProductEffects.insufficientFundsMessage(quote.getCurrencyType()));
        }

        ShopProductEffects.pay(user, quote);
        ShopProductEffects.apply(user, product, context);
        AppService.updateAllUsers();
        String priceText = quote.getTotalAmount() + " " + String.valueOf(quote.getCurrencyType());
        app.service.news.NewsPublisher.onShopPurchase(user, product.id, context.getQuantity(), priceText);
        return Result.success(ShopMessages.get("purchaseSuccess"));
    }

    public Result buyDaily(User user) {
        if (user == null) {
            return Result.failure(ShopMessages.get("userUnavailable"));
        }

        DailyOffer offer = getDailyOffer();
        if (offer == null || offer.isSold()) {
            return Result.failure(ShopMessages.get("dailyOfferUnavailable"));
        }

        ShopCatalogConfig.ProductConfig product = ShopCatalog.current().product(offer.getProductId());
        if (product == null) {
            return Result.failure(ShopMessages.get("productNotFound"));
        }

        ShopPurchaseContext context = new ShopPurchaseContext(1);
        if (!ShopProductEffects.canApply(user, product, context, true)) {
            return Result.failure(ShopProductEffects.validationMessage(user, product, context));
        }

        ShopPriceQuote baseQuote = ShopProductEffects.calculatePrice(product, context);
        int discounted = applyDiscount(baseQuote.getTotalAmount(), offer.getDiscountPercent());

        ShopPriceQuote quote = new ShopPriceQuote(
            discounted,
            discounted,
            baseQuote.getCurrencyType()
        );

        if (!ShopProductEffects.canPay(user, quote)) {
            return Result.failure(ShopProductEffects.insufficientFundsMessage(quote.getCurrencyType()));
        }

        ShopProductEffects.pay(user, quote);
        ShopProductEffects.apply(user, product, context);
        offer.setSold(true);
        AppService.updateAllUsers();
        String priceText = quote.getTotalAmount() + " " + String.valueOf(quote.getCurrencyType()) + " (daily)";
        app.service.news.NewsPublisher.onShopPurchase(user, product.id, 1, priceText);
        return Result.success(ShopMessages.get("purchaseSuccess"));
    }

    public DailyOffer getDailyOffer() {
        if (dailyOffer == null || !dailyOffer.getDate().equals(LocalDate.now()) || dailyOffer.isSold()) {
            if (dailyOffer == null || !dailyOffer.getDate().equals(LocalDate.now())) {
                resetDailyItem();
            }
        }
        if (dailyOffer != null && dailyOffer.isSold()) {
            return null;
        }
        return dailyOffer;
    }

    public DailyOffer getPersistedDailyOffer() {
        return dailyOffer;
    }

    public void restoreDailyOffer(
        String productId,
        LocalDate date,
        int discountPercent,
        boolean sold
    ) {
        if (productId == null || date == null) {
            dailyOffer = null;
            return;
        }

        if (!date.equals(LocalDate.now())) {
            dailyOffer = null;
            return;
        }

        dailyOffer = new DailyOffer(productId, date, discountPercent);
        dailyOffer.setSold(sold);
    }

    public void resetDailyItem() {
        User user = App.getCurrentUser();
        if (user == null) {
            dailyOffer = null;
            return;
        }

        ShopCatalogConfig config = ShopCatalog.current().config();
        List<String> eligible = config.dailyOffer.eligibleProductIds;
        if (eligible == null || eligible.isEmpty()) {
            dailyOffer = null;
            return;
        }

        ArrayList<ShopCatalogConfig.ProductConfig> candidates = new ArrayList<>();
        ShopPurchaseContext context = new ShopPurchaseContext(1);
        for (String productId : eligible) {
            ShopCatalogConfig.ProductConfig product = ShopCatalog.current().product(productId);
            if (product == null || !product.enabled) {
                continue;
            }
            if (ShopProductEffects.availability(user, product, context)
                == app.model.shop.enums.ShopProductAvailability.AVAILABLE) {
                candidates.add(product);
            }
        }

        if (candidates.isEmpty()) {
            dailyOffer = null;
            return;
        }

        Random random = new Random();
        ShopCatalogConfig.ProductConfig selected = candidates.get(random.nextInt(candidates.size()));
        List<Integer> discounts = config.dailyOffer.discountValues;
        int discount = discounts.get(random.nextInt(Math.max(1, discounts.size())));

        dailyOffer = new DailyOffer(selected.id, LocalDate.now(), discount);
        User current = App.getCurrentUser();
        if (current == null) {
            current = user;
        }
        app.service.news.NewsPublisher.onDailyOfferAvailable(current, dailyOffer);
    }

    public static int applyDiscount(int amount, int discountPercent) {
        if (discountPercent <= 0) {
            return amount;
        }
        int discounted = amount - (amount * discountPercent / 100);
        return Math.max(0, discounted);
    }

    public static DurationUntilMidnight remainingDailyTime() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime end = LocalDate.now().atTime(LocalTime.of(23, 59, 59));
        if (!now.isBefore(end)) {
            return new DurationUntilMidnight(java.time.Duration.ZERO);
        }
        return new DurationUntilMidnight(java.time.Duration.between(now, end));
    }

    public record DurationUntilMidnight(java.time.Duration duration) {
    }
}

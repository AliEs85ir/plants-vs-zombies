package app.view.shop.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ShopCatalogConfig {
    public ArrayList<CategoryConfig> categories = new ArrayList<>();
    public ArrayList<CurrencyFilterConfig> currencyFilters = new ArrayList<>();
    public DailyOfferRulesConfig dailyOffer = new DailyOfferRulesConfig();
    public ArrayList<AdvertisementConfig> advertisements = new ArrayList<>();
    public ArrayList<ProductConfig> products = new ArrayList<>();

    public ShopCatalogConfig sanitize() {
        if (categories == null) categories = new ArrayList<>();
        if (currencyFilters == null) currencyFilters = new ArrayList<>();
        if (dailyOffer == null) dailyOffer = new DailyOfferRulesConfig();
        if (advertisements == null) advertisements = new ArrayList<>();
        if (products == null) products = new ArrayList<>();
        categories.removeIf(item -> item == null);
        currencyFilters.removeIf(item -> item == null);
        advertisements.removeIf(item -> item == null);
        products.removeIf(item -> item == null);
        for (CategoryConfig category : categories) category.sanitize();
        for (CurrencyFilterConfig filter : currencyFilters) filter.sanitize();
        dailyOffer.sanitize();
        for (AdvertisementConfig ad : advertisements) ad.sanitize();
        for (ProductConfig product : products) product.sanitize();
        return this;
    }

    public static class CategoryConfig {
        public String id = "";
        public String title = "";
        public String iconPath = "";
        public int order = 0;
        public boolean enabled = true;

        public void sanitize() {
            id = text(id, "");
            title = text(title, id);
            iconPath = text(iconPath, "menu/icon/shop.png");
        }
    }

    public static class CurrencyFilterConfig {
        public String id = "all";
        public String label = "All";
        public String currency;
        public boolean enabled = true;

        public void sanitize() {
            id = text(id, "all");
            label = text(label, id);
        }
    }

    public static class DailyOfferRulesConfig {
        public String resetTime = "00:00:00";
        public String endTime = "23:59:59";
        public ArrayList<Integer> discountValues = new ArrayList<>();
        public ArrayList<String> eligibleProductIds = new ArrayList<>();
        public String selectionStrategy = "random";

        public void sanitize() {
            resetTime = text(resetTime, "00:00:00");
            endTime = text(endTime, "23:59:59");
            if (discountValues == null || discountValues.isEmpty()) {
                discountValues = new ArrayList<>();
                discountValues.add(30);
                discountValues.add(40);
                discountValues.add(50);
                discountValues.add(60);
            }
            if (eligibleProductIds == null) eligibleProductIds = new ArrayList<>();
            selectionStrategy = text(selectionStrategy, "random");
        }
    }

    public static class AdvertisementConfig {
        public String id = "";
        public String imagePath = "";
        public String url = "";
        public boolean enabled = true;
        public int weight = 1;

        public void sanitize() {
            id = text(id, "");
            imagePath = text(imagePath, "menu/image/adventure.png");
            url = text(url, "");
            weight = Math.max(1, weight);
        }
    }

    public static class ProductOptionConfig {
        public String id = "";
        public String label = "";
        public String currency = "COIN";
        public int unitPrice = 0;
        public boolean defaultOption = false;

        public void sanitize() {
            id = text(id, "");
            label = text(label, id);
            currency = text(currency, "COIN");
            unitPrice = Math.max(0, unitPrice);
        }
    }

    public static class ProductConfig {
        public String id = "";
        public String title = "";
        public String categoryId = "";
        public String imagePath = "";
        public String cardBackgroundPath = "";
        public String description = "";
        public int basePrice = 0;
        public String currency = "COIN";
        public String productType = "";
        public String quantityMode = "variable";
        public int defaultQuantity = 1;
        public int minQuantity = 1;
        public int maxQuantity = 99;
        public int quantityStep = 1;
        public boolean requiresPlantSelection = false;
        public boolean enabled = true;
        public int order = 0;
        public float imageOffsetX = 0f;
        public float imageOffsetY = 0f;
        public float imageScale = 1f;
        public ArrayList<ProductOptionConfig> options = new ArrayList<>();
        public HashMap<String, Object> params = new HashMap<>();

        public void sanitize() {
            id = text(id, "");
            title = text(title, id);
            categoryId = text(categoryId, "");
            imagePath = text(imagePath, "menu/icon/shop.png");
            cardBackgroundPath = text(cardBackgroundPath, "menu/image/btn1.png");
            description = text(description, "");
            basePrice = Math.max(0, basePrice);
            currency = text(currency, "COIN");
            productType = text(productType, id);
            quantityMode = text(quantityMode, "variable");
            defaultQuantity = Math.max(1, defaultQuantity);
            minQuantity = Math.max(1, minQuantity);
            maxQuantity = Math.max(minQuantity, maxQuantity);
            quantityStep = Math.max(1, quantityStep);
            imageScale = imageScale <= 0f ? 1f : imageScale;
            if (options == null) options = new ArrayList<>();
            options.removeIf(item -> item == null);
            for (ProductOptionConfig option : options) option.sanitize();
            if (params == null) params = new HashMap<>();
        }

        public int paramInt(String key, int fallback) {
            Object value = params.get(key);
            if (value instanceof Number number) {
                return number.intValue();
            }
            if (value instanceof String text) {
                try {
                    return Integer.parseInt(text.trim());
                } catch (NumberFormatException ignored) {
                    return fallback;
                }
            }
            return fallback;
        }

        public double paramDouble(String key, double fallback) {
            Object value = params.get(key);
            if (value instanceof Number number) {
                return number.doubleValue();
            }
            if (value instanceof String text) {
                try {
                    return Double.parseDouble(text.trim());
                } catch (NumberFormatException ignored) {
                    return fallback;
                }
            }
            return fallback;
        }
    }

    protected static String text(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value.trim();
    }
}

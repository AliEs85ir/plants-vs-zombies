package app.service.shop;

import app.view.shop.config.ShopCatalogConfig;
import app.view.shop.config.ShopCatalogConfigLoader;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ShopCatalog {
    private static ShopCatalog instance;

    private final ShopCatalogConfig config;
    private final Map<String, ShopCatalogConfig.ProductConfig> productsById = new HashMap<>();
    private final Map<String, ShopCatalogConfig.CategoryConfig> categoriesById = new HashMap<>();

    private ShopCatalog(ShopCatalogConfig config) {
        this.config = config == null ? new ShopCatalogConfig().sanitize() : config.sanitize();
        index();
    }

    public static ShopCatalog current() {
        if (instance == null) {
            instance = new ShopCatalog(ShopCatalogConfigLoader.load());
        }
        return instance;
    }

    public static void reload() {
        instance = new ShopCatalog(ShopCatalogConfigLoader.load());
    }

    private void index() {
        productsById.clear();
        categoriesById.clear();
        for (ShopCatalogConfig.CategoryConfig category : config.categories) {
            categoriesById.put(category.id, category);
        }
        for (ShopCatalogConfig.ProductConfig product : config.products) {
            productsById.put(product.id, product);
        }
    }

    public ShopCatalogConfig config() {
        return config;
    }

    public ShopCatalogConfig.ProductConfig product(String id) {
        return productsById.get(id);
    }

    public ShopCatalogConfig.CategoryConfig category(String id) {
        return categoriesById.get(id);
    }

    public List<ShopCatalogConfig.CategoryConfig> enabledCategories() {
        ArrayList<ShopCatalogConfig.CategoryConfig> categories = new ArrayList<>();
        for (ShopCatalogConfig.CategoryConfig category : config.categories) {
            if (category.enabled) {
                categories.add(category);
            }
        }
        categories.sort(Comparator.comparingInt(item -> item.order));
        return categories;
    }

    public List<ShopCatalogConfig.ProductConfig> enabledProducts() {
        ArrayList<ShopCatalogConfig.ProductConfig> products = new ArrayList<>();
        for (ShopCatalogConfig.ProductConfig product : config.products) {
            if (product.enabled) {
                products.add(product);
            }
        }
        products.sort(Comparator.comparingInt(item -> item.order));
        return products;
    }

    public List<ShopCatalogConfig.AdvertisementConfig> enabledAdvertisements() {
        ArrayList<ShopCatalogConfig.AdvertisementConfig> ads = new ArrayList<>();
        for (ShopCatalogConfig.AdvertisementConfig ad : config.advertisements) {
            if (ad.enabled) {
                ads.add(ad);
            }
        }
        return ads;
    }
}

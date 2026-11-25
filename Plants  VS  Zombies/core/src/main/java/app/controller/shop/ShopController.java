package app.controller.shop;

import app.model.App;
import app.model.shop.DailyOffer;
import app.model.shop.Shop;
import app.model.shop.purchase.ShopPurchaseContext;
import app.model.user.User;
import app.service.AppService;
import app.service.shop.ShopCatalog;
import app.service.shop.ShopFilterService;
import app.service.shop.ShopFilterState;
import app.view.shop.config.ShopCatalogConfig;
import app.view.shop.config.ShopConfig;
import app.view.shop.config.ShopConfigLoader;
import common.Result;
import game.settings.GameSettings;

import java.util.List;
import java.util.Random;

public class ShopController {

    private final ShopFilterState filterState;
    private ShopCatalogConfig.AdvertisementConfig selectedAdvertisement;

    public ShopController() {
        ShopConfig config = ShopConfigLoader.load();
        filterState = new ShopFilterState(
            config.priceFilter.minDefault,
            config.priceFilter.maxDefault
        );
        pickAdvertisement();
    }

    private Shop getShop() {
        User user = App.getCurrentUser();
        if (user == null) {
            return null;
        }
        return user.getShop();
    }

    public ShopFilterState filterState() {
        return filterState;
    }

    public List<ShopCatalogConfig.ProductConfig> allProducts() {
        return ShopCatalog.current().enabledProducts();
    }

    public List<ShopCatalogConfig.ProductConfig> filteredProducts() {
        User user = App.getCurrentUser();
        return ShopFilterService.filterProducts(user, filterState, allProducts());
    }

    public List<ShopCatalogConfig.CategoryConfig> categories() {
        return ShopCatalog.current().enabledCategories();
    }

    public List<ShopCatalogConfig.CurrencyFilterConfig> currencyFilters() {
        return ShopCatalog.current().config().currencyFilters;
    }

    public ShopCatalogConfig.ProductConfig product(String id) {
        return ShopCatalog.current().product(id);
    }

    public DailyOffer getDailyOffer() {
        Shop shop = getShop();
        if (shop == null) {
            return null;
        }
        return shop.getDailyOffer();
    }

    public Result buy(ShopCatalogConfig.ProductConfig product, ShopPurchaseContext context) {
        User user = App.getCurrentUser();
        if (user == null) {
            return Result.failure("User is not logged in");
        }
        Shop shop = user.getShop();
        if (shop == null) {
            return Result.failure("Shop is not available");
        }
        return shop.buyProduct(user, product, context);
    }

    public Result buyDaily() {
        User user = App.getCurrentUser();
        if (user == null) {
            return Result.failure("User is not logged in");
        }
        Shop shop = user.getShop();
        if (shop == null) {
            return Result.failure("Shop is not available");
        }
        return shop.buyDaily(user);
    }

    public int getCoins() {
        User user = App.getCurrentUser();
        return user == null ? 0 : user.getCoins();
    }

    public int getGems() {
        User user = App.getCurrentUser();
        return user == null ? 0 : user.getGems();
    }

    public void applyFilters() {
        filterState.applyDraft();
    }

    public ShopCatalogConfig.AdvertisementConfig selectedAdvertisement() {
        return selectedAdvertisement;
    }

    public void pickAdvertisement() {
        List<ShopCatalogConfig.AdvertisementConfig> ads = ShopCatalog.current().enabledAdvertisements();
        if (ads.isEmpty()) {
            selectedAdvertisement = null;
            return;
        }
        Random random = new Random();
        selectedAdvertisement = ads.get(random.nextInt(ads.size()));
    }

    public boolean isDebugMode() {
        return GameSettings.gameplay().debugMode();
    }

    public void addTestMoney() {
        User user = App.getCurrentUser();
        if (user != null) {
            user.addCoins(100000);
            user.addGems(10000);
            AppService.updateAllUsers();
        }
    }
}

package app.service.shop;

import app.enums.greenhouse.PotStatus;
import app.enums.plant.PlantName;
import app.model.greenhouse.Greenhouse;
import app.model.shop.enums.CurrencyType;
import app.model.shop.enums.ShopProductAvailability;
import app.model.shop.purchase.ShopPriceQuote;
import app.model.shop.purchase.ShopPurchaseContext;
import app.model.user.User;
import app.service.greenhouse.GreenhouseBillService;
import app.service.greenhouse.GreenhouseGameplaySettings;
import app.view.shop.config.ShopCatalogConfig;
import app.view.shop.config.ShopMessages;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public final class ShopProductEffects {
    private ShopProductEffects() {
    }

    public static CurrencyType parseCurrency(String raw) {
        if (raw == null || raw.isBlank()) {
            return CurrencyType.COIN;
        }
        try {
            return CurrencyType.valueOf(raw.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            return CurrencyType.COIN;
        }
    }

    public static ShopCatalogConfig.ProductOptionConfig resolveOption(
        ShopCatalogConfig.ProductConfig product,
        ShopPurchaseContext context
    ) {
        if (product.options == null || product.options.isEmpty()) {
            return null;
        }
        if (context.getSelectedOptionId() != null) {
            for (ShopCatalogConfig.ProductOptionConfig option : product.options) {
                if (option.id.equals(context.getSelectedOptionId())) {
                    return option;
                }
            }
        }
        for (ShopCatalogConfig.ProductOptionConfig option : product.options) {
            if (option.defaultOption) {
                return option;
            }
        }
        return product.options.get(0);
    }

    public static ShopPriceQuote calculatePrice(
        ShopCatalogConfig.ProductConfig product,
        ShopPurchaseContext context
    ) {
        ShopCatalogConfig.ProductOptionConfig option = resolveOption(product, context);
        int quantity = Math.max(1, context.getQuantity());
        if (option != null) {
            return new ShopPriceQuote(
                option.unitPrice,
                option.unitPrice * quantity,
                parseCurrency(option.currency)
            );
        }
        return new ShopPriceQuote(
            product.basePrice,
            product.basePrice * quantity,
            parseCurrency(product.currency)
        );
    }

    public static int maxQuantity(User user, ShopCatalogConfig.ProductConfig product, ShopPurchaseContext context) {
        if ("fixed".equalsIgnoreCase(product.quantityMode)) {
            return 1;
        }
        int configuredMax = product.maxQuantity;
        return switch (product.productType) {
            case "pot" -> Math.min(configuredMax, user.getGreenhouse().countAvailablePotSlots());
            case "pot_table" -> Math.min(
                configuredMax,
                remainingShopTables(user, product)
            );
            case "shovel" -> maxShovelQuantity(user, product, context);
            case "plant_food" -> Math.min(
                configuredMax,
                Math.max(0, product.paramInt("maxInventory", 5) - user.getFoodInventory().getAmount())
            );
            default -> configuredMax;
        };
    }

    private static int maxShovelQuantity(User user, ShopCatalogConfig.ProductConfig product, ShopPurchaseContext context) {
        ShopCatalogConfig.ProductOptionConfig option = resolveOption(product, context);
        if (option != null && "new_shovel".equals(option.id)) {
            int maxShovels = product.paramInt("maxShovels", 4);
            return Math.max(0, maxShovels - user.getGreenhouse().getShovelCount());
        }
        return product.maxQuantity;
    }

    private static int remainingShopTables(User user, ShopCatalogConfig.ProductConfig product) {
        int maxShopTables = product.paramInt("maxShopTables", 3);
        Greenhouse greenhouse = user.getGreenhouse();
        int remainingUnlocks = maxShopTables - greenhouse.getShopUnlockedTableCount();
        int lockedSlots = 0;
        if (greenhouse.hasLockedTable()) {
            for (int row = 0; row < greenhouse.getRow(); row++) {
                for (int col = 0; col < greenhouse.getCol(); col++) {
                    if (greenhouse.getPot(col + 1, row + 1).getPotStatus() == PotStatus.LOCKED) {
                        lockedSlots++;
                    }
                }
            }
        }
        return Math.max(0, Math.min(remainingUnlocks, lockedSlots));
    }

    public static ShopProductAvailability availability(
        User user,
        ShopCatalogConfig.ProductConfig product,
        ShopPurchaseContext context
    ) {
        if (user == null || !product.enabled) {
            return ShopProductAvailability.UNAVAILABLE;
        }
        if (maxQuantity(user, product, context) <= 0) {
            return ShopProductAvailability.CONDITION_NOT_MET;
        }
        if (!canApply(user, product, context, false)) {
            return ShopProductAvailability.CONDITION_NOT_MET;
        }
        return ShopProductAvailability.AVAILABLE;
    }

    public static boolean canApply(
        User user,
        ShopCatalogConfig.ProductConfig product,
        ShopPurchaseContext context,
        boolean strictQuantity
    ) {
        if (user == null || context.getQuantity() <= 0) {
            return false;
        }
        int quantity = context.getQuantity();
        if (strictQuantity) {
            int max = maxQuantity(user, product, context);
            if (quantity < product.minQuantity || quantity > max) {
                return false;
            }
        }
        return switch (product.productType) {
            case "pot" -> user.getGreenhouse().isPotAvailable(quantity);
            case "pot_table" -> canUnlockTables(user, product, quantity);
            case "shovel" -> canApplyShovel(user, product, context, quantity);
            case "plant_food" -> !user.getFoodInventory().willBeFull(quantity);
            case "buy_coins", "coin_pack", "buy_gems" -> true;
            case "random_seed_packet" -> !user.getUnlockedPlants().isEmpty();
            case "targeted_seed_packet" -> isValidTargetPlant(user, context);
            default -> false;
        };
    }

    private static boolean canUnlockTables(User user, ShopCatalogConfig.ProductConfig product, int quantity) {
        int maxShopTables = product.paramInt("maxShopTables", 3);
        Greenhouse greenhouse = user.getGreenhouse();
        if (greenhouse.getShopUnlockedTableCount() + quantity > maxShopTables) {
            return false;
        }
        int locked = countLockedTables(greenhouse);
        return locked >= quantity;
    }

    private static int countLockedTables(Greenhouse greenhouse) {
        int count = 0;
        for (int row = 0; row < greenhouse.getRow(); row++) {
            for (int col = 0; col < greenhouse.getCol(); col++) {
                if (greenhouse.getPot(col + 1, row + 1).getPotStatus() == PotStatus.LOCKED) {
                    count++;
                }
            }
        }
        return count;
    }

    private static boolean canApplyShovel(
        User user,
        ShopCatalogConfig.ProductConfig product,
        ShopPurchaseContext context,
        int quantity
    ) {
        ShopCatalogConfig.ProductOptionConfig option = resolveOption(product, context);
        if (option == null) {
            return false;
        }
        if ("new_shovel".equals(option.id)) {
            int maxShovels = product.paramInt("maxShovels", 4);
            return user.getGreenhouse().getShovelCount() + quantity <= maxShovels;
        }
        if ("reduce_timer".equals(option.id)) {
            GreenhouseBillService billService = new GreenhouseBillService(user.getGreenhouse());
            return billService.remainingCooldown().compareTo(Duration.ZERO) > 0;
        }
        return false;
    }

    private static boolean isValidTargetPlant(User user, ShopPurchaseContext context) {
        PlantName plant = context.getTargetPlant();
        return plant != null && user.isUnlocked(plant);
    }

    public static void apply(User user, ShopCatalogConfig.ProductConfig product, ShopPurchaseContext context) {
        int quantity = context.getQuantity();
        switch (product.productType) {
            case "pot" -> {
                for (int index = 0; index < quantity; index++) {
                    user.getGreenhouse().addPot();
                }
            }
            case "pot_table" -> {
                for (int index = 0; index < quantity; index++) {
                    user.getGreenhouse().unlockTableViaShop();
                }
            }
            case "shovel" -> applyShovel(user, product, context, quantity);
            case "plant_food" -> {
                for (int index = 0; index < quantity; index++) {
                    user.getFoodInventory().addPlantFood();
                }
            }
            case "buy_coins" -> user.addCoins(product.paramInt("coinsPerGem", 100) * quantity);
            case "coin_pack" -> user.addCoins(product.paramInt("coinsGranted", 10000));
            case "buy_gems" -> user.addGems(quantity);
            case "random_seed_packet" -> applyRandomSeedPackets(user, product, quantity);
            case "targeted_seed_packet" -> {
                int packetsPerGem = product.paramInt("packetsPerGem", 2);
                user.getSeedBank().addPacket(context.getTargetPlant(), packetsPerGem * quantity);
            }
            default -> {
            }
        }
    }

    private static void applyShovel(
        User user,
        ShopCatalogConfig.ProductConfig product,
        ShopPurchaseContext context,
        int quantity
    ) {
        ShopCatalogConfig.ProductOptionConfig option = resolveOption(product, context);
        if (option == null) {
            return;
        }
        Greenhouse greenhouse = user.getGreenhouse();
        if ("new_shovel".equals(option.id)) {
            int maxShovels = product.paramInt("maxShovels", 4);
            for (int index = 0; index < quantity; index++) {
                greenhouse.addShovel(maxShovels);
            }
            return;
        }
        if ("reduce_timer".equals(option.id)) {
            double percent = product.paramDouble("timerReductionPercent", 25d);
            int minimumMinutes = product.paramInt("minimumTimerMinutes", 15);
            float cooldownHours = GreenhouseGameplaySettings.current().billCooldownHours();
            for (int index = 0; index < quantity; index++) {
                greenhouse.reduceAllShovelTimers(
                    percent,
                    Duration.ofMinutes(minimumMinutes),
                    cooldownHours
                );
            }
        }
    }

    private static void applyRandomSeedPackets(
        User user,
        ShopCatalogConfig.ProductConfig product,
        int quantity
    ) {
        List<PlantName> unlocked = user.getUnlockedPlants();
        if (unlocked.isEmpty()) {
            return;
        }
        Random random = new Random();
        int packetsPerPurchase = product.paramInt("packetsPerPurchase", 1);
        for (int index = 0; index < quantity; index++) {
            PlantName selected = unlocked.get(random.nextInt(unlocked.size()));
            user.getSeedBank().addPacket(selected, packetsPerPurchase);
        }
    }

    public static String validationMessage(
        User user,
        ShopCatalogConfig.ProductConfig product,
        ShopPurchaseContext context
    ) {
        if (user == null) {
            return ShopMessages.get("userUnavailable");
        }
        if (product.requiresPlantSelection && context.getTargetPlant() == null) {
            return ShopMessages.get("selectPlant");
        }
        if (context.getTargetPlant() != null && !user.isUnlocked(context.getTargetPlant())) {
            return ShopMessages.get("plantNotUnlocked");
        }
        return switch (product.productType) {
            case "pot" -> ShopMessages.get("noFreeTable");
            case "pot_table" -> ShopMessages.get("maxTablesPurchased");
            case "shovel" -> shovelMessage(product, context, user);
            case "plant_food" -> ShopMessages.get("plantFoodMax");
            case "random_seed_packet" -> ShopMessages.get("noUnlockedPlant");
            default -> ShopMessages.get("productUnavailable");
        };
    }

    private static String shovelMessage(
        ShopCatalogConfig.ProductConfig product,
        ShopPurchaseContext context,
        User user
    ) {
        ShopCatalogConfig.ProductOptionConfig option = resolveOption(product, context);
        if (option != null && "new_shovel".equals(option.id)) {
            return ShopMessages.get("maxShovels");
        }
        return ShopMessages.get("timerMinimum");
    }

    public static boolean canPay(User user, ShopPriceQuote quote) {
        if (quote == null || quote.getCurrencyType() == CurrencyType.REAL_MONEY) {
            return false;
        }
        return switch (quote.getCurrencyType()) {
            case COIN -> user.getCoins() >= quote.getTotalAmount();
            case GEM -> user.getGems() >= quote.getTotalAmount();
            default -> false;
        };
    }

    public static void pay(User user, ShopPriceQuote quote) {
        if (quote == null) {
            return;
        }
        switch (quote.getCurrencyType()) {
            case COIN -> user.deductCoins(quote.getTotalAmount());
            case GEM -> user.deductGems(quote.getTotalAmount());
            default -> {
            }
        }
    }

    public static String insufficientFundsMessage(CurrencyType currencyType) {
        if (currencyType == CurrencyType.GEM) {
            return ShopMessages.get("insufficientGems");
        }
        if (currencyType == CurrencyType.REAL_MONEY) {
            return ShopMessages.get("realMoneyUnavailable");
        }
        return ShopMessages.get("insufficientCoins");
    }
}

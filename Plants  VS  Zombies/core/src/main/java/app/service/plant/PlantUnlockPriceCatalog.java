package app.service.plant;

import app.enums.plant.PlantName;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Data-driven diamond prices for unlocking plants in Collection.
 * Loaded from assets/ui/collection_plant_unlock_prices.json.
 */
public final class PlantUnlockPriceCatalog {
    public String currency = "gems";
    public int defaultPrice = 100;
    public Map<String, Integer> prices = new LinkedHashMap<>();

    public Map<String, Integer> pricesOrEmpty() {
        return prices == null ? Collections.emptyMap() : prices;
    }

    public int priceOf(PlantName plantName) {
        if (plantName == null) {
            return Math.max(0, defaultPrice);
        }
        Integer configured = pricesOrEmpty().get(plantName.name());
        if (configured != null) {
            return Math.max(0, configured);
        }
        return Math.max(0, defaultPrice);
    }

    public PlantUnlockPriceCatalog sanitize() {
        if (currency == null || currency.isBlank()) {
            currency = "gems";
        }
        if (defaultPrice < 0) {
            defaultPrice = 0;
        }
        if (prices == null) {
            prices = new LinkedHashMap<>();
        }
        Map<String, Integer> cleaned = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : prices.entrySet()) {
            if (entry.getKey() == null || entry.getKey().isBlank() || entry.getValue() == null) {
                continue;
            }
            cleaned.put(entry.getKey().trim(), Math.max(0, entry.getValue()));
        }
        prices = cleaned;
        return this;
    }
}
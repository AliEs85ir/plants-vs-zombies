package app.model.shop.purchase;

import app.enums.plant.PlantName;
import app.model.shop.enums.CurrencyType;

public final class ShopPurchaseContext {
    private int quantity = 1;
    private String selectedOptionId;
    private PlantName targetPlant;

    public ShopPurchaseContext() {
    }

    public ShopPurchaseContext(int quantity) {
        this.quantity = Math.max(1, quantity);
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = Math.max(1, quantity);
    }

    public String getSelectedOptionId() {
        return selectedOptionId;
    }

    public void setSelectedOptionId(String selectedOptionId) {
        this.selectedOptionId = selectedOptionId;
    }

    public PlantName getTargetPlant() {
        return targetPlant;
    }

    public void setTargetPlant(PlantName targetPlant) {
        this.targetPlant = targetPlant;
    }

    public ShopPurchaseContext copy() {
        ShopPurchaseContext copy = new ShopPurchaseContext(quantity);
        copy.selectedOptionId = selectedOptionId;
        copy.targetPlant = targetPlant;
        return copy;
    }
}

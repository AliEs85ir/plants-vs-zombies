package game.entity.plantfood;

public class PlantFoodInventory {
    private int amount;

    public static final int MAX_AMOUNT = 5;

    public PlantFoodInventory() {
        this.amount = 0;
    }

    public int getAmount() {
        return amount;
    }

    public int getMaxAmount() {
        return PlantFoodSettings.MAX_AMOUNT;
    }

    public boolean isFull() {
        return amount >= PlantFoodSettings.MAX_AMOUNT;
    }

    public boolean willBeFull(int count) {
        return amount + count - 1 >= PlantFoodSettings.MAX_AMOUNT;
    }

    public boolean hasPlantFood() {
        return amount > 0;
    }

    public void addPlantFood() {
        amount = Math.min(amount + 1, PlantFoodSettings.MAX_AMOUNT);
    }

    public void consumePlantFood() {
        amount = Math.max(amount - 1, 0);
    }

    public void setAmount(int amount) {
        this.amount = Math.max(0, Math.min(amount, PlantFoodSettings.MAX_AMOUNT));
    }

    public void clear() {
        amount = 0;
    }
}

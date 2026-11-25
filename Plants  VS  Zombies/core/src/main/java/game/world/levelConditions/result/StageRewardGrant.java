package game.world.levelConditions.result;

import app.enums.plant.PlantName;

public final class StageRewardGrant {
    public enum Kind {
        COINS,
        DIAMONDS,
        SEED_PACKETS,
        GREENHOUSE_TABLE,
        GREENHOUSE_POT,
        PLANT_UNLOCK
    }

    private final Kind kind;
    private final int amount;
    private final PlantName plantName;
    private final String label;

    private StageRewardGrant(Kind kind, int amount, PlantName plantName, String label) {
        this.kind = kind;
        this.amount = amount;
        this.plantName = plantName;
        this.label = label;
    }

    public static StageRewardGrant coins(int amount) {
        return new StageRewardGrant(Kind.COINS, amount, null, "+" + amount + " Coins");
    }

    public static StageRewardGrant diamonds(int amount) {
        return new StageRewardGrant(Kind.DIAMONDS, amount, null, "+" + amount + " Diamonds");
    }

    public static StageRewardGrant seeds(PlantName plant, int amount) {
        String name = plant != null ? plant.getLabel() : "Unknown";
        return new StageRewardGrant(
            Kind.SEED_PACKETS,
            amount,
            plant,
            "+" + amount + " " + name + " Seed Pack"
        );
    }

    public static StageRewardGrant greenhouseTable() {
        return new StageRewardGrant(Kind.GREENHOUSE_TABLE, 1, null, "+1 Greenhouse Table");
    }

    public static StageRewardGrant greenhousePot() {
        return new StageRewardGrant(Kind.GREENHOUSE_POT, 1, null, "+1 Pot");
    }

    public static StageRewardGrant plantUnlock(PlantName plant) {
        String name = plant != null ? plant.getLabel() : "Unknown";
        return new StageRewardGrant(Kind.PLANT_UNLOCK, 1, plant, "Unlocked " + name);
    }

    public Kind getKind() {
        return kind;
    }

    public int getAmount() {
        return amount;
    }

    public PlantName getPlantName() {
        return plantName;
    }

    public String getLabel() {
        return label;
    }
}

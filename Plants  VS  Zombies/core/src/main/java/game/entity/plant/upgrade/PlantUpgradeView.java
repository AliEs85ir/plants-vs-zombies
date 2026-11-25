package game.entity.plant.upgrade;

import app.enums.plant.PlantName;

public final class PlantUpgradeView {
    private final PlantName plantName;
    private final int currentLevel;
    private final int maxLevel;
    private final boolean atMaxLevel;
    private final boolean canUpgrade;
    private final int currentSeeds;
    private final int requiredSeeds;
    private final int currentCoins;
    private final int requiredCoins;
    private final float seedProgress;
    private final boolean seedsMet;
    private final boolean coinsMet;

    public PlantUpgradeView(
        PlantName plantName,
        int currentLevel,
        int maxLevel,
        boolean atMaxLevel,
        boolean canUpgrade,
        int currentSeeds,
        int requiredSeeds,
        int currentCoins,
        int requiredCoins,
        float seedProgress,
        boolean seedsMet,
        boolean coinsMet
    ) {
        this.plantName = plantName;
        this.currentLevel = currentLevel;
        this.maxLevel = maxLevel;
        this.atMaxLevel = atMaxLevel;
        this.canUpgrade = canUpgrade;
        this.currentSeeds = currentSeeds;
        this.requiredSeeds = requiredSeeds;
        this.currentCoins = currentCoins;
        this.requiredCoins = requiredCoins;
        this.seedProgress = seedProgress;
        this.seedsMet = seedsMet;
        this.coinsMet = coinsMet;
    }

    public PlantName plantName() {
        return plantName;
    }

    public int currentLevel() {
        return currentLevel;
    }

    public int maxLevel() {
        return maxLevel;
    }

    public boolean atMaxLevel() {
        return atMaxLevel;
    }

    public boolean canUpgrade() {
        return canUpgrade;
    }

    public int currentSeeds() {
        return currentSeeds;
    }

    public int requiredSeeds() {
        return requiredSeeds;
    }

    public int currentCoins() {
        return currentCoins;
    }

    public int requiredCoins() {
        return requiredCoins;
    }

    public float seedProgress() {
        return seedProgress;
    }

    public boolean seedsMet() {
        return seedsMet;
    }

    public boolean coinsMet() {
        return coinsMet;
    }

    public String seedProgressText() {
        if (atMaxLevel) {
            return "MAX";
        }
        return currentSeeds + " / " + requiredSeeds;
    }
}

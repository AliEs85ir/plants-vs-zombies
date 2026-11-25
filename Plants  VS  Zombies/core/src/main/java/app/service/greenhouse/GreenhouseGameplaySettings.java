package app.service.greenhouse;

import app.view.greenhouse.config.GreenhouseConfig;
import app.view.greenhouse.config.GreenhouseConfigLoader;

import java.time.Duration;

public final class GreenhouseGameplaySettings {
    private static GreenhouseGameplaySettings current;

    private final double marigoldProbability;
    private final double plantFoodProbability;
    private final Duration marigoldGrowthDuration;
    private final Duration plantFoodGrowthDuration;
    private final int marigoldRewardCoins;
    private final boolean boostGrantsCoins;
    private final int boostCoinReward;
    private final float billCooldownHours;
    private final GreenhouseConfig.GrowthCostConfig growthCost;
    private final boolean probabilitiesValid;
    private final GreenhouseConfig sourceConfig;

    private GreenhouseGameplaySettings(GreenhouseConfig config) {
        GreenhouseConfig safe = config == null ? new GreenhouseConfig().sanitize() : config.sanitize();
        this.sourceConfig = safe;
        this.marigoldProbability = safe.planting.marigoldProbability;
        this.plantFoodProbability = safe.planting.plantFoodProbability;
        this.probabilitiesValid = safe.planting.probabilitiesValid();
        long marigoldSeconds = Math.max(1L, Math.round(safe.growth.marigoldGrowthHours * 3600d));
        long plantFoodSeconds = Math.max(1L, Math.round(safe.growth.plantFoodGrowthHours * 3600d));
        this.marigoldGrowthDuration = Duration.ofSeconds(marigoldSeconds);
        this.plantFoodGrowthDuration = Duration.ofSeconds(plantFoodSeconds);
        this.marigoldRewardCoins = Math.max(0, safe.rewards.marigoldRewardCoins);
        this.boostGrantsCoins = safe.rewards.boostGrantsCoins;
        this.boostCoinReward = Math.max(0, safe.rewards.boostCoinReward);
        this.billCooldownHours = safe.bill.cooldownHours;
        this.growthCost = safe.growthCost;
    }

    public static GreenhouseGameplaySettings from(GreenhouseConfig config) {
        GreenhouseGameplaySettings settings = new GreenhouseGameplaySettings(config);
        current = settings;
        return settings;
    }

    public static GreenhouseGameplaySettings current() {
        if (current == null) {
            from(GreenhouseConfigLoader.load());
        }
        return current;
    }

    public static void setCurrent(GreenhouseGameplaySettings settings) {
        current = settings == null ? from(new GreenhouseConfig()) : settings;
    }

    public double marigoldProbability() {
        return marigoldProbability;
    }

    public double plantFoodProbability() {
        return plantFoodProbability;
    }

    public Duration marigoldGrowthDuration() {
        return marigoldGrowthDuration;
    }

    public Duration plantFoodGrowthDuration() {
        return plantFoodGrowthDuration;
    }

    public int marigoldRewardCoins() {
        return marigoldRewardCoins;
    }

    public boolean boostGrantsCoins() {
        return boostGrantsCoins;
    }

    public int boostCoinReward() {
        return boostCoinReward;
    }

    public float billCooldownHours() {
        return billCooldownHours;
    }

    public GreenhouseConfig.GrowthCostConfig growthCost() {
        return growthCost;
    }

    public boolean probabilitiesValid() {
        return probabilitiesValid;
    }

    public GreenhouseConfig sourceConfig() {
        return sourceConfig;
    }
}

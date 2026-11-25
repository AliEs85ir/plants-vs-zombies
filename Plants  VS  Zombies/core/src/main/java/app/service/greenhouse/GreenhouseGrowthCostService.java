package app.service.greenhouse;

import app.view.greenhouse.config.GreenhouseConfig;

import java.math.RoundingMode;
import java.time.Duration;
import java.util.Locale;

public class GreenhouseGrowthCostService {
    private final GreenhouseConfig.GrowthCostConfig config;

    public GreenhouseGrowthCostService() {
        this(GreenhouseGameplaySettings.current().growthCost());
    }

    public GreenhouseGrowthCostService(GreenhouseConfig.GrowthCostConfig config) {
        this.config = config == null ? new GreenhouseConfig.GrowthCostConfig() : config;
        this.config.sanitize();
    }

    public int compute(Duration remaining) {
        if (!config.enabled) {
            return 0;
        }
        if (remaining == null || remaining.isZero() || remaining.isNegative()) {
            return 0;
        }
        long seconds = remaining.getSeconds();
        if (remaining.getNano() > 0) {
            seconds += 1;
        }
        return computeFromSeconds(seconds);
    }

    public int compute(Duration remaining, int hours) {
        if (!config.enabled) {
            return 0;
        }
        if (hours <= 0) {
            return 0;
        }
        if (remaining == null || remaining.isZero() || remaining.isNegative()) {
            return 0;
        }
        long requestedSeconds = hours * 3600L;
        long remainingSeconds = remaining.getSeconds();
        if (remaining.getNano() > 0) {
            remainingSeconds += 1;
        }
        long seconds = Math.min(requestedSeconds, remainingSeconds);
        return computeFromSeconds(seconds);
    }

    private int computeFromSeconds(long seconds) {
        if (seconds <= 0) {
            return 0;
        }
        double units = seconds / (double) config.timeUnitSeconds;
        RoundingMode mode = parseRounding(config.roundingMode);
        int unitsRounded = switch (mode) {
            case FLOOR -> (int) Math.floor(units);
            case HALF_UP -> (int) Math.round(units);
            default -> (int) Math.ceil(units);
        };
        if (unitsRounded <= 0 && seconds > 0) {
            unitsRounded = 1;
        }
        int cost = unitsRounded * config.unitCostGems;
        if (config.minCost > 0) {
            cost = Math.max(cost, config.minCost);
        }
        if (config.maxCost > 0) {
            cost = Math.min(cost, config.maxCost);
        }
        return Math.max(0, cost);
    }

    private static RoundingMode parseRounding(String value) {
        if (value == null || value.isBlank()) {
            return RoundingMode.CEILING;
        }
        try {
            return RoundingMode.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (Exception exception) {
            return RoundingMode.CEILING;
        }
    }
}

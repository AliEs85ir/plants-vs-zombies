package game.entity.plant.upgrade;

import game.engine.time.GameClock;
import game.entity.plant.PlantDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class EffectivePlantStats {
    private final int sunCost;
    private final int maxHealth;
    private final int attackDamage;
    private final int cooldownTicks;
    private final float actionIntervalTicks;

    public EffectivePlantStats(
        int sunCost,
        int maxHealth,
        int attackDamage,
        int cooldownTicks,
        float actionIntervalTicks
    ) {
        this.sunCost = Math.max(0, sunCost);
        this.maxHealth = Math.max(1, maxHealth);
        this.attackDamage = Math.max(0, attackDamage);
        this.cooldownTicks = Math.max(0, cooldownTicks);
        this.actionIntervalTicks = Math.max(1f, actionIntervalTicks);
    }

    public static EffectivePlantStats from(PlantDefinition definition, PlantLevelModifiers modifiers) {
        PlantLevelModifiers mods = modifiers == null ? PlantLevelModifiers.NONE : modifiers;
        int baseSun = definition == null ? 0 : definition.getSunCost();
        int baseHp = definition == null ? 1 : definition.getMaxHealth();
        int baseDamage = definition == null ? 0 : definition.getAttackDamage();
        int baseCooldown = definition == null ? 0 : definition.getCooldown();
        float baseInterval = definition == null ? 1f : definition.getActionInterval();
        return fromBase(baseSun, baseHp, baseDamage, baseCooldown, baseInterval, mods);
    }

    public static EffectivePlantStats fromBase(
        int baseSunCost,
        int baseMaxHealth,
        int baseAttackDamage,
        int baseCooldownTicks,
        float baseActionIntervalTicks,
        PlantLevelModifiers modifiers
    ) {
        PlantLevelModifiers mods = modifiers == null ? PlantLevelModifiers.NONE : modifiers;
        int sunCost = baseSunCost - mods.costReduction();
        int maxHealth = baseMaxHealth + mods.extraHp();
        int attackDamage = baseAttackDamage + mods.totalExtraDamage();
        int cooldownTicks = baseCooldownTicks - mods.cooldownReductionTicks();
        float interval = baseActionIntervalTicks;
        double productionReduction = mods.productionTimeReductionSeconds() * GameClock.TICKS_PER_SECOND;
        if (productionReduction > 0d) {
            interval = (float) Math.max(1d, interval - productionReduction);
        }
        double speedBoost = mods.attackSpeedBoostPercentage();
        if (speedBoost > 0d) {
            interval = (float) Math.max(1d, interval / (1d + speedBoost));
        }
        return new EffectivePlantStats(sunCost, maxHealth, attackDamage, cooldownTicks, interval);
    }

    public int sunCost() {
        return sunCost;
    }

    public int maxHealth() {
        return maxHealth;
    }

    public int attackDamage() {
        return attackDamage;
    }

    public int cooldownTicks() {
        return cooldownTicks;
    }

    public float actionIntervalTicks() {
        return actionIntervalTicks;
    }

    /**
     * Ordered comparable stats derived from this snapshot.
     * Collection preview zips two lists by {@link NumericStat#id};
     * a new EffectivePlantStats field added here is picked up automatically.
     */
    public List<NumericStat> numericStats() {
        List<NumericStat> stats = new ArrayList<>(5);
        stats.add(new NumericStat("sunCost", sunCost, String.valueOf(sunCost)));
        stats.add(new NumericStat("maxHealth", maxHealth, String.valueOf(maxHealth)));
        stats.add(new NumericStat("attackDamage", attackDamage, String.valueOf(attackDamage)));
        stats.add(new NumericStat("cooldownTicks", cooldownTicks, formatSeconds(cooldownTicks)));
        stats.add(new NumericStat("actionIntervalTicks", actionIntervalTicks, formatSeconds(actionIntervalTicks)));
        return stats;
    }

    private static String formatSeconds(float ticks) {
        if (!Float.isFinite(ticks)) {
            return "0";
        }
        float seconds = ticks / GameClock.TICKS_PER_SECOND;
        if (Math.abs(seconds - Math.round(seconds)) < 0.05f) {
            return String.valueOf(Math.round(seconds));
        }
        return String.format(Locale.US, "%.1f", seconds);
    }

    public static final class NumericStat {
        public final String id;
        public final double comparable;
        public final String display;

        NumericStat(String id, double comparable, String display) {
            this.id = id;
            this.comparable = comparable;
            this.display = display;
        }
    }
}

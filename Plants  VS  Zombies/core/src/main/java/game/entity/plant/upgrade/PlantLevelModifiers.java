package game.entity.plant.upgrade;

import game.engine.time.GameClock;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class PlantLevelModifiers {
    public static final PlantLevelModifiers NONE = new PlantLevelModifiers(Map.of());

    private final Map<String, Object> values;

    public PlantLevelModifiers(Map<String, Object> values) {
        this.values = values == null ? Map.of() : Collections.unmodifiableMap(new LinkedHashMap<>(values));
    }

    public Map<String, Object> asMap() {
        return values;
    }

    public boolean has(String key) {
        return key != null && values.containsKey(key) && values.get(key) != null;
    }

    public int intValue(String key) {
        return (int) Math.round(doubleValue(key));
    }

    public double doubleValue(String key) {
        Object raw = values.get(key);
        if (raw == null) {
            return 0d;
        }
        if (raw instanceof Number number) {
            return number.doubleValue();
        }
        if (raw instanceof Boolean bool) {
            return bool ? 1d : 0d;
        }
        if (raw instanceof String text) {
            try {
                return Double.parseDouble(text.trim());
            } catch (NumberFormatException ignored) {
                return 0d;
            }
        }
        return 0d;
    }

    public boolean flag(String key) {
        Object raw = values.get(key);
        if (raw instanceof Boolean bool) {
            return bool;
        }
        return doubleValue(key) != 0d;
    }

    public int ticks(String secondsKey) {
        return (int) Math.round(doubleValue(secondsKey) * GameClock.TICKS_PER_SECOND);
    }

    public int extraHp() {
        return intValue(PlantUpgradeKeys.EXTRA_HP);
    }

    public int costReduction() {
        return intValue(PlantUpgradeKeys.COST_REDUCTION);
    }

    public int extraAttackerDamage() {
        return intValue(PlantUpgradeKeys.EXTRA_ATTACKER_DAMAGE);
    }

    public int extraAoEDamage() {
        return intValue(PlantUpgradeKeys.EXTRA_AOE_DAMAGE);
    }

    public int extraPoisonDamageTick() {
        return intValue(PlantUpgradeKeys.EXTRA_POISON_DAMAGE_TICK);
    }

    public int extraReflectDamage() {
        return intValue(PlantUpgradeKeys.EXTRA_REFLECT_DAMAGE);
    }

    public int extraExplodeDamage() {
        return intValue(PlantUpgradeKeys.EXTRA_EXPLODE_DAMAGE);
    }

    public int extraRange() {
        return intValue(PlantUpgradeKeys.EXTRA_RANGE);
    }

    public int totalExtraDamage() {
        return extraAttackerDamage()
            + extraAoEDamage()
            + extraPoisonDamageTick()
            + extraReflectDamage()
            + extraExplodeDamage();
    }

    public int cooldownReductionTicks() {
        return ticks(PlantUpgradeKeys.COOLDOWN_REDUCTION_SECONDS);
    }

    public double attackSpeedBoostPercentage() {
        return doubleValue(PlantUpgradeKeys.ATTACK_SPEED_BOOST_PERCENTAGE);
    }

    public double productionTimeReductionSeconds() {
        return doubleValue(PlantUpgradeKeys.PRODUCTION_TIME_REDUCTION_SECONDS);
    }

    public int growTimeReductionTicks() {
        return ticks(PlantUpgradeKeys.GROW_TIME_REDUCTION_SECONDS);
    }

    public int armTimeReductionTicks() {
        return ticks(PlantUpgradeKeys.ARM_TIME_REDUCTION_SECONDS);
    }

    public int digestTimeReductionTicks() {
        return ticks(PlantUpgradeKeys.DIGEST_TIME_REDUCTION_SECONDS);
    }

    public double extraButterChance() {
        return doubleValue(PlantUpgradeKeys.EXTRA_BUTTER_CHANCE);
    }

    public int extraImmediateSun() {
        return intValue(PlantUpgradeKeys.EXTRA_IMMEDIATE_SUN);
    }

    public int extraSunDrop() {
        return intValue(PlantUpgradeKeys.EXTRA_SUN_DROP);
    }

    public boolean hasDoubleSunChance() {
        return flag(PlantUpgradeKeys.HAS_DOUBLE_SUN_CHANCE);
    }

    static PlantLevelModifiers accumulate(Iterable<PlantLevelStep> steps, int currentLevel) {
        Map<String, Object> merged = new LinkedHashMap<>();
        if (steps == null || currentLevel < 2) {
            return new PlantLevelModifiers(merged);
        }
        for (PlantLevelStep step : steps) {
            if (step == null || step.level < 2 || step.level > currentLevel) {
                continue;
            }
            for (Map.Entry<String, Object> entry : step.modifiersOrEmpty().entrySet()) {
                if (entry.getKey() == null || entry.getKey().isBlank() || entry.getValue() == null) {
                    continue;
                }
                merge(merged, entry.getKey(), entry.getValue());
            }
        }
        return new PlantLevelModifiers(merged);
    }

    private static void merge(Map<String, Object> merged, String key, Object incoming) {
        Object existing = merged.get(key);
        if (existing == null) {
            merged.put(key, incoming);
            return;
        }
        if (isBooleanLike(existing) || isBooleanLike(incoming)) {
            merged.put(key, toBoolean(existing) || toBoolean(incoming));
            return;
        }
        merged.put(key, toDouble(existing) + toDouble(incoming));
    }

    private static boolean isBooleanLike(Object value) {
        return value instanceof Boolean;
    }

    private static boolean toBoolean(Object value) {
        if (value instanceof Boolean bool) {
            return bool;
        }
        return toDouble(value) != 0d;
    }

    private static double toDouble(Object value) {
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        if (value instanceof Boolean bool) {
            return bool ? 1d : 0d;
        }
        return 0d;
    }
}

package graphics.zombie.visibility;

import graphics.zombie.visibility.conditions.HasArmorEvaluator;
import graphics.zombie.visibility.conditions.HasEffectEvaluator;
import graphics.zombie.visibility.conditions.HealthBelowEvaluator;
import graphics.zombie.visibility.conditions.VisibilityConditionEvaluator;

import java.util.HashMap;
import java.util.Map;

public class VisibilityConditionRegistry {
    private static final Map<String, VisibilityConditionEvaluator> EVALUATORS = new HashMap<>();

    static {
        EVALUATORS.put("healthBelow", new HealthBelowEvaluator());
        EVALUATORS.put("hasEffect", new HasEffectEvaluator());
        EVALUATORS.put("hasArmor", new HasArmorEvaluator());
    }

    public static VisibilityConditionEvaluator getEvaluator(String type) {
        return EVALUATORS.get(type);
    }
}

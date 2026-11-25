package graphics.zombie.visibility.conditions;

import game.entity.zombie.Zombie;
import graphics.zombie.visibility.VisibilityConfig;

public class HealthBelowEvaluator implements VisibilityConditionEvaluator {
    @Override
    public boolean evaluate(Zombie zombie, VisibilityConfig.ConditionVisibility condition) {
        double threshold = ((Number) condition.getValue()).doubleValue();
        double maxHealth = zombie.getPropertyData().getInitialHealth();
        return zombie.getCurrentHealth() / maxHealth < threshold;
    }
}

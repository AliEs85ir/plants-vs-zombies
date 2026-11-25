package graphics.zombie.visibility.conditions;

import game.entity.zombie.Zombie;
import graphics.zombie.visibility.VisibilityConfig;

public class HasEffectEvaluator implements VisibilityConditionEvaluator {
    @Override
    public boolean evaluate(Zombie zombie, VisibilityConfig.ConditionVisibility condition) {
        String effectType = (String) condition.getValue();
        return zombie.getEffects().stream()
            .anyMatch(effect -> effect.getType().name().equals(effectType));
    }
}

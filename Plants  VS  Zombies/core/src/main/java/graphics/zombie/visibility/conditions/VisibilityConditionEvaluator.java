package graphics.zombie.visibility.conditions;

import game.entity.zombie.Zombie;
import graphics.zombie.visibility.VisibilityConfig;

public interface VisibilityConditionEvaluator {
    boolean evaluate(Zombie zombie, VisibilityConfig.ConditionVisibility condition);
}


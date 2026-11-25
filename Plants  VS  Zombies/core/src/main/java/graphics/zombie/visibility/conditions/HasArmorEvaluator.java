package graphics.zombie.visibility.conditions;

import game.entity.zombie.Zombie;
import graphics.zombie.visibility.VisibilityConfig;

public class HasArmorEvaluator implements VisibilityConditionEvaluator {
    @Override
    public boolean evaluate(Zombie zombie, VisibilityConfig.ConditionVisibility condition) {
        String armorAlias = (String) condition.getValue();
        String requiredPhase = condition.getPhase();

        return zombie.getArmors().stream()
            .anyMatch(armor -> {
                boolean aliasMatches = armor.getProperty().getAlias().equals(armorAlias);

                if (aliasMatches && requiredPhase != null) {
                    return armor.getPhase().name().equals(requiredPhase.toUpperCase());
                }

                return aliasMatches;
            });
    }
}

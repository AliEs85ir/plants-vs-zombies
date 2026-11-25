package graphics.general.effects;

import app.enums.plant.PlantState;
import game.entity.plant.Plant;
import game.entity.plant.PlantHealth;
import graphics.general.animation.condition.AnimationCondition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EffectConditionsTest {
    @Test
    void plantAliveStopsMatchingAfterDeath() {
        Plant plant = new Plant();
        plant.setHealth(new PlantHealth(100));
        AnimationCondition<Plant> alive = EffectConditions.plantAlive(plant);

        assertTrue(alive.matches(plant));
        plant.setState(PlantState.DEAD);
        assertFalse(alive.matches(plant));
    }

    @Test
    void combinedFreezeVisualStopsWhenPlantDiesOrTimeElapses() {
        Plant plant = new Plant();
        plant.setHealth(new PlantHealth(100));
        AnimationCondition<Plant> condition = EffectConditions.and(
            EffectConditions.plantAlive(plant),
            EffectConditions.withinSeconds(1f)
        );

        assertTrue(condition.matches(plant));
        condition.update(1.1f);
        assertFalse(condition.matches(plant));

        AnimationCondition<Plant> whileAlive = EffectConditions.and(
            EffectConditions.plantAlive(plant),
            EffectConditions.withinSeconds(10f)
        );
        assertTrue(whileAlive.matches(plant));
        plant.setState(PlantState.DEAD);
        assertFalse(whileAlive.matches(plant));
    }
}

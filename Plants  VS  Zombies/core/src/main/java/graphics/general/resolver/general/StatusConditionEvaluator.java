package graphics.general.resolver.general;

import app.enums.sun.SunState;
import game.entity.lawnmower.LawnMower;
import game.entity.obstacle.Obstacle;
import game.entity.plant.Plant;
import game.entity.sun.Sun;
import game.entity.zombie.Zombie;
import graphics.lawnmower.LMVisualState;
import graphics.obstacle.ObstacleVisualState;
import graphics.plant.PlantVisualState;
import graphics.sun.SunVisualState;
import graphics.zombie.ZombieVisualState;

public class StatusConditionEvaluator {

    public PlantVisualState evaluateEntityConditions(Plant plant) {
        if (plant == null) {
            return null;
        }
        if (plant.getHealth() != null && plant.getHealth().isDead()) {
            return PlantVisualState.DEATH;
        }
        if (plant.isFrozen()) {
            return PlantVisualState.FROZEN;
        }
        if (plant.isSleeping()) {
            return PlantVisualState.SLEEP;
        }
        return null;
    }

    public ZombieVisualState evaluateEntityConditions(Zombie zombie) {
        if(zombie == null) {
            return null;
        }

        if(zombie.isDead() && !zombie.persistAfterDeath()) {
            return ZombieVisualState.DEATH;
        }

        return null;
    }

    public LMVisualState evaluateEntityConditions(LawnMower lm) {
        if(lm == null) {
            return null;
        }
        if(lm.isUsed()) {
            return LMVisualState.TRANSITION;
        }

        return null;
    }

    public SunVisualState evaluateEntityConditions(Sun sun) {
        if(sun == null) {
            return null;
        }
        if(sun.getState() != SunState.COLLECTED) {
            return SunVisualState.IDLE;
        }

        return null;
    }

    public ObstacleVisualState evaluateEntityConditions(Obstacle obstacle) {

        if(obstacle.isDead()) {
            return ObstacleVisualState.DEATH;
        }
        return null;

    }
}

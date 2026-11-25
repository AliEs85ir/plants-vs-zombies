package graphics.general.effects;

import app.model.App;
import game.engine.time.GameClock;
import game.entity.plant.Plant;
import game.entity.plant.PlantFreezeController;
import game.entity.zombie.Zombie;
import graphics.general.animation.condition.AnimationCondition;
import graphics.general.event.VisualEvent;
import graphics.general.event.ZombieVisualEventType;

public class EffectConditions {
    public static AnimationCondition<Object> fromEvent(String eventType) {
        return context -> {

            if (context instanceof Zombie zombie) {
                VisualEvent<ZombieVisualEventType> event = App.getRealTimeEngine()
                    .getGraphicsEngine()
                    .getZombieInstance(zombie) == null ? null :
                    App.getRealTimeEngine()
                        .getGraphicsEngine()
                        .getZombieInstance(zombie)
                    .getCurrentEvent();
                return event != null && event.getType().name().equals(eventType);
            }

            // If you have a dedicated EffectEvent system later, check it here:
            // if (context instanceof EffectState state) { ... }

            return false;
        };
    }

    public static AnimationCondition<Object> healthBelow(float ratio) {
        return context -> {
            if (context instanceof Zombie zombie) {
                return (float) zombie.getCurrentHealth() / zombie.getPropertyData().getInitialHealth() <= ratio;
            }
            return false;
        };
    }

    public static AnimationCondition<Object> withinSeconds(float durationSeconds) {
        return new AnimationCondition<>() {
            private float elapsed;

            @Override
            public void update(float delta) {
                elapsed += delta;
            }

            @Override
            public boolean matches(Object context) {
                return elapsed < durationSeconds;
            }
        };
    }
    public static AnimationCondition<Object> plantFreeze1(PlantFreezeController controller) {
        return new AnimationCondition<>() {
            @Override
            public boolean matches(Object context) {
                boolean b = controller.getFreezeLevel() == 1;
                return b;
            }
        };
    }
    public static AnimationCondition<Object> plantFreeze2(PlantFreezeController controller) {
        return new AnimationCondition<>() {
            @Override
            public boolean matches(Object context) {
                boolean b = controller.getFreezeLevel() == 2;
                return b;
            }
        };
    }
    public static AnimationCondition<Object> sheepCondition(Zombie zombie, Plant plant) {
        return new AnimationCondition<>() {
            @Override
            public boolean matches(Object context) {
                if(zombie.isDead() || plant.isDead()) {
                    return false;
                }
                return true;
            }
        };
    }
    public static AnimationCondition<Object> plantZombieCondition(Zombie zombie) {
        return new AnimationCondition<>() {
            @Override
            public boolean matches(Object context) {
                return !zombie.isDead();
            }
        };
    }

    public static AnimationCondition<Plant> plantAlive(Plant plant) {
        return context -> plant != null && !plant.isDead();
    }

    public static <M> AnimationCondition<M> and(
        AnimationCondition<? super M> left,
        AnimationCondition<? super M> right
    ) {
        return new AnimationCondition<>() {
            @Override
            public void update(float delta) {
                if (left != null) {
                    left.update(delta);
                }
                if (right != null) {
                    right.update(delta);
                }
            }

            @Override
            public boolean matches(M context) {
                boolean leftMatches = left == null || left.matches(context);
                boolean rightMatches = right == null || right.matches(context);
                return leftMatches && rightMatches;
            }
        };
    }

    public static AnimationCondition<Object> withinTicks(long ticks) {
        return withinSeconds(ticks / (float) GameClock.TICKS_PER_SECOND);
    }


}

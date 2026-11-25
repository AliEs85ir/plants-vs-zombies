package graphics.general.modifier;

import app.model.App;
import game.entity.obstacle.Obstacle;
import game.entity.zombie.Zombie;
import graphics.general.modifier.visualModifiers.WhiteDamageModifier;
import graphics.obstacle.ObstacleRenderInstance;
import graphics.zombie.ZombieRenderInstance;

public final class HitFlashEffect {
    private static final float WHITE_DURATION = 0.18f;

    private HitFlashEffect() {
    }

    public static void apply(Zombie zombie) {
        if (zombie == null || App.getRealTimeEngine() == null || App.getRealTimeEngine().getGraphicsEngine() == null) {
            return;
        }
        ZombieRenderInstance instance = App.getRealTimeEngine().getGraphicsEngine().getZombieInstance(zombie);
        if (instance == null) {
            return;
        }
        instance.getModifierController().addOrReplace(new WhiteDamageModifier(WHITE_DURATION));
    }

    public static void apply(Obstacle obstacle) {
        if (obstacle == null || App.getRealTimeEngine() == null || App.getRealTimeEngine().getGraphicsEngine() == null) {
            return;
        }
        ObstacleRenderInstance instance = App.getRealTimeEngine().getGraphicsEngine().getObstacleInstance(obstacle);
        if (instance == null) {
            return;
        }
        instance.getModifierController().addOrReplace(new WhiteDamageModifier(WHITE_DURATION));
    }
}

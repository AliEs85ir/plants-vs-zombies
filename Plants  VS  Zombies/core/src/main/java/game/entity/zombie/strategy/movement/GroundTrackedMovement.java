package game.entity.zombie.strategy.movement;

import app.model.App;
import game.entity.zombie.Zombie;
import game.service.Zombie.ZombieService;
import graphics.general.event.ZombieVisualEventType;
import graphics.zombie.GroundSwatchTracker;
import graphics.zombie.ZombieRenderInstance;

public class GroundTrackedMovement implements MovementStrategy {

    public static boolean appliesTo(Zombie zombie) {
        return zombie != null && appliesTo(zombie.getName());
    }

    public static boolean appliesTo(String alias) {
        if (alias == null || alias.isEmpty()) {
            return false;
        }
        return alias.startsWith("Basic")
            || alias.contains("Flag")
            || alias.contains("Gargantuar")
            || alias.contains("Imp")
            || alias.contains("Newspaper");
    }

    public static boolean isDriving(Zombie zombie) {
        return zombie != null && zombie.getMovementStrategy() instanceof GroundTrackedMovement;
    }

    public static MovementStrategy defaultWalkStrategy(Zombie zombie) {
        return appliesTo(zombie) ? new GroundTrackedMovement() : new NormalMovement();
    }

    @Override
    public boolean move(Zombie zombie, ZombieService zombieService, long currentTick) {
        if (zombie.getBody() == null) {
            return false;
        }
        zombie.pushEvent(ZombieVisualEventType.WALK);
        if (trackerReady(zombie)) {
            zombie.getBody().setLinearVelocity(0f, 0f);
            return true;
        }
        float vx = (float) -zombie.getSpeed();
        zombie.getBody().setLinearVelocity(vx, 0f);
        return true;
    }

    private static boolean trackerReady(Zombie zombie) {
        if (App.getRealTimeEngine() == null || App.getRealTimeEngine().getGraphicsEngine() == null) {
            return false;
        }
        ZombieRenderInstance instance = App.getRealTimeEngine().getGraphicsEngine().getZombieInstance(zombie);
        if (instance == null) {
            return false;
        }
        GroundSwatchTracker tracker = instance.getGroundSwatchTracker();
        return tracker != null && tracker.isReady();
    }
}

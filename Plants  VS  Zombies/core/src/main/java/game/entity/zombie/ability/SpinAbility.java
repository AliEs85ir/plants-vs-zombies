package game.entity.zombie.ability;

import app.enums.obstacle.ObstacleType;
import app.enums.plant.PlantName;
import app.model.App;
import game.engine.time.GameClock;
import game.entity.obstacle.ObstacleFactory;
import game.entity.obstacle.ability.ProjectileAbility;
import game.entity.projectile.Projectile;
import game.entity.projectile.ShooterProjectile;
import game.entity.zombie.Zombie;
import game.entity.zombie.strategy.movement.NormalMovement;
import game.entity.zombie.strategy.movement.SpinMoveStrategy;
import game.service.Zombie.ZombieService;
import graphics.general.event.ZombieVisualEventType;

import java.util.ArrayList;
import java.util.List;

public class SpinAbility extends AbstractZombieAbility{
    private Zombie zombie;

    public enum SpinState {
        IDLE,
        SPINNING,
    }

    private boolean initialized;
    private SpinState state;
    private SpinState previousState;

    private long spinStartTick = -1;
    private static final long SPIN_DURATION_TICKS = 30;

    public SpinAbility() {
        this.state = SpinState.IDLE;
        this.previousState = null;
    }

    @Override
    public void update(Zombie zombie, ZombieService zombieService, long currentTick) {
        if (!initialized) {
            this.zombie = zombie;
            initialized = true;
        }

        boolean stateChanged = (state != previousState);

        switch (state) {
            case IDLE -> {
                if (stateChanged) {
                    zombie.setMoveSpeedMultiplier(1.0);
                    zombie.setMovementStrategy(new NormalMovement());
                    spinStartTick = -1;
                }
            }
            case SPINNING -> {
                if (stateChanged) {
                    zombie.pushEvent(ZombieVisualEventType.SPINUP);
                    zombie.pushEvent(ZombieVisualEventType.SPIN);
                    zombie.setMovementStrategy(new SpinMoveStrategy());
                    zombie.setMoveSpeedMultiplier(1.3);
                    spinStartTick = currentTick;
                }

                // Check timer duration
                if (currentTick - spinStartTick >= SPIN_DURATION_TICKS) {
                    zombie.pushEvent(ZombieVisualEventType.SPINDOWN);
                    setState(SpinState.IDLE);
                }
            }
        }

        previousState = state;
    }

    @Override
    public void onDeath(Zombie zombie) {

    }

    public SpinState getState() {
        return state;
    }

    public void setState(SpinState state) {
        this.state = state;
    }

    public boolean spawnProjectile(PlantName name) {
        switch (name) {
            case PEASHOOTER, THREEPEATER -> {
                spawnObstacle(ObstacleType.PEASHOOTER);
                return true;
            }
            case SNOW_PEA -> {
                spawnObstacle(ObstacleType.SNOW_PEA);
                return true;
            }
        }
        System.err.println(name);
        return false;
    }

    private void spawnObstacle(ObstacleType type) {
        System.err.println("SPAWNED OBS");
        App.getCurrentGameEngine().getTaskManager().scheduleRelative(
            GameClock.currentTick,
            5,
            () -> spawn(type)
        );
    }

    private void spawn(ObstacleType type) {
        ObstacleFactory.getInstance().createRuntimeObstacle(
            type,
            20,
            20,
            (float) zombie.getPosition().getX(),
            (float) zombie.getPosition().getY(),
            List.of(new ProjectileAbility())
        );
    }
}

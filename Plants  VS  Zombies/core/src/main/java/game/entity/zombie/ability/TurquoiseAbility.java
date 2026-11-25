package game.entity.zombie.ability;

import app.model.App;
import game.engine.time.GameClock;
import game.entity.plant.Plant;
import game.entity.zombie.Zombie;
import game.entity.zombie.strategy.movement.NormalMovement;
import game.entity.zombie.strategy.movement.StationaryMovement;
import game.service.Zombie.ZombieService;
import game.service.sun.SunSpawner;
import game.world.playground.Grid;
import graphics.general.event.ZombieVisualEventType;

public class TurquoiseAbility extends AbstractZombieAbility{

    private enum State { MOVING, CHARGING, COOLDOWN, PROCESS_COOLDOWN }

    private State currentState = State.MOVING;
    private long stateEndTick = 0;
    private long lastStealTick = 0;
    private long processStartTick = 0;
    private boolean processReady = true;

    private final double chargingTimeSeconds = 4.7;
    private final double cooldownTimeSeconds = 4.8;
    private final double processCooldownSeconds = 30.0;
    private final int ticksPerSecond = GameClock.TICKS_PER_SECOND;
    private int collectedSunAmount = 0;

    @Override
    public void update(Zombie zombie, ZombieService zombieService, long currentTick) {
        if (zombie.isDead()) return;

        switch (currentState) {
            case MOVING:
                handleMovingState(zombie, zombieService, currentTick);
                break;

            case CHARGING:
                handleChargingState(zombie, zombieService, currentTick);
                break;

            case COOLDOWN:
                handleCooldownState(zombie, currentTick);
                break;

            case PROCESS_COOLDOWN:
                handleProcessCooldownState(zombie, currentTick);
                break;
        }
    }

    private void handleMovingState(Zombie zombie, ZombieService zombieService, long currentTick) {
        if (processReady && isTherePlantInRange(zombie, zombieService)) {
            zombie.pushEvent(ZombieVisualEventType.SKULL_UP);
            processStartTick = currentTick;
            transitionToCharging(zombie, currentTick);
        }
    }

    private void transitionToCharging(Zombie zombie, long currentTick) {
        currentState = State.CHARGING;
        stateEndTick = currentTick + (long)(chargingTimeSeconds * ticksPerSecond);
        lastStealTick = currentTick;
        zombie.setMovementStrategy(new StationaryMovement());
        zombie.getWorld().addEvent("💎 Turquoise Zombie spotted a plant! Charging laser...");
    }

    private void handleChargingState(Zombie zombie, ZombieService zombieService, long currentTick) {
        zombie.pushEvent(ZombieVisualEventType.SKULL);

        if (currentTick - lastStealTick >= ticksPerSecond) {
            zombieService.getWorld().removeSunAmount(25);
            zombie.getWorld().addEvent("☀️ SUN STOLEN BY TURQUOISE! (-25)");
            collectedSunAmount += 25;
            lastStealTick = currentTick;
        }

        if (currentTick >= stateEndTick) {
            zombie.pushEvent(ZombieVisualEventType.SKULL_DOWN);
            zombie.pushEvent(ZombieVisualEventType.LASER);
            transitionToCooldown(zombie, zombieService, currentTick);
        }
    }

    private void transitionToCooldown(Zombie zombie, ZombieService zombieService, long currentTick) {
        App.getCurrentGameEngine().getTaskManager().scheduleRelative(
            currentTick,
            20,
             () -> executeAttack(zombie, zombieService)
        );
        currentState = State.COOLDOWN;
        stateEndTick = currentTick + (long)(cooldownTimeSeconds * ticksPerSecond);
        zombie.getWorld().addEvent("⚡ LASER FIRED! Turquoise is cooling down...");
    }

    private void handleCooldownState(Zombie zombie, long currentTick) {
        if (currentTick >= stateEndTick) {
            // After regular cooldown, transition to process cooldown
            transitionToProcessCooldown(zombie, currentTick);
        }
    }

    private void transitionToProcessCooldown(Zombie zombie, long currentTick) {
        currentState = State.PROCESS_COOLDOWN;
        processReady = false; // Mark process as not ready
        stateEndTick = currentTick + (long)(processCooldownSeconds * ticksPerSecond);
        zombie.setMovementStrategy(new NormalMovement());
        zombie.getWorld().addEvent("💎 Turquoise Zombie is on process cooldown...");
    }

    private void handleProcessCooldownState(Zombie zombie, long currentTick) {
        if (currentTick >= stateEndTick) {
            // Process cooldown finished
            currentState = State.MOVING;
            processReady = true; // Mark process as ready again
            zombie.getWorld().addEvent("💎 Turquoise Zombie can start a new process.");
        }
    }

    private boolean isTherePlantInRange(Zombie zombie, ZombieService zombieService) {
        for (Plant plant : zombieService.getWorld().getPlants()) {
            Grid grid = zombieService.getWorld().getGrid();
            if (grid.getEntityLane(zombie) == grid.getEntityLane(plant)) {
                if (inRange(plant, zombie)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean inRange(Plant plant, Zombie zombie) {
        double distance = zombie.getPosition().getX() - plant.getPosition().getX();
        double cellWidth = zombie.getWorld().getGrid().getConfig().getCellWidth();

        return distance > 0 && distance < cellWidth * 3;
    }

    private void executeAttack(Zombie zombie, ZombieService zombieService) {
        for (Plant plant : zombieService.getWorld().getPlants()) {
            Grid grid = zombieService.getWorld().getGrid();
            if (grid.getEntityLane(zombie) == grid.getEntityLane(plant)) {
                if (inRange(plant, zombie)) {
                    plant.markDead(zombie.getWorld());
                }
            }
        }
    }

    @Override
    public void onDeath(Zombie zombie) {
        int sunToSpawn = (int)(collectedSunAmount / 2.0f);
        if (sunToSpawn <= 0) {
            return;
        }

        if (App.getCurrentGameEngine() != null) {
            App.getCurrentGameEngine().getTaskManager().scheduleRelative(
                GameClock.currentTick,
                1,
                () -> {
                    SunSpawner sunSpawner = new SunSpawner(zombie.getWorld());
                    if(App.getRealTimeEngine().getPhysicService().getB2dWorld().isLocked()) {
                        return;
                    }
                    sunSpawner.spawnZombieSun(zombie, sunToSpawn);
                }
            );
        }
    }
}

package game.entity.zombie.ability;

import app.model.App;
import game.entity.zombie.Zombie;
import game.service.Zombie.ZombieService;
import game.service.sun.SunSpawner;
import game.world.GameWorld;
import graphics.general.event.ZombieVisualEventType;

public class SunProductionAbility implements ZombieAbility{
    private final int SUN_COOLDOWN_TICKS = 100;
    private long lastSpawnedTick = 0;
    private SunSpawner sunSpawner;

    public SunProductionAbility(GameWorld world) {
        this.sunSpawner = new SunSpawner(world);
    }

    @Override
    public void update(Zombie zombie, ZombieService zombieService, long currentTick) {
        if(canSpawnSun(currentTick)) {
            zombie.pushEvent(ZombieVisualEventType.POWER_UP);
            App.getCurrentGameEngine().getTaskManager().scheduleRelative(
                currentTick,
                10,
                () -> sunSpawner.spawnZombieSun(zombie, 50)
            );
            lastSpawnedTick = currentTick;
        }
    }

    @Override
    public void onDeath(Zombie zombie) {

    }

    private boolean canSpawnSun(long currentTick) {
        return currentTick - lastSpawnedTick > SUN_COOLDOWN_TICKS;
    }
}

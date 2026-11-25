package game.world.minigame.izombie;

import game.entity.registry.ZombieRegistry;
import game.entity.zombie.data.properties.Property;

public class ZombieCooldown {
    private final String name;
    private final long cooldownTicks;
    private long lastPlacedTick;
    private long currentTick;
    private boolean clearCooldown;

    public ZombieCooldown(String name) {
        this.name = name;
        this.cooldownTicks = getCoolDown(name);
        this.clearCooldown = true;
    }

    public void update(long currentTick) {
        this.currentTick = currentTick;
    }

    public boolean isAvailable() {
        if (clearCooldown) return true;
        return (currentTick - lastPlacedTick) >= cooldownTicks;
    }

    public void startCooldown(long currentTick) {
        this.lastPlacedTick = currentTick;
        this.clearCooldown = false;
    }

    public void clearCooldown() {
        this.clearCooldown = true;
    }


    public double getProgress() {
        if (clearCooldown || isAvailable()) return 1.0;
        long effective = cooldownTicks;
        if (effective <= 0) return 1.0;
        long elapsed = currentTick - lastPlacedTick;
        return Math.min(1.0, (double) elapsed / effective);
    }

    public long getRemainingTicks() {
        if (clearCooldown || isAvailable()) return 0;
        long effective = cooldownTicks;
        long elapsed = currentTick - lastPlacedTick;
        return Math.max(0, effective - elapsed);
    }

    public static int getCoolDown(String name){
        Property property = ZombieRegistry.getZombie(name);
        return property.getCooldown();
    }

}

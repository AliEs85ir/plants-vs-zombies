package game.entity.plant;

import app.enums.plant.PlantName;
import game.service.plant.PlantUpgradeService;

// >>> Represents plant cooldown state.
public class PlantCooldown
{
    private final PlantName name;
    private final long cooldownTicks;
    private long lastPlantTick;
    private long currentTick;
    private boolean clearCooldown;

    public PlantCooldown(PlantName name) {
        this.name = name;
        this.cooldownTicks = getCoolDown(name);
        this.clearCooldown = true;
    }

    public void update(long currentTick) {
        this.currentTick = currentTick;
    }

    public boolean isAvailable() {
        if (clearCooldown) return true;
        return (currentTick - lastPlantTick) >= cooldownTicks;
    }

    public void startCooldown(long currentTick) {
        this.lastPlantTick = currentTick;
        this.clearCooldown = false;
    }

    public void clearCooldown() {
        this.clearCooldown = true;
    }


    public double getProgress() {
        if (clearCooldown || isAvailable()) return 1.0;
        long effective = cooldownTicks;
        if (effective <= 0) return 1.0;
        long elapsed = currentTick - lastPlantTick;
        return Math.min(1.0, (double) elapsed / effective);
    }

    public long getRemainingTicks() {
        if (clearCooldown || isAvailable()) return 0;
        long effective = cooldownTicks;
        long elapsed = currentTick - lastPlantTick;
        return Math.max(0, effective - elapsed);
    }

    public long getLastPlantTick() {
        return lastPlantTick;
    }

    public boolean isCooldownCleared() {
        return clearCooldown;
    }

    public void restore(long restoredLastPlantTick, boolean restoredClearCooldown, long restoredCurrentTick) {
        this.lastPlantTick = Math.max(0L, restoredLastPlantTick);
        this.clearCooldown = restoredClearCooldown;
        this.currentTick = Math.max(0L, restoredCurrentTick);
    }

    public static int getCoolDown(PlantName name){
        return PlantUpgradeService.getEffectiveStats(name).cooldownTicks();
    }

}

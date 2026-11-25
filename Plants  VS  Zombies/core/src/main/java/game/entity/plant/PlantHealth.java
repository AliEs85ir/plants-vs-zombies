package game.entity.plant;

import app.model.App;
import game.entity.zombie.Zombie;

public class PlantHealth {
    private double currentHealth;
    private final int maxHealth;

    public PlantHealth(int maxHealth) {
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth <= 0 ? 1 : maxHealth;
    }

    public double getCurrentHealth() {
        return currentHealth;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void takeDamage(double damage , PlantDamage plantDamage , Zombie attacker , long currentTick) {
        if (damage < 0) return;
        plantDamage.setLastDamageTick(currentTick);
        plantDamage.setZombie(attacker);
        currentHealth = Math.max(0, currentHealth - damage);
    }

    public void heal(int amount) {
        if (amount < 0) return;
        currentHealth = Math.min(maxHealth, currentHealth + amount);
    }

    public void die()
    {
        this.currentHealth = 0;
    }

    public boolean isDead() {
        return currentHealth <= 0;
    }

    public void setCurrentHealth(double health) {
        this.currentHealth = Math.max(0, Math.min(maxHealth, health));
    }

    public float getHealthRatio() {
        return (float) currentHealth / maxHealth;
    }
}

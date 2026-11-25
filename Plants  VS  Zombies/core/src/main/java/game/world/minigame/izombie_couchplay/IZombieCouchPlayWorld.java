package game.world.minigame.izombie_couchplay;

import game.world.minigame.Minigame;
import game.world.minigame.izombie.IZombieWorld;
import game.world.seasons.Season;

public class IZombieCouchPlayWorld extends IZombieWorld {
    private int zombieSunAmount;

    public IZombieCouchPlayWorld(Minigame logic, Season season) {
        super(logic, season);
        this.zombieSunAmount = getSunAmount();
    }

    public int getPlantSunAmount() {
        return getSunAmount();
    }

    public int getZombieSunAmount() {
        return zombieSunAmount;
    }

    public void addZombieSunAmount(int amount) {
        this.zombieSunAmount += amount;
    }

    public void removeZombieSunAmount(int amount) {
        this.zombieSunAmount = Math.max(0, this.zombieSunAmount - amount);
    }

    public void setZombieSunAmount(int amount) {
        this.zombieSunAmount = Math.max(0, amount);
    }
}

package game.world.minigame.izombie;

import game.world.minigame.Minigame;
import game.world.minigame.MinigameWorld;
import game.world.seasons.Season;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IZombieWorld extends MinigameWorld {
    private Map<String, ZombieCooldown> selectedGameZombies;
    private boolean spawnedZombie;
    private boolean timeUp;

    public IZombieWorld(Minigame logic, Season season) {
        super(logic, season);
        selectedGameZombies = new HashMap<>();
    }

    public boolean hasSpawnedZombie() {
        return spawnedZombie;
    }

    public void markZombieSpawned() {
        spawnedZombie = true;
    }

    public boolean isOnCooldown(String alias) {
        ZombieCooldown cd = selectedGameZombies.get(alias);
        return cd != null && !cd.isAvailable();
    }

    public void loadSelectedGameZombies(List<String> selectedZombies) {
        this.selectedGameZombies = new HashMap<>();
        for (String name : selectedZombies) {
            selectedGameZombies.put(name, new ZombieCooldown(name));
        }
    }

    public ZombieCooldown getCoolDownZombie(String name) {
        return selectedGameZombies.getOrDefault(name, null);
    }

    public ArrayList<ZombieCooldown> getZombieCooldowns() {
        return new ArrayList<>(selectedGameZombies.values());
    }

    public boolean isTimeUp() {
        return timeUp;
    }

    public void setTimeUp(boolean timeUp) {
        this.timeUp = timeUp;
    }
}

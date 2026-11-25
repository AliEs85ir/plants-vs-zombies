package game.entity.plant;

import game.entity.zombie.Zombie;

public class PlantDamage {
    private long lastDamageTick = -1;
    private Zombie zombie;

    public void tackDamage(Zombie zombie , long currentTick)
    {
        this.lastDamageTick = currentTick;
        this.zombie = zombie;
    }

    public long getLastDamageTick() {
        return lastDamageTick;
    }

    public void setLastDamageTick(long lastDamageTick) {
        this.lastDamageTick = lastDamageTick;
    }

    public Zombie getZombie() {
        return zombie;
    }

    public void setZombie(Zombie zombie) {
        this.zombie = zombie;
    }
}

package game.entity.zombie.data.properties.ancientEgypt;

import game.entity.zombie.data.properties.ZombieProperty;

public class TombRaiserProperty extends ZombieProperty {
    private int Ammo;
    private int NumberOfTombsToSpawn;
    private int TimeBetweenCasts;
    private int TimeBetweenRaisings;

    @Override
    public int getAmmo() {
        return Ammo;
    }

    @Override
    public int getNumberOfTombsToSpawn() {
        return NumberOfTombsToSpawn;
    }

    @Override
    public float getTimeBetweenCasts() {
        return TimeBetweenCasts;
    }

    @Override
    public int getTimeBetweenRaisings() {
        return TimeBetweenRaisings;
    }
}

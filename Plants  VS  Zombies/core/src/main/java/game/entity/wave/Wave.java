package game.entity.wave;

import game.entity.zombie.Zombie;

import java.util.ArrayList;
import java.util.List;

// >>> Represents a single zombie wave.
public class Wave
{
    // -> Properties

    private int number; // Wave index
    private int difficulty; // Wave difficulty value
    private boolean finalWave; // Is this the final wave
    private final List<Zombie> zombies; // Zombies belonging to this wave
    private long delayTicks;


    // -> Constructor

    public Wave()
    {
        this.zombies = new ArrayList<>();
    }


    // -> Methods
    // Returns wave number.
    public int getNumber()
    {
        // TODO
        return number;
    }

    // Changes wave number.
    public void setNumber(int number)
    {
        // TODO
        this.number = number;
    }

    // Returns difficulty.
    public int getDifficulty()
    {
        // TODO
        return difficulty;
    }

    // Changes difficulty.
    public void setDifficulty(int difficulty)
    {
        // TODO
        this.difficulty = difficulty;
    }

    // Determines whether this is final wave.
    public boolean isFinalWave()
    {
        // TODO
        return finalWave;
    }

    // Changes final wave status.
    public void setFinalWave(boolean finalWave)
    {
        // TODO
        this.finalWave = finalWave;
    }

    // Returns all wave zombies.
    public List<Zombie> getZombies()
    {
        // TODO
        return zombies;
    }

    // Adds zombie to wave.
    public void addZombie(Zombie zombie)
    {
        // TODO
        zombies.add(zombie);
    }

    // Removes zombie from wave.
    public void removeZombie(Zombie zombie)
    {
        // TODO
        zombies.remove(zombie);
    }

    public long getDelayTicks()
    {
        return delayTicks;
    }

    public void setDelayTicks(long delayTicks)
    {
        this.delayTicks = delayTicks;
    }
}

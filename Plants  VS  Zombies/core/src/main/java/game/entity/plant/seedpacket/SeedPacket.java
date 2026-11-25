package game.entity.plant.seedpacket;

import app.enums.plant.PlantName;

public class SeedPacket
{
    private final PlantName plantName;
    private int amount;

    public SeedPacket(PlantName name)
    {
        this(name, 1);
    }

    public SeedPacket(PlantName name, int amount)
    {
        this.plantName = name;
        this.amount = Math.max(0, amount);
    }

    public void addPacket(int amount)
    {
        if (amount > 0)
        {
            this.amount += amount;
        }
    }

    public boolean removePacket(int amount)
    {
        if (amount <= 0 ) return false;

        this.amount = Math.max(0 , this.amount - amount);
        return true;
    }

    public int getAmount()
    {
        return amount;
    }

    public PlantName getPlantName()
    {
        return plantName;
    }

    public boolean isEmpty()
    {
        return amount <= 0;
    }

    public boolean hasEnough(int requiredAmount)
    {
        return amount >= requiredAmount;
    }
}

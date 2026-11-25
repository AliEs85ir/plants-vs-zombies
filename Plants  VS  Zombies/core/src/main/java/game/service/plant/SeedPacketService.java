package game.service.plant;

import app.enums.plant.PlantName;
import game.entity.plant.seedpacket.SeedBank;
import game.entity.plant.seedpacket.SeedPacket;

// >>> Handles seed packet operations.
public class SeedPacketService
{
    // -> Properties
    private SeedBank seedBank;


    // -> Methods

    // Returns seed bank.
    public SeedBank getSeedBank()
    {
        // TODO: implement
        return seedBank;
    }

    // Finds packet for specific plant.
    public SeedPacket findPacket(PlantName PlantName)
    {
        // TODO: implement
        return null;
    }

    // Checks whether plant can be planted.
    public boolean canPlant(
            PlantName PlantName,
            long currentTick)
    {
        // TODO: implement
        return false;
    }

    // Starts packet cooldown.
    public void registerPlanting(
            PlantName PlantName,
            long currentTick)
    {
        // TODO: implement
    }
}

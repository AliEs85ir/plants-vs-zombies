package app.model.greenhouse;

import app.enums.greenhouse.PotStatus;
import common.model.Position;

public class GreenhousePot
{
    private Position position;
    private PotStatus potStatus;
    private GreenhousePlant plant;
    private final int cost;

    public GreenhousePot(Position position, PotStatus potStatus , int cost) {
        setPosition(position);
        this.potStatus = potStatus;
        this.plant = null;
        this.cost = cost;
    }

    public Position getPosition()
    {
        return position;
    }

    public boolean isUnlocked()
    {
        return potStatus != PotStatus.LOCKED;
    }

    public void unlock()
    {
        if (isUnlocked()) return;
        this.potStatus = PotStatus.NO_POT;
    }

    public void addPhysicalPot()
    {
        if (this.potStatus != PotStatus.NO_POT) return;
        this.potStatus = PotStatus.EMPTY;
        this.plant = null;
    }

    public boolean hasPhysicalPot()
    {
        return potStatus == PotStatus.EMPTY
            || potStatus == PotStatus.GROWING
            || potStatus == PotStatus.READY;
    }

    public GreenhousePlant getPlant()
    {
        return plant;
    }

    public void setPlant(GreenhousePlant plant)
    {
        if (this.potStatus != PotStatus.EMPTY || plant == null) return;
        this.plant = plant;
        this.potStatus = PotStatus.GROWING;
    }

    public boolean isEmpty()
    {
        return this.potStatus == PotStatus.EMPTY;
    }

    public boolean isReady()
    {
        return isPlantReady();
    }

    public void setPosition(Position position) {
        if (position == null) return;
        if (position.getX() < 1 || position.getY() < 1) return;
        this.position = position;
    }

    public void collect()
    {
        if (plant == null) return;
        if (!isPlantReady()) return;
        this.plant = null;
        this.potStatus = PotStatus.EMPTY;
    }

    public boolean isPlantReady()
    {
        if (plant == null) return false;
        if (plant.isReady())
        {
            this.potStatus = PotStatus.READY;
            return true;
        }
        return false;
    }

    public PotStatus getPotStatus() {
        return potStatus;
    }

    public int getCost() {
        return cost;
    }
}

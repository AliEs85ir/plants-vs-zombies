package game.entity.plant;

import app.enums.plant.PlantName;

public class PlantFoodState
{
    private boolean active = false;
    private long startTick;
    private long durationTicks;
    private int requestedAnimationStage = -1;

    public boolean isActive()
    {
        return active;
    }

    public void activate(long tick , long durationTicks)
    {
        this.active = true;
        this.startTick = tick;
        this.durationTicks = durationTicks;
        this.requestedAnimationStage = -1;
    }

    public void activate(long tick , PlantName name)
    {
        activate(tick, (long) name.getPlantFoodDuration() * 10);
    }

    public void deactivate()
    {
        this.active = false;
        this.requestedAnimationStage = -1;
    }

    public long getStartTick()
    {
        return this.startTick;
    }

    public long getDurationTicks()
    {
        return durationTicks;
    }

    public void requestAnimationStage(int stage)
    {
        this.requestedAnimationStage = stage;
    }

    public int consumeRequestedAnimationStage()
    {
        int stage = requestedAnimationStage;
        requestedAnimationStage = -1;
        return stage;
    }

    public void update(long currentTick)
    {
        if (!active) return;
        if (durationTicks <= 0) return;
        if (currentTick - startTick >= durationTicks)
        {
            deactivate();
        }
    }
}

package game.entity.plant.ability;

import game.entity.plant.Plant;
import game.service.plant.PlantService;

public abstract class AbstractPlantAbility implements PlantAbility {
    protected long lastUpdateTick;
    protected boolean active;

    public AbstractPlantAbility() {
        this.lastUpdateTick = 0;
        this.active = true;
    }

    @Override
    public void update(Plant plant, PlantService plantService, long currentTick) {
        if (!active) return;
        doUpdate(plant, plantService, currentTick);
        lastUpdateTick = currentTick;
    }

    protected abstract void doUpdate(Plant plant, PlantService plantService, long currentTick);

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public void resetCycle(long currentTick) {
        lastUpdateTick = currentTick;
    }
}

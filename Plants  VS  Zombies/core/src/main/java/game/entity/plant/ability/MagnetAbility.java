package game.entity.plant.ability;

import game.entity.plant.Plant;
import game.service.plant.PlantService;

public class MagnetAbility extends AbstractPlantAbility {
    private final long intervalTicks;
    private final long lastMagnetTick;

    public MagnetAbility(long intervalTicks) {
        this.intervalTicks = intervalTicks;
        this.lastMagnetTick = 0;
    }

    @Override
    protected void doUpdate(Plant plant, PlantService plantService, long currentTick) {
        if (plant == null || !plant.isAlive()) {
        }

//        PlantName name = plant.getDefinition().getName();
//        if (name != PlantName.MAGNET_SHROOM) return;
//
//        if (currentTick - lastMagnetTick >= intervalTicks) {
//            plantService.getGameWorld().magnetizeMetal(plant.getPosition());
//            lastMagnetTick = currentTick;
//        }
    }
}

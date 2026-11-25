package game.service.plant;

import app.controller.game.EventResult;
import app.controller.game.izombie.IZombieMatchContext;
import app.controller.game.izombie.IZombieNetworkController;
import app.enums.plant.PlantName;
import app.view.game.PlayScreen;
import game.world.playground.Cell;

public final class IZombiePlantPlacementService extends PlantPlacementService {
    public IZombiePlantPlacementService(PlayScreen screen) {
        super(screen);
    }

    @Override
    public EventResult placePlant(PlantName plantName, Cell targetCell) {
        IZombieMatchContext context = IZombieMatchContext.get();
        if (context == null) {
            return super.placePlant(plantName, targetCell);
        }
        if (!context.isApplyingRemotePlacement()) {
            EventResult validation = getValidator().validate(plantName, targetCell);
            if (!validation.isSuccess()) {
                return validation;
            }
            IZombieNetworkController.publishPlantPlacement(
                plantName,
                targetCell.getRow(),
                targetCell.getCol()
            );
            return EventResult.PLANT_PLACEMENT_SUCCESS;
        }
        return commitPlant(plantName, targetCell);
    }
}

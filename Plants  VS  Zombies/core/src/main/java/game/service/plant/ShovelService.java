package game.service.plant;

import app.controller.game.EventResult;
import game.entity.plant.Plant;
import game.world.playground.Cell;
import graphics.general.effects.EffectLayer;
import graphics.general.effects.GameplayEffects;

public class ShovelService {
    private final PlantService plantService;

    public ShovelService(PlantService plantService) {
        this.plantService = plantService;
    }

    public EventResult shovel(Cell cell) {
        EventResult validation = validate(cell);
        if (!validation.isSuccess()) {
            return validation;
        }
        Plant plant = targetPlant(cell);
        GameplayEffects.spawn("plant unplanted", cell.getWorldPosition(), EffectLayer.BEHIND);
        game.audio.Sfx.play(game.audio.AudioId.SFX_SHOVEL);
        plantService.removePlant(plant);
        return EventResult.SHOVEL_SUCCESS;
    }

    public EventResult validate(Cell cell) {
        if (cell == null) {
            return EventResult.SHOVEL_NO_TARGET;
        }
        Plant plant = targetPlant(cell);
        if (plant == null) {
            return EventResult.SHOVEL_NO_TARGET;
        }
        if (!plant.canBeShoveled()) {
            return EventResult.SHOVEL_NOT_ALLOWED;
        }
        return EventResult.SHOVEL_SUCCESS;
    }

    public boolean canShovel(Cell cell) {
        return validate(cell).isSuccess();
    }

    private Plant targetPlant(Cell cell) {
        if (cell == null) {
            return null;
        }
        return cell.getDamageTarget();
    }
}

package game.world.minigame.wallnut_bowling.placement;

import app.controller.game.EventResult;
import app.enums.plant.PlantName;
import game.service.special_levels.ConveyorBeltService;
import game.world.GameWorld;
import game.world.chapter.rule.speciallevelRules.BowlingRule;
import game.world.playground.Cell;

public class BowlingPlacementValidator {
    private final GameWorld world;

    public BowlingPlacementValidator(GameWorld world) {
        this.world = world;
    }

    public EventResult validate(PlantName plantName, Cell targetCell) {
        if(targetCell == null) {
            return EventResult.INVALID_CELL;
        }

        if (world.isConveyorActive()) {
            ConveyorBeltService conveyor = world.getConveyorBeltService();
            if (conveyor == null || !conveyor.hasPlant(plantName)) {
                return EventResult.PLANT_PLACEMENT_NOT_ON_CONVEYOR;
            }
        }
        BowlingRule rule = world.getBowlingRule();
        if(rule == null) {
            return EventResult.BOWLING_PLACEMENT_RULE;
        }
        int redLine = rule.getRedLineColumn();
        if(targetCell.getCol() > redLine) {
            return EventResult.BOWLING_PLACEMENT_REDLINE;
        }
        return EventResult.BOWLING_PLACEMENT_SUCCESS;
    }

    public boolean canPlant(PlantName plantName, Cell targetCell) {
        return validate(plantName, targetCell).isSuccess();
    }


}

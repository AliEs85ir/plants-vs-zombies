package game.world.minigame.wallnut_bowling.placement;

import app.controller.game.EventResult;
import app.enums.obstacle.ObstacleType;
import app.enums.plant.PlantName;
import app.view.game.PlayScreen;
import game.engine.RealTimeGameEngine;
import game.entity.obstacle.Obstacle;
import game.entity.obstacle.ObstacleFactory;
import game.entity.obstacle.ability.ExplodeONutAbility;
import game.entity.obstacle.ability.TallnutAbility;
import game.entity.obstacle.ability.WalnutAbility;
import game.service.special_levels.ConveyorBeltService;
import game.world.GameWorld;
import game.world.playground.Cell;

import java.util.List;

public class BowlingPlacementService {
    private final GameWorld world;
    private final RealTimeGameEngine engine;
    private final BowlingPlacementValidator validator;

    public BowlingPlacementService(PlayScreen screen) {
        this.world = screen.getEngine().getWorld();
        this.engine = screen.getEngine();
        this.validator = new BowlingPlacementValidator(world);
    }

    public EventResult placeBowling(
        PlantName plantName,
        Cell targetCell
    ) {
        EventResult validation =
            validator.validate(plantName, targetCell);
        if(!validation.isSuccess()) {
            return validation;
        }
        if (world.isConveyorActive()) {
            ConveyorBeltService conveyor = world.getConveyorBeltService();
            if (conveyor != null) {
                conveyor.consumeSelected(plantName);
            }
        }
        System.out.println("IN BOWLING PLACEMENT AREA");
        game.audio.Sfx.bowlingPlaced();
        switch (plantName) {
            case WALL_NUT -> {
                ObstacleFactory.getInstance().createRuntimeObstacle(
                    ObstacleType.WALLNUT,
                    50,
                    50,
                    targetCell.getWorldX(),
                    targetCell.getWorldY(),
                    List.of(new WalnutAbility())
                );
            }
            case EXPLODE_O_NUT -> {
                ObstacleFactory.getInstance().createRuntimeObstacle(
                    ObstacleType.EXPLODE_O_NUT,
                    50,
                    50,
                    targetCell.getWorldX(),
                    targetCell.getWorldY(),
                    List.of(new ExplodeONutAbility())
                );
            }
            case TALL_NUT -> {
                ObstacleFactory.getInstance().createRuntimeObstacle(
                    ObstacleType.TALLNUT,
                    50,
                    50,
                    targetCell.getWorldX(),
                    targetCell.getWorldY(),
                    List.of(new TallnutAbility())
                );
            }
        }

        return EventResult.BOWLING_PLACEMENT_SUCCESS;
    }

    public BowlingPlacementValidator getValidator() {
        return validator;
    }
}

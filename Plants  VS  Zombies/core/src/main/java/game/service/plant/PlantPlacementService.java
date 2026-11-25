package game.service.plant;

import app.controller.game.EventResult;
import app.enums.plant.PlantName;
import app.service.plant.PlantBoostService;
import app.view.game.PlayScreen;
import app.view.game.states.PlantingState;
import com.badlogic.gdx.math.Vector2;
import common.model.Position;
import game.engine.RealTimeGameEngine;
import game.entity.plant.Plant;
import game.entity.plant.PlantCooldown;
import game.entity.plant.PlantDefinition;
import game.entity.registry.PlantRegistry;
import game.service.special_levels.ConveyorBeltService;
import game.world.GameWorld;
import game.world.minigame.vasebreaker.VaseBreakerWorld;
import game.world.playground.Cell;
import game.world.playground.MapConfig;
import graphics.general.effects.EffectLayer;
import graphics.general.effects.GameplayEffects;

public class PlantPlacementService {
    private final GameWorld world;
    private final RealTimeGameEngine engine;
    private final PlayScreen screen;
    private final PlantPlacementValidator validator;
    private final PlantFoodService plantFoodService;

    public PlantPlacementService(PlayScreen screen) {
        this.world = screen.getEngine().getWorld();
        this.engine = screen.getEngine();
        this.screen = screen;
        this.validator = new PlantPlacementValidator(world);
        this.plantFoodService = new PlantFoodService(world);
    }

    public EventResult placePlant(
        PlantName plantName,
        Cell targetCell
    ) {
        if (ImitaterReplacement.canBegin(plantName, targetCell)) {
            return ImitaterReplacement.begin(plantName, targetCell);
        }
        EventResult validation =
            validator.validate(plantName, targetCell);

        if (!validation.isSuccess()) {
            return validation;
        }
        return commitPlant(plantName, targetCell);
    }

    protected EventResult commitPlant(PlantName plantName, Cell targetCell) {
        if (plantName == null || targetCell == null) {
            return EventResult.INVALID_CELL;
        }

        PlantDefinition definition = PlantRegistry.get(plantName);
        if (definition == null) {
            return EventResult.INVALID_PLANT;
        }
        if (!targetCell.isPlantable(definition)) {
            return EventResult.PLANT_PLACEMENT_CELL_OCCUPIED;
        }

        MapConfig config = world.getMap().getMapConfig();

        Vector2 center = config.gridToWorld(
            targetCell.getRow(),
            targetCell.getCol()
        );

        long currentTick = engine.getClock().getCurrentTick();

        Plant plant = PlantFactory.create(
            plantName,
            new Position(center.x, center.y),
            currentTick ,
            targetCell
        );

        if (plant == null) {
            return EventResult.INVALID_PLANT;
        }

        targetCell.setPlant(plant);
        world.addPlant(plant);

        if (world.isConveyorActive()) {
            ConveyorBeltService conveyor = world.getConveyorBeltService();
            if (conveyor != null) {
                conveyor.consumeSelected(plantName);
            }
        } else if (world.isVaseBreakerActive()) {
            if (world instanceof VaseBreakerWorld vaseWorld) {
                vaseWorld.consumeSelected(plantName);
            }
        } else if (screen.getStateMachine().getCurrentState() instanceof PlantingState) {
            world.removeSunAmount(PlantUpgradeService.getEffectiveStats(plantName).sunCost());
        } else {
            world.removeSunAmount(PlantUpgradeService.getEffectiveStats(plantName).sunCost());

            PlantCooldown cooldown = world.getCoolDownPlant(plantName);
            if (cooldown != null) {
                cooldown.startCooldown(currentTick);
            }
        }

        engine.getGraphicsEngine().registerPlant(plant);

        world.getObserver().onPlantPlaced(
            plantName,
            targetCell.getRow(),
            targetCell.getCol()
        );
        world.increaseTotalPlantsPlanted();
        GameplayEffects.spawn("plant planted", targetCell.getWorldPosition(), EffectLayer.BEHIND);
        game.audio.Sfx.plantPlaced(plant, targetCell);
        applyBoostIfPresent(plantName, plant);

        return EventResult.PLANT_PLACEMENT_SUCCESS;
    }

    private void applyBoostIfPresent(PlantName plantName, Plant plant) {
        if (!PlantBoostService.consumeBoost(plantName)) {
            return;
        }
        plantFoodService.activateFromBoost(plant);
        if (screen.getHud() != null) {
            screen.getHud().refreshPlantCardBoostStates();
        }
    }

    public PlantPlacementValidator getValidator() {
        return validator;
    }
}

package game.service.plant;

import app.controller.game.EventResult;
import app.enums.plant.PlantName;
import app.model.App;
import common.model.Position;
import game.engine.time.GameClock;
import game.entity.plant.Plant;
import game.entity.plant.ability.ImitaterAbility;
import game.entity.plant.ability.PlantAbility;
import game.world.GameWorld;
import game.world.playground.Cell;

public final class ImitaterReplacement {
    private ImitaterReplacement() {
    }

    public static boolean isImitater(Plant plant) {
        return plant != null
            && plant.getDefinition() != null
            && plant.getDefinition().getName() == PlantName.IMITATER
            && !plant.isDead();
    }

    public static ImitaterAbility abilityOf(Plant plant) {
        if (plant == null || plant.getAbilities() == null) {
            return null;
        }
        for (PlantAbility ability : plant.getAbilities()) {
            if (ability instanceof ImitaterAbility imitaterAbility) {
                return imitaterAbility;
            }
        }
        return null;
    }

    public static boolean canBegin(PlantName selectedPlant, Cell cell) {
        if (selectedPlant == null || selectedPlant == PlantName.IMITATER || cell == null) {
            return false;
        }
        Plant plant = cell.getCurrentPlant();
        if (!isImitater(plant)) {
            return false;
        }
        ImitaterAbility ability = abilityOf(plant);
        return ability != null && ability.canBeginReplacement();
    }

    public static EventResult begin(PlantName selectedPlant, Cell cell) {
        if (!canBegin(selectedPlant, cell)) {
            return EventResult.PLANT_PLACEMENT_CELL_OCCUPIED;
        }
        Plant plant = cell.getCurrentPlant();
        ImitaterAbility ability = abilityOf(plant);
        boolean started = ability.beginReplacement(selectedPlant, GameClock.currentTick);
        return started ? EventResult.IMITATER_REPLACEMENT_SUCCESS : EventResult.PLANT_PLACEMENT_CELL_OCCUPIED;
    }

    public static Plant complete(Plant imitater, PlantName replacement, PlantService plantService, long currentTick) {
        if (imitater == null || replacement == null || replacement == PlantName.IMITATER) {
            return null;
        }
        Cell cell = imitater.getCell();
        if (cell == null) {
            return null;
        }
        Position position = imitater.getPosition();
        cell.removePlant(imitater);
        imitater.setCell(null);

        Plant next;
        try {
            next = PlantFactory.create(replacement, position, currentTick, cell);
        } catch (RuntimeException exception) {
            cell.setPlant(imitater);
            return null;
        }
        if (next == null) {
            cell.setPlant(imitater);
            return null;
        }

        cell.setPlant(next);
        GameWorld world = plantService != null ? plantService.getGameWorld() : null;
        if (world != null) {
            world.addPlant(next);
        }
        if (App.getRealTimeEngine() != null && App.getRealTimeEngine().getGraphicsEngine() != null) {
            App.getRealTimeEngine().getGraphicsEngine().registerPlant(next);
        }
        if (world != null && !imitater.isDead()) {
            imitater.markDead(world);
        }
        return next;
    }
}

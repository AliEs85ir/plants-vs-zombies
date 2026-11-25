package game.world.minigame.beghouled;

import app.enums.plant.PlantName;
import app.model.App;
import com.badlogic.gdx.physics.box2d.Body;
import common.model.Position;
import game.component.physic.PhysicService;
import game.entity.plant.Plant;
import game.service.plant.PlantFactory;
import game.world.GameWorld;
import game.world.playground.Cell;
import graphics.GraphicsEngine;

public final class BeghouledPlantOps {
    private static final float SNAP_PIXELS = 1.5f;

    private BeghouledPlantOps() {}

    public static void setWorldPos(Plant plant, float x, float y) {
        if (plant == null) {
            return;
        }
        plant.setPosition(new Position(x, y));
        Body body = plant.getBody();
        if (body != null) {
            float ppm = PhysicService.PIXEL_PER_METER;
            body.setTransform(x / ppm, y / ppm, 0f);
        }
    }

    /**
     * Always puts the plant on {@code destination}'s occupant list.
     * Compact used to remove a plant then skip re-add when the dest was the same
     * cell (plant.cell was never cleared), so refill spawned a second plant on top.
     */
    public static void attach(Plant plant, Cell destination) {
        if (plant == null || !allowsOccupancy(destination)) {
            return;
        }
        Cell from = plant.getCell();
        if (from != null && from != destination) {
            from.removePlant(plant);
        }
        if (!destination.getPlants().contains(plant)) {
            destination.addPlant(plant);
        } else {
            plant.setCell(destination);
        }
    }

    public static void teleport(Plant plant, Cell destination) {
        if (plant == null || !allowsOccupancy(destination)) {
            return;
        }
        attach(plant, destination);
        setWorldPos(plant, destination.getWorldX(), destination.getWorldY());
    }

    public static void slideTo(Plant plant, Cell destination, BeghouledWorld world, float startX, float startY) {
        if (plant == null || !allowsOccupancy(destination)) {
            return;
        }
        attach(plant, destination);
        float endX = destination.getWorldX();
        float endY = destination.getWorldY();
        if (Math.hypot(endX - startX, endY - startY) < SNAP_PIXELS) {
            setWorldPos(plant, endX, endY);
            return;
        }
        setWorldPos(plant, startX, startY);
        if (world != null) {
            float distance = (float) Math.hypot(endX - startX, endY - startY);
            float duration = Math.min(0.45f, 0.10f + distance / 900f);
            world.addSlide(new BeghouledSlide(plant, startX, startY, endX, endY, duration));
        } else {
            setWorldPos(plant, endX, endY);
        }
    }

    public static void swap(Plant first, Plant second) {
        swap(first, second, null);
    }

    public static void swap(Plant first, Plant second, BeghouledWorld world) {
        if (first == null || second == null) {
            return;
        }
        Cell firstCell = first.getCell();
        Cell secondCell = second.getCell();
        if (firstCell == null || secondCell == null
            || !allowsOccupancy(firstCell) || !allowsOccupancy(secondCell)) {
            return;
        }
        float firstX = (float) first.getPosition().getX();
        float firstY = (float) first.getPosition().getY();
        float secondX = (float) second.getPosition().getX();
        float secondY = (float) second.getPosition().getY();
        firstCell.removePlant(first);
        secondCell.removePlant(second);
        if (world == null) {
            teleport(first, secondCell);
            teleport(second, firstCell);
            return;
        }
        slideTo(first, secondCell, world, firstX, firstY);
        slideTo(second, firstCell, world, secondX, secondY);
    }

    public static Plant spawn(
        PlantName name,
        Cell cell,
        GameWorld world,
        GraphicsEngine graphicsEngine
    ) {
        if (name == null || cell == null || world == null || !allowsOccupancy(cell)) {
            return null;
        }
        long tick = 0;
        if (App.getCurrentGameEngine() != null && App.getCurrentGameEngine().getGameClock() != null) {
            tick = App.getCurrentGameEngine().getGameClock().getCurrentTick();
        }
        Plant plant = PlantFactory.create(
            name,
            new Position(cell.getWorldX(), cell.getWorldY()),
            tick,
            cell
        );
        cell.setPlant(plant);
        world.addPlant(plant);
        if (graphicsEngine != null) {
            graphicsEngine.registerPlant(plant);
        }
        return plant;
    }

    private static boolean allowsOccupancy(Cell cell) {
        return cell != null && !cell.hasObstacle();
    }
}

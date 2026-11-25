package game.service.drop;

import app.enums.drop.DropType;
import app.model.App;
import common.model.Position;
import game.entity.drop.Drop;
import game.entity.zombie.Zombie;
import game.world.GameWorld;
import graphics.GraphicsEngine;

import java.util.concurrent.ThreadLocalRandom;

public final class DropSpawner {
    private DropSpawner() {
    }

    public static void assignPlantFoodAtSpawn(Zombie zombie) {
        if (zombie == null || zombie.dropsPlantFood()) {
            return;
        }
        if (ThreadLocalRandom.current().nextFloat() < DropSettings.PLANT_FOOD_CHANCE) {
            zombie.markPlantFoodDrop();
        }
    }

    public static void trySpawnFrom(Zombie zombie) {
        if (zombie == null || zombie.getPosition() == null || zombie.getWorld() == null) {
            return;
        }
        DropType type = zombie.dropsPlantFood()
            ? DropType.PLANT_FOOD
            : DropTable.roll().orElse(null);
        if (type == null) {
            return;
        }
        spawn(zombie.getPosition(), zombie.getWorld(), type);
    }

    public static void spawnPlantFood(Position position, GameWorld world) {
        spawn(position, world, DropType.PLANT_FOOD);
    }

    private static void spawn(Position position, GameWorld world, DropType type) {
        if (position == null || world == null || type == null) {
            return;
        }
        float drift = (ThreadLocalRandom.current().nextFloat() * 2f - 1f) * DropSettings.MAX_HORIZONTAL_DRIFT;
        Drop drop = new Drop(type, position, drift);
        world.addDrop(drop);
        game.audio.Sfx.dropSpawned(type);
        if (App.getRealTimeEngine() != null && App.getRealTimeEngine().getGraphicsEngine() != null) {
            GraphicsEngine graphicsEngine = App.getRealTimeEngine().getGraphicsEngine();
            graphicsEngine.registerDrop(drop);
        }
    }
}

package game.component.contacts;

import game.entity.plant.Plant;
import game.entity.zombie.Zombie;
import game.world.GameWorld;
import game.world.playground.Grid;

import java.util.List;

public final class ZombiePlantContact {
    private ZombiePlantContact() {
    }

    public static Plant resolveTarget(Plant plant) {
        if (plant == null || plant.isDead()) {
            return null;
        }
        if (plant.getCell() != null) {
            Plant top = plant.getCell().getDamageTarget();
            if (top != null && !top.isDead()) {
                return top;
            }
        }
        return plant;
    }

    public static Plant blockingPlant(Zombie zombie) {
        if (zombie == null || zombie.isDead() || zombie.getWorld() == null || zombie.getPosition() == null) {
            return null;
        }
        GameWorld world = zombie.getWorld();
        Grid grid = world.getGrid();
        if (grid == null || grid.getConfig() == null || world.getPlants() == null) {
            return null;
        }
        int lane = grid.getEntityLane(zombie);
        float zombieX = (float) zombie.getPosition().getX();
        float reach = Math.max(36f, grid.getConfig().getCellWidth() * 0.55f);
        Plant best = null;
        float bestDistance = Float.MAX_VALUE;
        List<Plant> plants = world.getPlants();
        for (Plant plant : plants) {
            if (plant == null || plant.isDead() || plant.getPosition() == null) {
                continue;
            }
            if (grid.getEntityLane(plant) != lane) {
                continue;
            }
            float plantX = (float) plant.getPosition().getX();
            float delta = zombieX - plantX;
            if (delta < -reach * 0.4f || delta > reach) {
                continue;
            }
            float distance = Math.abs(delta);
            if (distance < bestDistance) {
                bestDistance = distance;
                best = plant;
            }
        }
        return resolveTarget(best);
    }

    public static boolean bindIfBlocking(Zombie zombie) {
        Plant target = blockingPlant(zombie);
        if (target == null) {
            return false;
        }
        zombie.setTargetPlant(target);
        return true;
    }
}

package game.entity.plant.ability;

import app.enums.plant.PlantName;
import app.model.App;
import common.model.Position;
import game.entity.plant.Plant;
import game.entity.zombie.Zombie;
import game.service.plant.AttackService;
import game.service.plant.PlantService;
import game.world.GameWorld;
import game.world.playground.Cell;
import game.world.playground.Grid;

import java.util.ArrayList;

public class WallNutAbility extends AbstractPlantAbility {

    @Override
    protected void doUpdate(Plant plant, PlantService plantService, long currentTick) {
        if (plant == null || !plant.isAlive() || plant.getHealth().getCurrentHealth() <= 0) return;
        if (plant.getPlantFoodState() != null && plant.getPlantFoodState().isActive()) return;

        abilityHandler(plant, currentTick, plantService);
    }

    private boolean abilityHandler(Plant plant, long currentTick, PlantService plantService) {
        PlantName name = plant.getDefinition().getName();
        GameWorld gameWorld = App.getCurrentGameEngine().getGameWorld();
        Grid grid = gameWorld.getGrid();
        AttackService attackService = new AttackService(gameWorld);

        int plantRow = (int) (plant.getPosition().getY() );
        int plantCol = (int) (plant.getPosition().getX() );

        long damageTick = plant.getPlantDamage() != null ? plant.getPlantDamage().getLastDamageTick() : -1;

        switch (name) {
            case SWEET_POTATO: {
                attractZombiesFromAdjacentLanes(plantRow, plantCol, grid);
                break;
            }

            case EXPLODE_O_NUT: {
                if (!plant.isAlive() || plant.getHealth().getCurrentHealth() <= 0) {
                    ExplodeAbility explode = new ExplodeAbility();
                    explode.setEnableExplodeONut(true);
                    plant.getAbilities().add(explode);
                }
                break;
            }

            case SUN_BEAN: {
                if (damageTick == currentTick && plantService.getSunSpawner() != null) {
                    plantService.getSunSpawner().spawnPlantSun(plant, 5);
                }
                break;
            }

            case WALL_NUT:
            case TALL_NUT:
            case PUMPKIN:
            default:
                break;
        }
        return true;
    }

    // ========== Helper Methods ==========
    private void redirectZombieToAdjacentLane(Zombie zombie, int currentRow, Grid grid) {
        double x = zombie.getPosition().getX();
        Position upPos = new Position(x, (currentRow - 1) );
        Position downPos = new Position(x, (currentRow + 1) );

        if (grid.isInsideGrid(upPos)) {
            zombie.setPosition(upPos);
        } else if (grid.isInsideGrid(downPos)) {
            zombie.setPosition(downPos);
        }
    }

    private void attractZombiesFromAdjacentLanes(int plantRow, int plantCol, Grid grid) {
//        int[] adjacentRows = {plantRow - 1, plantRow + 1};
//        for (int r : adjacentRows) {
//            Cell cell = grid.getCell(r, plantCol);
//            if (cell != null && cell.getZombies() != null) {
//                for (Zombie z : new ArrayList<>(cell.getZombies())) {
//                    if (z != null && !z.isDead()) {
//                        double x = z.getPosition().getX();
//                        Position newPos = new Position(x, plantRow );
//                        if (grid.isInsideGrid(newPos)) {
//                            z.setPosition(newPos);
//                        }
//                    }
//                }
//            }
//        }
    }
}

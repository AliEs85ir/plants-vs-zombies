package game.entity.plant.ability;

import app.enums.plant.PlantName;
import app.model.App;
import common.model.Position;
import game.entity.plant.Plant;
import game.entity.plant.PlantHealth;
import game.entity.zombie.Zombie;
import game.service.plant.PlantService;
import game.world.GameWorld;
import game.world.playground.Cell;
import game.world.playground.Grid;

import java.util.ArrayList;

public class WallNutPlantFood extends AbstractPlantAbility {

    @Override
    protected void doUpdate(Plant plant, PlantService plantService, long currentTick) {
        if (plant == null || !plant.isAlive() || plant.getHealth().getCurrentHealth() <= 0) return;
        if (plant.getPlantFoodState() == null || !plant.getPlantFoodState().isActive()) return;

        executePlantFoodEffect(plant, currentTick, plantService);
        plant.getPlantFoodState().deactivate();
    }

    private boolean executePlantFoodEffect(Plant plant, long currentTick, PlantService plantService) {
        PlantName name = plant.getDefinition().getName();
        GameWorld gameWorld = App.getCurrentGameEngine().getGameWorld();
        Grid grid = gameWorld.getGrid();

        int plantRow = plant.getRow();
        int plantCol = plant.getCol();

        switch (name) {
            case WALL_NUT: {
                int newMaxHp = plant.getHealth().getMaxHealth() + 4000;
                plant.setHealth(new PlantHealth(newMaxHp));
                gameWorld.addEvent(plant.getName() + " gained permanent armor (+4000 HP)!");
                break;
            }

            case TALL_NUT: {
                int newMaxHp = plant.getHealth().getMaxHealth() + 8000;
                plant.setHealth(new PlantHealth(newMaxHp));
                gameWorld.addEvent(plant.getName() + " gained heavy permanent armor (+8000 HP)!");
                break;
            }

            case ENDURIAN: {
                int newMaxHp = plant.getHealth().getMaxHealth() + 3000;
                plant.setHealth(new PlantHealth(newMaxHp));
                gameWorld.addEvent(plant.getName() + " gained metal armor (+3000 HP)!");
                break;
            }

            case GARLIC: {
//                if (grid.getConfig() != null) {
//                    for (int c = 0; c < grid.getColCount(); c++) {
//                        Cell cell = grid.getCell(c, plantRow);
//                        if (cell != null && cell.getZombies() != null) {
//                            for (Zombie z : new ArrayList<>(cell.getZombies())) {
//                                if (z != null && !z.isDead()) {
//                                    redirectZombieToAdjacentLane(z, plantRow, grid);
//                                }
//                            }
//                        }
//                    }
//                }
                gameWorld.addEvent(plant.getName() + " diverted all zombies in its lane!");
                break;
            }

            case SWEET_POTATO: {
                plant.heal(plant.getHealth().getMaxHealth());
                attractZombiesSurrounding(plantRow, plantCol, grid);
                gameWorld.addEvent(plant.getName() + " fully healed and pulled all surrounding zombies!");
                break;
            }

            case EXPLODE_O_NUT: {
                int newMaxHp = plant.getHealth().getMaxHealth() + 4000;
                plant.setHealth(new PlantHealth(newMaxHp));
                gameWorld.addEvent(plant.getName() + " gained explosive metal armor (+4000 HP)!");
                break;
            }

            case PUMPKIN: {
                int newMaxHp = plant.getHealth().getMaxHealth() + 4000;
                plant.setHealth(new PlantHealth(newMaxHp));
                gameWorld.addEvent(plant.getName() + " gained heavy metal shield (+4000 HP)!");
                break;
            }

            case SUN_BEAN: {
                int newMaxHp = plant.getHealth().getMaxHealth() + 1000;
                plant.setHealth(new PlantHealth(newMaxHp));
                gameWorld.addEvent(plant.getName() + " gained protective armor (+1000 HP)!");
                break;
            }

            default:
                return false;
        }
        return true;
    }

    // ========== Helper Methods ==========
    private void redirectZombieToAdjacentLane(Zombie zombie, int currentRow, Grid grid) {
        double x = zombie.getPosition().getX();
        Position upPos = new Position(x, currentRow - 1);
        Position downPos = new Position(x, currentRow + 1);

        if (grid.isInsideGrid(upPos)) {
            zombie.setPosition(upPos);
        } else if (grid.isInsideGrid(downPos)) {
            zombie.setPosition(downPos);
        }
    }

    private void attractZombiesSurrounding(int centerRow, int centerCol, Grid grid) {
//        for (int r = centerRow - 1; r <= centerRow + 1; r++) {
//            for (int c = centerCol - 1; c <= centerCol + 1; c++) {
//                if (r == centerRow) continue;
//                Cell cell = grid.getCell(c, r);
//                if (cell != null && cell.getZombies() != null) {
//                    for (Zombie z : new ArrayList<>(cell.getZombies())) {
//                        if (z != null && !z.isDead()) {
//                            double x = z.getPosition().getX();
//                            Position targetPos = new Position(x, centerRow);
//                            if (grid.isInsideGrid(targetPos)) {
//                                z.setPosition(targetPos);
//                            }
//                        }
//                    }
//                }
//            }
//        }
    }
}

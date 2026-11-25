package game.entity.plant.ability;

import app.enums.plant.PlantName;
import app.model.App;
import game.entity.DamageContext;
import game.entity.effect.FireEffect;
import game.entity.plant.Plant;
import game.entity.zombie.Zombie;
import game.service.plant.AttackService;
import game.service.plant.PlantService;
import game.world.GameWorld;
import game.world.playground.Cell;
import game.world.playground.Grid;

import java.util.ArrayList;

public class MeleePlantFood extends AbstractPlantAbility {

    private static final int TICKS_PER_SECOND = 50;

    @Override
    protected void doUpdate(Plant plant, PlantService plantService, long currentTick) {
        if (plant == null || !plant.isAlive() || plant.getHealth().getCurrentHealth() <= 0) return;
        if (plant.getPlantFoodState() == null || !plant.getPlantFoodState().isActive()) return;

        GameWorld gameWorld = App.getCurrentGameEngine().getGameWorld();
        AttackService attackService = new AttackService(gameWorld);

        executePlantFoodEffect(plant, currentTick, attackService, gameWorld);
        plant.getPlantFoodState().deactivate();
    }

    private void executePlantFoodEffect(Plant plant, long currentTick, AttackService attackService, GameWorld gameWorld) {
        PlantName name = plant.getDefinition().getName();
        int baseDamage = plant.getAttackDamage();
        Grid grid = gameWorld.getGrid();

        int plantRow = (int) (plant.getPosition().getY() / grid.getConfig().getCellHeight());
        int plantCol = (int) (plant.getPosition().getX() / grid.getConfig().getCellWidth());

        switch (name) {
            case PHAT_BEET: {
                damageZombiesInArea(grid, attackService, plantRow, plantCol, 1, baseDamage * 6, currentTick);
                gameWorld.addEvent(plant.getName() + " created a powerful sonic shockwave!");
                break;
            }

            case CHOMPER: {
                int count = 0;
                for (Zombie z : new ArrayList<>(gameWorld.getZombies())) {
                    if (z != null && !z.isDead()) {
                        z.getTakingDamageStrategy().takeDamage(z, currentTick, new DamageContext(plant, 9999, false));
                        count++;
                        if (count >= 3) break;
                    }
                }
                gameWorld.addEvent(plant.getName() + " swallowed 3 zombies from distance!");
                break;
            }

            case WASABI_WHIP: {
//                for (int r = plantRow - 1; r <= plantRow + 1; r++) {
//                    for (int c = plantCol - 1; c <= plantCol + 1; c++) {
//                        Cell cell = grid.getCell(r, c);
//                        if (cell != null && cell.getZombies() != null) {
//                            for (Zombie z : new ArrayList<>(cell.getZombies())) {
//                                if (z != null && !z.isDead()) {
//                                    z.addEffect(new FireEffect(currentTick, 5 * TICKS_PER_SECOND));
//                                    attackService.damageZombie(z, currentTick, baseDamage * 4);
//                                }
//                            }
//                        }
//                    }
//                }
                gameWorld.addEvent(plant.getName() + " spun its flaming whip in all directions!");
                break;
            }

            case KIWIBEAST: {
                damageZombiesInArea(grid, attackService, plantRow, plantCol, 1, baseDamage * 5, currentTick);
                gameWorld.addEvent(plant.getName() + " slammed the ground with massive force!");
                break;
            }

            default:
                break;
        }
    }

    private boolean damageZombiesInArea(Grid grid, AttackService attackService, int centerRow, int centerCol, int radius, int damage, long currentTick) {
        boolean attacked = false;
//        for (int r = centerRow - radius; r <= centerRow + radius; r++) {
//            for (int c = centerCol - radius; c <= centerCol + radius; c++) {
//                Cell cell = grid.getCell(r, c);
//                if (cell != null && cell.getZombies() != null) {
//                    for (Zombie z : new ArrayList<>(cell.getZombies())) {
//                        if (z != null && !z.isDead()) {
//                            attackService.damageZombie(z, currentTick, damage);
//                            attacked = true;
//                        }
//                    }
//                }
//            }
//        }
        return attacked;
    }
}

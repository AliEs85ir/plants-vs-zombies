package game.entity.plant.ability;

import app.enums.plant.PlantName;
import app.model.App;
import game.entity.DeathStyle;
import game.entity.plant.Plant;
import game.entity.zombie.Zombie;
import game.service.plant.AttackService;
import game.service.plant.PlantService;
import game.world.GameWorld;
import game.world.playground.Grid;

public class MeleeAbility extends AbstractPlantAbility {

    private final long attackCooldown;
    private long lastAttackTick;
    private static final int TICKS_PER_SECOND = 50;

    public MeleeAbility(long intervalTicks) {
        this.attackCooldown = intervalTicks;
        this.lastAttackTick = 0;
    }

    @Override
    public void resetCycle(long currentTick) {
        super.resetCycle(currentTick);
        lastAttackTick = currentTick;
    }

    @Override
    protected void doUpdate(Plant plant, PlantService plantService, long currentTick) {
        if (plant == null || !plant.isAlive() || plant.getHealth().getCurrentHealth() <= 0) return;

        GameWorld gameWorld = App.getCurrentGameEngine().getGameWorld();
        AttackService attackService = new AttackService(gameWorld);

        if (lastAttackTick != 0 && (currentTick - lastAttackTick < attackCooldown)) {
            return;
        }

        boolean attacked = performAttack(plant, currentTick, attackService, gameWorld);

        if (attacked) {
            lastAttackTick = currentTick;
        }
    }

    private boolean performAttack(Plant plant, long currentTick, AttackService attackService, GameWorld gameWorld) {
        return false;
    }

    private Zombie getFrontMostZombieInCell(Grid grid, int row, int col) {
//        Cell cell = grid.getCell(row, col);
//        if (cell == null) return null;
//        List<Zombie> zombies = cell.getZombies();
//        if (zombies == null || zombies.isEmpty()) return null;

        Zombie frontMost = null;
//        for (Zombie z : new ArrayList<>(zombies)) {
//            if (z != null && !z.isDead()) {
//                if (frontMost == null || z.getPosition().getX() < frontMost.getPosition().getX()) {
//                    frontMost = z;
//                }
//            }
//        }
        return frontMost;
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

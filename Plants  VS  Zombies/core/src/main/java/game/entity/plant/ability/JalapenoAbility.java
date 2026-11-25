package game.entity.plant.ability;

import app.enums.plant.PlantGameplayPhase;
import common.model.Position;
import game.engine.time.GameClock;
import game.entity.plant.Plant;
import game.service.plant.AttackService;
import game.service.plant.PlantService;
import game.world.GameWorld;
import game.world.playground.Cell;
import game.world.playground.Grid;
import graphics.general.event.PlantVisualEventType;

public class JalapenoAbility extends AbstractPlantAbility {
    private static final int IMPACT_DELAY_TICKS = 8;
    private static final int FIRE_DURATION_TICKS = 20;
    private static final float FIRE_HOLD_SECONDS = 2.4f;

    private boolean started;
    private boolean detonated;

    @Override
    protected void doUpdate(Plant plant, PlantService plantService, long currentTick) {
        if (plant == null || started) {
            return;
        }
        started = true;
        plant.setGameplayPhase(PlantGameplayPhase.ATTACKING);
        PlantAbilitySupport.play(plant, PlantVisualEventType.ATTACK_TRIGGERED);
        PlantAbilitySupport.schedule(currentTick, IMPACT_DELAY_TICKS, () -> detonate(plant, plantService));
    }

    private void detonate(Plant plant, PlantService plantService) {
        if (detonated || plant == null) {
            return;
        }
        detonated = true;
        long currentTick = GameClock.currentTick;
        GameWorld world = plantService.getGameWorld();
        Cell cell = PlantTargeting.cellOf(world, plant);
        if (cell != null) {
            AttackService attackService = PlantAbilitySupport.attacks(world);
            attackService.damageZombiesInRowWithFire(
                cell.getRow(),
                plant.getAttackDamage(),
                currentTick,
                FIRE_DURATION_TICKS
            );
            spawnLaneFire(world.getGrid(), cell.getRow());
        }
        PlantAbilitySupport.notifyExploded(plant);
        PlantAbilitySupport.consumeLater(plant, plantService, currentTick, GameClock.TICKS_PER_SECOND);
    }

    private void spawnLaneFire(Grid grid, int row) {
        if (grid == null) {
            return;
        }
        for (int col = 0; col < grid.getColCount(); col++) {
            Cell tile = grid.getCell(row, col);
            if (tile == null) {
                continue;
            }
            boolean flip = col % 2 == 1;
            PlantAbilitySupport.spawnEffect(
                "jalapeno_fire",
                new Position(tile.getWorldX(), tile.getWorldY()),
                flip,
                FIRE_HOLD_SECONDS
            );
        }
    }
}

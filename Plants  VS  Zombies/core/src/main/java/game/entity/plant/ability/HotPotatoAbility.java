package game.entity.plant.ability;

import app.enums.plant.PlantGameplayPhase;
import game.engine.time.GameClock;
import game.entity.plant.Plant;
import game.service.plant.PlantService;
import game.world.playground.Cell;
import game.world.playground.celltypes.GrassCellType;
import game.world.playground.celltypes.IceCellType;
import graphics.general.event.PlantVisualEventType;

public class HotPotatoAbility extends AbstractPlantAbility {
    public static final int ARM_DELAY_TICKS = GameClock.TICKS_PER_SECOND;
    public static final int ATTACK_DURATION_TICKS = GameClock.TICKS_PER_SECOND;

    private boolean attackStarted;
    private boolean melted;
    private boolean finished;
    private long attackStartTick;

    @Override
    protected void doUpdate(Plant plant, PlantService plantService, long currentTick) {
        if (plant == null || finished) {
            return;
        }
        if (!attackStarted) {
            if (currentTick - plant.getPlantedTick() < ARM_DELAY_TICKS) {
                return;
            }
            attackStarted = true;
            attackStartTick = currentTick;
            plant.setGameplayPhase(PlantGameplayPhase.ATTACKING);
            PlantAbilitySupport.play(plant, PlantVisualEventType.ATTACK_TRIGGERED);
            meltCell(plant);
            return;
        }
        if (currentTick - attackStartTick >= ATTACK_DURATION_TICKS) {
            finished = true;
            PlantAbilitySupport.consumeLater(plant, plantService, currentTick, 0);
        }
    }

    public boolean hasMelted() {
        return melted;
    }

    public boolean hasStartedAttack() {
        return attackStarted;
    }

    public boolean isFinished() {
        return finished;
    }

    public static boolean isIceTerrain(Cell cell) {
        return cell != null && cell.getCellType() instanceof IceCellType;
    }

    public static boolean meltIceTerrain(Cell cell) {
        if (!isIceTerrain(cell)) {
            return false;
        }
        cell.setCellType(new GrassCellType());
        return true;
    }

    private void meltCell(Plant plant) {
        if (melted) {
            return;
        }
        melted = true;
        meltIceTerrain(plant.getCell());
    }
}

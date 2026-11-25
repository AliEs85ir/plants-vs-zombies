package game.entity.plant.ability;

import app.enums.plant.PlantGameplayPhase;
import game.engine.time.GameClock;
import game.entity.obstacle.Obstacle;
import game.entity.obstacle.ability.TakeDamageAbility;
import game.entity.plant.Plant;
import game.service.plant.PlantPlacementPolicy;
import game.service.plant.PlantService;
import game.world.playground.Cell;
import graphics.general.event.PlantVisualEventType;

public class GraveBusterAbility extends AbstractPlantAbility {
    public static final int DAMAGE_PER_TICK = 50;
    public static final float ATTACK_CLIP_SECONDS = 1.0f;
    public static final int ATTACK_DURATION_TICKS =
        Math.max(1, Math.round(ATTACK_CLIP_SECONDS * GameClock.TICKS_PER_SECOND));

    private Obstacle targetGrave;
    private boolean targetLocked;
    private boolean attackStarted;
    private boolean attack1Looping;
    private boolean finished;
    private long attackStartTick = -1L;
    private long lastDamageTick = Long.MIN_VALUE;

    @Override
    protected void doUpdate(Plant plant, PlantService plantService, long currentTick) {
        if (plant == null || finished) {
            return;
        }
        if (!targetLocked) {
            lockTarget(plant);
            targetLocked = true;
        }
        if (isTargetGone(plant)) {
            finish(plant, plantService, currentTick);
            return;
        }
        if (!attackStarted) {
            attackStarted = true;
            attackStartTick = currentTick;
            plant.setGameplayPhase(PlantGameplayPhase.ATTACKING);
            PlantAbilitySupport.play(plant, PlantVisualEventType.ATTACK_TRIGGERED);
            return;
        }
        if (!attack1Looping) {
            if (currentTick - attackStartTick < ATTACK_DURATION_TICKS) {
                return;
            }
            attack1Looping = true;
            PlantAbilitySupport.play(plant, PlantVisualEventType.ATTACK_2_TRIGGERED);
        }
        applyDamage(plant, plantService, currentTick);
    }

    private void lockTarget(Plant plant) {
        Cell cell = plant.getCell();
        Obstacle obstacle = cell != null ? cell.getObstacle() : null;
        if (PlantPlacementPolicy.isGrave(obstacle)) {
            targetGrave = obstacle;
        }
    }

    private void applyDamage(Plant plant, PlantService plantService, long currentTick) {
        if (currentTick == lastDamageTick || targetGrave == null) {
            return;
        }
        lastDamageTick = currentTick;
        targetGrave.takeDamage(DAMAGE_PER_TICK);
        if (isTargetGone(plant) || isGraveDestroyed(targetGrave)) {
            finish(plant, plantService, currentTick);
        }
    }

    private void finish(Plant plant, PlantService plantService, long currentTick) {
        if (finished) {
            return;
        }
        finished = true;
        setActive(false);
        PlantAbilitySupport.consumeLater(plant, plantService, currentTick, 0);
    }

    public static boolean isGraveDestroyed(Obstacle grave) {
        if (grave == null || grave.isDead()) {
            return true;
        }
        if (grave.getAbilities() == null) {
            return false;
        }
        for (var ability : grave.getAbilities()) {
            if (ability instanceof TakeDamageAbility takeDamage && takeDamage.getCurrentHealth() <= 0) {
                return true;
            }
        }
        return false;
    }

    private boolean isTargetGone(Plant plant) {
        if (targetGrave == null) {
            return true;
        }
        if (isGraveDestroyed(targetGrave)) {
            return true;
        }
        Cell cell = plant != null ? plant.getCell() : null;
        return cell != null && cell.getObstacle() != targetGrave;
    }

    public Obstacle getTargetGrave() {
        return targetGrave;
    }

    public boolean hasStartedAttack() {
        return attackStarted;
    }

    public boolean isAttack1Looping() {
        return attack1Looping;
    }

    public boolean isFinished() {
        return finished;
    }
}

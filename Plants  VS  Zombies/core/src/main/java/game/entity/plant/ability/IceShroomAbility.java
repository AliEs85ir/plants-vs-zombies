package game.entity.plant.ability;

import app.enums.effect.EffectType;
import app.enums.plant.PlantGameplayPhase;
import game.engine.time.GameClock;
import game.entity.effect.Effect;
import game.entity.effect.TimedEffect;
import game.entity.plant.Plant;
import game.entity.zombie.Zombie;
import game.service.plant.AttackService;
import game.service.plant.PlantService;
import game.world.GameWorld;
import graphics.general.event.PlantVisualEventType;

import java.util.ArrayList;

public class IceShroomAbility extends AbstractPlantAbility {
    public static final int ARM_DELAY_TICKS = GameClock.TICKS_PER_SECOND;
    public static final int ATTACK_DURATION_TICKS = GameClock.TICKS_PER_SECOND;
    public static final int FREEZE_DURATION_TICKS = 5 * GameClock.TICKS_PER_SECOND;

    private boolean attackStarted;
    private boolean attackExecuted;
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
            applyFreeze(plant, plantService, currentTick);
            return;
        }
        if (currentTick - attackStartTick >= ATTACK_DURATION_TICKS) {
            finished = true;
            PlantAbilitySupport.consumeLater(plant, plantService, currentTick, 0);
        }
    }

    @Override
    public boolean handleDeath(Plant plant, PlantService plantService, long currentTick) {
        if (!attackStarted || attackExecuted) {
            return false;
        }
        PlantAbilitySupport.keepAlive(plant);
        applyFreeze(plant, plantService, currentTick);
        return true;
    }

    public boolean hasExecutedAttack() {
        return attackExecuted;
    }

    public boolean hasStartedAttack() {
        return attackStarted;
    }

    public boolean isFinished() {
        return finished;
    }

    public static boolean shouldApplyFreeze(Zombie zombie, long currentTick, int freezeDurationTicks) {
        if (zombie == null || zombie.isDead()) {
            return false;
        }
        Effect existing = zombie.findEffect(EffectType.FREEZE);
        if (existing instanceof TimedEffect timed) {
            long remaining = timed.getStartTick() + timed.getDurationTicks() - currentTick;
            return remaining < freezeDurationTicks;
        }
        return true;
    }

    private void applyFreeze(Plant plant, PlantService plantService, long currentTick) {
        if (attackExecuted) {
            return;
        }
        attackExecuted = true;
        GameWorld world = plantService != null ? plantService.getGameWorld() : null;
        if (world == null) {
            return;
        }
        AttackService attackService = PlantAbilitySupport.attacks(world);
        for (Zombie zombie : new ArrayList<>(world.getZombies())) {
            if (!shouldApplyFreeze(zombie, currentTick, FREEZE_DURATION_TICKS)) {
                continue;
            }
            attackService.freezeZombie(zombie, currentTick, FREEZE_DURATION_TICKS);
        }
        PlantAbilitySupport.notifyExploded(plant);
    }
}

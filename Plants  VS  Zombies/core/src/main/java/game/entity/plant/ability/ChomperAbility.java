package game.entity.plant.ability;

import app.enums.plant.PlantGameplayPhase;
import app.enums.plant.PlantName;
import game.engine.time.GameClock;
import game.entity.plant.Plant;
import game.entity.zombie.Zombie;
import game.service.plant.PlantService;
import game.service.plant.PlantUpgradeService;
import graphics.general.event.PlantVisualEventType;
import graphics.plant.PlantRenderInstance;
import graphics.plant.PlantVisualState;

public class ChomperAbility extends AbstractPlantAbility {
    private static final int SWALLOW_DELAY_TICKS = 7;
    private static final int DIGEST_SECONDS = 10;

    private Zombie target;
    private long digestEndTick;
    private long recoverStartTick;
    private boolean recoverClipSeen;

    @Override
    protected void doUpdate(Plant plant, PlantService plantService, long currentTick) {
        if (plant == null || !plant.isAlive() || plantService == null) {
            return;
        }
        switch (plant.getGameplayPhase()) {
            case ATTACKING -> {
            }
            case DIGESTING -> updateDigesting(plant, currentTick);
            case RECOVERING -> updateRecovering(plant, currentTick);
            case DEFAULT -> tryBeginAttack(plant, plantService, currentTick);
            default -> {
            }
        }
    }

    private void tryBeginAttack(Plant plant, PlantService plantService, long currentTick) {
        Zombie zombie = PlantTargeting.firstZombieInFrontCell(plantService.getGameWorld(), plant);
        if (zombie == null) {
            return;
        }
        target = zombie;
        plant.setGameplayPhase(PlantGameplayPhase.ATTACKING);
        PlantAbilitySupport.play(plant, PlantVisualEventType.ATTACK_TRIGGERED);
        PlantAbilitySupport.schedule(currentTick, SWALLOW_DELAY_TICKS, () -> swallow(plant, plantService));
    }

    private void swallow(Plant plant, PlantService plantService) {
        if (plant == null || !plant.isAlive() || plant.getGameplayPhase() != PlantGameplayPhase.ATTACKING) {
            return;
        }
        if (PlantTargeting.isEligible(target) && plantService != null && plantService.getGameWorld() != null) {
            PlantAbilitySupport.attacks(plantService.getGameWorld())
                .killZombie(target, GameClock.currentTick, plant);
        }
        beginDigesting(plant, GameClock.currentTick);
    }

    private void beginDigesting(Plant plant, long currentTick) {
        target = null;
        plant.setGameplayPhase(PlantGameplayPhase.DIGESTING);
        digestEndTick = currentTick + digestTicks();
        recoverClipSeen = false;
    }

    private void updateDigesting(Plant plant, long currentTick) {
        if (currentTick < digestEndTick) {
            return;
        }
        beginRecovering(plant, currentTick);
    }

    private void beginRecovering(Plant plant, long currentTick) {
        recoverStartTick = currentTick;
        recoverClipSeen = false;
        plant.setGameplayPhase(PlantGameplayPhase.RECOVERING);
        PlantAbilitySupport.play(plant, PlantVisualEventType.RECOVER_TRIGGERED);
    }

    private void updateRecovering(Plant plant, long currentTick) {
        PlantRenderInstance instance = PlantAbilitySupport.instanceOf(plant);
        if (instance == null) {
            finishRecovering(plant);
            return;
        }
        PlantVisualState visual = instance.getCurrentVisualState();
        if (visual == PlantVisualState.RECOVER) {
            recoverClipSeen = true;
            if (instance.getAnimationController().isFinished()) {
                finishRecovering(plant);
            }
            return;
        }
        if (recoverClipSeen && visual == PlantVisualState.IDLE) {
            finishRecovering(plant);
            return;
        }
        if (!recoverClipSeen && currentTick - recoverStartTick >= recoverFallbackTicks()) {
            finishRecovering(plant);
        }
    }

    private void finishRecovering(Plant plant) {
        recoverClipSeen = false;
        plant.setGameplayPhase(PlantGameplayPhase.DEFAULT);
    }

    private static int digestTicks() {
        int reduction = PlantUpgradeService.getModifiers(PlantName.CHOMPER).digestTimeReductionTicks();
        return Math.max(1, DIGEST_SECONDS * GameClock.TICKS_PER_SECOND - reduction);
    }

    private static int recoverFallbackTicks() {
        return Math.max(1, 4 * GameClock.TICKS_PER_SECOND);
    }
}

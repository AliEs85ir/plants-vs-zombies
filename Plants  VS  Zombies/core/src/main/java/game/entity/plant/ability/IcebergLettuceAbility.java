package game.entity.plant.ability;

import app.enums.plant.PlantGameplayPhase;
import game.engine.time.GameClock;
import game.entity.plant.Plant;
import game.entity.zombie.Zombie;
import game.service.plant.AttackService;
import game.service.plant.PlantService;
import graphics.general.event.PlantVisualEventType;

public class IcebergLettuceAbility extends AbstractPlantAbility {
    private static final int FREEZE_DURATION_TICKS = 7 * GameClock.TICKS_PER_SECOND;
    private static final int IMPACT_DELAY_TICKS = 15;

    private boolean attacking;
    private boolean finished;
    private boolean frozen;
    private Zombie target;

    @Override
    protected void doUpdate(Plant plant, PlantService plantService, long currentTick) {
        if (plant == null || attacking || finished) {
            return;
        }
        Zombie trigger = PlantTargeting.firstTriggeringZombie(plantService.getGameWorld(), plant);
        if (trigger == null) {
            return;
        }
        target = trigger;
        attacking = true;
        plant.setGameplayPhase(PlantGameplayPhase.ATTACKING);
        PlantAbilitySupport.play(plant, PlantVisualEventType.ATTACK_TRIGGERED);
        PlantAbilitySupport.schedule(currentTick, IMPACT_DELAY_TICKS, () -> applyFreeze(plant, plantService));
    }

    @Override
    public boolean handleDeath(Plant plant, PlantService plantService, long currentTick) {
        if (!attacking) {
            return false;
        }
        PlantAbilitySupport.keepAlive(plant);
        return true;
    }

    private void applyFreeze(Plant plant, PlantService plantService) {
        if (frozen) {
            return;
        }
        frozen = true;
        finished = true;
        long currentTick = GameClock.currentTick;
        AttackService attackService = PlantAbilitySupport.attacks(plantService.getGameWorld());
        if (target != null && !target.isDead()) {
            attackService.freezeZombie(target, currentTick, FREEZE_DURATION_TICKS);
        }
        PlantAbilitySupport.notifyExploded(plant);
        PlantAbilitySupport.consumeLater(plant, plantService, currentTick, Math.max(1, GameClock.TICKS_PER_SECOND / 2));
    }
}

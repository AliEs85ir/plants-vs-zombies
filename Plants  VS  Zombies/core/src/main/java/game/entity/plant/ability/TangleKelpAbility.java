package game.entity.plant.ability;

import app.enums.plant.PlantGameplayPhase;
import game.audio.Sfx;
import game.engine.time.GameClock;
import game.entity.plant.Plant;
import game.entity.zombie.Zombie;
import game.service.plant.PlantService;
import graphics.general.event.PlantVisualEventType;

public class TangleKelpAbility extends AbstractPlantAbility {
    private static final int IMPACT_DELAY_TICKS = 30;

    private boolean attacking;
    private boolean finished;
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
        Sfx.onEvent(plant);
        game.audio.Sfx.play(game.audio.AudioId.VOX_DOLPHIN_JUMP);
        PlantAbilitySupport.schedule(currentTick, IMPACT_DELAY_TICKS, () -> finish(plant, plantService));
    }

    @Override
    public boolean handleDeath(Plant plant, PlantService plantService, long currentTick) {
        if (!attacking) {
            return false;
        }
        PlantAbilitySupport.keepAlive(plant);
        return true;
    }

    private void finish(Plant plant, PlantService plantService) {
        if (finished) {
            return;
        }
        finished = true;
        long currentTick = GameClock.currentTick;
        if (target != null && !target.isDead()) {
            target.die();
        }
        PlantAbilitySupport.notifyExploded(plant);
        PlantAbilitySupport.consumeLater(plant, plantService, currentTick, Math.max(1, GameClock.TICKS_PER_SECOND / 2));
    }
}

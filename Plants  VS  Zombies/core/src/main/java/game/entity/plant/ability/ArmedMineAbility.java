package game.entity.plant.ability;

import app.enums.plant.PlantGameplayPhase;
import game.engine.time.GameClock;
import game.entity.DeathStyle;
import game.entity.plant.Plant;
import game.service.plant.AttackService;
import game.service.plant.PlantService;
import game.world.GameWorld;
import game.world.playground.Cell;
import graphics.general.event.PlantVisualEventType;

public class ArmedMineAbility extends AbstractPlantAbility {
    private static final int IMPACT_DELAY_TICKS = 8;

    private final long armTicks;
    private final int explosionRadius;
    private final String explosionEffectId;
    private boolean exploding;
    private boolean detonated;

    public ArmedMineAbility(long armTicks, int explosionRadius, String explosionEffectId) {
        this.armTicks = armTicks;
        this.explosionRadius = explosionRadius;
        this.explosionEffectId = explosionEffectId;
    }

    @Override
    protected void doUpdate(Plant plant, PlantService plantService, long currentTick) {
        if (plant == null || exploding) {
            return;
        }
        if (plant.getGameplayPhase() == PlantGameplayPhase.DEFAULT) {
            plant.setGameplayPhase(PlantGameplayPhase.UNARMED);
        }
        if (plant.getGameplayPhase() == PlantGameplayPhase.UNARMED
            && currentTick - plant.getPlantedTick() >= armTicks) {
            plant.setGameplayPhase(PlantGameplayPhase.ARMED);
            PlantAbilitySupport.play(plant, PlantVisualEventType.RECOVER_TRIGGERED);
        }
        if (plant.getGameplayPhase() != PlantGameplayPhase.ARMED) {
            return;
        }

        GameWorld world = plantService.getGameWorld();
        if (PlantTargeting.firstTriggeringZombie(world, plant) == null) {
            return;
        }
        beginExplosion(plant, plantService, currentTick);
    }

    @Override
    public boolean handleDeath(Plant plant, PlantService plantService, long currentTick) {
        if (plant == null) {
            return false;
        }
        PlantGameplayPhase phase = plant.getGameplayPhase();
        if (phase == PlantGameplayPhase.UNARMED || phase == PlantGameplayPhase.DEFAULT) {
            return false;
        }
        if (!exploding) {
            beginExplosion(plant, plantService, currentTick);
        }
        PlantAbilitySupport.keepAlive(plant);
        return true;
    }

    private void beginExplosion(Plant plant, PlantService plantService, long currentTick) {
        if (exploding) {
            return;
        }
        exploding = true;
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
        PlantAbilitySupport.spawnEffect(explosionEffectId, plant.getPosition());
        PlantAbilitySupport.notifyExploded(plant);
        GameWorld world = plantService.getGameWorld();
        Cell cell = PlantTargeting.cellOf(world, plant);
        if (cell != null) {
            AttackService attackService = PlantAbilitySupport.attacks(world);
            attackService.damageArea(
                cell.getRow(),
                cell.getCol(),
                explosionRadius,
                plant.getAttackDamage(),
                currentTick,
                DeathStyle.ASH
            );
        }
        PlantAbilitySupport.consumeLater(plant, plantService, currentTick, GameClock.TICKS_PER_SECOND);
    }
}

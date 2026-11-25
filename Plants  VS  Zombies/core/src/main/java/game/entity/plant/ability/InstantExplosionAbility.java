package game.entity.plant.ability;

import app.enums.plant.PlantGameplayPhase;
import game.engine.time.GameClock;
import game.entity.DeathStyle;
import game.entity.plant.Plant;
import game.service.plant.AttackService;
import game.service.plant.PlantService;
import game.world.playground.Cell;
import graphics.general.event.PlantVisualEventType;

public class InstantExplosionAbility extends AbstractPlantAbility {
    private final int explosionRadius;
    private final String explosionEffectId;
    private final int impactDelayTicks;
    private final float areaPercent;
    private boolean started;
    private boolean detonated;

    public InstantExplosionAbility(int explosionRadius, String explosionEffectId) {
        this(explosionRadius, explosionEffectId, 8, 0f);
    }

    public InstantExplosionAbility(int explosionRadius, String explosionEffectId, int impactDelayTicks) {
        this(explosionRadius, explosionEffectId, impactDelayTicks, 0f);
    }

    public InstantExplosionAbility(
        int explosionRadius,
        String explosionEffectId,
        int impactDelayTicks,
        float areaPercent
    ) {
        this.explosionRadius = explosionRadius;
        this.explosionEffectId = explosionEffectId;
        this.impactDelayTicks = impactDelayTicks;
        this.areaPercent = areaPercent;
    }

    @Override
    protected void doUpdate(Plant plant, PlantService plantService, long currentTick) {
        if (plant == null || started) {
            return;
        }
        started = true;
        plant.setGameplayPhase(PlantGameplayPhase.ATTACKING);
        PlantAbilitySupport.play(plant, PlantVisualEventType.ATTACK_TRIGGERED);
        PlantAbilitySupport.schedule(currentTick, impactDelayTicks, () -> detonate(plant, plantService));
    }

    private void detonate(Plant plant, PlantService plantService) {
        if (detonated || plant == null) {
            return;
        }
        detonated = true;
        long currentTick = GameClock.currentTick;
        Cell cell = PlantTargeting.cellOf(plantService.getGameWorld(), plant);
        PlantAbilitySupport.spawnEffect(explosionEffectId, plant.getPosition());
        if ("cherry_bomb_explosion".equals(explosionEffectId)) {
            PlantAbilitySupport.spawnEffect("cherry bomb explosion top", plant.getPosition());
        }
        PlantAbilitySupport.notifyExploded(plant);
        if (cell != null) {
            AttackService attackService = PlantAbilitySupport.attacks(plantService.getGameWorld());
            attackService.damageArea(
                cell.getRow(),
                cell.getCol(),
                explosionRadius,
                plant.getAttackDamage(),
                currentTick,
                DeathStyle.ASH,
                areaPercent
            );
        }
        PlantAbilitySupport.consumeLater(plant, plantService, currentTick, GameClock.TICKS_PER_SECOND);
    }
}

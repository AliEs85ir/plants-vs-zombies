package game.entity.plant.ability;

import app.enums.plant.PlantGameplayPhase;
import common.model.Position;
import game.engine.time.GameClock;
import game.entity.DeathStyle;
import game.entity.plant.Plant;
import game.entity.projectile.ShooterProjectile;
import game.service.plant.AttackService;
import game.service.plant.PlantService;
import game.service.projectile.ProjectileFactory;
import game.world.playground.Cell;
import graphics.general.event.PlantVisualEventType;

import java.util.concurrent.ThreadLocalRandom;

public class GrapeshotAbility extends AbstractPlantAbility {
    private static final int PROJECTILE_COUNT = 8;
    private static final double PROJECTILE_SPEED = 2.6;
    private static final int IMPACT_DELAY_TICKS = 9;
    private static final float AREA_PERCENT = 0.10f;

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
        Cell cell = PlantTargeting.cellOf(plantService.getGameWorld(), plant);
        PlantAbilitySupport.spawnEffect("grapeshot_explosion", plant.getPosition());
        PlantAbilitySupport.notifyExploded(plant);
        if (cell != null) {
            AttackService attackService = PlantAbilitySupport.attacks(plantService.getGameWorld());
            attackService.damageArea(
                cell.getRow(),
                cell.getCol(),
                1,
                plant.getAttackDamage(),
                currentTick,
                DeathStyle.ASH,
                AREA_PERCENT
            );
        }
        spawnGrapes(plant, plantService);
        PlantAbilitySupport.consumeLater(plant, plantService, currentTick, GameClock.TICKS_PER_SECOND);
    }

    private void spawnGrapes(Plant plant, PlantService plantService) {
        Position origin = plant.getPosition();
        for (int i = 0; i < PROJECTILE_COUNT; i++) {
            double angle = ThreadLocalRandom.current().nextDouble(360.0);
            Position spawnPos = new Position(origin.getX(), origin.getY());
            ShooterProjectile grape = ProjectileFactory.createShooterProjectile(
                plant,
                20,
                spawnPos,
                PROJECTILE_SPEED,
                angle
            );
            plantService.addProjectile(grape);
        }
    }
}

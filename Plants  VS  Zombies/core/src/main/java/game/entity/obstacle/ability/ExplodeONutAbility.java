package game.entity.obstacle.ability;

import app.enums.plant.PlantName;
import app.view.game.PlayScreen;
import game.entity.DeathStyle;
import game.entity.obstacle.Obstacle;
import game.entity.registry.PlantRegistry;
import game.entity.zombie.Zombie;
import game.service.obstacle.ObstacleService;
import game.service.plant.AttackService;
import game.world.playground.Cell;
import game.world.playground.Grid;
import graphics.general.effects.GameplayEffects;

import java.util.ArrayList;
import java.util.List;

public class ExplodeONutAbility implements ObstacleAbility {
    private static final int EXPLOSION_RADIUS = 1;

    private float speedX = 2f;
    private float speedY = 0f;
    private boolean isInitialized = false;

    @Override
    public void update(Obstacle obstacle, ObstacleService obstacleService, long currentTick) {
        if (!isInitialized) {
            obstacle.getBody().setLinearVelocity(speedX, speedY);
            obstacle.getBody().setAngularVelocity(-6f);
            obstacleService.applyRotation(obstacle);
            isInitialized = true;
        }

        Grid grid = obstacleService.getWorld().getGrid();

        if (!grid.isInsideMap(obstacle.getPosition())) {
            obstacle.die();
            return;
        }

        handleHitAndBounce(obstacle, obstacleService, currentTick);
    }

    @Override
    public void onDeath(Obstacle obstacle) {
    }

    private void handleHitAndBounce(Obstacle obstacle, ObstacleService obstacleService, long currentTick) {
        List<Zombie> targets = obstacle.getTargetZombies();
        if (targets == null || targets.isEmpty()) {
            return;
        }

        Grid grid = obstacleService.getWorld().getGrid();
        Cell cell = grid.getCellAt(obstacle.getPosition());
        int damage = PlantRegistry.get(PlantName.EXPLODE_O_NUT).getAttackDamage();
        AttackService attackService = new AttackService(obstacleService.getWorld());

        PlayScreen.shake(0.6f, 16f);
        game.audio.Sfx.play(game.audio.AudioId.SFX_EXPLOSION);
        GameplayEffects.spawnExplosionPair(obstacle.getPosition());

        if (cell != null) {
            attackService.damageArea(
                cell.getRow(),
                cell.getCol(),
                EXPLOSION_RADIUS,
                damage,
                currentTick,
                DeathStyle.ASH
            );
        } else {
            for (Zombie zombie : new ArrayList<>(targets)) {
                if (zombie != null && !zombie.isDead()) {
                    attackService.damageZombie(zombie, currentTick, damage, DeathStyle.ASH, obstacle);
                }
            }
        }

        obstacle.clearTargetZombies();
        obstacle.die();
    }
}

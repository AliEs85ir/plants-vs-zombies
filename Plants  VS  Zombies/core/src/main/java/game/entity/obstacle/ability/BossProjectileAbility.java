package game.entity.obstacle.ability;

import app.enums.obstacle.ObstacleType;
import app.model.App;
import app.view.game.PlayScreen;
import com.badlogic.gdx.math.Vector2;
import game.engine.time.GameClock;
import game.entity.obstacle.Obstacle;
import game.entity.obstacle.ObstacleFactory;
import game.entity.plant.Plant;
import game.service.Zombie.ZombieService;
import game.service.obstacle.ObstacleService;
import game.world.GameWorld;
import game.world.playground.Cell;
import graphics.general.effects.EffectConditions;
import graphics.general.effects.EffectLayer;
import graphics.general.effects.EffectRequest;
import graphics.general.effects.GameplayEffects;
import graphics.general.effects.anchers.WorldAnchor;
import graphics.general.event.ObstacleVisualEventType;

import java.util.Collections;
import java.util.List;

import static game.entity.zombie.ability.TombRaiserAbility.spawnTomb;

public class BossProjectileAbility implements ObstacleAbility{
    private Vector2 velocity;
    private float landY;
    private Cell target;
    private boolean landed;

    public BossProjectileAbility(Cell target, float velocityY) {
        this.target = target;
        this.landY = target.getWorldY() + target.getHeight() / 2;
        this.velocity = new Vector2(0, -velocityY);
    }

    @Override
    public void update(Obstacle obstacle, ObstacleService obstacleService, long currentTick) {
        if(landed) {return;}
        obstacle.getBody().setLinearVelocity(velocity);
        if(obstacle.getPosition().getY() <= landY) {
            obstacle.stopMovement();
            obstacle.pushEvent(ObstacleVisualEventType.IMPACT);
            shakeOnImpact(obstacle);
            for(Plant plant : obstacleService.getWorld().getPlants()) {
                if(target.equals(obstacleService.getWorld()
                    .getGrid().getCellAt(plant.getPosition()))) {
                    plant.markDead(obstacleService.getWorld());
                }
            }
            obstacle.die();
            landed = true;
            spawnImpactEffect(obstacle);
            playImpactSfx(obstacle);
        }
    }

    private void spawnImpactEffect(Obstacle obstacle) {
        if (obstacle == null || obstacle.getType() != ObstacleType.SLINGSHOT || target == null) {
            return;
        }
        GameplayEffects.spawn("zomboss iceage boom", target.getWorldPosition(), EffectLayer.FRONT);
    }

    private static void playImpactSfx(Obstacle obstacle) {
        if (obstacle == null || obstacle.getType() == null) {
            return;
        }
        switch (obstacle.getType()) {
            case MISSILE -> game.audio.Sfx.play(game.audio.AudioId.SFX_EXPLOSION);
            case SLINGSHOT -> game.audio.Sfx.play(game.audio.AudioId.SFX_FROZEN);
            case FIREBALL -> game.audio.Sfx.playRandom(game.audio.AudioId.SFX_IGNITE, game.audio.AudioId.SFX_JALAPENO);
            default -> game.audio.Sfx.play(game.audio.AudioId.SFX_GARGANTUAR_THUMP);
        }
    }

    private static void shakeOnImpact(Obstacle obstacle) {
        if (obstacle == null || obstacle.getType() == null) {
            return;
        }
        switch (obstacle.getType()) {
            case MISSILE -> PlayScreen.shake(0.65f, 22f);
            case SLINGSHOT -> PlayScreen.shake(0.55f, 18f);
            case FIREBALL -> PlayScreen.shake(0.45f, 16f);
            default -> PlayScreen.shake(0.4f, 14f);
        }
    }

    private void spawnImpDragon() {
        if (target == null) {
            return;
        }
        GameWorld world = App.getCurrentGameEngine().getGameWorld();
        if (world == null) {
            return;
        }
        new ZombieService(world).spawnZombie("Imp9", target.getWorldPosition());
    }

    public static void SpawnTargetEffect(Cell target) {
        if (target == null || App.getRealTimeEngine() == null || App.getRealTimeEngine().getGraphicsEngine() == null) {
            return;
        }
        EffectRequest<Object> request = new EffectRequest<>(
            "target",
            null,
            new WorldAnchor(target.getWorldPosition()),
            EffectLayer.FRONT,
            EffectConditions.withinSeconds(3)
        );
        App.getRealTimeEngine().getGraphicsEngine().getEffectManager().spawn(request);
    }

    @Override
    public void onDeath(Obstacle obstacle) {
        if(obstacle.getType() == ObstacleType.FIREBALL) {
            ObstacleFactory.getInstance().createRuntimeObstacle(
                ObstacleType.TEMPORARY_SCORCH,
                target.getWidth() / 2,
                target.getHeight() / 2,
                target.getWorldX(),
                target.getWorldY(),
                List.of(new TimerdLifeAbility(4))
            );
            spawnImpDragon();
        }
        else if(obstacle.getType() == ObstacleType.MISSILE) {
            List<Cell> cells = App.getCurrentGameEngine().getGameWorld().getGrid().getFirstColumns(5);
            Collections.shuffle(cells);
            int placed = 0;
            for(Cell cell : cells) {
                if(placed >= 2) {
                    break;
                }
                if(cell.hasPlant() || cell.hasObstacle()) {
                    continue;
                }
                App.getCurrentGameEngine().getTaskManager().scheduleRelative(
                    GameClock.currentTick,
                    10,
                    () -> spawnTomb(cell)
                );
                App.getRealTimeEngine().getGraphicsEngine().getEffectManager().spawn(
                    new EffectRequest<>("spawn tomb",
                        null,
                        new WorldAnchor(cell.getWorldPosition()),
                        EffectLayer.FRONT )
                );
                placed++;
            }
        }
    }

}

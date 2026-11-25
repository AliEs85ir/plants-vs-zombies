package game.entity.zombie.ability.plantzombies;

import app.enums.obstacle.ObstacleType;
import app.model.App;
import com.badlogic.gdx.math.Vector2;
import game.entity.obstacle.ObstacleFactory;
import game.entity.zombie.Zombie;
import game.entity.zombie.ability.AbstractZombieAbility;
import game.service.Zombie.ZombieService;
import graphics.general.effects.EffectConditions;
import graphics.general.effects.EffectLayer;
import graphics.general.effects.EffectRequest;
import graphics.general.effects.anchers.EntityAnchor;
import graphics.zombie.ZombieRenderInstance;

import java.util.List;

public class SquashAbility extends AbstractZombieAbility {
    private static final double WALK_SPEED_MULTIPLIER = 1.8;

    private boolean effectSpawned;
    private boolean headHidden;

    @Override
    public void update(Zombie zombie, ZombieService zombieService, long currentTick) {
        configureWalkSpeed(zombie);
        if (!effectSpawned) {
            spawnEffect(zombie);
            effectSpawned = true;
        }
        if (!headHidden) {
            headHidden = hideZombieHead(zombie);
        }

    }

    private static void configureWalkSpeed(Zombie zombie) {
        if (zombie == null || zombie.isDead()) {
            return;
        }
        if (zombie.getMoveSpeedMultiplier() < WALK_SPEED_MULTIPLIER) {
            zombie.setMoveSpeedMultiplier(WALK_SPEED_MULTIPLIER);
        }
    }

    private static void spawnEffect(Zombie zombie) {
        Vector2 offset = new Vector2(-5, 70);
        EntityAnchor<Zombie> anchor = new EntityAnchor<>(zombie, zombie1 -> {
            return zombie1.getPosition().toVector();
        }, offset);
        EffectRequest<Zombie> request = new EffectRequest<>(
            "squash idle",
            zombie,
            anchor,
            EffectLayer.FRONT,
            EffectConditions.plantZombieCondition(zombie)
        );
        request.setFlipX(true);
        var idleEffect = App.getRealTimeEngine().getGraphicsEngine().getEffectManager().spawn(request);
        ObstacleFactory.getInstance().createRuntimeObstacle(
            ObstacleType.SQUASH,
            50,
            40,
            (float) zombie.getPosition().getX() - offset.x,
            (float) zombie.getPosition().getY() + offset.y,
            List.of(new game.entity.obstacle.ability.SquashAbility(zombie, offset, idleEffect))
        );
    }

    private static boolean hideZombieHead(Zombie zombie) {
        ZombieRenderInstance instance = App.getRealTimeEngine().getGraphicsEngine().getZombieInstance(zombie);
        if (instance == null) {
            return false;
        }
        instance.getVisibilityController().setInvisible("zombie_jaw");
        instance.getVisibilityController().setInvisible("zombie_skull");
        return true;
    }

    @Override
    public void onDeath(Zombie zombie) {
    }
}

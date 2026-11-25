package game.entity.zombie.ability.plantzombies;

import app.enums.obstacle.ObstacleType;
import app.model.App;
import com.badlogic.gdx.math.Vector2;
import game.entity.obstacle.ObstacleFactory;
import game.entity.obstacle.ability.ProjectileAbility;
import game.entity.zombie.Zombie;
import game.entity.zombie.ability.AbstractZombieAbility;
import game.service.Zombie.ZombieService;
import graphics.general.effects.EffectConditions;
import graphics.general.effects.EffectLayer;
import graphics.general.effects.EffectRequest;
import graphics.general.effects.anchers.EntityAnchor;
import graphics.zombie.ZombieRenderInstance;

import java.util.List;

public class PeashooterAbility extends AbstractZombieAbility {
    private boolean effectSpawned;
    private boolean headHidden;
    private int tick = 5;

    @Override
    public void update(Zombie zombie, ZombieService zombieService, long currentTick) {
        if (!effectSpawned) {
            spawnEffect(zombie);
            effectSpawned = true;
        }
        if (!headHidden) {
            headHidden = hideZombieHead(zombie);
        }
        if(tick >= 12) {
            ObstacleFactory.getInstance().createRuntimeObstacle(
                ObstacleType.PEASHOOTER,
                20,
                40,
                (float) zombie.getPosition().getX() - 15,
                (float) zombie.getPosition().getY() + 40,
                List.of(new ProjectileAbility())
            );
            tick = 0;
        }
        tick++;
    }

    private static void spawnEffect(Zombie zombie) {
        EntityAnchor<Zombie> anchor = new EntityAnchor<>(zombie, zombie1 -> {
            return zombie1.getPosition().toVector();
        }, new Vector2(-10, 50));
        EffectRequest<Zombie> request = new EffectRequest<>(
            "peashooter shoot",
            zombie,
            anchor,
            EffectLayer.FRONT,
            EffectConditions.plantZombieCondition(zombie)
        );
        request.setFlipX(true);
        App.getRealTimeEngine().getGraphicsEngine().getEffectManager().spawn(request);
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

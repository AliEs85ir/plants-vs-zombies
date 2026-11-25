package game.entity.zombie.ability;

import app.enums.obstacle.ObstacleType;
import app.model.App;
import common.model.Position;
import game.engine.time.GameClock;
import game.entity.obstacle.Obstacle;
import game.entity.obstacle.ObstacleFactory;
import game.entity.obstacle.ability.SnowballAbility;
import game.entity.projectile.Projectile;
import game.entity.zombie.Zombie;
import game.service.Zombie.ZombieService;
import game.service.projectile.ProjectileFactory;
import graphics.general.event.ZombieVisualEventType;

import java.util.List;

public class ProjectileThrowAbility extends AbstractZombieAbility{
    private long COOLDOWN_TICKS = 15 * GameClock.TICKS_PER_SECOND;
    private long LAST_THROW = -1;

    @Override
    public void update(Zombie zombie, ZombieService zombieService, long currentTick) {
        if(!canThrow(currentTick)) {
            return;
        }
        zombie.pushEvent(ZombieVisualEventType.THROW_SNOWBALL);
        Position projectilePosition = new Position(zombie.getPosition().getX() - 50, zombie.getPosition().getY() + 40);
        App.getCurrentGameEngine().getTaskManager().scheduleRelative(
            currentTick,
            10,
            () -> throwProjectile(zombie, projectilePosition)
        );
        LAST_THROW = currentTick;
    }

    private static void throwProjectile(Zombie zombie, Position projectilePosition) {
        ObstacleFactory.getInstance().createRuntimeObstacle(
            ObstacleType.SNOWBALL,
            40,
            40,
            (float) projectilePosition.getX(),
            (float) projectilePosition.getY(),
            List.of(new SnowballAbility())
        );
    }

    @Override
    public void onDeath(Zombie zombie) {

    }

    public boolean canThrow(long currentTick) {
        return LAST_THROW < 0 || LAST_THROW + COOLDOWN_TICKS < currentTick;
    }
}

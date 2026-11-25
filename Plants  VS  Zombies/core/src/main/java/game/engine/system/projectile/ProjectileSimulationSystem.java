package game.engine.system.projectile;

import app.model.App;
import game.engine.time.TickSystem;
import game.entity.projectile.Projectile;
import game.entity.projectile.ProjectileObstacleSweep;
import game.entity.projectile.ProjectileFireTransform;
import game.world.GameWorld;
import common.model.Position;

import java.util.Iterator;
import java.util.List;

public class ProjectileSimulationSystem implements TickSystem {
    private final GameWorld gameWorld;

    public ProjectileSimulationSystem(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
    }

    @Override
    public void update(long currentTick) {
        List<Projectile> projectiles = gameWorld.getProjectiles();
        Iterator<Projectile> iterator = projectiles.iterator();
        while (iterator.hasNext()) {

            Projectile projectile = iterator.next();

            if (!projectile.isActive()) {
                destroyProjectileBody(projectile);
                iterator.remove();
                continue;
            }
            projectile.update(gameWorld, currentTick);
            if (projectile.isActive()) {
                ProjectileObstacleSweep.collideAlongMove(gameWorld, projectile);
            }
            if (!projectile.isActive()) {
                destroyProjectileBody(projectile);
                iterator.remove();
                continue;
            }
            Position current = projectile.getPosition();
            if (projectile.hasLastPosition() && current != null) {
                ProjectileFireTransform.applyAlongSegment(
                    gameWorld,
                    projectile,
                    projectile.getLastX(),
                    projectile.getLastY(),
                    current.getX(),
                    current.getY()
                );
                ProjectileFireTransform.thawPlantsAlongSegment(
                    gameWorld,
                    projectile,
                    projectile.getLastX(),
                    projectile.getLastY(),
                    current.getX(),
                    current.getY()
                );
            }
            projectile.rememberPosition();
        }
    }

    private void destroyProjectileBody(Projectile projectile) {
        if (projectile == null) {
            return;
        }
        projectile.rememberPosition();
        if (projectile.getBody() != null && App.getRealTimeEngine() != null) {
            projectile.destroyBody(App.getRealTimeEngine().getPhysicService());
        }
    }
}

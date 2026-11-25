package game.service.projectile;

import com.badlogic.gdx.physics.box2d.Body;
import common.model.Position;
import game.component.physic.BodyFactory;
import game.entity.Entity;
import game.entity.plant.Plant;
import game.entity.projectile.LobbedProjectile;
import game.entity.projectile.Projectile;
import game.entity.projectile.ProjectileBodyRadii;
import game.entity.projectile.ShooterProjectile;
import game.entity.zombie.Zombie;

public final class ProjectileFactory {

    private ProjectileFactory() {
    }

    public static ShooterProjectile createShooterProjectile(Plant owner, int damage, Position position, double speed, double angle) {
        return createShooterProjectile(owner, damage, position, speed, angle, 0);
    }

    public static ShooterProjectile createShooterProjectile(Plant owner, int damage, Position position, double speed, double angle, int delayOffset) {
        ShooterProjectile projectile = new ShooterProjectile(owner, damage, speed, angle, delayOffset);
        attachBody(projectile, position, speed, angle);
        return projectile;
    }

    public static LobbedProjectile createLobbedProjectile(Plant owner, int damage, Position position, double speed, Zombie target) {
        Position fallbackImpact = target != null && target.getPosition() != null
            ? new Position(target.getPosition().getX(), target.getPosition().getY())
            : new Position(position.getX(), position.getY());
        return createLobbedProjectile(owner, damage, position, speed, target, fallbackImpact);
    }

    public static LobbedProjectile createLobbedProjectile(
        Plant owner,
        int damage,
        Position position,
        double speed,
        Zombie target,
        Position impactPosition
    ) {
        Position impact = impactPosition != null
            ? new Position(impactPosition.getX(), impactPosition.getY())
            : new Position(position.getX(), position.getY());
        LobbedProjectile projectile = new LobbedProjectile(owner, damage, speed, target, impact);
        attachBody(projectile, position);
        return projectile;
    }

    private static void attachBody(Projectile projectile, Position position) {
        if (position != null) {
            projectile.setSpawnWorld(new Position(position.getX(), position.getY()));
        }
        if (!BodyFactory.isInitialized()) {
            return;
        }
        float radius = ProjectileBodyRadii.forPlant(projectile.getPlantName());
        Body body = BodyFactory.getInstance().createProjectileBody((float) position.getX(), (float) position.getY(), radius);
        body.setUserData(projectile);
        projectile.setBody(body);
    }

    private static void attachBody(Projectile projectile, Position position, double speed, double angle) {
        attachBody(projectile, position);
        double angleRad = Math.toRadians(angle);
        float vx = (float) (speed * Math.cos(angleRad));
        float vy = (float) (speed * Math.sin(angleRad));
        if (projectile.getBody() != null) {
            if (projectile.getBody() != null) {
            projectile.getBody().setLinearVelocity(vx, vy);
        }
        }
    }
}

package game.entity.projectile;

import app.enums.effect.EffectType;
import app.enums.projectile.ProjectileType;
import app.model.App;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import common.model.Position;
import game.component.physic.PhysicService;
import game.entity.obstacle.Obstacle;
import game.entity.plant.Plant;
import game.entity.zombie.Zombie;
import graphics.general.effects.EffectLayer;
import graphics.general.effects.EffectRequest;
import graphics.general.effects.anchers.WorldAnchor;

public class ShooterProjectile extends Projectile {
    protected double angle;

    public ShooterProjectile(Plant owner, int damage, double speed, double angle) {
        this(owner, damage, speed, angle, 0);
    }

    public ShooterProjectile(Plant owner, int damage, double speed, double angle, int delayOffset) {
        super(owner, damage, speed, ProjectileType.NORMAL, delayOffset);
        this.angle = angle;
    }

    @Override
    protected void launch() {
        if (body == null) {
            return;
        }

        float radians =
            MathUtils.degreesToRadians * (float) angle;

        float velocity =
            (float) speed /
                PhysicService.PIXEL_PER_METER;

        body.setLinearVelocity(
            velocity * MathUtils.cos(radians),
            velocity * MathUtils.sin(radians)
        );

        startPhysicsPosition =
            new Vector2(body.getPosition());

    }

    @Override
    public void handleHit(Obstacle obstacle, long currentTick) {
        if (!active || obstacle == null || obstacle.isDead() || hitObstacles.contains(obstacle) || !obstacle.isDamageable()) {
            return;
        }
        if (canBypassObstacles) {
            hitObstacles.add(obstacle);
            return;
        }
        applyDamage(obstacle);
        if (!infinitePierce && hitObstacles.size() >= maxTargets) {
            destroy();
        }
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
        if (launched && body != null) {
            launch();
        }
    }

    @Override
    public void handleHit(Zombie zombie, long currentTick) {
        if (!active || zombie == null || hitZombies.contains(zombie)) {
            return;
        }
        if (zombie.isDead()) {
            return;
        }
        if (enemyOnly && zombie.hasEffect(EffectType.CHARM)) {
            return;
        }
        Position targetCenter = zombie.getPosition();
        applyDamage(zombie, resolveDamage(), currentTick);
        spawnHitEffectAt(targetCenter);
        if (!infinitePierce && hitZombies.size() >= maxTargets) {
            destroy();
        }
    }

    private void spawnHitEffectAt(Position targetCenter) {
        String effectId = hitEffectId != null && !hitEffectId.isBlank() ? hitEffectId : legacyHitEffectId();
        if (effectId == null || targetCenter == null) {
            return;
        }
        lastHitEffectWorld = new Position(targetCenter.getX(), targetCenter.getY());
        if (App.getRealTimeEngine() == null || App.getRealTimeEngine().getGraphicsEngine() == null) {
            return;
        }
        var effectManager = App.getRealTimeEngine().getGraphicsEngine().getEffectManager();
        if (effectManager == null) {
            return;
        }
        effectManager.spawn(new EffectRequest<>(effectId, null, new WorldAnchor(lastHitEffectWorld), EffectLayer.FRONT));
    }

    private String legacyHitEffectId() {
        Plant plant = getOwner();
        if (plant == null || plant.getDefinition() == null) {
            return null;
        }
        return switch (plant.getDefinition().getName()) {
            case CACTUS -> "cactus_hit";
            case GRAPESHOT -> "grapeshot_hit";
            default -> null;
        };
    }
}
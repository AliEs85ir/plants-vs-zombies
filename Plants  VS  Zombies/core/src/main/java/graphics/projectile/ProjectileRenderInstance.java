package graphics.projectile;

import common.model.Position;
import game.entity.projectile.Projectile;
import graphics.general.instance.RenderInstance;

public class ProjectileRenderInstance
    extends RenderInstance<Projectile, ProjectileVisualState, ProjectileVisualEventType, ProjectileAnimationVariant> {

    private Position cachedPosition;

    public ProjectileRenderInstance(Projectile projectile) {
        super(projectile, ProjectileVisualState.FLYING);
        this.cachedPosition = resolveWorldPosition(projectile);
    }

    @Override
    protected void updatePosition() {
        Position resolved = resolveWorldPosition(getModel());
        if (resolved != null) {
            cachedPosition = resolved;
        }
    }

    public Position getRenderPosition() {
        return cachedPosition;
    }

    @Override
    public boolean isReadyForRemoval() {
        return getCurrentVisualState() == ProjectileVisualState.IMPACT
            && getAnimationController().isFinished();
    }

    @Override
    protected float generateRandomVariationInterval() {
        return Float.MAX_VALUE;
    }

    private static Position resolveWorldPosition(Projectile projectile) {
        if (projectile == null) {
            return null;
        }
        Position impact = projectile.getLastHitEffectWorld();
        if (impact != null) {
            return new Position(impact.getX(), impact.getY());
        }
        return projectile.getPosition();
    }
}
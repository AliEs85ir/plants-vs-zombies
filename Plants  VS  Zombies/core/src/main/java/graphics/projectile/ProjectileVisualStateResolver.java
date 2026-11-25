package graphics.projectile;

import game.entity.projectile.Projectile;
import graphics.general.event.VisualEvent;
import graphics.general.resolver.VisualStateResolver;

public class ProjectileVisualStateResolver
    implements VisualStateResolver<Projectile, ProjectileVisualState, ProjectileRenderInstance> {

    @Override
    public ProjectileVisualState resolve(Projectile model, ProjectileRenderInstance instance,
                                         ProjectileVisualState currentState, boolean animationFinished) {
        if (currentState == ProjectileVisualState.IMPACT) {
            return ProjectileVisualState.IMPACT;
        }

        VisualEvent<ProjectileVisualEventType> event = instance.pollEvent();
        if (event != null && event.getType() == ProjectileVisualEventType.DESTROY) {
            return ProjectileVisualState.IMPACT;
        }
        return ProjectileVisualState.FLYING;
    }
}

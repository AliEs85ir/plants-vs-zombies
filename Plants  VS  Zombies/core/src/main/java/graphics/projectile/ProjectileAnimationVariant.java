// graphics/projectile/ProjectileAnimationVariant.java
package graphics.projectile;

import game.entity.projectile.Projectile;
import graphics.general.animation.AnimationVariant;
import graphics.general.animation.condition.AnimationCondition;

public class ProjectileAnimationVariant extends AnimationVariant<Projectile, ProjectileAnimationTag> {
    public ProjectileAnimationVariant(String clipName, boolean loop, int priority, ProjectileAnimationTag tag,
                                      AnimationCondition<Projectile> condition, String pamPath) {
        super(clipName, loop, priority, tag, condition, pamPath);
    }

    public ProjectileAnimationVariant(String clipName, boolean loop, int priority, ProjectileAnimationTag tag,
                                      AnimationCondition<Projectile> condition) {
        super(clipName, loop, priority, tag, condition);
    }

    public ProjectileAnimationVariant(String clipName, boolean loop, int priority, ProjectileAnimationTag tag) {
        super(clipName, loop, priority, tag);
    }
}
